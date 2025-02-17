package group.project.cursusonlinecoursemanagement.course.service;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.FeedbackRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.FeedbackResponse;

import java.util.List;
import java.util.UUID;

public interface FeedbackService {
    FeedbackResponse addFeedback(UUID courseId, String email, FeedbackRequest feedback);

    List<FeedbackResponse> getAllFeedbacksOfCourse(UUID courseId);
}
