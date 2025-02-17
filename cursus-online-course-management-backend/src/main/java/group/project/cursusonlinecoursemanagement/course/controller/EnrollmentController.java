package group.project.cursusonlinecoursemanagement.course.controller;

import com.itextpdf.text.DocumentException;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.EnrollmentRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.EnrollmentResponse;
import group.project.cursusonlinecoursemanagement.course.service.EnrollmentService;
import group.project.cursusonlinecoursemanagement.shared.permission.user.UserStatus;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(
        name = "CRUD REST APIs for Enrollment"
)
@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(
            summary = "Get All Enrollments REST API",
            description = "Get All Enrollments REST API is used to fetch all the enrollments from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/auth/getAll")
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        List<EnrollmentResponse> enrollments = enrollmentService.getAllEnrollment();
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    @Operation(
            summary = "Get Enrollments By Id REST API",
            description = "Get Enrollments By Id REST API is used to get single enrollment from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/auth/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable Long id) {
        EnrollmentResponse enrollment = enrollmentService.getEnrollmentById(id);
        return new ResponseEntity<>(enrollment, HttpStatus.OK);
    }

    @Operation(
            summary = "Create Enrollments REST API",
            description = "Create Enrollments REST API is used to save enrollment into database"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @UserStatus(unAllowedStatuses = {Status.BLOCK_ROLE_STUDENT,Status.BLOCK_ACCOUNT})
    @PostMapping("/create")
    public ResponseEntity<EnrollmentResponse> createEnrollment(@Valid @RequestBody EnrollmentRequest enrollmentRequest) {
        return new ResponseEntity<>(enrollmentService.createEnrollment(enrollmentRequest), HttpStatus.CREATED);
    }

    //Không có update vì enrollment không nên có update
//    @Operation(
//            summary = "Update Enrollment REST API",
//            description = "Update Enrollment REST API is used to update a particular enrollment from the database"
//    )
//    @ApiResponse(
//            responseCode = "200",
//            description = "Http Status 200 SUCCESS"
//    )
//    @SecurityRequirement(
//            name = "Bear Authentication"
//    )
//    @PutMapping("/auth/{id}")
//    public ResponseEntity<String> updateEnrollment(@PathVariable Long id, @Valid @RequestBody EnrollmentResponse enrollmentResponse) {
//        enrollmentService.updateEnrollment(id, enrollmentResponse);
//        return new ResponseEntity<>("Enrollment updated successfully", HttpStatus.OK);
//    }

    @Operation(
            summary = "Get Enrollments By User Id REST API",
            description = "Get Enrollments By User Id REST API is used to get enrollments from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/auth/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentByUserId(@PathVariable Long userId) {
        List<EnrollmentResponse> enrollment = enrollmentService.getAllEnrollmentByUserId(userId);
        return new ResponseEntity<>(enrollment, HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @GetMapping("/auth/course/{courseId}/getEnrollmentOfUser")
    public ResponseEntity<EnrollmentResponse> getEnrollmentOfUser(@PathVariable UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        EnrollmentResponse enrollment = enrollmentService.getEnrollmentOfUser(courseId, email);
        return new ResponseEntity<>(enrollment, HttpStatus.OK);
    }

    @Operation(
            summary = "Generate certificate",
            description = "Generate certificate for user base on Enrollment"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/generateCertificate/{enrollmentId}")
    public ResponseEntity<InputStreamResource> generateCertificate(@PathVariable  Long enrollmentId) throws DocumentException, IOException {
        InputStreamResource inputStreamResource = enrollmentService.generateCertificate(enrollmentId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=certificate.pdf");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(inputStreamResource);
    }

    @Operation(
            summary = "Generate certificate",
            description = "Generate certificate for user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/auth/generateCertificate/course/{courseId}")
    public ResponseEntity<InputStreamResource> generateCertificateOfCourse(@PathVariable UUID courseId) throws DocumentException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        InputStreamResource inputStreamResource = enrollmentService.generateCertificateOfCourse(courseId, email);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=certificate.pdf");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(inputStreamResource);
    }

    @Operation(
            summary = "Get Five New Enrollments REST API",
            description = "Get Five New Enrollments REST API is used to fetch the five most recent enrollments from the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Enrollments fetched successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/auth/getFiveNewEnrollments")
    public ResponseEntity<List<EnrollmentResponse>> getFiveNewEnrollments() {
        List<EnrollmentResponse> enrollments = enrollmentService.getFiveNewEnrollment();
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }
}
