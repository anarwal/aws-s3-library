package edu.common.service;

import java.io.*;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import static com.amazonaws.services.s3.internal.Constants.MB;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Service
public class S3Service implements IFileStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3Service.class);

    private String bucket;

    /**
     * Provides an interface for accessing the Amazon S3 web service.
     */
    private AmazonS3 s3;

    /**
     * TransferManager provides a simple API for uploading content to Amazon S3, it uses Amazon S3 multipart uploads to achieve enhanced throughput, performance and reliability.
     */
    private TransferManager tm;

    /**
     * Creates the S3 Service Object using the provided credentials. Defaults to US_EAST_1 region.
     *
     * @param bucket           {@link String} S3 Bucket created by S3 customer
     * @param awsKey           {@link BasicAWSCredentials#accessKey}
     * @param awsSecret        {@link BasicAWSCredentials#secretKey}
     * @param minPartSize      {@link Integer} Sets the minimum part size for upload parts
     * @param uploadThreshold  {@link Integer} Sets the size threshold, in bytes, for when to use multipart uploads
     * @param partSize         {@link Integer} Sets the minimum size in bytes of each part when a multi-part copy operation is carried out
     * @param copyThreshold    {@link Integer} Sets the size threshold, in bytes, for when to use multi-part copy
     * @param executorThread   {@link Integer} Sets the number of threads in the pool used to operate off a shared unbounded queue
     */
    public S3Service(String bucket, String awsKey, String awsSecret, Integer minPartSize, Integer uploadThreshold, Integer partSize, Integer copyThreshold, Integer executorThread) {
        this(bucket, awsKey, awsSecret, "us-east-1", minPartSize, uploadThreshold, partSize, copyThreshold, executorThread);
    }

    /**
     * Creates the S3 Service Object using the provided credentials and region.
     *
     * @param bucket    {@link String} S3 Bucket created by S3 customer
     * @param awsKey    {@link BasicAWSCredentials#accessKey}
     * @param awsSecret {@link BasicAWSCredentials#secretKey}
     * @param region    {@link Regions} Sets S3 Region
     * @param minPartSize      {@link Integer} Sets the minimum part size for upload parts
     * @param uploadThreshold  {@link Integer} Sets the size threshold, in bytes, for when to use multipart uploads
     * @param partSize         {@link Integer} Sets the minimum size in bytes of each part when a multi-part copy operation is carried out
     * @param copyThreshold    {@link Integer} Sets the size threshold, in bytes, for when to use multi-part copy
     * @param executorThread   {@link Integer} Sets the number of threads in the pool used to operate off a shared unbounded queue
     */
    public S3Service(String bucket, String awsKey, String awsSecret, String region, Integer minPartSize, Integer uploadThreshold, Integer partSize, Integer copyThreshold, Integer executorThread) {
        Assert.notNull(bucket, "bucket must be provided");
        Assert.notNull(awsKey, "awsKey must be provided");
        Assert.notNull(awsSecret, "awsSecret must be provided");
        Assert.notNull(region, "region must be provided");
        Assert.notNull(minPartSize,  "minimum part size must be provided");
        Assert.notNull(uploadThreshold,  "upload threshold must be provided");
        Assert.notNull(partSize,  "part size must be provided");
        Assert.notNull(copyThreshold,  "copy threshold must be provided");
        Assert.notNull(executorThread,  "executor thread must be provided");
        this.bucket = bucket;
        s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsKey, awsSecret)))
                .build();

        tm = TransferManagerBuilder.standard()
                .withS3Client(s3)
                .withDisableParallelDownloads(false)
                .withMinimumUploadPartSize(Long.valueOf(minPartSize * MB))
                .withMultipartUploadThreshold(Long.valueOf(uploadThreshold * MB))
                .withMultipartCopyPartSize(Long.valueOf(partSize * MB))
                .withMultipartCopyThreshold(Long.valueOf(copyThreshold * MB))
                .withExecutorFactory(() -> createExecutorService(executorThread))
                .build();
    }

    /**
     * Creates/Updates the Provided {@link File} into S3 Bucket
     *
     * @param key  {@link PutObjectRequest#key}
     * @param file {@link PutObjectRequest#file}
     */
    @Override
    public void put(String key, File file) {
        Assert.notNull(key, "key must be provided");
        Assert.notNull(file, "file must be provided");
        StopWatch stopWatch = new StopWatch();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Sending document to S3 bucket {} with location {}", keyValue("bucket", bucket), keyValue("location", key));
                stopWatch.start();
            }
            PutObjectRequest request = new PutObjectRequest(bucket, key, file);

            request.setGeneralProgressListener(progressEvent -> LOGGER.info("Transferred bytes: {}", progressEvent.getBytesTransferred()));

            Upload upload = tm.upload(request);
            upload.waitForCompletion();
            tm.abortMultipartUploads(bucket, days());
            if (LOGGER.isDebugEnabled()) {
                stopWatch.stop();
                LOGGER.info("Document saved to S3 bucket {} with location {} in {} milliseconds",
                        keyValue("bucket", bucket), keyValue("location", key), stopWatch.getTotalTimeMillis());
            }
        } catch (AmazonServiceException e) {
            String.format("Failed saving to S3 bucket %s with location %s\n\n%s", keyValue("bucket", bucket).toString(), keyValue("location", key).toString(), e.getMessage());
            throw e;
        }
        catch (InterruptedException e) {
            throw new FileStorageServiceException(e);
        }
    }

    /**
     * Creates/Updates the Provided content into S3 Bucket
     *
     * @param key         {@link PutObjectRequest#key}
     * @param content     byte[] used for {@link PutObjectRequest#inputStream}
     * @param contentType Content Type for {@link ObjectMetadata#metadata}
     */
    @Override
    public void put(String key, byte[] content, String contentType) {
        Assert.notNull(key, "key must be provided");
        Assert.notNull(content, "content must be provided");
        Assert.notNull(contentType, "contentType must be provided");
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        StopWatch stopWatch = new StopWatch();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Sending document to S3 bucket {} with location {}", keyValue("bucket", bucket), keyValue("location", key));
                stopWatch.start();
            }
            PutObjectRequest request = new PutObjectRequest(bucket, key, new ByteArrayInputStream(content), objectMetadata);

            request.setGeneralProgressListener(progressEvent -> LOGGER.info("Transferred bytes: {}", progressEvent.getBytesTransferred()));

            Upload upload = tm.upload(request);
            upload.waitForCompletion();
            tm.abortMultipartUploads(bucket, days());
            if (LOGGER.isDebugEnabled()) {
                stopWatch.stop();
                LOGGER.info("Document saved to S3 bucket {} with location {} in {} milliseconds",
                        keyValue("bucket", bucket), keyValue("location", key), stopWatch.getTotalTimeMillis());
            }
        } catch (AmazonServiceException e) {
            String errorMessage = String.format("Failed saving to S3 bucket %s with location %s\n\n%s",
                    keyValue("bucket", bucket).toString(),
                    keyValue("location", key).toString(), e.getMessage());
            LOGGER.error(errorMessage, e);
            throw e;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the S3 content by Key.
     *
     * @param key {@link S3ObjectIdBuilder#key}
     * @return String
     */
    @Override
    public byte[] get(String key) throws FileNotFoundException {
        Assert.notNull(key, "key must be provided");
        S3Object object = null;
        try {
            LOGGER.debug("Fetching document from S3 bucket {} with location {}", keyValue("bucket", bucket), keyValue("location", key));
            object = s3.getObject(new GetObjectRequest(bucket, key));
            LOGGER.debug("Finished fetching document from S3 bucket {} with location {}", keyValue("bucket", bucket), keyValue("location", key));
            return toByteArray(object.getObjectContent());
        } catch (AmazonServiceException aws) {
            String errorMessage = String.format("Failed fetching document from S3 bucket %s with location %s\n\n%s",
                    keyValue("bucket", bucket),
                    keyValue("location", key), aws.getMessage());
            LOGGER.error(errorMessage, aws);
            if (aws.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new FileNotFoundException("Unable to locate document asset with Key " + key);
            }
            throw new IllegalStateException("Unexpected error trying to download asset, try again later", aws);
        } finally {
            closeQuietly(object);
        }
    }

    /**
     * Gets modified date of object by Key.
     *
     * @param key {@link S3ObjectIdBuilder#key}
     * @return Date
     */
    public Date getDate(String key) throws FileNotFoundException {
        Assert.notNull(key, "key must be provided");
        ObjectMetadata objectMetadata = null;
        try{
            LOGGER.debug("Getting last modified date for document from S3 bucket {} with location {}", keyValue("bucket", bucket), keyValue("location", key));
            objectMetadata= s3.getObjectMetadata(bucket, key);
            Date date = objectMetadata.getLastModified();
            return date;
        }catch (AmazonServiceException aws) {
            String errorMessage = String.format("Failed fetching document from S3 bucket %s with location %s\n\n%s",
                    keyValue("bucket", bucket),
                    keyValue("location", key), aws.getMessage());
            LOGGER.error(errorMessage, aws);
            if (aws.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new FileNotFoundException("Unable to locate document asset with Key " + key);
        }
            throw new IllegalStateException("Unexpected error trying to download asset, try again later", aws);
        }
    }
    /**
     * Deletes the S3 content by Key. Does nothing if not document found.
     *
     * @param key {@link DeleteObjectRequest#key}
     */
    @Override
    public void delete(String key) {
        Assert.notNull(key, "key must be provided");
        try {
            s3.deleteObject(new DeleteObjectRequest(bucket, key));
        } catch (AmazonServiceException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Closes the connection to S3. Silences any exceptions thrown.
     *
     * @param object {@link S3Object} to close.
     */
    private void closeQuietly(S3Object object) {
        if(object != null) {
            try {
                object.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Converts Input Stream to a byte[]
     *
     * @param is {@link InputStream}
     * @return byte[]
     */
    private byte[] toByteArray(InputStream is) {
        try {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected error trying to download asset, try again later", e);
        }
    }

    /**
     * Thread pools address provide improved performance when executing large numbers of
     * asynchronous tasks, due to reduced per-task invocation overhead, and they provide
     * a means of bounding and managing the resources, including threads, consumed when
     * executing a collection of tasks.
     *
     * @param threadNumber {@link Integer} Sets the number of threads in the pool used to operate off a shared unbounded queue
     * @return the newly created thread pool
     */
    private ThreadPoolExecutor createExecutorService(int threadNumber) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int threadCount = 1;

            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("jsa-amazon-s3-transfer-manager-worker-" + threadCount++);
                return thread;
            }
        };
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNumber, threadFactory);
    }

    /**
     * Preset value of one hour, used to abort S3 upload operation
     * @return one hour
     */
    private Date days() {
        int oneDay = 1000 * 60 * 60;
        return new Date(System.currentTimeMillis() - oneDay);
    }
}