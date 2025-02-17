package group.project.cursusonlinecoursemanagement.course.service.impl;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.FeedbackRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.FeedbackResponse;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Feedback;
import group.project.cursusonlinecoursemanagement.course.repository.FeedbackRepository;
import group.project.cursusonlinecoursemanagement.course.service.FeedbackService;
import group.project.cursusonlinecoursemanagement.user.service.impl.UserServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final CourseServiceImpl courseServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final EnrollmentServiceImpl enrollmentServiceImpl;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, CourseServiceImpl courseServiceImpl, UserServiceImpl userServiceImpl, EnrollmentServiceImpl enrollmentServiceImpl) {
        this.feedbackRepository = feedbackRepository;
        this.courseServiceImpl = courseServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.enrollmentServiceImpl = enrollmentServiceImpl;
    }

    @Override
    public FeedbackResponse addFeedback(UUID courseId, String email, FeedbackRequest feedbackRequest) {
        validateEnrollment(courseId, email);
        Feedback feedback = createFeedback(courseId, email, feedbackRequest);
        return saveAndConvertFeedback(feedback);
    }

    @Override
    public List<FeedbackResponse> getAllFeedbacksOfCourse(UUID courseId) {
        return feedbackRepository.findByCourse_CourseId(courseId).stream()
                .map(FeedbackResponse::convertEntityToResponse)
                .sorted((f1, f2) -> f2.getCreatedAt().compareTo(f1.getCreatedAt()))
                .toList();
    }

    private void validateEnrollment(UUID courseId, String email) {
        if (!enrollmentServiceImpl.isExistedEnrollmentByCourseIdAndUser(courseId, email)) {
            throw new RuntimeException("You are not enrolled in this course");
        }
    }

    private Feedback createFeedback(UUID courseId, String email, FeedbackRequest feedbackRequest) {
        Feedback feedback = new Feedback();
        feedback.setFeedback(feedbackRequest.getFeedback());
        feedback.setRating(feedbackRequest.getRating());
        feedback.setCourse(courseServiceImpl.getCourseFromRepoById(courseId));
        feedback.setUser(userServiceImpl.findUserByEmail(email));
        feedback.setCreatedAt(LocalDateTime.now());
        return feedback;
    }

    private FeedbackResponse saveAndConvertFeedback(Feedback feedback) {
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return FeedbackResponse.convertEntityToResponse(savedFeedback);
    }

}
