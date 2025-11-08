package springcrud.securityimp.Repo;

import jakarta.transaction.Transactional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springcrud.securityimp.model.Course;
import springcrud.securityimp.model.User;

import java.util.List;
@Repository
public interface CourseRepo extends JpaRepository<Course,Integer> {
    List<Course> findByCreatedBy(User admin);

    @Transactional
    @Modifying
    @Query("DELETE FROM Course c WHERE c.createdBy.id = :userId")
    void deleteByCreatedById(int userId);
}
