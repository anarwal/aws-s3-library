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