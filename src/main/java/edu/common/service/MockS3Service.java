package edu.common.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Date;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Service
public class MockS3Service implements IFileStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockS3Service.class);
    private static final String INVALID_KEY_MESSAGE = "Key must be provided";
    private static final String INVALID_FILE_MESSAGE = "File must be provided";

    private String filePath;

    /**
     * @param filePath {@link Path} Location to store the file
     */
    public MockS3Service(String filePath) {
        Assert.isTrue(StringUtils.isNotBlank(filePath), INVALID_FILE_MESSAGE);
        this.filePath = filePath;
    }

    /**
     * Stores file at given location on file system
     *
     * @param key  {@link String} Sets name of the file to save on file system
     * @param file {@link File} Sets the file to be read
     */
    @Override
    public void put(String key, File file) {
        Assert.isTrue(StringUtils.isNotBlank(key), INVALID_KEY_MESSAGE);
        Assert.notNull(file, INVALID_FILE_MESSAGE);
        StopWatch stopWatch = new StopWatch();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Storing file {} on local filesystem at {}", keyValue("file", file), keyValue("location", filePath));
                stopWatch.start();
            }

            byte[] bytes = FileUtils.readFileToByteArray(file);
            FileUtils.writeByteArrayToFile(new File(filePath+key), bytes);

            if (LOGGER.isDebugEnabled()) {
                stopWatch.stop();
                LOGGER.info("{} saved in file system at {} in {} milliseconds as {}",
                        keyValue("fileName", file), keyValue("location", filePath), stopWatch.getTotalTimeMillis(), keyValue("fileName", file));
            }
        } catch (IOException e) {
            throw new FileStorageServiceException(e);
        }
    }

    /**
     * Creates/Updates the Provided content into S3 Bucket
     *
     * @param key      Sets name of the file to save on file system
     * @param content  Sets the content to write to the file
     * @param encoding Sets the encoding to use, {@code null} means platform default
     */
    @Override
    public void put(String key, byte[] content, String encoding) {
        Assert.isTrue(StringUtils.isNotBlank(key), INVALID_KEY_MESSAGE);
        Assert.notNull(content, "content must be provided");
        Assert.isTrue(StringUtils.isNotBlank(encoding), "contentType must be provided");
        StopWatch stopWatch = new StopWatch();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Storing file {} on local filesystem with location {}", keyValue("file", key), keyValue("location", filePath));
                stopWatch.start();
            }
            FileUtils.writeByteArrayToFile(new File(filePath+key), content);
            if (LOGGER.isDebugEnabled()) {
                stopWatch.stop();
                LOGGER.info("{} saved in file system at {} in {} milliseconds",
                        keyValue("fileName", key), keyValue("location", filePath), stopWatch.getTotalTimeMillis());
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Gets the file stored on file system.
     *
     * @param key {@link String} Sets name of the file to be fetched
     * @return String
     */
    @Override
    public byte[] get(String key) throws IOException {
        Assert.isTrue(StringUtils.isNotBlank(key), INVALID_KEY_MESSAGE);
        LOGGER.debug("Fetching {} from file system with location {}", keyValue("fileName", key));
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(filePath+key));
        LOGGER.debug("Finished fetching document from file system");
        return bytes;
    }

    /**
     * Gets the file stored on file system.
     *
     * @param key {@link String} Sets name of the file to be fetched
     * @return String
     */
    @Override
    public Date getDate(String key) throws IOException, ParseException {
        Assert.isTrue(StringUtils.isNotBlank(key), INVALID_KEY_MESSAGE);
        LOGGER.debug("Getting creation date for {} with location {}", keyValue("fileName", key));

        String path = filePath + key;
        File f = new File (path);
        if(f.exists()){
            LOGGER.debug("Finished getting creation date of document");
            return new Date(new File (path).lastModified());
        } else {
            String erorMessage = "File not found: {}";
            LOGGER.error(erorMessage, path);
            throw new FileNotFoundException(erorMessage.replace("{}", "") + path);
        }
    }

    /**
     * Deletes the file by Key. Does nothing if not document found.
     *
     * @param key {@link String} Sets name of the file to be deleted
     */
    @Override
    public void delete(String key) {
        Assert.isTrue(StringUtils.isNotBlank(key), INVALID_KEY_MESSAGE);
        FileUtils.deleteQuietly(FileUtils.getFile(filePath+key));
    }

    private String returnTargetPath() {
        return filePath;
    }
}
