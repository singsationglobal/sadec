package com.singsation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
public class GoogleCloudStorageService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCloudStorageService.class);

    @Value("${google.cloud.bucket.name}")
    private String bucketName;

    @Value("${google.cloud.access.key}")
    private String accessKey;

    @Value("${google.cloud.secret.key}")
    private String secretKey;

    private S3Client getS3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create("https://storage.googleapis.com"))
                .region(Region.US_EAST_1) 
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = folder + "/" + UUID.randomUUID().toString() + extension;

        String contentType = file.getContentType();
        if (contentType == null) contentType = "application/octet-stream";

        logger.info("Uploading to Google Cloud: bucket={}, key={}, size={}", bucketName, fileName, file.getSize());

        try (S3Client s3 = getS3Client()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .build();

            s3.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String publicUrl = "https://storage.googleapis.com/" + bucketName + "/" + fileName;
            logger.info("Google Cloud Upload Successful: {}", publicUrl);
            return publicUrl;

        } catch (S3Exception e) {
            logger.error("Google Cloud Upload Failed: {}", e.getMessage());
            throw new RuntimeException("Google Cloud upload failed: " + e.getMessage(), e);
        }
    }

    public String uploadSplashScreen(MultipartFile imageFile) throws IOException {
        return uploadFile(imageFile, "splashsadec");
    }
}
