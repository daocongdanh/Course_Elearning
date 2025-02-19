package group.project.cursusonlinecoursemanagement.course.service.impl;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.CreateDraftLessonRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.ReorderLessonRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.UpdateLessonRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetLessonResponse;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Course;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Lesson;
import group.project.cursusonlinecoursemanagement.course.domain.entity.LessonStatus;
import group.project.cursusonlinecoursemanagement.course.repository.CommentRepository;
import group.project.cursusonlinecoursemanagement.course.repository.CourseRepository;
import group.project.cursusonlinecoursemanagement.course.repository.LessonRepository;
import group.project.cursusonlinecoursemanagement.course.service.LessonService;
import group.project.cursusonlinecoursemanagement.shared.domain.FileMetadata;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import group.project.cursusonlinecoursemanagement.shared.service.FirebaseStorageService;
import group.project.cursusonlinecoursemanagement.util.FileUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final ModelMapper modelMapper;
    private final CourseRepository courseRepository;
    private final CommentRepository commentRepository;
    private final FileUtil fileUtil;

    public LessonServiceImpl(LessonRepository lessonRepository,
                             FirebaseStorageService firebaseStorageService,
                             ModelMapper modelMapper,
                             CourseRepository courseRepository,
                             CommentRepository commentRepository,
                             FileUtil fileUtil) {
        this.lessonRepository = lessonRepository;
        this.firebaseStorageService = firebaseStorageService;
        this.modelMapper = modelMapper;
        this.courseRepository = courseRepository;
        this.commentRepository = commentRepository;
        this.fileUtil = fileUtil;
    }

    @Override
    public List<GetLessonResponse> getAllLesson() {
        try {
            List<Lesson> lessons = lessonRepository.findAll();
            return lessons.stream()
                    .map(lesson -> {
                        //Map entity into response
                        GetLessonResponse response = modelMapper.map(lesson, GetLessonResponse.class);
//                        //Check has video
//                        if (lesson.getKeyVideo() != null) {
//                            //Get link url
//                            response.setVideoUrl(firebaseStorageService.getSignUrl(lesson.getKeyVideo()).toString());
//                        }
                        response.setVideoUrl(lesson.getKeyVideo());
                        return response;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all lessons: " + e.getMessage(), e);
        }
    }

    @Override
    public GetLessonResponse getLessonById(UUID lessonId) {
        try {
            //Get a lesson by id
            Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                    () -> new ResourceNotFoundException("Lesson", "id", String.valueOf(lessonId))
            );
            //Map entity into response
            GetLessonResponse response = GetLessonResponse.convertEntityToResponse(lesson);
//            //Check has video
//            if (lesson.getKeyVideo() != null) {
//                //Get link url
//                response.setVideoUrl(firebaseStorageService.getSignUrl(lesson.getKeyVideo()).toString());
//            }
            response.setVideoUrl(lesson.getKeyVideo());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error get lesson by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public GetLessonResponse createDraftLesson(CreateDraftLessonRequest lessonDraftRequest, UUID courseId) {
        try {
            //Find the course of this lesson if it doesn't have a course like the course id throw exception
            Course course = courseRepository.findById(courseId).orElseThrow(
                    () -> new ResourceNotFoundException("Course", "id", String.valueOf(courseId))
            );
            //Create new entity Lesson
            Lesson lesson = CreateDraftLessonRequest.convertRequestToEntity(lessonDraftRequest);
            //Add course into the lesson
            lesson.setCourse(course);
            lesson.setPosition(course.getLessons().size() + 1);
            //Save into a database
            lessonRepository.save(lesson);
            //Change the modifiedDate of course
            course.setModifiedDate(LocalDateTime.now());
            //Save into a database
            courseRepository.save(course);
            return GetLessonResponse.convertEntityToResponse(lesson);
        } catch (Exception e) {
            throw new RuntimeException("Error create draft lesson: " + e.getMessage(), e);
        }
    }

    @Override
    public GetLessonResponse updateLesson(UUID lessonId, UpdateLessonRequest lessonRequest) {
        try {
            //Get a lesson by id
            Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                    () -> new ResourceNotFoundException("Lesson", "id", String.valueOf(lessonId))
            );
            //Update lesson
            lesson.setLessonTitle(lessonRequest.getLessonTitle() != null ? lessonRequest.getLessonTitle() : lesson.getLessonTitle());
            lesson.setLessonDescription(lessonRequest.getLessonDescription() != null ? lessonRequest.getLessonDescription() : lesson.getLessonDescription());
            lesson.setIsFree(lessonRequest.getIsFree() != null ? lessonRequest.getIsFree() : lesson.getIsFree());
            lesson.setIsPublished(lessonRequest.getIsPublished() != null ? lessonRequest.getIsPublished() : lesson.getIsPublished());
            //Save into a database
            GetLessonResponse getLessonResponse = GetLessonResponse.convertEntityToResponse(lessonRepository.save(lesson));
            if (lessonRepository.countLessonWithPublicStatus(lesson.getCourse().getCourseId()) == 0) {
                Course course = courseRepository.findById(lesson.getCourse().getCourseId()).orElseThrow(
                        () -> new ResourceNotFoundException("Course", "id", String.valueOf(lesson.getCourse().getCourseId())));
                course.setCourseStatus(false);
                courseRepository.save(course);
            }
            return getLessonResponse;
        } catch (Exception e) {
            throw new RuntimeException("Error upload data of this lesson: " + e.getMessage(), e);
        }
    }

    @Override
    public GetLessonResponse uploadVideo(UUID lessonId, MultipartFile file) {
        try {
            //Get a lesson by id
            Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                    () -> new ResourceNotFoundException("Lesson", "id", String.valueOf(lessonId))
            );
            String url = fileUtil.upload(file);
            lesson.setKeyVideo(url);
            lessonRepository.save(lesson);
            //Mapper model response
            GetLessonResponse getLessonResponse = GetLessonResponse.convertEntityToResponse(lesson);
            //Get link sign 24h

//            getLessonResponse.setVideoUrl(firebaseStorageService.getSignUrl(lesson.getKeyVideo()).toString());
            getLessonResponse.setVideoUrl(lesson.getKeyVideo());
            return getLessonResponse;
        } catch (Exception e) {
            throw new RuntimeException("Error upload video of this lesson: " + e.getMessage(), e);
        }
    }


    @Override
    public void deleteLesson(UUID lessonId) {
        try {
            //Get a lesson by id
            Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                    () -> new ResourceNotFoundException("Lesson", "id", String.valueOf(lessonId))
            );
            //Check comment
            if (commentRepository.existsByLessonId(lessonId)) {
                throw new RuntimeException("This lesson is already has person comments.");
            }
            //Check UserLessonProgress
            if (!lesson.getLessonProgressList().isEmpty()) {
                throw new RuntimeException("This lesson is already has person studies.");
            }
            //Check the lesson has video in the cloud
            if (!lesson.getKeyVideo().isEmpty()) {
                //If it exited, remove on cloud
                firebaseStorageService.deleteFile(lesson.getKeyVideo());
            }
            //Delete in database
            lessonRepository.deleteById(lesson.getLessonId());
        } catch (Exception e) {
            throw new RuntimeException("Error delete lesson: " + e.getMessage(), e);
        }
    }

    /**
     * Reorders the lessons within a course based on the provided list of reorder requests.
     *
     * @param courseId              The UUID of the course whose lessons are to be reordered.
     * @param reorderLessonRequests The list of ReorderLessonRequest objects containing lesson IDs and new positions.
     * @throws RuntimeException If the reorder requests are empty, contain duplicate positions, or if an error occurs during the reordering process.
     */
    @Override
    public void reorderLessons(UUID courseId, List<ReorderLessonRequest> reorderLessonRequests) {
        validateReorderRequests(reorderLessonRequests);
        List<Lesson> currentLessons = getLessonsByCourseId(courseId);
        validatePositions(reorderLessonRequests, currentLessons);
        updateLessonPositions(reorderLessonRequests);
    }

    @Override
    public Boolean userIsCompletedLesson(UUID lessonId, String email) {
        return lessonRepository.existsByLessonProgressList_Lesson_LessonIdAndLessonProgressList_User_EmailAndLessonProgressList_Status(lessonId, email, LessonStatus.COMPLETED);
    }

    @Override
    public List<GetLessonResponse> deleteLessonAndReorder(UUID lessonId) {
        Lesson deleteLesson = getLessonFromRepoById(lessonId);
        UUID courseId = deleteLesson.getCourse().getCourseId();
        List<Lesson> lessons = getLessonsByCourseId(courseId);
        List<ReorderLessonRequest> reorderLessonRequests = prepareReorderRequests(lessons, deleteLesson);

        lessonRepository.deleteByLessonId(lessonId);

        // Verify deletion
        if (lessonRepository.existsById(lessonId)) {
            throw new RuntimeException("Failed to delete lesson with ID: " + lessonId);
        }

        if (!reorderLessonRequests.isEmpty()) {
            reorderLessons(courseId, reorderLessonRequests);
        }

        return convertToGetLessonResponses(getLessonsByCourseId(courseId));
    }

    private List<ReorderLessonRequest> prepareReorderRequests(List<Lesson> lessons, Lesson deleteLesson) {
        List<ReorderLessonRequest> reorderLessonRequests = new ArrayList<>();
        for (Lesson lesson : lessons) {
            if (!lesson.getLessonId().equals(deleteLesson.getLessonId())
                    && lesson.getPosition() > deleteLesson.getPosition()
            ) {
                lesson.setPosition(lesson.getPosition() - 1);
                reorderLessonRequests.add(new ReorderLessonRequest(lesson.getLessonId(), lesson.getPosition()));
            }
        }
        return reorderLessonRequests;
    }

    private List<GetLessonResponse> convertToGetLessonResponses(List<Lesson> lessons) {
        return lessons.stream()
                .map(this::convertToGetLessonResponse)
                .collect(Collectors.toList());
    }

    private GetLessonResponse convertToGetLessonResponse(Lesson lesson) {
        GetLessonResponse response = GetLessonResponse.convertEntityToResponse(lesson);
//        if (lesson.getKeyVideo() != null) {
//            response.setVideoUrl(firebaseStorageService.getSignUrl(lesson.getKeyVideo()).toString());
//        }
        response.setVideoUrl(lesson.getKeyVideo());
        return response;
    }

    /**
     * Validates the reorder requests to ensure they are not empty and do not contain duplicate positions.
     *
     * @param reorderLessonRequests The list of ReorderLessonRequest objects to be validated.
     * @throws RuntimeException If the reorder requests are empty or contain duplicate positions.
     */
    private void validateReorderRequests(List<ReorderLessonRequest> reorderLessonRequests) {
        if (reorderLessonRequests.isEmpty()) {
            throw new RuntimeException("List reorder lesson is empty.");
        }

        Set<Integer> requestPositions = new HashSet<>();
        for (ReorderLessonRequest request : reorderLessonRequests) {
            if (!requestPositions.add(request.getPosition())) {
                throw new RuntimeException("Duplicate position in reorder lesson requests: " + request.getPosition());
            }
        }
    }

    /**
     * Retrieves the lessons associated with the specified course ID.
     *
     * @param courseId The UUID of the course whose lessons are to be retrieved.
     * @return A list of Lesson objects associated with the specified course.
     */
    private List<Lesson> getLessonsByCourseId(UUID courseId) {
        return lessonRepository.findByCourse_CourseId(courseId);
    }

    /**
     * Validates the positions in the reorder requests to ensure they do not conflict with existing positions.
     *
     * @param reorderLessonRequests The list of ReorderLessonRequest objects to be validated.
     * @param currentLessons        The list of current lessons in the course.
     * @throws RuntimeException If a position in the reorder requests already exists for another lesson in the course.
     */
    private void validatePositions(List<ReorderLessonRequest> reorderLessonRequests, List<Lesson> currentLessons) {
        // Create a map of current lessons' IDs to their positions for quick lookup
        Map<UUID, Integer> currentLessonPositions = currentLessons.stream()
                .collect(Collectors.toMap(Lesson::getLessonId, Lesson::getPosition));

        // Set to track new positions in the reorder requests
        Set<Integer> newPositions = new HashSet<>();

        // Validate each reorder request
        for (ReorderLessonRequest request : reorderLessonRequests) {
            UUID lessonId = request.getLessonId(); // Get the lesson ID from the request
            Integer newPosition = request.getPosition(); // Get the new position from the request

            Integer currentPosition = currentLessonPositions.get(lessonId); // Get the current position of the lesson

            // Check if the new position is different from the current position
            // and if it is already used in the new positions
            if (!newPosition.equals(currentPosition) && !newPositions.add(newPosition)) {
                throw new RuntimeException("Duplicate position in reorder lesson requests: " + newPosition);
            }

            currentLessonPositions.remove(lessonId); // Remove the processed lesson from the map
        }

        // Ensure that no existing positions are reused in the new positions
        for (Integer currentPosition : currentLessonPositions.values()) {
            if (newPositions.contains(currentPosition)) {
                throw new RuntimeException("Position already exists for another lesson in the course: " + currentPosition);
            }
        }
    }

    /**
     * Updates the positions of lessons based on the provided reorder requests.
     *
     * @param reorderLessonRequests The list of ReorderLessonRequest objects containing lesson IDs and new positions.
     * @throws RuntimeException If an error occurs during the reordering process.
     */
    private void updateLessonPositions(List<ReorderLessonRequest> reorderLessonRequests) {
        try {
            for (ReorderLessonRequest request : reorderLessonRequests) {
                Lesson lesson = getLessonFromRepoById(request.getLessonId());
                lesson.setPosition(request.getPosition());
                lessonRepository.save(lesson);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reorder lesson: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a lesson from the repository by its ID.
     *
     * @param lessonId The UUID of the lesson to be retrieved.
     * @return The Lesson object associated with the specified ID.
     * @throws ResourceNotFoundException If the lesson with the given ID is not found.
     */
    private Lesson getLessonFromRepoById(UUID lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("Lesson", "id", String.valueOf(lessonId))
        );
    }
}