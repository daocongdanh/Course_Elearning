package group.project.cursusonlinecoursemanagement.course.controller;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.UserLessonProgressResponse;
import group.project.cursusonlinecoursemanagement.course.service.UserLessonProgressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "CRUD REST APIs for Lesson Progression"
)
@RestController
@RequestMapping("/api/lessonProgress/auth")
public class UserLessonProgressController {
    private final UserLessonProgressService userLessonProgressService;

    public UserLessonProgressController(UserLessonProgressService userLessonProgressService) {
        this.userLessonProgressService = userLessonProgressService;
    }


    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping("/startCourse")
    public ResponseEntity<String> startLessons(@RequestParam UUID courseId) {
        userLessonProgressService.startLessons(courseId);
        return new ResponseEntity<>("Course started successfully", HttpStatus.OK);
    }


    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping("/course/{courseId}/lesson/{lessonId}/completed")
    public ResponseEntity<String> completeLesson(@PathVariable UUID courseId, @PathVariable UUID lessonId) {
        userLessonProgressService.completeLesson(lessonId,courseId);
        return new ResponseEntity<>("Lesson completed successfully", HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @GetMapping("getProgressCount/{courseId}")
    public ResponseEntity<Double> getProgressCount(@PathVariable UUID courseId) {
        return new ResponseEntity<>(userLessonProgressService.getProgressCount(courseId), HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PatchMapping("/course/{courseId}/lesson/{lessonId}/uncompleted")
    public ResponseEntity<String> unCompleteLesson(@PathVariable UUID courseId, @PathVariable UUID lessonId) {
        userLessonProgressService.unCompleteLesson(lessonId,courseId);
        return new ResponseEntity<>("Lesson uncompleted successfully", HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
//    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/getUserProgress/{courseId}")
    public ResponseEntity<List<UserLessonProgressResponse>> getLessonProgress(@PathVariable UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return new ResponseEntity<>(userLessonProgressService.getLessonProgress(courseId, email), HttpStatus.OK);
    }

}
