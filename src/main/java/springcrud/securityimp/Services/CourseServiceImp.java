package springcrud.securityimp.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import springcrud.securityimp.Dto.CourseCreateDTO;
import springcrud.securityimp.Dto.CourseDTO;
import springcrud.securityimp.Repo.CourseRepo;
import springcrud.securityimp.Repo.EnrollementRepo;
import springcrud.securityimp.Repo.ImageRepository;
import springcrud.securityimp.Repo.UserRepo;
import springcrud.securityimp.model.Course;
import springcrud.securityimp.model.Image;
import springcrud.securityimp.model.Role;
import springcrud.securityimp.model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImp {

    private final CourseRepo courseRepository;
    private final UserRepo userRepository;
    private final EnrollementRepo enrollmentRepository;
    private final ImageRepository imageRepository;

    // ✅ Use subfolder for better organization
    private static final String UPLOAD_DIR = "uploads/images/";

    // ✅ Create Course with Image
    @Transactional
    public CourseDTO createCourse(CourseCreateDTO dto, MultipartFile imageFile, int adminId) throws IOException {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admins can create courses");
        }

        if (imageFile == null || imageFile.isEmpty()) {
            throw new RuntimeException("Image file is required for course creation");
        }

        // Ensure upload directory exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save image to local folder
        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, imageFile.getBytes());

        // Store a clean forward-slash relative path (for frontend)
        String relativePath = (UPLOAD_DIR + fileName).replace("\\", "/");

        Image image = Image.builder()
                .fileName(fileName)
                .fileType(imageFile.getContentType())
                .filePath(relativePath)
                .build();

        imageRepository.save(image);

        Course course = new Course();
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCapacity(dto.getCapacity());
        course.setInstructor(dto.getInstructor());
        course.setCreatedBy(admin);
        course.setImage(image);

        courseRepository.save(course);
        return toDto(course);
    }

    // ✅ Update Course (keep old image if none uploaded)
    @Transactional
    public CourseDTO updateCourse(int id, CourseCreateDTO dto, MultipartFile imageFile) throws IOException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCapacity(dto.getCapacity());
        course.setInstructor(dto.getInstructor());

        if (imageFile != null && !imageFile.isEmpty()) {
            Image existingImage = course.getImage();

            // Ensure upload directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save new image file
            String newFileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path newFilePath = uploadPath.resolve(newFileName);
            Files.write(newFilePath, imageFile.getBytes());
            String relativePath = (UPLOAD_DIR + newFileName).replace("\\", "/");

            if (existingImage != null) {
                // Delete old physical file if exists
                try {
                    Path oldFile = Paths.get(existingImage.getFilePath().replace("\\", "/"));
                    if (Files.exists(oldFile)) {
                        Files.delete(oldFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Update image entity
                existingImage.setFileName(newFileName);
                existingImage.setFileType(imageFile.getContentType());
                existingImage.setFilePath(relativePath);
                imageRepository.save(existingImage);
            } else {
                // Create new image
                Image newImage = Image.builder()
                        .fileName(newFileName)
                        .fileType(imageFile.getContentType())
                        .filePath(relativePath)
                        .build();
                imageRepository.save(newImage);
                course.setImage(newImage);
            }
        }

        courseRepository.save(course);
        return toDto(course);
    }

    // ✅ Get courses created by admin
    @Transactional
    public List<CourseDTO> getCoursesByAdmin(int adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        List<Course> courses = courseRepository.findByCreatedBy(admin);
        return courses.stream().map(this::toDto).collect(Collectors.toList());
    }

    public CourseDTO getCourseById(int id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return toDto(course);
    }

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Delete course with image cleanup
    @Transactional
    public void deleteCourse(int id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // ✅ Step 1: Clean up enrollments first (to avoid FK constraint errors)
        if (!course.getEnrollments().isEmpty()) {
            course.getEnrollments().forEach(enrollment -> enrollment.setCourse(null));
            enrollmentRepository.deleteAll(course.getEnrollments());
        }

        // ✅ Step 2: Detach image before deletion
        Image image = course.getImage();
        if (image != null) {
            course.setImage(null);
        }

        // ✅ Step 3: Delete the course itself
        courseRepository.delete(course);

        // ✅ Step 4: Delete image record + file safely
        if (image != null) {
            try {
                // Use absolute path to the uploads directory
                Path uploadRoot = Paths.get("uploads/images");
                Path filePath = uploadRoot.resolve(image.getFileName());

                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                System.err.println("⚠️ Failed to delete image file: " + e.getMessage());
            }

            imageRepository.delete(image);
        }
    }


    // ✅ Convert entity → DTO
    private CourseDTO toDto(Course course) {
        int enrolledCount = enrollmentRepository.countByCourse(course);

        String cleanPath = null;
        if (course.getImage() != null && course.getImage().getFilePath() != null) {
            cleanPath = course.getImage().getFilePath().replace("\\", "/");
        }

        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .capacity(course.getCapacity())
                .instructor(course.getInstructor())
                .createdById(course.getCreatedBy() != null ? course.getCreatedBy().getId() : null)
                .createdByName(course.getCreatedBy() != null ? course.getCreatedBy().getUsername() : null)
                .enrolledCount(enrolledCount)
                .imagePath(cleanPath)
                .build();
    }
}
