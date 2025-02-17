package group.project.cursusonlinecoursemanagement.course.controller;

import group.project.cursusonlinecoursemanagement.shared.domain.FileMetadata;
import group.project.cursusonlinecoursemanagement.shared.service.FirebaseStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;

@Tag(
        name = "Firebase Storage Service for Upload Files"
)
@Validated
@RestController
@RequestMapping("/api/storage")
public class FirebaseStorageController {

    private final FirebaseStorageService firebaseStorageService;

    public FirebaseStorageController(FirebaseStorageService firebaseStorageService) {
        this.firebaseStorageService = firebaseStorageService;
    }

    @Operation(
            summary = "Upload a file to Firebase Storage",
            description = "Uploads a file to a specified folder in Firebase Storage and returns metadata of the uploaded file.",
            requestBody = @RequestBody(
                    description = "The file to be uploaded. Maximum size is 500 MB.",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns the metadata of the uploaded file."),
                    @ApiResponse(responseCode = "400", description = "Bad Request if the file name or folder name is invalid.")
            }
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PostMapping(value = "/auth/{nameFolder}/{nameFile}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileMetadata> uploadFile(
            @Pattern(regexp = "[a-zA-Z0-9_\\s]*", message = "Key name must not contain Vietnamese characters")
            @Size(max = 50, message = "Key name must not exceed 50 characters")
            @PathVariable(name = "nameFile")
            String fileName,
            @RequestParam("file") MultipartFile file, //500MB
            @Pattern(regexp = "[a-zA-Z0-9_\\s]*", message = "Key name must not contain Vietnamese characters")
            @Size(max = 50, message = "Key name must not exceed 50 characters")
            @PathVariable(name = "nameFolder")
            String fileFolder) {
        return new ResponseEntity<>(firebaseStorageService.uploadFile(file, fileFolder, fileName), HttpStatus.OK);

    }

    @Operation(
            summary = "List all files",
            description = "Returns a list of metadata for all files in Firebase Storage."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of files."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    })
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @GetMapping("/auth/getListFiles")
    public ResponseEntity<List<FileMetadata>> listFiles() {
        return new ResponseEntity<>(firebaseStorageService.listFiles(), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a file",
            description = "Deletes a file from Firebase Storage based on the file name."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the file."),
            @ApiResponse(responseCode = "400", description = "Bad Request if the file name is invalid."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    })
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @DeleteMapping("/auth/")
    public ResponseEntity<String> deleteFile(@RequestParam("fileName") String fileName) {
        return new ResponseEntity<>(firebaseStorageService.deleteFile(fileName), HttpStatus.OK);
    }

    @Operation(
            summary = "Get signed URL for a file",
            description = "Returns a signed URL for a file in Firebase Storage, valid for 60 minutes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated the signed URL."),
            @ApiResponse(responseCode = "400", description = "Bad Request if the file name is invalid."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    })
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @GetMapping("/auth/getSignUrl")
    public ResponseEntity<String> getSignUrl(@RequestParam("fileName") String fileName) {
        URL url = firebaseStorageService.getSignUrl(fileName); //60 MINUTES
        return new ResponseEntity<>(url.toString(), HttpStatus.OK);
    }
}

