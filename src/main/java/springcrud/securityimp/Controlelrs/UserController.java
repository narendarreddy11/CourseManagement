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
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }


}
