package group.project.cursusonlinecoursemanagement.course.service;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.CreateDraftCourseRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.UpdateCourseRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.CreateDraftCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetUserOwnedCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Course;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


public interface CourseService {
    List<GetCourseResponse> getAllCourses();

    GetCourseResponse getCourseById(UUID id);

    CreateDraftCourseResponse createDraftCourse(CreateDraftCourseRequest createDraftCourseRequest);

    GetCourseResponse updateCourse(UUID id, UpdateCourseRequest updateCourseRequest);

    GetCourseResponse uploadThumbnail(UUID id, MultipartFile file) throws IOException;

    void deleteCourse(UUID id);
    
    List<GetCourseResponse> getCoursesOfInstructor(String email);

    List<GetCourseResponse> getCoursesOfInstructorById(UUID userId);
    
    List<GetCourseResponse> searchCourses(Long categoryId, String title);

    List<GetCourseResponse> getPublishedCourses();

    Course findCourseById(UUID courseId);

    List<GetUserOwnedCourseResponse> getStudentOwnedCourses(String email);

    List<GetUserOwnedCourseResponse> getStudentOwnedCoursesById(UUID userId);

    GetCourseResponse getCourseByIdByUser(UUID courseId);

    GetCourseResponse getStudentOwnedCourseWithLessonsAndProgressById(String email, UUID courseId);
}
