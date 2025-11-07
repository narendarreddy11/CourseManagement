package springcrud.securityimp.Controlelrs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springcrud.securityimp.Dto.ProfileDTO;
import springcrud.securityimp.Services.ProfileService;

import java.io.IOException;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * ✅ Create or Update user profile
     * Supports form-data upload (text + optional image)
     */
    @PostMapping(value = "/{userId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ProfileDTO> createOrUpdateProfile(
            @PathVariable int userId,
            @RequestPart("profile") ProfileDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        ProfileDTO updatedProfile = profileService.createOrUpdateProfile(userId, dto, image);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * ✅ Get user profile by user ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> getProfile(@PathVariable int userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
    }
}
