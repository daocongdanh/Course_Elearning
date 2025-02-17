package group.project.cursusonlinecoursemanagement.course.controller;


import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.CreateDraftCourseRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.UpdateCourseRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.CreateDraftCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetUserOwnedCourseResponse;
import group.project.cursusonlinecoursemanagement.course.service.CourseService;
import group.project.cursusonlinecoursemanagement.shared.permission.user.UserStatus;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(
        name = "CRUD REST APIs for Course"
)
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }


    @Operation(
            summary = "Get All Courses REST API",
            description = "Get All Courses REST API is used to fetch all the courses from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("/getAllCourses")
    public ResponseEntity<List<GetCourseResponse>> getAllCourses() {
        List<GetCourseResponse> courses = courseService.getAllCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/searchCoursesByDesc")
    public ResponseEntity<List<GetCourseResponse>> searchCoursesByDesc(@RequestParam String desc) {
        List<GetCourseResponse> courses = courseService.searchCoursesByDesc(desc);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }
    
    /**
     * Retrieves all instructor's courses from the service.
     *
     * @return A ResponseEntity containing a list of GetCourseResponse objects and an HTTP status code.
     */
    @Operation(
            summary = "Get All Instructor's Courses REST API",
            description = "Get All Instructor's Courses REST API is used to fetch all the courses of instructor from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @GetMapping("/auth/getCoursesOfInstructor")
    public ResponseEntity<List<GetCourseResponse>> getCoursesOfInstructor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        List<GetCourseResponse> courses = courseService.getCoursesOfInstructor(email);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @Operation(
            summary = "Get Courses Of Instructor By Id REST API",
            description = "Get Courses Of Instructor By Id REST API is used to fetch all the courses of a specific instructor from the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Courses fetched successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @GetMapping("/auth/getCoursesOfInstructorById")
    public ResponseEntity<List<GetCourseResponse>> getCoursesOfInstructorById(@RequestParam UUID userId) {
        List<GetCourseResponse> courses = courseService.getCoursesOfInstructorById(userId);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @Operation(
            summary = "Get Course By Id REST API",
            description = "Get Course By Id REST API is used to get single course from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("/getCourseById/{id}")
    public ResponseEntity<GetCourseResponse> getCourseById(@PathVariable UUID id) {
        GetCourseResponse course = courseService.getCourseById(id);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    @Operation(
            summary = "Create Course REST API",
            description = "Create Course REST API is used to save course into database"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @UserStatus(unAllowedStatuses = {Status.BLOCK_ROLE_INSTRUCTOR,Status.BLOCK_ACCOUNT})
    @PostMapping("/auth/createCourse")
    public ResponseEntity<CreateDraftCourseResponse> createCourse(@Valid @RequestBody CreateDraftCourseRequest createCourseRequest) {
        return new ResponseEntity<>(courseService.createDraftCourse(createCourseRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update Course REST API",
            description = "Update Course REST API is used to update a particular course from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @UserStatus(unAllowedStatuses = {Status.BLOCK_ROLE_INSTRUCTOR,Status.BLOCK_ACCOUNT})
    @PutMapping(value = "/auth/updateCourse/{id}")
    public ResponseEntity<GetCourseResponse> updateCourse(@PathVariable UUID id, @Valid @RequestBody UpdateCourseRequest updateCourseRequest) {
        return new ResponseEntity<>(courseService.updateCourse(id, updateCourseRequest), HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @PutMapping(value = "/auth/uploadThumbnail/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GetCourseResponse> uploadThumbnail(@PathVariable UUID id, @RequestParam(value = "file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(courseService.uploadThumbnail(id, file), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete Course REST API",
            description = "Delete Course REST API is used to delete a particular course from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @DeleteMapping("/auth/deleteCourse/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return new ResponseEntity<>("Course deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/searchCourses")
    public ResponseEntity<List<GetCourseResponse>> searchCourses(@RequestParam(required = false) Long categoryId, @RequestParam(required = false) String title) {
        List<GetCourseResponse> courses = courseService.searchCourses(categoryId, title);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }


    @GetMapping("/getPublishedCourses")
    public ResponseEntity<List<GetCourseResponse>> getPublishedCourses() {
        List<GetCourseResponse> courses = courseService.getPublishedCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @Operation(
            summary = "Get Student Owned Courses REST API",
            description = "Get Student Owned Courses REST API is used to fetch all the courses owned by student from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/auth/getStudentOwnedCourses")
    public ResponseEntity<List<GetUserOwnedCourseResponse>> getStudentOwnedCourses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        List<GetUserOwnedCourseResponse> courses = courseService.getStudentOwnedCourses(email);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @GetMapping("/auth/getStudentOwnedCoursesById")
    public ResponseEntity<List<GetUserOwnedCourseResponse>> getStudentOwnedCourses(@RequestParam UUID userId) {
        List<GetUserOwnedCourseResponse> courses = courseService.getStudentOwnedCoursesById(userId);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @Operation()
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("/getCourseByIdByUser/{courseId}")
    public ResponseEntity<GetCourseResponse> getCourseByIdByUser(@PathVariable UUID courseId) {
        GetCourseResponse course = courseService.getCourseByIdByUser(courseId);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    @Operation(
            summary = "Get Student Owned Course With Lessons And Progress By Id REST API",
            description = "Get Student Owned Course With Lessons And Progress By Id REST API is used to fetch a particular course owned by student with lessons and progress from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/auth/getStudentOwnedCourseWithLessonsAndProgressById/{courseId}")
    public ResponseEntity<GetCourseResponse> getStudentOwnedCourseWithLessonsAndProgressById(@PathVariable UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        GetCourseResponse course = courseService.getStudentOwnedCourseWithLessonsAndProgressById(email, courseId);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

}