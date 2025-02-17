package group.project.cursusonlinecoursemanagement.course.service.impl;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.UpsertCategoryRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetCategoryResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetCategoryWithCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Category;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Course;
import group.project.cursusonlinecoursemanagement.course.repository.CategoryRepository;
import group.project.cursusonlinecoursemanagement.course.repository.CourseRepository;
import group.project.cursusonlinecoursemanagement.course.service.CategoryService;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.AppExceptionHandler;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.DuplicateFieldException;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categorieRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categorieRepository, ModelMapper modelMapper, CourseRepository courseRepository) {
        this.categorieRepository = categorieRepository;
        this.modelMapper = modelMapper;
        this.courseRepository = courseRepository;
    }

    @Override
    public List<GetCategoryWithCourseResponse> getAllCategoriesWithCourse() {
        try {
            //Lấy luôn cả list course id đang sài category đó
            List<Category> categories = categorieRepository.findAll();
            return categories.stream()
                    .map(category -> {
                        //Lấy ra danh sách course của category
                        List<Course> courses = category.getCourses();
                        //Chuyển đổi từ entity sang response
                        List<GetCourseResponse> listCourse = courses.stream()
                                .map(GetCourseResponse::convertEntityToResponseBasis)
                                .collect(Collectors.toList());
                        //Trả về response
                        return GetCategoryWithCourseResponse.builder()
                                .categoryId(category.getCategoryId())
                                .categoryName(category.getCategoryName())
                                .categoryIcon(category.getCategoryIcon())
                                .courses(listCourse)
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new AppExceptionHandler("Error retrieving all categories with courses");
        }
    }

    @Override
    public List<GetCategoryResponse> getAllCategories() {
        try {
            //Chỉ lấy category thôi
            List<Category> categories = categorieRepository.findAll();
            return categories.stream()
                    .map(GetCategoryResponse::convertEntityToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new AppExceptionHandler("Error retrieving all categories");
        }
    }

    @Override
    public GetCategoryResponse createCategory(UpsertCategoryRequest createUpsertCategoryRequest) {
        String categoryName = createUpsertCategoryRequest.getCategoryName();
        if (existsByCategoryName(categoryName)) {
            throw new DuplicateFieldException("Category with name '" + categoryName + "' already exists");
        }
        if (existsByCategoryIcon(createUpsertCategoryRequest.getCategoryIcon())) {
            throw new DuplicateFieldException("Category with icon '" + createUpsertCategoryRequest.getCategoryIcon() + "' already exists");
        }
        try {
            //Chuyển đổi thành category entity
            Category category = modelMapper.map(createUpsertCategoryRequest, Category.class);
            //Lưu vào database
            categorieRepository.save(category);
            //Trả về true thông tin nếu thành công
            return GetCategoryResponse.convertEntityToResponse(category);
        } catch (Exception e) {
            throw new AppExceptionHandler("Error retrieving all courses");
        }
    }

    @Override
    public GetCategoryWithCourseResponse getCategoryWithCourseById(Long id) {
        //Tìm category nếu có thì sẽ trả về data, không thì báo lỗi runtime
        Category category = categorieRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        return GetCategoryWithCourseResponse.convertEntityToResponse(category);
    }

    @Override
    public GetCategoryWithCourseResponse updateCategory(Long id, UpsertCategoryRequest newCategory) {
        //Tìm category nếu có thì sẽ trả về data, không thì báo lỗi runtime
        Category category = categorieRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        String categoryName = newCategory.getCategoryName();
        if (categorieRepository.existsByCategoryNameWithCategory(categoryName, id)) {
            throw new DuplicateFieldException("Category with name '" + categoryName + "' already exists");
        }
        if (categorieRepository.existsByCategoryIconWithIcon(newCategory.getCategoryIcon(), id)) {
            throw new DuplicateFieldException("Category with icon '" + newCategory.getCategoryIcon() + "' already exists");
        }
        //Update name or icon mới cho category
        category.setCategoryIcon(newCategory.getCategoryIcon());
        category.setCategoryName(newCategory.getCategoryName());
        //Lưu lại trong data bse
        categorieRepository.save(category);
        return GetCategoryWithCourseResponse.convertEntityToResponse(category);
    }

    @Override
    public void deleteCategory(Long id) {
        //Tìm category nếu có thì sẽ trả về data, không thì báo lỗi runtime
        Category categoryDelete = categorieRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        //Lấy tất cả course
        List<Course> courses = courseRepository.findAll();
        //Nếu course không empty thì mới check category trong course
        if (!courses.isEmpty()) {
            //Kiểm tra có course nào còn đang thuộc loại category đang muốn xóa
            for (Course course : courses) {
                List<Category> categories = course.getCategories();
                for (Category category : categories) {
                    //Kiểm tra id cho từng list category của course
                    if (category.getCategoryId().equals(categoryDelete.getCategoryId())) {
                        //Nếu có course vẫn còn trong category thì báo lỗi
                        throw new RuntimeException("Cannot delete category with associated course: " + course.getCourseTitle());
                    }
                }
            }
        }
        //Nếu không có course nào thì delete
        categorieRepository.delete(categoryDelete);
    }

    @Override
    public GetCategoryResponse getCategoryById(Long categoryId) {
        return GetCategoryResponse.convertEntityToResponse(
                categorieRepository.findById(categoryId).orElseThrow(
                        () -> new ResourceNotFoundException("Category", "categoryId", categoryId.toString())
                )
        );
    }

    private Boolean existsByCategoryName(String categoryName) {
        //return true if the following category name existed in db
        return categorieRepository.existsByCategoryName(categoryName);
    }

    private Boolean existsByCategoryIcon(String categoryIcon) {
        return categorieRepository.existsByCategoryIcon(categoryIcon);
    }
}
