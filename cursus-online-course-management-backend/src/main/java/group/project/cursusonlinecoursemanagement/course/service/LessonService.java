package group.project.cursusonlinecoursemanagement.course.service;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.CreateDraftLessonRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.ReorderLessonRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.UpdateLessonRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetLessonResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface LessonService {
    List<GetLessonResponse> getAllLesson();

    GetLessonResponse getLessonById(UUID lessonId);

    GetLessonResponse createDraftLesson(CreateDraftLessonRequest lessonRequest, UUID courseId);

    GetLessonResponse updateLesson(UUID lessonId, UpdateLessonRequest lessonRequest);

    GetLessonResponse uploadVideo(UUID lessonId, MultipartFile file);

    void deleteLesson(UUID lessonId);
    
    void reorderLessons(UUID courseId, List<ReorderLessonRequest> reorderLessonRequests);

    Boolean userIsCompletedLesson(UUID lessonId, String email);

    List<GetLessonResponse> deleteLessonAndReorder(UUID lessonId);
}
