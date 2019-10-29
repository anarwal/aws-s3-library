# aws-s3-library
![Tests](https://github.com/anarwal/aws-secret-manager/workflows/.github/workflows/ci.yml/badge.svg)

Simple library to talk to AWS S3 bucket using AWS SDK. It provides an interface to communicate with S3 bucket using AWS CRUD APIs. 
You can choose either to use:
 1. AWS S3 (Production usage)
 2. File System (Mock for local testing)

To perform a build and execute all unit tests:
```
mvn clean install
```

To execute all component tests:
```
mvn -P test-integration test
```

#### Functions provided:

- put(String key, File file): Create or update the Provided {@link File} within file based storage
- put(String key, byte[] content, String contentType): Create or update the provided content within file based storage
- get(String key): Gets the content from file based storage by key
- getDate(String key): Gets the last modified date of file based storage by key 
- delete(String key): Deletes the content by key

You have two different classes you can use:
- S3Service: For application use case (directly talks to S3 bucket)
- MockS3Service: For testing purpose, this talks to your machine's file system

----------
Instantiate bean by including following after adding dependency to pom:
```
    @Value("${aws.s3.awsKey}")
    private String awsKey;

    @Value("${aws.s3.awsSecret}")
    private String awsSecret;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.minPartSize}")
    private Integer minPartSize;
    
    @Value("${aws.s3.uploadThreshold}")
    private Integer uploadThreshold;
        
    @Value("${aws.s3.partSize}")
    private Integer partSize;
    
    @Value("${aws.s3.copyThreshold}")
    private Integer copyThreshold;
    
    @Value("${aws.sm.executorThread}")
    private Integer executorThread;
    
    @Value("${aws.sm.filePath}")
    private String filePath;

    @Bean
    public S3Service s3Service(){
        return new AWSSecretManagerService(awsKey, awsSecret, region, minPartSize, uploadThreshold, partSize, copyThreshold, executorThread, filePath);
    }
```
