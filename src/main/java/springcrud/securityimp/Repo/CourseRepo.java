package springcrud.securityimp.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springcrud.securityimp.model.Course;
import springcrud.securityimp.model.User;

import java.util.List;
@Repository
public interface CourseRepo extends JpaRepository<Course,Integer> {
    List<Course> findByCreatedBy(User admin);
}
