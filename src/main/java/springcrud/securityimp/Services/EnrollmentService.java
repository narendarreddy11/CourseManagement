package springcrud.securityimp.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springcrud.securityimp.Dto.EnrollmentDTO;
import springcrud.securityimp.Repo.CourseRepo;
import springcrud.securityimp.Repo.EnrollementRepo;
import springcrud.securityimp.Repo.UserRepo;
import springcrud.securityimp.model.Course;
import springcrud.securityimp.model.Enrollment;
import springcrud.securityimp.model.Role;
import springcrud.securityimp.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollementRepo enrollmentRepository;
    private final UserRepo userRepository;
    private final CourseRepo courseRepository;

    // ✅ Enroll a student in a course
    @Transactional
    public EnrollmentDTO enroll(int studentId, int courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 🚫 Prevent admins from enrolling
        if (student.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admins cannot enroll in courses");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 🚫 Check if already enrolled
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("Already enrolled in this course");
        }

        // 🚫 Check capacity
        long enrolledCount = enrollmentRepository.countByCourse(course);
        if (enrolledCount >= course.getCapacity()) {
            throw new RuntimeException("Course capacity reached");
        }

        // ✅ Enroll student
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);

        return toDTO(enrollment);
    }

    // ✅ Get all enrollments of a student
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getStudentEnrollments(int studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return enrollmentRepository.findByStudent(student)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getAllEnrollments() {
        return enrollmentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public long getAllEnrollmentsCount() {
        return enrollmentRepository.count();
    }
    // ✅ Cancel enrollment
    @Transactional
    public void cancelEnrollment(int studentId, int courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);
    }
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByUserId(int userId) {
        User student = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);

        return enrollments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ Entity → DTO converter
    private EnrollmentDTO toDTO(Enrollment e) {
        return EnrollmentDTO.builder()
                .id(e.getEnrollmentId())
                .studentId(e.getStudent().getId())
                .studentName(e.getStudent().getUsername())
                .courseId(e.getCourse().getId())
                .courseTitle(e.getCourse().getTitle())
                .enrolledAt(e.getEnrolledAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();
    }
}
