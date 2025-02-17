package group.project.cursusonlinecoursemanagement.course.service;


import com.itextpdf.text.DocumentException;
import com.paypal.api.payments.Payment;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.EnrollmentRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.EnrollmentResponse;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Enrollment;
import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EnrollmentService {
    List<EnrollmentResponse> getAllEnrollment();

    List<EnrollmentResponse> getFiveNewEnrollment();

    List<EnrollmentResponse> getAllEnrollmentByUserId(Long user_id);

    EnrollmentResponse getEnrollmentById(Long id);

    EnrollmentResponse createEnrollment(EnrollmentRequest enrollmentRequest);

//    EnrollmentResponse updateEnrollment(Long id, EnrollmentRequest enrollmentRequest);

    void deleteEnrollment(Long id);

    EnrollmentResponse getEnrollmentOfUser(UUID courseId, String email);

    Map<String, Object> getMonthlyStatistics(String email, int month, int year);

    EnrollmentResponse createUserEnrollment(String email, UUID courseId, Double total);

    InputStreamResource generateCertificate(Long enrollmentId) throws DocumentException, IOException;

    List<Enrollment> getEnrollmentsOfUser(String email);

    InputStreamResource generateCertificateOfCourse(UUID courseId, String email) throws DocumentException, IOException;
}
