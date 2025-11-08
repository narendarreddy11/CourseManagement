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
    public void deleteUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Step 1: Break relationships before deleting
        // Remove user from all enrollments
        user.getEnrollments().forEach(enrollment -> {
            enrollment.setStudent(null);
            enrollment.setCourse(null);
        });
        user.getEnrollments().clear();

        // If user created courses (admin case), break that link too
        user.getCreatedCourses().forEach(course -> course.setCreatedBy(null));
        user.getCreatedCourses().clear();

        // ✅ Step 2: Delete profile manually if exists
        if (user.getProfile() != null) {
            user.getProfile().setUser(null);
            user.setProfile(null);
        }

        // ✅ Step 3: Delete the user
        userRepository.delete(user);
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
