package group.project.cursusonlinecoursemanagement.course.domain.dto;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Course;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Lesson;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonDto implements Serializable {

    private static ModelMapper modelMapper = new ModelMapper();

    private UUID lessonId;

    private String lessonTitle;

    private String lessonDescription;
    
    private Integer position;
    
    private Boolean isFree;
    
    private Boolean isPublished;

    private String videoUrl;

    private Course course;

//    private List<UserLessonProgress> lessonProgressList;

    public static LessonDto convertEntityToResponse(Lesson category) {
        return modelMapper.map(category, LessonDto.class);
    }
}
