package springcrud.securityimp.Repo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springcrud.securityimp.model.Profile;


@Repository
public interface ProfileRepo extends JpaRepository<Profile,Integer> {

    Profile findByUserId(Integer integer);
   @Modifying
    @Transactional
    @Query("DELETE FROM Profile p WHERE p.user.id = :userId")
    void deleteByUserId(int userId);
}
