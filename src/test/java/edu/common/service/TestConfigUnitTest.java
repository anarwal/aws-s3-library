package edu.common.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TestConfigUnitTest {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    S3Service s3Service;

    @Test
    public void verify() throws Exception {
        assertNotNull("ApplicationContext", ctx);
        assertNotNull("S3Service", s3Service);
    }
}