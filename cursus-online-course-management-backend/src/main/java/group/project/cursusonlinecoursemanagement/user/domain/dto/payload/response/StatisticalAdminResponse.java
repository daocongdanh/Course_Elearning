package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticalAdminResponse {
    double thisMonthRevenue;
    double totalRevenue;
    double percentageRevenueOfThisMonthAndLastMonth;

    int thisMonthCourses;
    int totalCourse;
    double percentageCoursesOfThisMonthAndLastMonth;

    int thisMonthEnrollments;
    int totalEnrollment;
    double percentageEnrollmentsOfThisMonthAndLastMonth;

    int thisMonthUsers;
    int totalUsers;
    double percentageUsersOfThisMonthAndLastMonth;
}
