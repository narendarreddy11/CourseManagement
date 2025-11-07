package springcrud.securityimp.Repo;


import org.springframework.data.jpa.repository.JpaRepository;
import springcrud.securityimp.model.Image;

public interface ImageRepository extends JpaRepository<Image, Integer> {

}
