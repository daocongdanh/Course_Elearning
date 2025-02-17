package group.project.cursusonlinecoursemanagement.user.service;

import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.CourseEnrollmentCountByDate;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.GetInstructorResponse;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.GetStudentResponse;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AdminService {



    File generateReport(Date startDate, Date endDate);

    void updateStatusOfUser(UUID id,String newStatus);

    List<GetStudentResponse> getAllStudent();

    List<GetInstructorResponse> getAllInstructor();

    Double sumOfRevenue();

    Double sumOfRevenueByDate(int month, int year);

    int countAllUser();

    int countAllUserByMonth(int month, int year);

    int countAllCourse();

    int countAllCourseByMonth(int month, int year);

    int countNumberOfEnrollment();

    int countNumberOfEnrollmentByDate(int month, int year);

    int countStudentsByMonth(int month, int year);

    int countInstructorsByMonth(int month, int year);

    Map<String, Double> eachMonthRevenue();

    Map<String, Integer> eachMonthNumberOfEnrollment();

    int countAllInstructor();

    LocalDateTime createDateOfFirstUserRegister();

    Map<String, Integer> eachMonthNumberOfUser();

    List<CourseEnrollmentCountByDate> getCourseAndEnrollmentCountBy30Date();
}
