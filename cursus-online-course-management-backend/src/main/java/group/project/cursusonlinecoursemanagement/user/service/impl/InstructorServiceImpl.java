package group.project.cursusonlinecoursemanagement.user.service.impl;

import com.google.api.client.util.ArrayMap;
import group.project.cursusonlinecoursemanagement.course.service.CourseService;
import group.project.cursusonlinecoursemanagement.course.service.EnrollmentService;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import group.project.cursusonlinecoursemanagement.shared.service.CloudinaryService;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.GetInstructorBalanceResponse;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.InstructorResponse;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Instructor;
import group.project.cursusonlinecoursemanagement.user.repository.InstructorRepository;
import group.project.cursusonlinecoursemanagement.user.service.InstructorService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final CloudinaryService cloudinaryService;

    public InstructorServiceImpl(InstructorRepository instructorRepository, CourseService courseService, EnrollmentService enrollmentService, CloudinaryService cloudinaryService) {
        this.instructorRepository = instructorRepository;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.cloudinaryService = cloudinaryService;
    }

    //Hàm này tính doanh thu tổng của một instructor
    @Override
    public Double sumEnrollmentPriceByInstructorId(UUID instructorId) {
        try {
            Double sum = instructorRepository.sumEnrollmentPriceByInstructorId(instructorId);
            return sum != null ? 0.0 : null;
        } catch (Exception e) {
            return 0.0;
        }

    }

    //Hàm này tính tổng doanh thu theo tháng và năm (VD: truyền vào tháng 6, năm 2024 thì nó sẽ tính doanh thu của chỉ riêng tháng 6/2024)
    @Override
    public Double sumEnrollmentPriceByInstructorIdAndDate(UUID instructorId, int month, int year) {
        try {
            Double sum = instructorRepository.sumEnrollmentPriceByInstructorIdAndDate(instructorId, month, year);
            return sum != null ? 0.0 : null;
        } catch (Exception e) {
            return 0.0;
        }

        //return (Double) instructorRepository.sumEnrollmentPriceByInstructorIdAndDate(instructorId, enrollmentDate);
    }

    //Hàm này tính tổng khóa học đã bán của một instructor
    @Override
    public int countNumberOfEnrollmentOfInstructor(UUID instructorId) {
        try {
            return instructorRepository.countNumberOfEnrollmentOfInstructor(instructorId);
        } catch (Exception e) {
            return 0;
        }

    }

    //Hàm này tính số khóa học đã bán được theo tháng và năm (VD: truyền vào tháng 6, năm 2024 thì nó sẽ tính doanh thu của chỉ riêng tháng 6/2024)
    @Override
    public int countNumberOfEnrollmentByInstructorIdAndDate(UUID uuid, int month, int year) {
        try {
            return instructorRepository.countNumberOfEnrollmentByInstructorIdAndDate(uuid, month, year);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int countAllCourseOfInstructor(UUID uuid) {
        try {
            return instructorRepository.countAllCourseOfInstructor(uuid);
        } catch (Exception e) {
            return 0;
        }
    }

    //Hàm này trả ra một map với key là thời gian (tháng - năm) và giá trị là doanh thu của tháng đó(phục vụ cho việc vẽ biểu đồ)
    @Override
    public Map<String, Double> eachMonthRevenueOfInstructor(UUID instructorId) {

        Map<String, Double> graphData = new ArrayMap<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dateOfFirstMonthEnrollment = instructorRepository.createDateOfFirstEnrollment(instructorId);
            while (dateOfFirstMonthEnrollment.isBefore(now)) {
                graphData.put(dateOfFirstMonthEnrollment.getMonth() + " - " + dateOfFirstMonthEnrollment.getYear(), sumEnrollmentPriceByInstructorIdAndDate(instructorId, dateOfFirstMonthEnrollment.getMonth().getValue(), dateOfFirstMonthEnrollment.getYear()));
                dateOfFirstMonthEnrollment = dateOfFirstMonthEnrollment.plusMonths(1);
            }
        } catch (Exception e) {
            graphData.put("Empty list", 0.0);
        }

        return graphData;
    }

    //Hàm này trả ra một map với key là thời gian (tháng - năm) và giá trị là số khóa học đã bán của tháng đó(phục vụ cho việc vẽ biểu đồ)
    @Override
    public Map<String, Integer> eachMonthNumberOfEnrollmentOfInstructor(UUID instructorId) {
        Map<String, Integer> graphData = new ArrayMap<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dateOfFirstMonthEnrollment = instructorRepository.createDateOfFirstEnrollment(instructorId);
            while (dateOfFirstMonthEnrollment.isBefore(now)) {
                graphData.put(dateOfFirstMonthEnrollment.getMonth() + " - " + dateOfFirstMonthEnrollment.getYear(), countNumberOfEnrollmentByInstructorIdAndDate(instructorId, dateOfFirstMonthEnrollment.getMonth().getValue(), dateOfFirstMonthEnrollment.getYear()));
                dateOfFirstMonthEnrollment = dateOfFirstMonthEnrollment.plusMonths(1);
            }
        } catch (Exception e) {
            graphData.put("Empty list", 0);
        }

        return graphData;
    }

    @Override
    public Double getWalletBalance(String email) {
        try {
            return instructorRepository.getWalletBalance(email);
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public InstructorResponse getInstructorById(UUID instructorId) {
        Instructor instructor = instructorRepository.getInstructorByUserId(instructorId).orElseThrow(() ->
                new ResourceNotFoundException("Instructor", "ID", instructorId.toString()));

        return InstructorResponse.convertEntityToResponse(instructor);
    }

    @Override
    public GetInstructorBalanceResponse getBalance(String email) {
        return GetInstructorBalanceResponse.builder().build().convertEntityToDto(getInstructorFromRepoByEmail(email));
    }

    @Override
    public String getEmailById(UUID userId) {
        return instructorRepository.getInstructorByUserId(userId).orElseThrow(() ->
                new ResourceNotFoundException("Instructor", "ID", userId.toString())).getUser().getEmail();
    }

    private Instructor getInstructorFromRepoByEmail(String email) {
        return instructorRepository.findByUser_Email(email).orElseThrow(() ->
                new ResourceNotFoundException("Instructor", "email", email));
    }

    @Override
    public Double totalQuarterlyRevenue(int quarter, String email) {
        try {
            return instructorRepository.quarterlyRevenue(
                    LocalDateTime.now().minusMonths(quarter),
                    email
            );
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public int countNumberOfEnrollmentByQuarterOfInstructor(int quarter, String email) {
        try {
            return instructorRepository.countNumberOfEnrollmentByQuarterOfInstructor(
                    LocalDateTime.now().minusMonths(quarter),
                    email
            );
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Map<String, Double> revenueOfEachCourse(String email) {
        Map<String, Double> graphData = new LinkedHashMap<>();
        courseService.getCoursesOfInstructor(email)
                .forEach(course -> graphData.put(
                                course.getCourseTitle(),
                                instructorRepository.quarterlyRevenueOfEachCourse(
                                        LocalDateTime.now().minusMonths(1),
                                        email,
                                        course.getCourseId()
                                )
                        )
                );
        return graphData;
    }

    @Override
    public Object composeAnalysis(String email) {
        List<Map<String, Object>> monthlyStatistics = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 12; i++) {
            int month = now.minusMonths(i).getMonthValue();
            int year = now.minusMonths(i).getYear();
            Map<String, Object> statistics = enrollmentService.getMonthlyStatistics(email, month, year);
            monthlyStatistics.add(statistics);
        }
        return monthlyStatistics.reversed();
    }

}
