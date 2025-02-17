package group.project.cursusonlinecoursemanagement.user.controller;


import group.project.cursusonlinecoursemanagement.shared.exception.handler.AppExceptionHandler;
import group.project.cursusonlinecoursemanagement.user.service.InstructorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Tag(
        name = "CRUD REST APIs for Instructor"
)
@Controller
@PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
@RequestMapping("/api/instructor/statistic")
public class InstructorController {
    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }


    @GetMapping("/chart/{instructorId}")
    public String getChart(@PathVariable UUID instructorId, Model model) {
        try {
            LocalDateTime now = LocalDateTime.now();

            double totalRevenue = instructorService.sumEnrollmentPriceByInstructorId(instructorId);
            double lastMonthRevenue = instructorService.sumEnrollmentPriceByInstructorIdAndDate(instructorId, now.minusMonths(1).getMonth().getValue(), now.getYear());
            int numberOfCourse = instructorService.countAllCourseOfInstructor(instructorId);
            int totalEnrollments = instructorService.countNumberOfEnrollmentOfInstructor(instructorId);
            Map<String, Integer> chartEnrollment;
            chartEnrollment = instructorService.eachMonthNumberOfEnrollmentOfInstructor(instructorId);

            Map<String, Double> chartRevenue;
            chartRevenue = instructorService.eachMonthRevenueOfInstructor(instructorId);
            model.addAttribute("totalRevenue", totalRevenue);
            model.addAttribute("lastMonthRevenue", lastMonthRevenue);
            model.addAttribute("totalEnrollments", totalEnrollments);
            model.addAttribute("numberOfCourse", numberOfCourse);
            model.addAttribute("chartEnrollment", chartEnrollment);
            model.addAttribute("chartRevenue", chartRevenue);
            return "instructor-charts";
        }catch (Exception e){
            throw new AppExceptionHandler(e.toString());
        }
    }

//    @GetMapping("/value/{instructorId}")
//    public Map<String, Integer> getValue(@PathVariable UUID instructorId) {
//        try {
//            Map<String, Integer> graphData = new TreeMap<>();
//            graphData = instructorService.eachMonthNumberOfEnrollmentOfInstructor(instructorId);
//            return graphData;
//        }catch (Exception e){
//            throw new AppExceptionHandler("Error retrieving all courses");
//        }
//    }
    @GetMapping("/sum_of_revenue/{instructorId}")
    @Operation(summary = "Sum of revenue", description = "Return the revenue of all time")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<?> getSumOfRevenue(@PathVariable UUID instructorId){
        return ResponseEntity.ok(instructorService.sumEnrollmentPriceByInstructorId(instructorId));
    }

    @GetMapping("/last_month_revenue/{instructorId}")
    @Operation(summary = "Last month revenue", description = "Return the revenue of last month")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<?> getLastMonthRevenue(@PathVariable UUID instructorId){
        LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.ok(instructorService.countNumberOfEnrollmentByInstructorIdAndDate(instructorId, now.minusMonths(1).getMonth().getValue(), now.getYear()));
    }

    @GetMapping("/number_of_courses/{instructorId}")
    @Operation(summary = "Number of course", description = "Return the number of course have been created")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<?> getNumberOfCourse(@PathVariable UUID instructorId){
        return ResponseEntity.ok(instructorService.countAllCourseOfInstructor(instructorId));
    }

    @GetMapping("/number_of_enrollment/{instructorId}")
    @Operation(summary = "Number of enrollment", description = "Return the number of course have been sold")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<?> getNumberOfEnrollment(@PathVariable  UUID instructorId){
        return ResponseEntity.ok(instructorService.countNumberOfEnrollmentOfInstructor(instructorId));
    }

    @GetMapping("/auth/quarterly_revenue/{quarter}")
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @Operation(summary = "Quarterly Revenue", description = "Return the revenue of month, quarter or year of instructor")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<?> getQuarterlyRevenueOfInstructor(@PathVariable int quarter){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok((instructorService.totalQuarterlyRevenue(quarter, email)));
    }

    @GetMapping("/auth/quarterly_number_of_enrollment/{quarter}")
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @Operation(summary = "Number of enrollment of instructor", description = "Return the number of course have been sold")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<?> getNumberOfEnrollmentOfInstructor(@PathVariable int quarter){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(instructorService.countNumberOfEnrollmentByQuarterOfInstructor(quarter, email));
    }

    @GetMapping("/auth/revenue_of_each_course")
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @Operation(summary = "Revenue of each course of instructor", description = "Return the revenue of each course have been sold in last month")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<?> revenueOfEachCourse(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Map<String, Double> chartRevenue;
        chartRevenue = instructorService.revenueOfEachCourse(email);
        return ResponseEntity.ok(chartRevenue);
    }

    @GetMapping("/auth/compose_analysis")
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @Operation(summary = "Compose analysis")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<?> composeAnalysis(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(instructorService.composeAnalysis(email));
    }

    @GetMapping("/auth/wallet_balance")
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @Operation(summary = "Get Wallet Balance")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<?> walletBalance(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(instructorService.getWalletBalance(email));
    }

    @GetMapping("/auth/get_balance")
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @Operation(summary = "Get balance of instructor")
    public ResponseEntity<?> getBalance(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(instructorService.getBalance(email));
    }

    @Operation(
            summary = "Get Instructor Analysis",
            description = "Retrieve the analysis data for a specific instructor using their userId.",
            parameters = {
                    @Parameter(name = "userId", description = "UUID of the instructor", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Analysis data retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = @SecurityRequirement(name = "Bear Authentication")
    )
    @GetMapping("/analysis")
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    public ResponseEntity<?> getInstructorAnalysis(@RequestParam UUID userId) {
        String email = instructorService.getEmailById(userId);
        return ResponseEntity.ok(instructorService.composeAnalysis(email));
    }

    @GetMapping("/quarterlyEnrollment")
    @Operation(
            summary = "Get Number of Enrollments by Quarter",
            description = "Retrieve the number of enrollments for an instructor for a specific quarter. The email is fetched using the provided userId.",
            parameters = {
                    @Parameter(name = "userId", description = "UUID of the instructor", required = true),
                    @Parameter(name = "quarter", description = "Quarter of the year", required = true)
            },
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    public ResponseEntity<?> getNumberOfEnrollmentByQuarter(
            @RequestParam UUID userId,
            @RequestParam int quarter) {
        String email = instructorService.getEmailById(userId);
        return ResponseEntity.ok(instructorService.countNumberOfEnrollmentByQuarterOfInstructor(quarter, email));
    }

    @GetMapping("/quarterlyRevenue")
    @Operation(
            summary = "Get Quarterly Revenue",
            description = "Retrieve the total revenue for an instructor for a specific quarter. The email is fetched using the provided userId.",
            parameters = {
                    @Parameter(name = "userId", description = "UUID of the instructor", required = true),
                    @Parameter(name = "quarter", description = "Quarter of the year", required = true)
            },
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    public ResponseEntity<?> getQuarterlyRevenue(
            @RequestParam UUID userId,
            @RequestParam int quarter) {
        String email = instructorService.getEmailById(userId);
        return ResponseEntity.ok(instructorService.totalQuarterlyRevenue(quarter, email));
    }

    @GetMapping("/revenueOfEachCourse")
    @Operation(
            summary = "Get Revenue of Each Course",
            description = "Retrieve the revenue generated from each course for an instructor. The email is fetched using the provided userId.",
            parameters = {
                    @Parameter(name = "userId", description = "UUID of the instructor", required = true)
            },
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    public ResponseEntity<?> getRevenueOfEachCourse(@RequestParam UUID userId) {
        String email = instructorService.getEmailById(userId);
        Map<String, Double> chartRevenue = instructorService.revenueOfEachCourse(email);
        return ResponseEntity.ok(chartRevenue);
    }

}
