package group.project.cursusonlinecoursemanagement.course.domain.dto;


import group.project.cursusonlinecoursemanagement.course.domain.entity.*;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto implements Serializable {

    private static ModelMapper modelMapper = new ModelMapper();

    private UUID courseId;
    private String courseTitle;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private boolean courseStatus;
    private List<Enrollment> enrollments;
    private String thumbnailUrl;
    private BigDecimal price;
    private float discount;
    private Map<UUID, Lesson> lessons;
    private List<Category> categories;
    private User user;

    public static CourseDto convertEntityToDto(Course course) {
        return modelMapper.map(course, CourseDto.class);
    }

    public static Course convertDtoToEntity(CourseDto courseDto) {
        return modelMapper.map(courseDto, Course.class);
    }

    public static List<CourseDto> convertEntitiesToDtos(List<Course> courses) {
        return courses.stream()
                .map(CourseDto::convertEntityToDto)
                .collect(Collectors.toList());
    }
}