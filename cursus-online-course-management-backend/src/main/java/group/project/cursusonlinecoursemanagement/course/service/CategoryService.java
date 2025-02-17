package group.project.cursusonlinecoursemanagement.course.service;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.UpsertCategoryRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetCategoryResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetCategoryWithCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Category;

import java.util.List;

public interface CategoryService {
    List<GetCategoryWithCourseResponse> getAllCategoriesWithCourse();
    List<GetCategoryResponse> getAllCategories();
    GetCategoryResponse createCategory(UpsertCategoryRequest createUpsertCategoryRequest);
    GetCategoryWithCourseResponse getCategoryWithCourseById(Long id);
    GetCategoryWithCourseResponse updateCategory(Long id, UpsertCategoryRequest category);
    void deleteCategory(Long id);
    GetCategoryResponse getCategoryById(Long categoryId);
}
