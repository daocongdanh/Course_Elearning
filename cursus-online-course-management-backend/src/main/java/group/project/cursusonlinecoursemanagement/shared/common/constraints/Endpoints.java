package group.project.cursusonlinecoursemanagement.shared.common.constraints;

public class Endpoints {

    public static final String[] PRIVATE_ENDPOINT = {
            "/api/category/auth/.*",
            "/api/courses/auth/.*",
            "/api/lessons/auth/.*",
            "/api/user/auth/.*",
            "/api/amazon/auth/.*",
            "/api/storage/auth/.*",
            "/api/enrollment/auth/.*",
            "/api/cloudinary/auth/.*",
            "/api/admin/auth/.*",
            "/api/auth/introspect",
            "/api/paymentRequest/.*",
            "/api/lessonProgress/auth/.*",
            "/api/enrollment/auth/.*",
            "/api/instructor/statistic/.*",
            "/api/payment/.*",
            "/api/feedbacks/auth/.*",
            "/api/bmi/.*",
            "/api/bmi",
            "/api/bmi-history",
            "/api/bmi-history/.*"
    };

    public static final String[] ALLOWED_ORIGINS = {
            "http://localhost:3000",
            "http://localhost:8080",
            "https://cursusmate.netlify.app",
            "https://www.cursusmate.online",
            "https://cursusmate.online",
            "https://guardianshield.uk"
    };
}
