package group.project.cursusonlinecoursemanagement.course.repository;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByCourse_CourseId(UUID courseId);
}