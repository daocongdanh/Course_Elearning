package group.project.cursusonlinecoursemanagement.user.service;

import group.project.cursusonlinecoursemanagement.user.domain.dto.InstructorDto;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.GetInstructorBalanceResponse;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.InstructorResponse;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface InstructorService {
    Double sumEnrollmentPriceByInstructorId(UUID uuid);

    Double sumEnrollmentPriceByInstructorIdAndDate(UUID uuid, int month, int year);

    int countNumberOfEnrollmentOfInstructor(UUID uuid);

    int countNumberOfEnrollmentByInstructorIdAndDate(UUID uuid, int month, int year);

    int countAllCourseOfInstructor(UUID uuid);

    Map<String, Double> eachMonthRevenueOfInstructor(UUID uuid);

    Map<String, Integer> eachMonthNumberOfEnrollmentOfInstructor(UUID uuid);

    Double totalQuarterlyRevenue(int quarter, String email);

    int countNumberOfEnrollmentByQuarterOfInstructor(int quarter, String email);

    Map<String, Double> revenueOfEachCourse(String email);

    Object composeAnalysis(String email);

    Double getWalletBalance(String email);

    InstructorResponse getInstructorById(UUID instructorId);

    GetInstructorBalanceResponse getBalance(String email);

    String getEmailById(UUID userId);
}
