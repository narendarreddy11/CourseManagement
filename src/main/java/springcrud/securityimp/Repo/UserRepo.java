package springcrud.securityimp.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import springcrud.securityimp.model.User;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    void deleteByUsername(String username);
}
