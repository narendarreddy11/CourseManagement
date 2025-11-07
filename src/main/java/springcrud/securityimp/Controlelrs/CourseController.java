package springcrud.securityimp.Controlelrs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springcrud.securityimp.Dto.CourseCreateDTO;
import springcrud.securityimp.Dto.CourseDTO;
import springcrud.securityimp.Services.CourseServiceImp;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseServiceImp courseService;

    // ✅ Create new course with image (Admin only)
    @PostMapping(value = "/create/{adminId}", consumes = {"multipart/form-data"})
    public ResponseEntity<CourseDTO> createCourse(
            @PathVariable int adminId,
            @RequestPart("course") CourseCreateDTO dto,
            @RequestPart("image") MultipartFile image
    ) throws IOException {
        return ResponseEntity.ok(courseService.createCourse(dto, image, adminId));
    }

    // ✅ Update existing course (with optional image replacement)
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable int id,
            @RequestPart("course") CourseCreateDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return ResponseEntity.ok(courseService.updateCourse(id, dto, image));
    }

    // ✅ Get all courses created by specific admin
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<?> getCoursesByAdmin(@PathVariable int adminId) {
        List<CourseDTO> courses = courseService.getCoursesByAdmin(adminId);
        if (courses.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("message", "No courses created by this admin yet."));
        }
        return ResponseEntity.ok(courses);
    }
    // ✅ Get single course by ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable int id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // ✅ Get all courses
    @GetMapping
    public List<CourseDTO> getAllCourses() {
        return courseService.getAllCourses();
    }

    // ✅ Delete course
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable int id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully");
    }
}
