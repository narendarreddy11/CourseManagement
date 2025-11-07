package springcrud.securityimp.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springcrud.securityimp.Dto.LoginRequestDto;
import springcrud.securityimp.Dto.LoginResponseDto;
import springcrud.securityimp.Dto.UserDTO;
import springcrud.securityimp.Dto.UserRegisterDTO;
import springcrud.securityimp.Repo.*;
import springcrud.securityimp.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp {

    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepository;
    private final UserDetailsServiceImp userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // ✅ Repositories for cascade-safe deletion
    private final EnrollementRepo enrollmentRepository;
    private final CourseRepo courseRepository;
    private final ProfileRepo profileRepository;

    // ✅ Register new user
    public UserDTO register(UserRegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail()) || userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username or Email already exists");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole() != null ? dto.getRole() : Role.USER)
                .build();

        userRepository.save(user);
        return toDTO(user);
    }

    // ✅ Get all users
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get user by ID
    public Optional<UserDTO> getUserById(int id) {
        return userRepository.findById(id).map(this::toDTO);
    }

    // ✅ Delete user safely (with cascade handling)
    @Transactional
    public String deleteUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin accounts cannot be deleted!");
        }

        System.out.println("🗑 Deleting user: " + user.getUsername() + " (Role: " + user.getRole() + ")");

        // ✅ 1. Delete profile
        if (user.getProfile() != null) {
            System.out.println("Deleting profile for user " + user.getUsername());
            profileRepository.delete(user.getProfile());
            user.setProfile(null);
        }

        // ✅ 2. Delete enrollments (where user is a student)
        if (!user.getEnrollments().isEmpty()) {
            System.out.println("Deleting " + user.getEnrollments().size() + " enrollments for user " + user.getUsername());
            enrollmentRepository.deleteAll(user.getEnrollments());
            user.getEnrollments().clear();
        }

        // ✅ 3. Delete created courses (and their enrollments/images)
        if (!user.getCreatedCourses().isEmpty()) {
            System.out.println("Deleting " + user.getCreatedCourses().size() + " courses created by " + user.getUsername());
            for (Course course : user.getCreatedCourses()) {

                // Delete enrollments in each course
                if (!course.getEnrollments().isEmpty()) {
                    System.out.println("Deleting enrollments for course: " + course.getTitle());
                    enrollmentRepository.deleteAll(course.getEnrollments());
                    course.getEnrollments().clear();
                }

                // Delete image if exists
                if (course.getImage() != null) {
                    course.setImage(null);
                }

                courseRepository.delete(course);
            }
            user.getCreatedCourses().clear();
        }

        // ✅ 4. Finally delete user
        System.out.println("Deleting user entity now...");
        userRepository.delete(user);

        System.out.println("✅ Successfully deleted user and all related data!");
        return "User deleted successfully!";
    }

    // ✅ Find by username
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toDTO);
    }

    // ✅ Convert Entity → DTO
    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .maxCourses(user.getMax_courses())
                .build();
    }

    // ✅ Login
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDto.getUsername());
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new LoginResponseDto(
                user.getId(),
                token,
                userDetails.getUsername(),
                user.getRole().name(),
                user.getEmail()
        );
    }
}
