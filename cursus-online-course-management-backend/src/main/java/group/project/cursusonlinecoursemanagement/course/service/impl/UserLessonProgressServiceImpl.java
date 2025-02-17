package group.project.cursusonlinecoursemanagement.course.service.impl;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.UserLessonProgressResponse;
import group.project.cursusonlinecoursemanagement.course.domain.entity.*;
import group.project.cursusonlinecoursemanagement.course.repository.EnrollmentRepository;
import group.project.cursusonlinecoursemanagement.course.repository.UserLessonProgressRepository;
import group.project.cursusonlinecoursemanagement.course.service.UserLessonProgressService;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserLessonProgressServiceImpl implements UserLessonProgressService {
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseServiceImpl courseServiceImpl;

    public UserLessonProgressServiceImpl(UserLessonProgressRepository userLessonProgressRepository, EnrollmentRepository enrollmentRepository, @Lazy CourseServiceImpl courseServiceImpl) {
        this.userLessonProgressRepository = userLessonProgressRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseServiceImpl = courseServiceImpl;
    }

    @Override
    public void startLessons(UUID courseId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            if (enrollmentRepository.existsByCourse_CourseIdAndUser_Email(courseId, email)) {
                int result = userLessonProgressRepository.startCourse(courseId, email);
                if (result <= 0) {
                    throw new RuntimeException("No lessons progress updated");
                }
            } else {
                throw new RuntimeException("Course have not registered");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error starting Lessons");
        }

    }

    @Override
    public void completeLesson(UUID lessonId, UUID courseId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            if (enrollmentRepository.existsByCourse_CourseIdAndUser_Email(courseId, email)) {
                int result = userLessonProgressRepository.completeLesson(courseId, lessonId, email);
                if (result <= 0) {
                    throw new RuntimeException("No lesson progress updated");
                }
            } else {
                throw new RuntimeException("Course have not registered");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error completing a lesson");
        }
    }

    @Override
    public Double getProgressCount(UUID courseId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            if (enrollmentRepository.existsByCourse_CourseIdAndUser_Email(courseId, email)) {
                return courseServiceImpl.calculateCourseProgress(
                        courseServiceImpl.getCourseFromRepoById(courseId),
                        email
                );
            } else {
                throw new RuntimeException("Course have not registered");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error completing a lesson");
        }
    }

    @Override
    public void unCompleteLesson(UUID lessonId, UUID courseId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            if (enrollmentRepository.existsByCourse_CourseIdAndUser_Email(courseId, email)) {
                UserLessonProgress userLessonProgress = userLessonProgressRepository.findByLesson_LessonIdAndUser_Email(lessonId, email)
                        .orElseThrow(() -> new RuntimeException("Lesson not found"));
                if (userLessonProgress.getStatus().equals(LessonStatus.COMPLETED)) {
                    userLessonProgress.setStatus(LessonStatus.NOT_STARTED);
                    userLessonProgressRepository.save(userLessonProgress);
                } else {
                    throw new RuntimeException("Lesson is not completed");
                }
            } else {
                throw new RuntimeException("Course have not registered");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error uncompleted a lesson");
        }
    }

    @Override
    public List<UserLessonProgressResponse> getLessonProgress(UUID courseId, String email) {
        try {
            Enrollment enrollment = getEnrollment(courseId, email);
            if (enrollment != null) {
                updateLessonProgress(enrollment, email);
            } else {
                throw new RuntimeException("Course has not been registered");
            }
            return getUserLessonProgressResponses(courseId, email);
        } catch (Exception e) {
            throw new RuntimeException("Error getting lesson progress", e);
        }
    }

    public List<UserLessonProgress> getLessonProgressOfStudent(UUID courseId, String email) {
        try {
            Enrollment enrollment = getEnrollment(courseId, email);
            if (enrollment != null) {
                updateLessonProgress(enrollment, email);
            } else {
                throw new RuntimeException("Course has not been registered");
            }
            return userLessonProgressRepository.findByUser_EmailAndLesson_Course_CourseId(email, courseId);
        } catch (Exception e) {
            throw new RuntimeException("Error getting lesson progress", e);
        }
    }

    private Enrollment getEnrollment(UUID courseId, String email) {
        return enrollmentRepository.findByCourse_CourseIdAndUser_Email(courseId, email)
                .orElseThrow(() -> new RuntimeException("Course has not been registered"));
    }

    private void updateLessonProgress(Enrollment enrollment, String email) {
        enrollment.getCourse().getLessons().stream()
                .filter(Lesson::getIsPublished)
                .forEach(lesson -> {
                    UserLessonProgress userLessonProgress = getUserLessonProgress(lesson, email, enrollment.getUser());
                    userLessonProgressRepository.save(userLessonProgress);
                });
    }

    private UserLessonProgress getUserLessonProgress(Lesson lesson, String email, User user) {
        return userLessonProgressRepository.findByLesson_LessonIdAndUser_Email(lesson.getLessonId(), email)
                .orElseGet(() -> {
                            UserLessonProgress userLessonProgress = new UserLessonProgress();
                            userLessonProgress.setId(new UserLessonProgressId(lesson.getLessonId(), user.getUserId()));
                            userLessonProgress.setUser(user);
                            userLessonProgress.setLesson(lesson);
                            userLessonProgress.setStatus(LessonStatus.NOT_STARTED);
                            return userLessonProgress;
                        }
                );
    }

    private List<UserLessonProgressResponse> getUserLessonProgressResponses(UUID courseId, String email) {
        return userLessonProgressRepository.findByUser_EmailAndLesson_Course_CourseId(email, courseId).stream()
                .map(UserLessonProgressResponse::convertEntityToResponse)
                .toList();
    }


}
