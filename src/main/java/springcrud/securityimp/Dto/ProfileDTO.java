package springcrud.securityimp.Dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private int id;
    private int userId;
    private String firstName;
    private String lastName;
    private long phone;
    private String address;
    private String bio;
    private String profileImagePath;
}
