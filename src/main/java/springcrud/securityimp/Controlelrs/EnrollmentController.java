package springcrud.securityimp.Controlelrs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springcrud.securityimp.Dto.EnrollmentDTO;
import springcrud.securityimp.Services.EnrollmentService;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // ✅ Enroll a student in a course
    @PostMapping("/enroll/{studentId}/{courseId}")
    public ResponseEntity<EnrollmentDTO> enroll(
            @PathVariable int studentId,
            @PathVariable int courseId
    ) {
        return ResponseEntity.ok(enrollmentService.enroll(studentId, courseId));
    }

    // ✅ Get all enrollments of a student (used in dashboard or courses tab)
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentDTO>> getStudentEnrollments(
            @PathVariable int studentId
    ) {
        return ResponseEntity.ok(enrollmentService.getStudentEnrollments(studentId));
    }

    // ✅ NEW: Get enrolled courses by User ID (used to persist “Enrolled” status)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByUserId(
            @PathVariable int userId
    ) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByUserId(userId);
        return ResponseEntity.ok(enrollments);
    }

    // ✅ Cancel enrollment
    @DeleteMapping("/cancel/{studentId}/{courseId}")
    public ResponseEntity<String> cancelEnrollment(
            @PathVariable int studentId,
            @PathVariable int courseId
    ) {
        enrollmentService.cancelEnrollment(studentId, courseId);
        return ResponseEntity.ok("Enrollment cancelled successfully");
    }
}
