package group.project.cursusonlinecoursemanagement.course.service.impl;

import com.google.api.client.util.ArrayMap;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.EnrollmentRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.EnrollmentResponse;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Course;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Enrollment;
import group.project.cursusonlinecoursemanagement.course.repository.CourseRepository;
import group.project.cursusonlinecoursemanagement.course.repository.EnrollmentRepository;
import group.project.cursusonlinecoursemanagement.course.service.EnrollmentService;
import group.project.cursusonlinecoursemanagement.course.service.UserLessonProgressService;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.AppExceptionHandler;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Instructor;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import group.project.cursusonlinecoursemanagement.user.repository.InstructorRepository;
import group.project.cursusonlinecoursemanagement.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final UserLessonProgressService userLessonProgressService;


    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, UserRepository userRepository, CourseRepository courseRepository, InstructorRepository instructorRepository, @Lazy UserLessonProgressService userLessonProgressService) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
        this.userLessonProgressService = userLessonProgressService;
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollment() {
        try {
            List<Enrollment> enrollments = enrollmentRepository.findAll();
            return enrollments.stream()
                    .map(EnrollmentResponse::convertEntityToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new AppExceptionHandler("Error retrieving all enrollments");
        }
    }

    @Override
    public List<EnrollmentResponse> getFiveNewEnrollment() {
        try {
            Pageable pageable = PageRequest.of(0, 5, Sort.by("enrollmentDate").descending());
            List<Enrollment> enrollments = enrollmentRepository.findAll(pageable).getContent();
            return enrollments.stream()
                    .map(EnrollmentResponse::convertEntityToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new AppExceptionHandler("Error retrieving 5 enrollments");
        }
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollmentByUserId(Long user_id) {
        try {
            List<Enrollment> enrollments = enrollmentRepository.findEnrollmentByUserId(user_id);
            return enrollments.stream()
                    .map(EnrollmentResponse::convertEntityToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new AppExceptionHandler("Error retrieving enrollments with user id: " + user_id);
        }
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Enrollment", "ID", id.toString()));
        return EnrollmentResponse.convertEntityToResponse(enrollment);
    }

    @Override
    public EnrollmentResponse createEnrollment(EnrollmentRequest enrollmentRequest) {
        try {
            Enrollment enrollment = new Enrollment();
            enrollment.setEnrollmentDate(LocalDateTime.now());
            User enrollmentUser = userRepository.findById(enrollmentRequest.getUserId()).orElseThrow(() ->
                    new ResourceNotFoundException("User", "ID", enrollmentRequest.getUserId().toString()));
            enrollment.setUser(enrollmentUser);
            Course enrollmentCourse = courseRepository.findById(enrollmentRequest.getCourseId()).orElseThrow(() ->
                    new ResourceNotFoundException("Course", "ID", enrollmentRequest.getCourseId().toString()));
            enrollment.setCourse(enrollmentCourse);
            enrollment.setEnrollmentPrice(enrollmentCourse.getPrice().doubleValue());
            Instructor courseInstructor = instructorRepository.getInstructorByUserId(enrollmentCourse.getUser().getUserId()).orElseThrow(() ->
                    new ResourceNotFoundException("User", "ID", enrollmentCourse.getUser().getUserId().toString()));
            courseInstructor.setWalletBalance(enrollmentCourse.getPrice().doubleValue());
            instructorRepository.save(courseInstructor);
            enrollmentRepository.save(enrollment);
            return EnrollmentResponse.convertEntityToResponse(enrollment);
        } catch (Exception e) {
            throw new AppExceptionHandler("Error create new enrollment");
        }
    }

    //Không cho update vì cơ bản enrollment không có update
//    @Override
//    public EnrollmentResponse updateEnrollment(Long id, EnrollmentRequest enrollmentRequest) {
//        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(() ->
//                new ResourceNotFoundException("Enrollment", "ID", id.toString()));
//        try {
//            enrollment.setEnrollmentDate(enrollmentRequest.getEnrollmentDate());
//            User enrollmentUser = userRepository.findById(enrollmentRequest.getUserId()).orElseThrow(() ->
//                    new ResourceNotFoundException("User", "ID", enrollmentRequest.getUserId().toString()));
//            enrollment.setUser(enrollmentUser);
//            Course  enrollmentCourse = courseRepository.findById(enrollmentRequest.getCourseId()).orElseThrow(() ->
//                    new ResourceNotFoundException("Course", "ID", enrollmentRequest.getCourseId().toString()));
//            enrollment.setCourse(enrollmentCourse);
//            enrollmentRepository.save(enrollment);
//            return modelMapper.map(enrollment, EnrollmentResponse.class);
//        } catch (Exception e) {
//            throw new AppExceptionHandler("Error update enrollment");
//        }
//    }

    @Override
    public void deleteEnrollment(Long id) {
        try {
            enrollmentRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppExceptionHandler("Error delete enrollment");
        }
    }

    @Override
    public EnrollmentResponse getEnrollmentOfUser(UUID courseId, String email) {
        EnrollmentResponse enrollmentResponse = EnrollmentResponse.convertEntityToResponse(getEnrollmentByCourseIdAndUser(courseId, email));
        enrollmentResponse.setLessonProgressList(userLessonProgressService.getLessonProgress(courseId, email));
        return enrollmentResponse;
    }

    private Enrollment getEnrollmentByCourseIdAndUser(UUID courseId, String email) {
        return enrollmentRepository.findByCourse_CourseIdAndUser_Email(courseId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "Course ID", courseId.toString()));
    }
    public boolean isExistedEnrollmentByCourseIdAndUser(UUID courseId, String email){
        return enrollmentRepository.existsByCourse_CourseIdAndUser_Email(courseId, email);
    }
    @Override
    public Map<String, Object> getMonthlyStatistics(String email, int month, int year) {
        Map<String, Object> statistics = new ArrayMap<>();

        Specification<Enrollment> specification = getEnrollmentSpecification(email, month, year);

        List<Enrollment> enrollments = enrollmentRepository.findAll(specification);

        double totalRevenue = enrollments.stream().mapToDouble(Enrollment::getEnrollmentPrice).sum();
        long numberOfEnrollments = enrollments.size();

        statistics.put("month-year", month + "-" + year);
        statistics.put("totalRevenue", totalRevenue);
        statistics.put("numberOfEnrollments", numberOfEnrollments);

        return statistics;
    }

    @Override
    public EnrollmentResponse createUserEnrollment(String email, UUID courseId, Double total) {
        try {
            Enrollment enrollment = initializeEnrollment(email, courseId, total);
            updateInstructorWallet(enrollment.getCourse().getUser().getUserId(), total);
            return saveEnrollment(enrollment);
        } catch (Exception e) {
            throw new AppExceptionHandler("Error creating user enrollment");
        }
    }

    @Override
    public InputStreamResource generateCertificate(Long enrollmentId) throws DocumentException, IOException {
        EnrollmentResponse enrollmentResponse = getEnrollmentById(enrollmentId);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A5, 36, 36, 20, 36);

        PdfWriter writer = PdfWriter.getInstance(document, out);

        // Load custom font
//        BaseFont customFont = BaseFont.createFont(font, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//        Font fontTitle = new Font(customFont, 40, Font.BOLD, BaseColor.BLUE.darker().darker());
//        Font fontContent = new Font(customFont, 29, Font.NORMAL, BaseColor.BLACK);
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 40, BaseColor.BLUE.darker().darker());
        Font fontContent = FontFactory.getFont(FontFactory.HELVETICA, 29, BaseColor.BLACK);

        document.open();

         //Add background image
//        Image background = Image.getInstance(background);
//        background.setAbsolutePosition(0, 0);
//        background.scaleToFit(PageSize.A5.getWidth(), PageSize.A5.getHeight());
//        document.add(background);

        // Add title
        Paragraph title = new Paragraph("Certificate of Completion", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(200);
        document.add(title);

        // Add recipient name
        Paragraph recipient = new Paragraph("Presented to: " + enrollmentResponse.getUserName(), fontContent);
        recipient.setAlignment(Element.ALIGN_CENTER);
        recipient.setSpacingBefore(50);
        document.add(recipient);

        // Add course name
        Paragraph course = new Paragraph("For completing the course: " + enrollmentResponse.getCourseName(), fontContent);
        course.setAlignment(Element.ALIGN_CENTER);
        course.setSpacingBefore(20);
        document.add(course);

        // Add date
        LocalDateTime enrollDate = enrollmentResponse.getEnrollmentDate();
        Paragraph date = new Paragraph("Date: " +enrollDate.getDayOfMonth() + "/" + enrollDate.getMonth().getValue() +"/" + enrollDate.getYear(), fontContent);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingBefore(10);
        document.add(date);

        // Add signature image
//        Image signature = Image.getInstance(sign);
//        signature.setAbsolutePosition(275, 42);
//        signature.scaleToFit(185, 75);
//        document.add(signature);

        document.close();
        PdfReader reader = new PdfReader(new ByteArrayInputStream(out.toByteArray()));
        Rectangle halfPage = new Rectangle(0, PageSize.A5.getHeight()/(float) 2.6, PageSize.A5.getWidth(), PageSize.A5.getHeight());
        Document croppedDocument = new Document(halfPage);

        ByteArrayOutputStream croppedOut = new ByteArrayOutputStream();
        PdfWriter croppedWriter = PdfWriter.getInstance(croppedDocument, croppedOut);

        croppedDocument.open();
        PdfContentByte cb = croppedWriter.getDirectContent();
        PdfImportedPage page = croppedWriter.getImportedPage(reader, 1);
        cb.addTemplate(page, 0, PageSize.A5.getHeight() / 2.5);
        croppedDocument.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(croppedOut.toByteArray());
        return new InputStreamResource(bis);
    }

    @Override
    public List<Enrollment> getEnrollmentsOfUser(String email) {
        return enrollmentRepository.findByUser_Email(email);
    }

    @Override
    public InputStreamResource generateCertificateOfCourse(UUID courseId, String email) throws DocumentException, IOException {
        Enrollment enrollment = enrollmentRepository.findByCourse_CourseIdAndUser_Email(courseId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "Course ID", courseId.toString() + email));
        if (userLessonProgressService.getProgressCount(courseId) < 100) {
            throw new RuntimeException("Course not completed yet");
        }
        return generateCertificate(enrollment.getEnrollmentId());
    }

    private Enrollment initializeEnrollment(String email, UUID courseId, Double total) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(getUserByEmail(email));
        enrollment.setCourse(getCourseById(courseId));
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setEnrollmentPrice(total);
        return enrollment;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() ->
                new ResourceNotFoundException("User", "Email", email));
    }

    private Course getCourseById(UUID courseId) {
        return courseRepository.findById(courseId).orElseThrow(() ->
                new ResourceNotFoundException("Course", "ID", courseId.toString()));
    }

    private void updateInstructorWallet(UUID instructorId, Double total) {
        Instructor instructor = instructorRepository.findByUser_UserId(instructorId).orElseThrow(() -> new ResourceNotFoundException("Instructor", "instructorId", instructorId.toString()));
        instructor.setWalletBalance(instructor.getWalletBalance() + (total*0.9));
        instructorRepository.save(instructor);
    }

    private EnrollmentResponse saveEnrollment(Enrollment enrollment) {
        return EnrollmentResponse.convertEntityToResponse(enrollmentRepository.save(enrollment));
    }


    private static Specification<Enrollment> getEnrollmentSpecification(String email, int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("course").get("user").get("email"), email),
                criteriaBuilder.between(root.get("enrollmentDate"), start, end)
        );
    }
}
