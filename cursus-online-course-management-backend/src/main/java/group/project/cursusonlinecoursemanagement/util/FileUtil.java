package group.project.cursusonlinecoursemanagement.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Component
public class FileUtil {
    public static final String UPLOAD_FOLDER = "uploads";
    public static final String BASE_URL = "http://localhost:8080";

    public String upload(MultipartFile file) {

        try {
            // Tạo tên tệp duy nhất với UUID
            String fileName = UUID.randomUUID().toString();

            // Tạo đường dẫn lưu tệp (/uploads/fileName)
            Path path = Paths.get(UPLOAD_FOLDER + File.separator + fileName);

            // Lưu tệp
            Files.copy(file.getInputStream(), path);

            return BASE_URL + File.separator + UPLOAD_FOLDER + File.separator + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể tải lên tệp");
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);

            Path filePath = Paths.get(UPLOAD_FOLDER + File.separator + fileName);

            if (!Files.exists(filePath)) {
                throw new RuntimeException("Tệp không tồn tại: " + fileName);
            }

            Files.delete(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể xóa tệp: " + fileUrl);
        }
    }

    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(BASE_URL)) {
            throw new IllegalArgumentException("URL không hợp lệ: " + fileUrl);
        }

        String prefix = BASE_URL + "/" + UPLOAD_FOLDER + "/";
        return fileUrl.replace(prefix, "");
    }
}
