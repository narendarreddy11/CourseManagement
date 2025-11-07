package springcrud.securityimp.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private int userid;
    private String token;
    private String username;
    private String role;
    private String email;

}

