package group.project.cursusonlinecoursemanagement.course.controller;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.CreateDraftLessonRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.ReorderLessonRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.UpdateLessonRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetLessonResponse;
import group.project.cursusonlinecoursemanagement.course.service.LessonService;
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

import java.util.List;
import java.util.UUID;

@Tag(
        name = "CRUD REST APIs for Lesson"
)
@RestController
@RequestMapping("/api/lessons")
public class LessonController {
    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @PostMapping("/auth/createLesson/{courseId}")
    public ResponseEntity<GetLessonResponse> createLesson(@Valid @RequestBody CreateDraftLessonRequest lessonRequest,  @PathVariable UUID courseId) {
        return new ResponseEntity<>(lessonService.createDraftLesson(lessonRequest, courseId), HttpStatus.CREATED);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @GetMapping("/auth/getAllLessons")
    public ResponseEntity<List<GetLessonResponse>> getAllCourses() {
        List<GetLessonResponse> lessons = lessonService.getAllLesson();
        return new ResponseEntity<>(lessons, HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @GetMapping("/auth/getLessonById/{id}")
    public ResponseEntity<GetLessonResponse> getCourseById(@PathVariable UUID id) {
        GetLessonResponse lesson = lessonService.getLessonById(id);
        return new ResponseEntity<>(lesson, HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @PutMapping(value = "/auth/updateLesson/{id}")
    public ResponseEntity<GetLessonResponse> updateLesson(@PathVariable UUID id,
                                                          @Valid @RequestBody UpdateLessonRequest updateLessonRequest) {
        GetLessonResponse lesson = lessonService.updateLesson(id, updateLessonRequest);
        return new ResponseEntity<>(lesson, HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @PutMapping(value = "/auth/uploadVideo/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GetLessonResponse> uploadVideo(@PathVariable UUID id, @RequestParam(value = "file") MultipartFile file){
        return new ResponseEntity<>(lessonService.uploadVideo(id, file), HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @DeleteMapping("/auth/deleteLesson/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable UUID id) {
        lessonService.deleteLesson(id);
        return new ResponseEntity<>("Lesson deleted successfully", HttpStatus.OK);
    }
    
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @PatchMapping("/auth/course/{courseId}/reorderLesson")
    public ResponseEntity<String> reorderLesson(@PathVariable UUID courseId, @Valid @RequestBody List<ReorderLessonRequest> reorderLessonRequests) {
        lessonService.reorderLessons(courseId, reorderLessonRequests);
        return new ResponseEntity<>("Lesson reordered successfully", HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @GetMapping("/auth/userIsCompletedLesson/{lessonId}")
    public ResponseEntity<Boolean> userIsCompleted(@PathVariable UUID lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return new ResponseEntity<>(lessonService.userIsCompletedLesson(lessonId, email), HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @DeleteMapping("/auth/deleteLessonAndReorder/{lessonId}")
    public ResponseEntity<List<GetLessonResponse>> deleteLessonAndReorder(@PathVariable UUID lessonId) {
        List<GetLessonResponse> lessons = lessonService.deleteLessonAndReorder(lessonId);
        return new ResponseEntity<>(lessons, HttpStatus.OK);
    }

}
