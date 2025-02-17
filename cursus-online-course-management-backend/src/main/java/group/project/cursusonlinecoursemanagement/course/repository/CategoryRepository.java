package group.project.cursusonlinecoursemanagement.course.repository;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByCategoryName(String categoryName);

    boolean existsByCategoryIcon(String categoryIcon);

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.categoryName = :categoryName AND c.categoryId != :categoryId")
    boolean existsByCategoryNameWithCategory(String categoryName, Long categoryId);

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.categoryIcon = :categoryIcon AND c.categoryId != :categoryId")
    boolean existsByCategoryIconWithIcon(String categoryIcon, Long categoryId);
}
