package edu.common.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = TestConfig.class)
public class MockS3ServiceUnitTest {

    private MockS3Service mockS3Service;

    @Before
    public void setUp() throws IOException{
        String filePath= RandomStringUtils.randomAlphabetic(15);
        mockS3Service = new MockS3Service(filePath);
        assertNotNull(mockS3Service);
    }

    /**
     * Verify that {@link MockS3Service#MockS3Service} correctly fails when filePath is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void filePathNull() throws IOException {
        new MockS3Service(null);
    }

    /**
     * Verify that {@link MockS3Service#put(String, File)} correctly fails when a key is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void putFileNullKey() {
        File file = new File("");
        mockS3Service.put(null, file);
    }

    /**
     * Verify that {@link MockS3Service#put(String, File)}  correctly fails when a {@link File} is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void putFileNullFile() {
        String key = RandomStringUtils.randomAlphabetic(10);
        mockS3Service.put(key, null);
    }

    /**
     * Verify that {@link MockS3Service#put} correctly fails when key is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void putContentNullKey() {
        String content = RandomStringUtils.randomAlphabetic(100);
        String contentType = RandomStringUtils.randomAlphabetic(20);
        mockS3Service.put(null, content.getBytes(), contentType);
    }

    /**
     * Verify that {@link MockS3Service#put} correctly fails when content is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void putContentNullContent() {
        String key = RandomStringUtils.randomAlphabetic(15);
        String contentType = RandomStringUtils.randomAlphabetic(20);
        mockS3Service.put(key, null, contentType);
    }

    /**
     * Verify that {@link MockS3Service#put} correctly fails when contentType is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void putContentNullContentType() {
        String key = RandomStringUtils.randomAlphabetic(30);
        String content = RandomStringUtils.randomAlphabetic(200);
        mockS3Service.put(key, content.getBytes(), null);
    }

    /**
     * Verify that {@link MockS3Service#get(String)} correctly fails when key is not provided.
     * @throws IOException when a file matching the provided key is not found
     */
    @Test(expected = IllegalArgumentException.class)
    public void get() throws IOException {
        mockS3Service.get(null);
    }

    /**
     * Verify that {@link MockS3Service#getDate(String)} correctly fails when key is not provided.
     * @throws IOException when a file matching the provided key is not found
     */
    @Test(expected = IllegalArgumentException.class)
    public void getDateForNullKey() throws IOException, ParseException {
        mockS3Service.getDate(null);
    }

    /**
     * Verify that {@link MockS3Service#getDate(String)} throws FileNotFoundException exception is invalid file path is provided
     * @throws IOException when a file matching the provided key is not found
     */
    @Test(expected = FileNotFoundException.class)
    public void getDateForInvalidPath() throws IOException, ParseException {
        mockS3Service.getDate("SomeKey");
    }

    /**
     * Verify that {@link MockS3Service#delete(String)} correctly fails when key is not provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void delete() {
        mockS3Service.delete(null);
    }


}
