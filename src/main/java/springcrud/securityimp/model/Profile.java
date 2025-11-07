package springcrud.securityimp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user_profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Profile {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;
    private String bio;
    private Long phonoenumber;
    private String email;
    private String Address;
    private String profileImagePath;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
