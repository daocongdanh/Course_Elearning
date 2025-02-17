package group.project.cursusonlinecoursemanagement.user.controller;

import com.google.api.client.util.ArrayMap;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.AppExceptionHandler;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.StatusUpdateRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.*;
import group.project.cursusonlinecoursemanagement.user.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Tag(
        name = "CRUD REST APIs for Admin"
)
@Controller
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(
            summary = "Retrieve all students",
            description = "Fetch a list of all students currently in the system. This API can only be accessed by users with the ROLE_ADMIN role.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a list of all students")
    })
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/auth/getAllStudent")
    public ResponseEntity<?> getAllStudent() {
        List<GetStudentResponse> user = adminService.getAllStudent();
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Retrieve all instructors",
            description = "Fetches a list of all instructors from the system.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            tags = { "Instructor" }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of instructors"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, invalid or missing credentials"),
            @ApiResponse(responseCode = "403", description = "Forbidden, insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/auth/getAllInstructor")
    public ResponseEntity<?> getAllInstructor() {
        List<GetInstructorResponse> user = adminService.getAllInstructor();
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Update user status",
            description = "Update the status of a user based on the ID. This API can only be accessed by users with the ROLE_ADMIN role. If the status provided is null, it returns a 400 Bad Request error.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = {
                    @Parameter(name = "id", description = "ID of the user whose status is to be updated", required = true, example = "a2c8e8c2-122a-4f8e-8b58-5c6d8d0f0e2b"),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status updated successfully", content = @Content(schema = @Schema(type = "string", example = "Update status successfully!"))),
            @ApiResponse(responseCode = "400", description = "Status must not be null", content = @Content(schema = @Schema(type = "string", example = "Status must not be null.")))
    })
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/auth/updateStatusUser/{id}")
    public ResponseEntity<?> updateStatusUser(@PathVariable UUID id,@RequestParam (value = "newStatus") String newStatus) {
        adminService.updateStatusOfUser(id,newStatus);
        return ResponseEntity.ok("Update status successfully!");
    }

    @Operation(
            summary = "Retrieve statistical report for the current and last month",
            description = "This endpoint retrieves a statistical report with revenue, user count, course count, and enrollment count for the current month compared to the previous month. Requires admin role and Bearer authentication.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved statistical data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatisticalAdminResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error retrieving the statistical data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/auth/statisticalAdmin")
    public ResponseEntity<?> getStatisticalAdmin() {
        try {
            LocalDateTime now = LocalDateTime.now();

            StatisticalAdminResponse report = new StatisticalAdminResponse();

            //Revenue
            double lastMonthRevenue = adminService.sumOfRevenueByDate(now.minusMonths(1).getMonth().getValue(), now.getYear());
            double thisMonthRevenue = adminService.sumOfRevenueByDate(now.getMonth().getValue(), now.getYear());
            report.setThisMonthRevenue(thisMonthRevenue);
            report.setTotalRevenue(adminService.sumOfRevenue());
            if (lastMonthRevenue != 0) {
                report.setPercentageRevenueOfThisMonthAndLastMonth(((thisMonthRevenue - lastMonthRevenue) / lastMonthRevenue) * 100);
            } else if(thisMonthRevenue !=0) {
                report.setPercentageRevenueOfThisMonthAndLastMonth(100 * thisMonthRevenue);
            } else report.setPercentageRevenueOfThisMonthAndLastMonth(0);

            //User
            int lastMonthUsers = adminService.countAllUserByMonth(now.minusMonths(1).getMonth().getValue(), now.getYear());
            int thisMonthUsers = adminService.countAllUserByMonth(now.getMonth().getValue(), now.getYear());
            report.setThisMonthUsers(thisMonthUsers);
            report.setTotalUsers(adminService.countAllUser());
            if (lastMonthUsers != 0) {
                report.setPercentageUsersOfThisMonthAndLastMonth(((double) (thisMonthUsers - lastMonthUsers) / lastMonthUsers) * 100);
            } else if(thisMonthUsers !=0) {
                report.setPercentageUsersOfThisMonthAndLastMonth(100 * thisMonthUsers);
            } else report.setPercentageUsersOfThisMonthAndLastMonth(0);

            //Course
            int lastMonthCourses = adminService.countAllCourseByMonth(now.minusMonths(1).getMonth().getValue(), now.getYear());
            int thisMonthCourses = adminService.countAllCourseByMonth(now.getMonth().getValue(), now.getYear());
            report.setThisMonthCourses(thisMonthCourses);
            report.setTotalCourse(adminService.countAllCourse());
            if (lastMonthCourses != 0) {
                report.setPercentageCoursesOfThisMonthAndLastMonth(((double) (thisMonthCourses - lastMonthCourses) / lastMonthCourses) * 100);
            } else if(thisMonthCourses !=0) {
                report.setPercentageCoursesOfThisMonthAndLastMonth(100 * thisMonthCourses);
            } else report.setPercentageCoursesOfThisMonthAndLastMonth(0);


            //Enrollment
            int lastMonthEnrollments = adminService.countNumberOfEnrollmentByDate(now.minusMonths(1).getMonth().getValue(), now.getYear());
            int thisMonthEnrollments = adminService.countNumberOfEnrollmentByDate(now.getMonth().getValue(), now.getYear());
            report.setThisMonthEnrollments(thisMonthEnrollments);
            report.setTotalEnrollment(adminService.countNumberOfEnrollment());
            if (lastMonthEnrollments != 0) {
                report.setPercentageEnrollmentsOfThisMonthAndLastMonth(((double) (thisMonthEnrollments - lastMonthEnrollments) / lastMonthEnrollments) * 100);
            } else if(thisMonthCourses !=0) {
                report.setPercentageEnrollmentsOfThisMonthAndLastMonth(100 * thisMonthEnrollments);
            } else report.setPercentageEnrollmentsOfThisMonthAndLastMonth(0);

            return ResponseEntity.ok(report);
        }catch (Exception e){
            throw new AppExceptionHandler("Error retrieving all courses");
        }
    }

    @Operation(
            summary = "Get chart count of students and instructors for the last 5 months",
            description = "Retrieves the count of students and instructors for each of the last 5 months.",
            security = @SecurityRequirement(name = "Bear Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/auth/getChartCountUser")
    public ResponseEntity<?> getChartCountUser() {
        try {
            LocalDateTime now = LocalDateTime.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();

            // Tạo danh sách các tháng cần báo cáo
            List<Integer> months = new ArrayList<>(IntStream.range(0, 6)
                    .map(i -> currentMonth - i)
                    .map(month -> month <= 0 ? month + 12 : month) // Xử lý các tháng trước tháng 1
                    .boxed()
                    .toList());

            // Đảo ngược danh sách tháng
            Collections.reverse(months);


            // Tạo danh sách báo cáo cho từng tháng
            List<ChartStatisticalUser> report = new ArrayList<>();
            for (Integer monthNumber : months) {
                int year = currentYear - (monthNumber > currentMonth ? 1 : 0);
                String monthName = Month.of(monthNumber).getDisplayName(TextStyle.FULL, Locale.ENGLISH);;

                // Gọi phương thức dịch vụ để lấy số lượng học viên và giảng viên cho từng tháng
                int numberStudent = adminService.countStudentsByMonth(monthNumber, year);
                int numberInstructor = adminService.countInstructorsByMonth(monthNumber, year);

                // Thêm dữ liệu báo cáo vào danh sách
                report.add(ChartStatisticalUser.builder()
                        .month(monthName)
                        .numberStudent(numberStudent)
                        .numberInstructor(numberInstructor)
                        .build());
            }

            return ResponseEntity.ok(report);
        }catch (Exception e){
            throw new AppExceptionHandler("Error retrieving user by month");
        }
    }

    @Operation(
            summary = "Get Course and Enrollment Count for the Last 30 Days",
            description = "Fetches the count of courses and enrollments created in the last 30 days.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved counts"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/auth/getCourseAndEnrollmentCountBy30Date")
    public ResponseEntity<List<CourseEnrollmentCountByDate>> getCourseAndEnrollmentCountBy30Date() {
        List<CourseEnrollmentCountByDate> counts = adminService.getCourseAndEnrollmentCountBy30Date();
        return ResponseEntity.ok(counts);
    }


    @Operation(
            summary = "Export an excel file report",
            description = "Returns an excel report of revenue and enrollment. This API can only be accessed by users with the ROLE_ADMIN role.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Generate Successfully")
    })
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/auth/generate_report")
    public ResponseEntity<Resource> exportEnrollmentData(@RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
                                                         @RequestParam("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate) throws IOException {
        File reportFile = adminService.generateReport(startDate,endDate);
        Resource resource = new FileSystemResource(reportFile);
        if (reportFile.exists()) {
            ResponseEntity<Resource> response =  ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportFile.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
            Resource resources = new ClassPathResource("excelReport/"+reportFile.getName());
            if(resources.exists()){
                resources.getFile().delete();
            }
            return response;
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
