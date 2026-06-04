package com.singsation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

@Service
public class OracleStorageService {

    private static final Logger logger = LoggerFactory.getLogger(OracleStorageService.class);

    @Value("${oracle.bucket.songs:https://objectstorage.af-johannesburg-1.oraclecloud.com/p/ly3fXNVvsURnYZICts4JJ2dB26QbNB4qackx_aHakhOiZoKwE0A1KxgcuV_LxN3m/n/axcbefxpjvzm/b/karaokesongs/o/}")
    private String songsBucketUrl;

    @Value("${oracle.bucket.videos:https://objectstorage.af-johannesburg-1.oraclecloud.com/p/a9EEAyJ-mAJqDVJgTBZoGS8oqs41s2O1RmKzZH6e3CdxGc_IFl0tRAP27-ZeSZtH/n/axcbefxpjvzm/b/karaokevideos/o/}")
    private String videosBucketUrl;

    @Value("${oracle.bucket.images:https://objectstorage.af-johannesburg-1.oraclecloud.com/p/kKhjYjoOBqP_HftX7DkuU2jcZDLcyTFKEkPpIT3d-kXe9jcamKt5tc25g0qhFO1K/n/axcbefxpjvzm/b/karaokeimages/o/}")
    private String imagesBucketUrl;

    @Value("${oracle.bucket.storage:https://objectstorage.af-johannesburg-1.oraclecloud.com/p/YzDrr5iAzA58e3OMQ4qOsL62ub9bNaa2IcKgwTJA45ffSJBl0I031bVhtGqNZvuC/n/axcbefxpjvzm/b/karaokestorage/o/}")
    private String storageBucketUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(60))
            .build();

    public String uploadFile(MultipartFile file, String folder, BucketType bucketType) throws IOException {
        String bucketUrl = getBucketUrl(bucketType);
        
        // Ensure bucket URL ends with /
        if (!bucketUrl.endsWith("/")) {
            bucketUrl = bucketUrl + "/";
        }
        
        // Create filename with folder and unique ID
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = folder + "/" + UUID.randomUUID().toString() + extension;
        String uploadUrl = bucketUrl + fileName;
        
        // Determine content type
        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            if (originalFilename != null) {
                if (originalFilename.endsWith(".mp3")) contentType = "audio/mpeg";
                else if (originalFilename.endsWith(".mp4")) contentType = "video/mp4";
                else if (originalFilename.endsWith(".jpg") || originalFilename.endsWith(".jpeg")) contentType = "image/jpeg";
                else if (originalFilename.endsWith(".png")) contentType = "image/png";
                else contentType = "application/octet-stream";
            } else {
                contentType = "application/octet-stream";
            }
        }
        
        logger.info("========================================");
        logger.info("📤 UPLOADING FILE");
        logger.info("Bucket Type: {}", bucketType);
        logger.info("Upload URL: {}", uploadUrl);
        logger.info("File Name: {}", originalFilename);
        logger.info("File Size: {} bytes", file.getSize());
        logger.info("Content Type: {}", contentType);
        logger.info("========================================");
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Content-Type", contentType)
                .PUT(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            logger.info("Upload Response Status: {}", response.statusCode());
            logger.info("Upload Response Body: {}", response.body());
            
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                String publicUrl = uploadUrl;
                logger.info("✅ UPLOAD SUCCESSFUL! URL: {}", publicUrl);
                return publicUrl;
            } else {
                logger.error("❌ UPLOAD FAILED! Status: {}, Body: {}", response.statusCode(), response.body());
                throw new RuntimeException("Upload failed with status: " + response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Upload interrupted", e);
        } catch (Exception e) {
            logger.error("❌ UPLOAD EXCEPTION: ", e);
            throw new RuntimeException("Upload failed: " + e.getMessage(), e);
        }
    }

    public String uploadSplashScreen(MultipartFile imageFile) throws IOException {
        logger.info("Uploading splash screen: {}", imageFile.getOriginalFilename());
        return uploadFile(imageFile, "splash", BucketType.IMAGES);
    }

    public String uploadSongAudio(MultipartFile audioFile) throws IOException {
        logger.info("Uploading song audio: {}", audioFile.getOriginalFilename());
        return uploadFile(audioFile, "audio", BucketType.SONGS);
    }

    public String uploadSongVideo(MultipartFile videoFile) throws IOException {
        logger.info("Uploading song video: {}", videoFile.getOriginalFilename());
        return uploadFile(videoFile, "video", BucketType.VIDEOS);
    }

    public String uploadStorageFile(MultipartFile file, String folder) throws IOException {
        logger.info("Uploading storage file to {}: {}", folder, file.getOriginalFilename());
        return uploadFile(file, folder, BucketType.STORAGE);
    }

    private String getBucketUrl(BucketType type) {
        return switch (type) {
            case SONGS -> songsBucketUrl;
            case VIDEOS -> videosBucketUrl;
            case IMAGES -> imagesBucketUrl;
            case STORAGE -> storageBucketUrl;
        };
    }

    public enum BucketType {
        SONGS, VIDEOS, IMAGES, STORAGE
    }
}