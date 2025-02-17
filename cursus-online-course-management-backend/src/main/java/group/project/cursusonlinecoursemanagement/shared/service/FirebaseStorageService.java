package group.project.cursusonlinecoursemanagement.shared.service;

import com.google.cloud.storage.*;
import com.google.api.gax.paging.Page;
import group.project.cursusonlinecoursemanagement.shared.domain.FileMetadata;
import jakarta.validation.ConstraintViolationException;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class FirebaseStorageService {

    private final Storage storage;

    private final Tika tika = new Tika();

    @Value("${firebase.bucketName}")
    private String bucketName;

    public FirebaseStorageService(Storage storage) {
        this.storage = storage;
    }

    public FileMetadata uploadFile(MultipartFile file, String folderName, String fileName) {
        String key = generateFileName(fileName, file);
        String filePath = toCamelCase(folderName) + "/" + key;
        try {
            validateFile(file);
            BlobId blobId = BlobId.of(bucketName, filePath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            Blob blob = storage.create(blobInfo, file.getBytes());
            return FileMetadata.builder()
                    .bucket(bucketName)
                    .folder(folderName)
                    .key(filePath)
                    .extension(StringUtils.getFilenameExtension(file.getOriginalFilename()))
                    .mime(tika.detect(file.getOriginalFilename()))
                    .size(file.getSize())
                    .url(blob.getMediaLink())
                    .build();
        } catch (StorageException e) {
            throw new RuntimeException("Failed to put video from Firebase Storage. Service error: " + e.getMessage(), e);
        } catch (ConstraintViolationException e) {
            throw new RuntimeException("Failed to put video from Firebase. Invalid file: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading file bytes: " + e.getMessage(), e);
        }
    }

    public List<FileMetadata> listFiles() {
        try {
            Page<Blob> blobs = storage.list(bucketName);
            List<FileMetadata> fileNames = new ArrayList<>();
            for (Blob blob : blobs.iterateAll()) {
                fileNames.add(FileMetadata.builder()
                        .bucket(bucketName)
                        .folder(getNameLessonFromPath(blob.getBlobId().getName()))
                        .key(blob.getBlobId().getName())
                        .extension(getFileExtension(blob.getBlobId().getName()))
                        .mime(blob.getContentType())
                        .size(blob.getSize())
                        .url(blob.getMediaLink())
                        .build());
            }
            return fileNames;
        } catch (StorageException e) {
            throw new RuntimeException("Failed to put video from Firebase Storage. Service error: " + e.getMessage(), e);
        }
        
    }

    public String deleteFile(String filePath) {
        try {
            BlobId blobId = BlobId.of(bucketName, filePath);
            storage.delete(blobId);
            return "File deleted successfully";
        } catch (StorageException e) {
            throw new RuntimeException("Failed to put delete from Firebase Storage. Service error: " + e.getMessage(), e);
        }
    }

    public URL getSignUrl(String filePath) {
        try {
            BlobId blobId = BlobId.of(bucketName, filePath);
            Blob blob;
            try {
                blob = storage.get(blobId);
            } catch (StorageException e) {
                throw new RuntimeException("Failed to retrieve Blob from Firebase Storage");
            }
            if (blob == null) {
                try {
                    throw new IOException("File not found in Firebase Storage");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return blob.signUrl(24, TimeUnit.HOURS, Storage.SignUrlOption.withV4Signature());
        } catch (StorageException e) {
            throw new RuntimeException("Failed to put video from Firebase Storage. Service error: " + e.getMessage(), e);
        }
    }



    private String generateFileName(String keyName, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        int lastDotIndex = fileName != null ? fileName.lastIndexOf('.') : 0;
        String extension = fileName != null ? fileName.substring(lastDotIndex + 1) : null;
        return keyName + "." + extension;
    }

    public static String getNameLessonFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length > 0) {
            return parts[0];
        } else {
            throw new IllegalArgumentException("Invalid path format");
        }
    }

    private String toCamelCase(String input) {
        // Tách chuỗi thành các từ
        String[] parts = input.split(" ");

        // Xây dựng chuỗi camelCase
        StringBuilder camelCaseString = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == 0) {
                // Chữ đầu tiên viết thường
                camelCaseString.append(part.toLowerCase());
            } else {
                // Chữ cái đầu tiên của từ sau viết hoa, các chữ còn lại viết thường
                camelCaseString.append(part.substring(0, 1).toUpperCase());
                camelCaseString.append(part.substring(1).toLowerCase());
            }
        }

        return camelCaseString.toString();
    }

//    private String generateFileName(String keyName, MultipartFile file) {
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss_ddMMyyyy");
//        String formattedDate = now.format(formatter);
//        String fileName = file.getOriginalFilename();
//        int lastDotIndex = 0;
//        if (fileName != null) {
//            lastDotIndex = fileName.lastIndexOf('.');
//        }
//        String extension = fileName != null ? fileName.substring(lastDotIndex + 1) : null;
//
//        return formattedDate + "_" + keyName + "." + extension;
//    }

    private void validateFile(MultipartFile file) {
        long fileSize = file.getSize();
        String fileName = file.getOriginalFilename();
        String fileExtension = null;
        if (fileName != null) {
            fileExtension = getFileExtension(fileName);
        }
        if (!List.of("mp4", ".mov").contains(fileExtension != null ? fileExtension.toLowerCase() : null)) {
            throw new ConstraintViolationException("File type not allowed", null);
        } else if ("mp4".equalsIgnoreCase(fileExtension) && fileSize > 500 * 1024 * 1024) {
            throw new ConstraintViolationException("MP4 file size must not exceed 250MB", null);
        }
    }

    public static String getFileExtension(String path) {
        int dotIndex = path.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex != path.length() - 1) {
            return path.substring(dotIndex + 1);
        } else {
            throw new IllegalArgumentException("Invalid file path format");
        }
    }
}