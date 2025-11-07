package springcrud.securityimp.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import springcrud.securityimp.model.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

        private int id;
        private String username;
        private String email;
        private Role role;
        private Integer maxCourses;

}
