package group.project.cursusonlinecoursemanagement.course.service;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.UserLessonProgressResponse;

import java.util.List;
import java.util.UUID;

public interface UserLessonProgressService {

    void startLessons(UUID courseId);

    void completeLesson(UUID lessonId, UUID courseId);

    Double getProgressCount(UUID courseId);

    void unCompleteLesson(UUID lessonId, UUID courseId);

    List<UserLessonProgressResponse> getLessonProgress(UUID courseId, String email);
}
