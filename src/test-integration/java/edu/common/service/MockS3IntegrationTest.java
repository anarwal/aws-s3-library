package edu.common.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
public class MockS3IntegrationTest {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    private MockS3Service mockS3Service;

    public String key;

    @Before
    public void setUp() {
        assertNotNull(ctx);
        assertNotNull(mockS3Service);

        key = "capstone-common-mock-" + UUID.randomUUID().toString() + LocalDate.now().hashCode();
        assertNotNull(key);
    }

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
        mockS3Service.put(key, file);

        // get the saved file contents
        byte[] results = mockS3Service.get(key);
        assertTrue(results.length > 0);

        // Verify Content is correct
        assertEquals(fileToString,  new String(results));

        // delete the file
        mockS3Service.delete(key);

        // if the file is successfully deleted then any subsequent attempt to read it would throw an IllegalStateException
        boolean fileWasDeleted = false;
        try {
            mockS3Service.get(key);
        } catch (FileNotFoundException fe) {
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
        mockS3Service.put(key, content.getBytes(),"UTF-8");

        // get the saved file contents
        byte[] results = mockS3Service.get(key);
        assertTrue(results.length > 0);

        // Verify Content is correct
        assertEquals(content, new String(results));

        // delete the file
        mockS3Service.delete(key);

        // if the file is successfully deleted then any subsequent attempt to read it would throw an IllegalStateException
        boolean fileWasDeleted = false;
        try {
            mockS3Service.get(key);
        } catch (FileNotFoundException fe) {
            fileWasDeleted = true;
        }
        assertTrue(fileWasDeleted);
    }

    @Test
    public void verifyFileDate() throws Exception {
        // generate random string content to put, get and delete
        String content = RandomStringUtils.randomAlphanumeric(100000);
        assertNotNull(content);

        // create the file
        mockS3Service.put(key, content.getBytes(),"UTF-8");

        // get creation date of file
        Date date = mockS3Service.getDate(key);
        assertTrue(date.toString().length()>0);

        // delete the file
        mockS3Service.delete(key);

        // if the file is successfully deleted then any subsequent attempt to read it would throw an IllegalStateException
        boolean fileWasDeleted = false;
        try {
            mockS3Service.get(key);
        } catch (FileNotFoundException fe) {
            fileWasDeleted = true;
        }
        assertTrue(fileWasDeleted);
    }

}
