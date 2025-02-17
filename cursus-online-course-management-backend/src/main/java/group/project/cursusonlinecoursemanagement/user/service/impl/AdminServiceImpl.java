package group.project.cursusonlinecoursemanagement.user.service.impl;

import com.google.api.client.util.ArrayMap;
import group.project.cursusonlinecoursemanagement.course.repository.CourseRepository;
import group.project.cursusonlinecoursemanagement.course.repository.EnrollmentRepository;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.CourseEnrollmentCountByDate;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.GetInstructorResponse;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.GetStudentResponse;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Role;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import group.project.cursusonlinecoursemanagement.user.repository.UserRepository;
import group.project.cursusonlinecoursemanagement.user.service.AdminService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AdminServiceImpl implements AdminService {
    //Admin ServiceIMPL
    private final UserRepository userRepository;
    private static final double percentRose = 0.1;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AdminServiceImpl(UserRepository userRepository, CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public List<GetStudentResponse> getAllStudent() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .filter(user -> user.getRoles().contains(Role.ROLE_STUDENT))
                .map(GetStudentResponse::convertUserToUserWithEnrollment)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetInstructorResponse> getAllInstructor() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .filter(user -> user.getRoles().contains(Role.ROLE_INSTRUCTOR))
                .map(GetInstructorResponse::convertEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatusOfUser(UUID id, String newStatus) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", id.toString()));
        try {
            Status status = Status.valueOf(newStatus.toUpperCase());
            user.setStatus(status);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + newStatus, e);
        } catch (Exception e) {
            throw new RuntimeException("Error update status: " + e.getMessage(), e);
        }
    }

    @Override
    public Double sumOfRevenue() {
        try {
            return userRepository.sumALLEnrollmentPrice() * percentRose;
        } catch (Exception e) {
            return 0.0;
        }
    }


    @Override
    public Double sumOfRevenueByDate(int month, int year) {
        try {
            return userRepository.sumEnrollmentPriceByDate(month, year) * percentRose;
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public int countNumberOfEnrollment() {
        try {
            return userRepository.countNumberOfEnrollment();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int countNumberOfEnrollmentByDate(int month, int year) {
        try {
            return userRepository.countNumberOfEnrollmentByDate(month, year);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int countStudentsByMonth(int month, int year) {
        try {
            return userRepository.countStudentsByMonth(month, year);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int countInstructorsByMonth(int month, int year) {
        try {
            return userRepository.countInstructorsByMonth(month, year);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int countAllCourse() {
        try {
            return userRepository.countAllCourse();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int countAllCourseByMonth(int month, int year) {
        try {
            return userRepository.countNumberOfCourseByMonth(month, year);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Map<String, Double> eachMonthRevenue() {
        Map<String, Double> graphData = new ArrayMap<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dateOfFirstUserRegister = userRepository.createDateOfFirstUserRegister();
            while (dateOfFirstUserRegister.isBefore(now)) {
                graphData.put(dateOfFirstUserRegister.getMonth() + " - " + dateOfFirstUserRegister.getYear(), sumOfRevenueByDate(dateOfFirstUserRegister.getMonth().getValue(), dateOfFirstUserRegister.getYear()));
                dateOfFirstUserRegister = dateOfFirstUserRegister.plusMonths(1);
            }
        } catch (Exception e) {
            graphData.put("Empty list", 0.0);
        }

        return graphData;
    }

    @Override
    public Map<String, Integer> eachMonthNumberOfEnrollment() {
        Map<String, Integer> graphData = new ArrayMap<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dateOfFirstUserRegister = userRepository.createDateOfFirstUserRegister();
            while (dateOfFirstUserRegister.isBefore(now)) {
                graphData.put(dateOfFirstUserRegister.getMonth() + " - " + dateOfFirstUserRegister.getYear(), countNumberOfEnrollmentByDate(dateOfFirstUserRegister.getMonth().getValue(), dateOfFirstUserRegister.getYear()));
                dateOfFirstUserRegister = dateOfFirstUserRegister.plusMonths(1);
            }
        } catch (Exception e) {
            graphData.put("Empty list", 0);
        }
        return graphData;
    }

    @Override
    public int countAllInstructor() {
        try {
            return userRepository.countAllInstructor();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int countAllUser() {
        try {
            return userRepository.countAllUser();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int countAllUserByMonth(int month, int year) {
        try {
            return userRepository.countAllUserByMonth(month, year);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public LocalDateTime createDateOfFirstUserRegister() {
        return null;
    }

    @Override
    public Map<String, Integer> eachMonthNumberOfUser() {
        Map<String, Integer> graphData = new ArrayMap<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dateOfFirstUserRegister = userRepository.createDateOfFirstUserRegister();
            while (dateOfFirstUserRegister.isBefore(now)) {
                graphData.put(dateOfFirstUserRegister.getMonth() + " - " + dateOfFirstUserRegister.getYear(), countAllUserByMonth(dateOfFirstUserRegister.getMonth().getValue(), dateOfFirstUserRegister.getYear()));
                dateOfFirstUserRegister = dateOfFirstUserRegister.plusMonths(1);
            }

        } catch (Exception e) {
            graphData.put("Empty list", 0);
        }
        return graphData;
    }

    @Override
    public List<CourseEnrollmentCountByDate> getCourseAndEnrollmentCountBy30Date() {
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(30);
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = LocalDate.now();

        // Generate all dates in the last 30 days
        List<LocalDate> allDates = startDate.datesUntil(endDate.plusDays(1))
                .toList();

        // Fetch course counts
        List<CourseEnrollmentCountByDate> courseCounts = courseRepository.findCourseCountByDate(startDateTime);

        // Fetch enrollment counts
        List<CourseEnrollmentCountByDate> enrollmentCounts = enrollmentRepository.findEnrollmentCountByDate(startDateTime);

        // Combine both lists and group by date
        Map<LocalDate, CourseEnrollmentCountByDate> combinedData = Stream.concat(courseCounts.stream(), enrollmentCounts.stream())
                .collect(Collectors.groupingBy(
                        entry -> entry.getDate().toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    long courseCount = list.stream().mapToLong(CourseEnrollmentCountByDate::getCourseCount).sum();
                                    long enrollmentCount = list.stream().mapToLong(CourseEnrollmentCountByDate::getEnrollmentCount).sum();
                                    return new CourseEnrollmentCountByDate(
                                            list.get(0).getDate().toLocalDate().atStartOfDay(),
                                            courseCount,
                                            enrollmentCount
                                    );
                                }
                        )
                ));

        // Ensure all dates are present with default 0 values
        List<CourseEnrollmentCountByDate> resultList = allDates.stream()
                .map(date -> combinedData.getOrDefault(date, new CourseEnrollmentCountByDate(date.atStartOfDay(), 0, 0)))
                .collect(Collectors.toList());

        // Optional: Format date for output (if needed for debugging)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        resultList.forEach(entry -> {
            String formattedDate = entry.getDate().toLocalDate().format(formatter);
            System.out.println("Date: " + formattedDate + ", Course Count: " + entry.getCourseCount() + ", Enrollment Count: " + entry.getEnrollmentCount());
        });

        return resultList;
    }

    public List<Map<String, Object>> sumOfRevenueByDateRange(Date startDate, Date endDate) {
        LocalDateTime startDateString= startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endDateString= endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return userRepository.sumEnrollmentPriceByDateRange(startDateString, endDateString);
    }
    @Override
    public File generateReport(Date startDate,Date endDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Resource resource = new ClassPathResource("templates/FinancialReport.xlsx");
        try (InputStream templateInputStream = resource.getInputStream();
             Workbook workbook = WorkbookFactory.create(templateInputStream)) {

            // Write Monthly Enrollments
            List<Map<String, Object>> enrollmentData = sumOfRevenueByDateRange(startDate,endDate);
            writeDataToExcel(workbook, enrollmentData);
            ClassPathResource classResource = new ClassPathResource("excelReport/Report"+authentication.getName()+".xlsx");
            if (!classResource.exists()){
                File newFile = new File(Objects.requireNonNull(classResource.getFilename()));
                newFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                workbook.write(fileOutputStream);
                fileOutputStream.close();
                workbook.close();
                return newFile;
            }
            // Save the modified Excel file to the specified output directory
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public void writeDataToExcel(Workbook workbook, List<Map<String, Object>> data) throws IOException {
        Sheet sheet = workbook.getSheet("RevenueByDates");

        // Create header row
        Row headerRow = sheet.createRow(0);
        Cell dateCell = headerRow.createCell(0);
        dateCell.setCellValue("Date");
        Cell sumCell = headerRow.createCell(1);
        sumCell.setCellValue("Sum");

        // Write data to Excel
        int rowIndex = 1;
        for (Map<String, Object> map : data) {
            Row row = sheet.createRow(rowIndex);
            Cell dateCellValue = row.createCell(0);
            dateCellValue.setCellValue(map.get("date").toString()); // assuming only one key-value pair in each map
            Cell sumCellValue = row.createCell(1);
            sumCellValue.setCellValue((Double) map.get("sum")); // assuming only one key-value pair in each map
            rowIndex++;
        }

        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

}

