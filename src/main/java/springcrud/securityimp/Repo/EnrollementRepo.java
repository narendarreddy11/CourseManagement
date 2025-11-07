package springcrud.securityimp.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import springcrud.securityimp.model.Course;
import springcrud.securityimp.model.Enrollment;
import springcrud.securityimp.model.User;

import java.util.List;
import java.util.Optional;

public interface EnrollementRepo extends JpaRepository<Enrollment,Integer> {
    List<Enrollment> findByStudent(User student);
    List<Enrollment> findByCourse(Course course);
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    boolean existsByStudentAndCourse(User student, Course course);

    int countByCourse(Course course);

}
