package springcrud.securityimp.Controlelrs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springcrud.securityimp.Dto.LoginRequestDto;
import springcrud.securityimp.Dto.LoginResponseDto;
import springcrud.securityimp.Dto.UserDTO;
import springcrud.securityimp.Dto.UserRegisterDTO;
import springcrud.securityimp.Services.UserServiceImp;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImp userService;

    // ✅ Register new user
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegisterDTO dto) {
        UserDTO user = userService.register(dto);
        return ResponseEntity.ok(user);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto req) {
        return ResponseEntity.ok(userService.login(req));
    }

    // ✅ Get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ✅ Get user by ID
    @GetMapping("/id/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/name/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Delete user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            String message = userService.deleteUserById(id);

            // Always return 200 OK with message
            return ResponseEntity.ok(Map.of("message", message));

        } catch (RuntimeException e) {
            // If admin restriction → treat as warning but still 200
            if ("Admin accounts cannot be deleted!".equals(e.getMessage())) {
                return ResponseEntity.ok(Map.of("message", e.getMessage()));
            }

            // Other errors (e.g., user not found) → 400
            String errorMsg = (e.getMessage() != null && !e.getMessage().isBlank())
                    ? e.getMessage()
                    : "Error deleting user";

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", errorMsg));
        }
    }


}
