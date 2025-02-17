package group.project.cursusonlinecoursemanagement.course.service.impl;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.CreateDraftCourseRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.UpdateCourseRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.CreateDraftCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetUserOwnedCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.entity.*;
import group.project.cursusonlinecoursemanagement.course.repository.CategoryRepository;
import group.project.cursusonlinecoursemanagement.course.repository.CourseRepository;
import group.project.cursusonlinecoursemanagement.course.service.CourseService;
import group.project.cursusonlinecoursemanagement.course.service.EnrollmentService;
import group.project.cursusonlinecoursemanagement.shared.domain.FileMetadata;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.AppExceptionHandler;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import group.project.cursusonlinecoursemanagement.shared.service.CloudinaryService;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Instructor;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import group.project.cursusonlinecoursemanagement.user.repository.InstructorRepository;
import group.project.cursusonlinecoursemanagement.user.repository.UserRepository;
import group.project.cursusonlinecoursemanagement.user.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentService enrollmentService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;
    private final CategoryRepository categoryRepository;
    private final LessonServiceImpl lessonServiceImpl;
    private final UserLessonProgressServiceImpl userLessonProgressServiceImpl;
    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;

    public CourseServiceImpl(CourseRepository courseRepository, @Lazy EnrollmentService enrollmentService,
                             ModelMapper modelMapper,
                             UserService userService,
                             CloudinaryService cloudinaryService,
                             CategoryRepository categoryRepository,
                             LessonServiceImpl lessonServiceImpl,
                             UserLessonProgressServiceImpl userLessonProgressServiceImpl,
                             UserRepository userRepository,
                             InstructorRepository instructorRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentService = enrollmentService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
        this.categoryRepository = categoryRepository;
        this.lessonServiceImpl = lessonServiceImpl;
        this.userLessonProgressServiceImpl = userLessonProgressServiceImpl;
        this.userRepository = userRepository;
        this.instructorRepository = instructorRepository;
    }


    @Override
    public List<GetCourseResponse> getAllCourses() {
        try {
            List<Course> courses = courseRepository.findAll();
            return getGetCourseResponses(courses);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all courses: " + e.getMessage(), e);
        }
    }


    @Override
    public GetCourseResponse getCourseById(UUID id) {
        Course course = getCourseFromRepoById(id);
        GetCourseResponse response = GetCourseResponse.convertEntityToResponseBasis(course);
        return response;
    }

    public Course getCourseFromRepoById(UUID courseId) {
        return courseRepository.findById(courseId).orElseThrow(() ->
                new ResourceNotFoundException("Course", "ID", courseId.toString()));
    }

    /**
     * Creates a draft course based on the provided request details.
     *
     * @param createDraftCourseRequest The request object containing the draft course's details.
     * @return A CreateDraftCourseResponse object containing the details of the created draft course.
     * @throws RuntimeException If an error occurs while creating the draft course.
     */
    @Override
    public CreateDraftCourseResponse createDraftCourse(CreateDraftCourseRequest createDraftCourseRequest) {
        try {
            // Get the email of the instructor creating the course from the authentication token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Retrieve the user entity by email
            User user = userService.getUserFromRepoByEmail(authentication.getName());

            // Create a new course entity from the request
            // The entity is missing fields: description, thumbnailUrl, price, discount, categories
            Course course = CreateDraftCourseRequest.convertRequestToEntity(createDraftCourseRequest);

            // Set the user, created date, and modified date for the course
            course.setUser(user);
            course.setCreatedDate(LocalDateTime.now());
            course.setModifiedDate(LocalDateTime.now());

            // Save the new course entity to the database
            courseRepository.save(course);

            // Convert the saved course entity to a response object and return it
            return modelMapper.map(course, CreateDraftCourseResponse.class);
        } catch (Exception e) {
            // Handle any exceptions during the course creation process
            throw new RuntimeException("Error creating new course: " + e.getMessage(), e);
        }
    }

    /**
     * Updates a course with the provided details.
     *
     * @param id                  The UUID of the course to update.
     * @param updateCourseRequest The UpdateCourseRequest object containing the course's updated details.
     * @return A GetCourseResponse object containing the details of the updated course.
     * @throws AppExceptionHandler If an error occurs while updating the course.
     */
    @Override
    public GetCourseResponse updateCourse(UUID id, UpdateCourseRequest updateCourseRequest) {

        // Retrieve the course by ID, or throw ResourceNotFoundException if not found
            Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", "ID", id.toString()));

        // Update the course title if provided, otherwise keep the existing title
        course.setCourseTitle(updateCourseRequest.getCourseTitle() != null ? updateCourseRequest.getCourseTitle() : course.getCourseTitle());

        // Update the course description if provided, otherwise keep the existing description
        course.setDescription(updateCourseRequest.getDescription() != null ? updateCourseRequest.getDescription() : course.getDescription());

        // Update the course price if provided, otherwise keep the existing price
        course.setPrice(updateCourseRequest.getPrice() != null ? updateCourseRequest.getPrice() : course.getPrice());

        // Update the course discount if provided and greater than 0, otherwise keep the existing discount
        course.setDiscount(updateCourseRequest.getDiscount() > 0 ? updateCourseRequest.getDiscount() : course.getDiscount());

        // Update the course status if provided and different from the current status,
        // otherwise keep the existing status
        course.setCourseStatus(updateCourseRequest.isCourseStatus() != course.isCourseStatus() ? updateCourseRequest.isCourseStatus() : course.isCourseStatus());

        // Update the modified date to the current time
        course.setModifiedDate(LocalDateTime.now());

        // Update the categories if provided
        if (updateCourseRequest.getCategoriesId() != null) {
            // Retrieve new categories based on provided category IDs
            List<Category> newCategories = categoryRepository.findAllById(updateCourseRequest.getCategoriesId());

            // Remove the course from the current categories
            course.getCategories().forEach(category -> category.getCourses().remove(course));

            // Clear the current categories from the course
            course.getCategories().clear();

            // Add the course to the new categories and set the new categories to the course
//            newCategories.forEach(category -> category.getCourses().add(course));
            course.setCategories(newCategories);
        }
        return GetCourseResponse.convertEntityToResponseBasis(courseRepository.save(course));
    }

    @Override
    @Transactional
    public GetCourseResponse uploadThumbnail(UUID id, MultipartFile file) throws IOException {
        //Lấy course
        Course course = courseRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Course", "ID", id.toString()));
        String url = cloudinaryService.uploadFile(file,"image");
        GetCourseResponse response = GetCourseResponse.convertEntityToResponseBasis(course);
        //Trả về "Upload Thumbnail successful" nêu thành công
        response.setThumbnailUrl(url);
        course.setKeyThumbnail(url);
        courseRepository.save(course);
        return response;
    }

    @Override
    @Transactional
    public void deleteCourse(UUID id) {
        try {
            //Retrieve the course to delete
            Course course = courseRepository.findById(id).orElseThrow(() ->
                    new ResourceNotFoundException("Course", "ID", id.toString()));
            //Get lesson
            List<Lesson> lessons = course.getLessons();
            //Check lessons
            if (!lessons.isEmpty()) {
                throw new RuntimeException("This course also includes lessons.");
            }
            //Check enrollments
            if (!course.getEnrollments().isEmpty()) {
                throw new RuntimeException("This course is already has person enrollments.");
            }
            //Check Category of Course
            if (!course.getCategories().isEmpty()) {
                //Delete Category of this Course
                course.getCategories().forEach(category -> category.getCourses().remove(course));
                course.getCategories().clear();
                courseRepository.save(course);
            }
            //Delete in database
            courseRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error delete course:" + e.getMessage(), e);
        }
    }

    /**
     * Retrieves courses associated with an instructor identified by their email address.
     *
     * @param email The email address of the instructor.
     * @return A list of {@link GetCourseResponse} objects representing courses taught by the instructor.
     * @throws AppExceptionHandler If the user identified by the email is not registered as an instructor.
     */
    @Override
    public List<GetCourseResponse> getCoursesOfInstructor(String email) {
        // Check if the user identified by 'email' is registered as an instructor
        boolean isInstructor = userService.isInstructor(email);

        if (isInstructor) {
            // Retrieve courses associated with the instructor's email from the repository order by created date
            List<Course> courses = courseRepository.findByUser_EmailOrderByCreatedDateDesc(email);

            // Map each Course entity to a GetCourseResponse DTO using ModelMapper
            return courses.stream()
                    .map(GetCourseResponse::convertEntityToResponseBasis)
                    .collect(Collectors.toList());
        }

        // Throw an exception if the user is not registered as an instructor
        throw new AppExceptionHandler("User is not an instructor");
    }

    @Override
    public List<GetCourseResponse> getCoursesOfInstructorById(UUID userId) {
        Instructor instructor = instructorRepository.getInstructorByUserId(userId).orElseThrow(() ->
                new ResourceNotFoundException("User", "ID", userId.toString()));

        // Retrieve courses associated with the instructor's email from the repository order by created date
        List<Course> courses = courseRepository.findByUser_EmailOrderByCreatedDateDesc(instructor.getUser().getEmail());

        // Map each Course entity to a GetCourseResponse DTO using ModelMapper
        return courses.stream()
                .map(GetCourseResponse::convertEntityToResponseBasis)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetCourseResponse> searchCourses(Long categoryId, String title) {
        List<Course> courses = new ArrayList<>();

        // First, try to find by title and category ID
        if (title != null && !title.isEmpty() && categoryId != null) {
            courses = courseRepository.findByCourseTitleContainsIgnoreCaseAndCategories_CategoryId(title, categoryId);
        }

        // If no courses found and title are empty, try to find by title only
        if (courses.isEmpty() && title != null && !title.isEmpty()) {
            courses = courseRepository.findByCourseTitleContainsIgnoreCase(title);
        }

        // If still no courses found and category ID are provided, try to find by category ID only
        if (courses.isEmpty() && categoryId != null) {
            courses = courseRepository.findByCategories_CategoryId(categoryId);
        }

        // Convert courses to DTOs
        return getGetCourseResponses(courses);
    }

    @Override
    public List<GetCourseResponse> getPublishedCourses() {
        return getGetCourseResponses(
                courseRepository.findByCourseStatus(true)
        );
    }

    @Override
    public Course findCourseById(UUID courseId) {
        return courseRepository.findById(courseId).orElseThrow(() ->
                new ResourceNotFoundException("Course", "ID", courseId.toString()));
    }

    @Override
    public List<GetUserOwnedCourseResponse> getStudentOwnedCourses(String email) {
        List<Enrollment> enrollment = enrollmentService.getEnrollmentsOfUser(email);
        if (!enrollment.isEmpty()) {
            return enrollment.stream()
                    .map(enroll -> {
                        GetUserOwnedCourseResponse response = GetUserOwnedCourseResponse.convertEntityToResponse(enroll);
                        response.setUserCourseProgress(calculateCourseProgress(enroll.getCourse(), enroll.getUser().getEmail()));
                        return response;
                    })
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public List<GetUserOwnedCourseResponse> getStudentOwnedCoursesById(UUID userId) {
        String email = userService.getUserDtoById(userId).getEmail();
        List<Enrollment> enrollment = enrollmentService.getEnrollmentsOfUser(email);
        if (!enrollment.isEmpty()) {
            return enrollment.stream()
                    .map(enroll -> {
                        GetUserOwnedCourseResponse response = GetUserOwnedCourseResponse.convertEntityToResponse(enroll);
                        response.setUserCourseProgress(calculateCourseProgress(enroll.getCourse(), enroll.getUser().getEmail()));
                        return response;
                    })
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public GetCourseResponse getCourseByIdByUser(UUID courseId) {
        Course course = getCourseFromRepoById(courseId);
        course.setLessons(course.getLessons().stream().filter(Lesson::getIsPublished).toList());
        GetCourseResponse response = GetCourseResponse.convertEntityToResponseBasis(course);
        return response;
    }

    @Override
    public GetCourseResponse getStudentOwnedCourseWithLessonsAndProgressById(String email, UUID courseId) {
        Course course = getCourseFromRepoById(courseId);
        List<UserLessonProgress> lessonProgress = userLessonProgressServiceImpl.getLessonProgressOfStudent(courseId, email);
        course.setLessons(course.getLessons().stream()
                .filter(Lesson::getIsPublished)
                .peek(lesson -> lesson.setLessonProgressList(lessonProgress.stream()
                        .filter(progress -> progress.getLesson().getLessonId().equals(lesson.getLessonId()))
                        .toList()))
                .toList()
        );

        return GetCourseResponse.convertEntityToResponseBasis(course);
    }

    @Override
    public List<GetCourseResponse> searchCoursesByDesc(String desc) {
        if(desc.isEmpty())
            return new ArrayList<>();
        List<Course> courses = courseRepository.searchByDescription(desc);
        return getGetCourseResponses(courses);
    }

    public Double calculateCourseProgress(Course course, String email) {
        List<Lesson> lessons = course.getLessons().stream().filter(Lesson::getIsPublished).toList();
        int countCompletedLessons = lessons.stream()
                .map(lesson -> lessonServiceImpl.userIsCompletedLesson(lesson.getLessonId(), email))
                .filter(Boolean::booleanValue)
                .toList()
                .size();
        return (double) countCompletedLessons / lessons.size() * 100;
    }

    private List<GetCourseResponse> getGetCourseResponses(List<Course> courses) {
        return courses.stream()
                .map(course -> {
                    GetCourseResponse response = GetCourseResponse.convertEntityToResponseBasis(course);
                    return response;
                })
                .collect(Collectors.toList());
    }
}
