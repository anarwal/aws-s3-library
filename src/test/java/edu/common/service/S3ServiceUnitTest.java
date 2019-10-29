package edu.common.service;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = TestConfig.class)
public class S3ServiceUnitTest {

    private S3Service s3Service;

    @Before
    public void setUp() {
        String bucket = RandomStringUtils.randomAlphabetic(10);
        String awsKey = RandomStringUtils.randomAlphabetic(12);
        String awsSecret = RandomStringUtils.randomAlphabetic(14);
        String region = "us-east-1";
        Integer minPartSize = 5;
        Integer uploadThreshold = 16;
        Integer partSize = 5;
        Integer copyThreshold = 50;
        Integer executorThread = 10;
        s3Service = new S3Service(bucket, awsKey, awsSecret, region, minPartSize, uploadThreshold, partSize, copyThreshold, executorThread);
        assertNotNull(s3Service);
    }

    /**
     * Verify that {@link S3Service#S3Service} correctly fails when a bucket is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void bucketNull() throws FileNotFoundException {
        String awsKey = RandomStringUtils.randomAlphabetic(12);
        String awsSecret = RandomStringUtils.randomAlphabetic(14);
        Integer minPartSize = 5;
        Integer uploadThreshold = 16;
        Integer partSize = 5;
        Integer copyThreshold = 50;
        Integer executorThread = 10;
        new S3Service(null, awsKey, awsSecret, minPartSize, uploadThreshold, partSize, copyThreshold, executorThread);
    }

    /**
     * Verify that {@link S3Service#S3Service}  correctly fails when a awsKey is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void awsKeyNull() {
        String bucket = RandomStringUtils.randomAlphabetic(10);
        String awsSecret = RandomStringUtils.randomAlphabetic(14);
        Integer minPartSize = 5;
        Integer uploadThreshold = 16;
        Integer partSize = 5;
        Integer copyThreshold = 50;
        Integer executorThread = 10;
        new S3Service(bucket, null, awsSecret, minPartSize, uploadThreshold, partSize, copyThreshold, executorThread);
    }

    /**
     * Verify that {@link S3Service#S3Service}  correctly fails when a awsSecret is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void awsSecretNull() {
        String bucket = RandomStringUtils.randomAlphabetic(10);
        String awsKey = RandomStringUtils.randomAlphabetic(12);
        Integer minPartSize = 5;
        Integer uploadThreshold = 16;
        Integer partSize = 5;
        Integer copyThreshold = 50;
        Integer executorThread = 10;
        new S3Service(bucket, awsKey, null, minPartSize, uploadThreshold, partSize, copyThreshold, executorThread);
    }


    /**
     * Verify that {@link S3Service#S3Service}  correctly fails when a region is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void regionNull() {
        String bucket = RandomStringUtils.randomAlphabetic(10);
        String awsKey = RandomStringUtils.randomAlphabetic(12);
        String awsSecret = RandomStringUtils.randomAlphabetic(14);
        Integer minPartSize = 5;
        Integer uploadThreshold = 16;
        Integer partSize = 5;
        Integer copyThreshold = 50;
        Integer executorThread = 10;
        new S3Service(bucket, awsKey, awsSecret,null, minPartSize, uploadThreshold, partSize, copyThreshold, executorThread);
    }

    /**
     * Verify that {@link S3Service#S3Service}  correctly fails when a minimum partSize is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void minPartNull() {
        String bucket = RandomStringUtils.randomAlphabetic(10);
        String awsKey = RandomStringUtils.randomAlphabetic(12);
        String awsSecret = RandomStringUtils.randomAlphabetic(14);
        String region = "us-east-1";
        Integer uploadThreshold = 16;
        Integer partSize = 5;
        Integer copyThreshold = 50;
        Integer executorThread = 10;
        new S3Service(bucket, awsKey, awsSecret,region, null, uploadThreshold, partSize, copyThreshold, executorThread);
    }

    /**
     * Verify that {@link S3Service#S3Service}  correctly fails when  uploadThreshold is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void uploadThresholdNull() {
        String bucket = RandomStringUtils.randomAlphabetic(10);
        String awsKey = RandomStringUtils.randomAlphabetic(12);
        String awsSecret = RandomStringUtils.randomAlphabetic(14);
        String region = "us-east-1";
        Integer minPartSize = 5;
        Integer partSize = 5;
        Integer copyThreshold = 50;
        Integer executorThread = 10;
        new S3Service(bucket, awsKey, awsSecret,region, minPartSize, null, partSize, copyThreshold, executorThread);
    }

    /**
     * Verify that {@link S3Service#S3Service}  correctly fails when partSize is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void partSizeNull() {
        String bucket = RandomStringUtils.randomAlphabetic(10);
        String awsKey = RandomStringUtils.randomAlphabetic(12);
        String awsSecret = RandomStringUtils.randomAlphabetic(14);
        String region = "us-east-1";
        Integer minPartSize = 5;
        Integer uploadThreshold = 16;
        Integer copyThreshold = 50;
        Integer executorThread = 10;
        new S3Service(bucket, awsKey, awsSecret,region, minPartSize, uploadThreshold, null, copyThreshold, executorThread);
    }

    /**
     * Verify that {@link S3Service#S3Service}  correctly fails when copyThreshold is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void copyThresholdNull() {
        String bucket = RandomStringUtils.randomAlphabetic(10);
        String awsKey = RandomStringUtils.randomAlphabetic(12);
        String awsSecret = RandomStringUtils.randomAlphabetic(14);
        String region = "us-east-1";
        Integer minPartSize = 5;
        Integer uploadThreshold = 16;
        Integer partSize = 5;
        Integer executorThread = 10;
        new S3Service(bucket, awsKey, awsSecret,null, minPartSize, uploadThreshold, partSize, null, executorThread);
    }
    /**
     * Verify that {@link S3Service#S3Service}  correctly fails when a executorThread is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void executorThreadNull() {
        String bucket = RandomStringUtils.randomAlphabetic(10);
        String awsKey = RandomStringUtils.randomAlphabetic(12);
        String awsSecret = RandomStringUtils.randomAlphabetic(14);
        String region = "us-east-1";
        Integer minPartSize = 5;
        Integer uploadThreshold = 16;
        Integer partSize = 5;
        Integer copyThreshold = 50;
        new S3Service(bucket, awsKey, awsSecret,null, minPartSize, uploadThreshold, partSize, copyThreshold, null);
    }


    /**
     * Verify that {@link S3Service#put(String, File)} correctly fails when a key is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void putFileNullKey() {
        File file = new File("");
        s3Service.put(null, file);
    }

    /**
     * Verify that {@link S3Service#put(String, File)} correctly fails when a {@link File} is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void putFileNullFile() {
        String key = RandomStringUtils.randomAlphabetic(10);
        s3Service.put(key, null);
    }

    /**
     * Verify that {@link S3Service#put} correctly fails when key is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void putContentNullKey() {
        String content = RandomStringUtils.randomAlphabetic(100);
        String contentType = RandomStringUtils.randomAlphabetic(20);
        s3Service.put(null, content.getBytes(), contentType);
    }

    /**
     * Verify that {@link S3Service#put} correctly fails when content is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void putContentNullContent() {
        String key = RandomStringUtils.randomAlphabetic(15);
        String contentType = RandomStringUtils.randomAlphabetic(20);
        s3Service.put(key, null, contentType);
    }

    /**
     * Verify that {@link S3Service#put} correctly fails when contentType is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void putContentNullContentType() {
        String key = RandomStringUtils.randomAlphabetic(30);
        String content = RandomStringUtils.randomAlphabetic(200);
        s3Service.put(key, content.getBytes(), null);
    }

    /**
     * Verify that {@link S3Service#get(String)} correctly fails when key is not provided.
     * @throws FileNotFoundException when a file matching the provided key is not found
     */
    @Test(expected = IllegalArgumentException.class)
    public void get() throws FileNotFoundException {
        s3Service.get(null);
    }

    /**
     * Verify that {@link S3Service#getDate(String)} correctly fails when key is not provided.
     * @throws FileNotFoundException when a file matching the provided key is not found
     */
    @Test(expected = IllegalArgumentException.class)
    public void getDate() throws FileNotFoundException {
        s3Service.getDate(null);
    }

    /**
     * Verify that {@link S3Service#delete(String)} correctly fails when key is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void delete() {
        s3Service.delete(null);
    }



}
