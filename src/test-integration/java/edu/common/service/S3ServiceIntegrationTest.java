package edu.common.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class S3ServiceIntegrationTest {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    private S3Service s3Service;

    private String key;

    @Before
    public void setUp() {
        assertNotNull(ctx);
        assertNotNull(s3Service);

        key = "capstone-common-s3-" + UUID.randomUUID().toString() + LocalDate.now().hashCode();
        assertNotNull(key);
    }

    //TODO: Add tests for more file-types
    @Test
    public void verifyFile() throws Exception {
        // load the file to put, get and delete
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(classLoader.getResource("json/MockInput").getFile());
        assertNotNull(file);

        // read the contents of the file into a string
        String fileToString = FileUtils.readFileToString(file, Charset.defaultCharset());
        assertNotNull(fileToString);

        // create the file
        s3Service.put(key, file);

        // get the saved file contents
        byte[] results = s3Service.get(key);
        assertTrue(results.length > 0);

        // Verify Content is correct
        assertEquals(fileToString,  new String(results));

        // delete the file
        s3Service.delete(key);

        // if the file is successfully deleted then any subsequent attempt to read it would throw an FileNotFoundException
        boolean fileWasDeleted = false;
        try {
            s3Service.get(key);
        } catch (FileNotFoundException fnfe) {
            fileWasDeleted = true;
        }
        assertTrue(fileWasDeleted);
    }

    @Test
    public void verifyContent() throws Exception {
        // generate random string content to put, get and delete
        String content = RandomStringUtils.randomAlphanumeric(100000);
        assertNotNull(content);

        // create the file
        s3Service.put(key, content.getBytes(), ContentType.TEXT_PLAIN.getMimeType());

        // get the saved file contents
        byte[] results = s3Service.get(key);
        assertTrue(results.length > 0);

        // Verify Content is correct
        assertEquals(content,  new String(results));

        // delete the file
        s3Service.delete(key);

        // if the file is successfully deleted then any subsequent attempt to read it would throw an FileNotFoundException
        boolean fileWasDeleted = false;
        try {
            s3Service.get(key);
        } catch (FileNotFoundException fnfe) {
            fileWasDeleted = true;
        }
        assertTrue(fileWasDeleted);
     }

    @Test
    public void verifyDate() throws Exception {
        // generate random string content to put, get and delete
        String content = RandomStringUtils.randomAlphanumeric(100000);
        assertNotNull(content);

        // create the file
        s3Service.put(key, content.getBytes(), ContentType.TEXT_PLAIN.getMimeType());

        // get the saved file contents
        Date date = s3Service.getDate(key);
        assertTrue(date.toString().length()>0);

        // delete the file
        s3Service.delete(key);

        // if the file is successfully deleted then any subsequent attempt to read it would throw an FileNotFoundException
        boolean fileWasDeleted = false;
        try {
            s3Service.get(key);
        } catch (FileNotFoundException fnfe) {
            fileWasDeleted = true;
        }
        assertTrue(fileWasDeleted);
    }
}
