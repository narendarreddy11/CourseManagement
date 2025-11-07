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
public class UserRegisterDTO {

        private String username;
        private String email;
        private String password;
        private Role role;

    }


