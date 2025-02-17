package group.project.cursusonlinecoursemanagement.course.controller;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.UpsertCategoryRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetCategoryResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetCategoryWithCourseResponse;
import group.project.cursusonlinecoursemanagement.course.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "CRUD REST APIs for Category"
)
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @Operation(
            summary = "Get All Categories REST API",
            description = "Get All Categories REST API is used to fetch all the categories from the database",
            responses = {
            @ApiResponse(responseCode = "200", description = "Successful response"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    }
    )

    @GetMapping("/getAllWithCourses")
    public ResponseEntity<List<GetCategoryWithCourseResponse>> getAllCategoriesWithCourse() {
        List<GetCategoryWithCourseResponse> categories = categoryService.getAllCategoriesWithCourse();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<GetCategoryResponse>> getAllCategories() {
        List<GetCategoryResponse> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/getCategoryResponseById/{id}")
    public ResponseEntity<GetCategoryResponse> getCategoryResponseById(@PathVariable Long id) {
        return new ResponseEntity<>(categoryService.getCategoryById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/getCategoryWithCourseById/{id}")
    public ResponseEntity<GetCategoryWithCourseResponse> getCategoryWithCourseById(@PathVariable Long id) {
        return new ResponseEntity<>(categoryService.getCategoryWithCourseById(id), HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/auth/createCategory")
    public ResponseEntity<GetCategoryResponse> createCategory(@Valid @RequestBody UpsertCategoryRequest createCategoryRequest) {
        return new ResponseEntity<>(categoryService.createCategory(createCategoryRequest), HttpStatus.CREATED);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/auth/updateCategory/{id}")
    public ResponseEntity<GetCategoryWithCourseResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody UpsertCategoryRequest updateCategoryRequest) {
        return new ResponseEntity<>(categoryService.updateCategory(id, updateCategoryRequest), HttpStatus.OK);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/auth/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>("Course deleted successfully", HttpStatus.OK);
    }
}
