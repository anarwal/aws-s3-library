package edu.common.service;

import java.io.IOException;

import edu.common.service.MockS3Service;
import edu.common.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

@Configuration
@ConfigurationProperties
@PropertySources(@PropertySource("classpath:test.properties"))
public class TestConfig {

    private String bucket;
    private String awsKey;
    private String awsSecret;
    private String region;
    private Integer minPartSize;
    private Integer uploadThreshold;
    private Integer partSize;
    private Integer copyThreshold;
    private Integer executorThread;
    private String filePath;

    @Autowired
    Environment env;

    @Bean
    public S3Service s3Service(){
        return new S3Service(env.getProperty("bucket"), env.getProperty("awsKey"), env.getProperty("awsSecret"), env.getProperty("region"), Integer.parseInt(env.getProperty("minPartSize")), Integer.parseInt(env.getProperty("uploadThreshold")), Integer.parseInt(env.getProperty("partSize")), Integer.parseInt(env.getProperty("copyThreshold")), Integer.parseInt(env.getProperty("executorThread")));
    }

    @Bean
    public MockS3Service mockS3Service() throws IOException{
        return new MockS3Service(env.getProperty("filePath"));
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setAwsKey(String awsKey) {
        this.awsKey = awsKey;
    }

    public void setAwsSecret(String awsSecret) {
        this.awsSecret = awsSecret;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setMinPartSize(Integer minPartSize) {
        this.minPartSize = minPartSize;
    }

    public void setUploadThreshold(Integer uploadThreshold) {
        this.uploadThreshold = uploadThreshold;
    }

    public void setPartSize(Integer partSize) {
        this.partSize = partSize;
    }

    public void setCopyThreshold(Integer copyThreshold) {
        this.copyThreshold = copyThreshold;
    }

    public void setExecutorThread(Integer executorThread) {
        this.executorThread = executorThread;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
