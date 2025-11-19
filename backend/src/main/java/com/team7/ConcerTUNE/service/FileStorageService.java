package com.team7.ConcerTUNE.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.team7.ConcerTUNE.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.base-url}")
    private String baseUrl;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int IMAGE_SIZE = 1080;

    public List<String> uploadFiles(MultipartFile[] files, String folder) {
        if (s3Client == null) {
            log.warn("S3 client not configured. Using local storage fallback.");
            throw new RuntimeException("S3 not configured");
        }

        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile file : files) {

            String url = uploadFile(file, folder);
            uploadedUrls.add(url);
        }

        return uploadedUrls;
    }

    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);

        String fileName = generateFileName(file, folder);

        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new BadRequestException("Invalid image file");
            }

            BufferedImage squareImage = cropToSquare(originalImage);

            BufferedImage resizedImage = resizeImage(squareImage, IMAGE_SIZE, IMAGE_SIZE);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);
            PutObjectRequest putObjectRequest = getPutObjectRequest(file, outputStream, fileName);

            s3Client.putObject(putObjectRequest);

            log.info("Successfully uploaded file to S3: {}", fileName);

            return baseUrl + fileName;
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }
    private PutObjectRequest getPutObjectRequest(MultipartFile file, ByteArrayOutputStream outputStream, String fileName) {
        byte[] imageBytes = outputStream.toByteArray();
        InputStream inputStream = new ByteArrayInputStream(imageBytes);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");
        metadata.setContentLength(imageBytes.length);

        return new PutObjectRequest(
                bucketName,
                fileName,
                inputStream,
                metadata
        );
    }


    private BufferedImage cropToSquare(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int squareSize = Math.min(width, height);

        int x = (width - squareSize) / 2;
        int y = (height - squareSize) / 2;

        return image.getSubimage(x, y, squareSize, squareSize);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }

    private String generateFileName(MultipartFile file, String folder) {
        return folder + "/" + UUID.randomUUID().toString() + ".jpg";
    }

    public void deleteFile(String fileKey) {
        if (s3Client == null) {
            log.warn("S3 client not configured, Cannot delete file.");
            throw new RuntimeException("S3 not configured");
        }

        s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileKey));
        log.info("Successfully deleted file from S3: {}", fileKey);
    }
}