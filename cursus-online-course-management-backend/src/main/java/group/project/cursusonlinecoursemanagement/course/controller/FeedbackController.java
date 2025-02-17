package group.project.cursusonlinecoursemanagement.course.controller;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.FeedbackRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.FeedbackResponse;
import group.project.cursusonlinecoursemanagement.course.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "CRUD REST APIs for Course's Feedback"
)
@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Operation(
            summary = "Add feedback to a course",
            description = "Add feedback to a course"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping("/auth/addFeedback/{courseId}")
    public ResponseEntity<FeedbackResponse> addFeedback(@PathVariable UUID courseId, @RequestBody @Valid FeedbackRequest feedback) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        FeedbackResponse feedbackResponse = feedbackService.addFeedback(courseId, email, feedback);
        return ResponseEntity.ok(feedbackResponse);
    }

    @Operation(
            summary = "Get all feedbacks of a course",
            description = "Get all feedbacks of a course"
    )
//    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_INSTRUCTOR')")
    @GetMapping("/getFeedbacks/{courseId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedback(@PathVariable UUID courseId) {
        List<FeedbackResponse> feedbackResponse = feedbackService.getAllFeedbacksOfCourse(courseId);
        return ResponseEntity.ok(feedbackResponse);
    }
}
