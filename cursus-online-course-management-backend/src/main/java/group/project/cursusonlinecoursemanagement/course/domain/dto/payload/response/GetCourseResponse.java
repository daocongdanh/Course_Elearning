package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response;


import group.project.cursusonlinecoursemanagement.course.domain.entity.Category;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Course;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetCourseResponse {

    private UUID courseId;
    private String courseTitle;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    //    private List<Enrollment> enrollments;
    private boolean courseStatus;
    private String thumbnailUrl;
    private String keyThumbnail;
    private BigDecimal price;
    private float discount;
    private List<LessonResponse> lessons;
    private List<GetCategoryResponse> categories;
    private UUID userId;


    public static GetCourseResponse convertEntityToResponseBasis(Course course) {
        GetCourseResponse response = new GetCourseResponse();

        response.setCourseId(course.getCourseId());
        response.setCourseTitle(course.getCourseTitle());
        response.setDescription(course.getDescription());
        response.setCreatedDate(course.getCreatedDate());
        response.setModifiedDate(course.getModifiedDate());
//        response.setEnrollments(course.getEnrollments());
        response.setCourseStatus(course.isCourseStatus());
        response.setKeyThumbnail(course.getKeyThumbnail());
        response.setPrice(course.getPrice());
        response.setDiscount(course.getDiscount());
        response.setLessons(course.getLessons().stream().map(LessonResponse::convertEntityToResponse).sorted(Comparator.comparingInt(LessonResponse::getPosition)).collect(Collectors.toList()));
        List<GetCategoryResponse> categoryResponses = new ArrayList<>();
        for (Category category : course.getCategories()) {
            categoryResponses.add(new GetCategoryResponse(category.getCategoryId(), category.getCategoryName(), category.getCategoryIcon()));
        }
        response.setCategories(categoryResponses);
        response.setThumbnailUrl(course.getKeyThumbnail());
        response.setUserId(course.getUser().getUserId());
        return response;
    }
}
