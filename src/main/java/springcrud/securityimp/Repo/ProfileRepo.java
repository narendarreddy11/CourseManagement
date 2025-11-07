package springcrud.securityimp.Repo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springcrud.securityimp.model.Profile;


@Repository
public interface ProfileRepo extends JpaRepository<Profile,Integer> {

    Profile findByUserId(Integer integer);

}
