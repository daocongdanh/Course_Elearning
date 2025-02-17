package group.project.cursusonlinecoursemanagement.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Component
public class FileUtil {
    @Value("${upload.video-src}")
    private String uploadSrc;
    @Value("${upload.video-target}")
    private String uploadTarget;

    private String generateUniqueFileName(MultipartFile file){
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        return UUID.randomUUID().toString() + "_" + fileName;
    }

    public String upload(MultipartFile file){
        if(file == null)
            return "";
        try{
            String uniqueFileName = generateUniqueFileName(file);
            Path uploadSrc = Paths.get(this.uploadSrc);
//            Path uploadTarget = Paths.get(this.uploadTarget);

            Path destinationSrc = Paths.get(uploadSrc.toString(),uniqueFileName);
//            Path destinationTarget = Paths.get(uploadTarget.toString(),uniqueFileName);

            Files.copy(file.getInputStream(), destinationSrc, StandardCopyOption.REPLACE_EXISTING);
//            Files.copy(file.getInputStream(), destinationTarget, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName;
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
