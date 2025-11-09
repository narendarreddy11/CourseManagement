package springcrud.securityimp.Controlelrs;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import springcrud.securityimp.Dto.EnrollmentDTO;
import springcrud.securityimp.Services.CourseServiceImp;
import springcrud.securityimp.Services.EnrollmentService;
import springcrud.securityimp.Services.UserServiceImp;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserServiceImp userService;
    private final CourseServiceImp courseService;
    private final EnrollmentService enrollmentService;

    // ✅ Get total users (only ADMIN can access)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/count")
    public ResponseEntity<Long> getUsersCount() {
        long count = userService.getAllUsers().size();
        return ResponseEntity.ok(count);
    }

    // ✅ Get total students (only ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/students/count")
    public ResponseEntity<Long> getStudentsCount() {
        long count = userService.getAllUsers()
                .stream()
                .filter(u -> u.getRole().name().equalsIgnoreCase("STUDENT"))
                .count();
        return ResponseEntity.ok(count);
    }

    // ✅ Get total courses (only ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/courses/count")
    public ResponseEntity<Long> getCoursesCount() {
        long count = courseService.getAllCourses().size();
        return ResponseEntity.ok(count);
    }

    // ✅ Get total enrollments (only ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/enrollments/count")
    public ResponseEntity<Long> getEnrollmentsCount() {
        long count = enrollmentService.getAllEnrollmentsCount();
        return ResponseEntity.ok(count);
    }
    // ✅ Get all enrollments (Admin only)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

}
