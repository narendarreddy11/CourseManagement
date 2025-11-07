package springcrud.securityimp.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import springcrud.securityimp.Dto.ProfileDTO;
import springcrud.securityimp.Repo.ProfileRepo;
import springcrud.securityimp.Repo.UserRepo;
import springcrud.securityimp.model.Profile;
import springcrud.securityimp.model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepo profileRepo;
    private final UserRepo userRepo;

    private final String uploadDir = "uploads/profile-images/";

    /**
     * ✅ Create or Update a user's profile
     */
    @Transactional
    public ProfileDTO createOrUpdateProfile(int userId, ProfileDTO dto, MultipartFile image) throws IOException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find existing profile or create new one
        Profile profile = profileRepo.findByUserId(userId);
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
        }

        // Update fields
        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setPhonoenumber(dto.getPhone());
        profile.setAddress(dto.getAddress());
        profile.setBio(dto.getBio());

        // ✅ Handle profile image upload (replace old if exists)
        if (image != null && !image.isEmpty()) {
            Files.createDirectories(Paths.get(uploadDir));

            // Delete old image file if exists
            if (profile.getProfileImagePath() != null) {
                File oldFile = new File(profile.getProfileImagePath().substring(1)); // remove leading "/"
                if (oldFile.exists()) oldFile.delete();
            }

            String fileName = user.getUsername() + "_" + System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, image.getBytes());

            profile.setProfileImagePath("/" + uploadDir + fileName);
        }

        profileRepo.save(profile);
        return toDTO(profile);
    }

    /**
     * ✅ Get profile by user ID
     */
    public ProfileDTO getProfile(int userId) {
        Profile profile = profileRepo.findByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("Profile not found for user ID: " + userId);
        }
        return toDTO(profile);
    }

    /**
     * ✅ Convert entity → DTO
     */
    private ProfileDTO toDTO(Profile p) {
        return ProfileDTO.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .phone(p.getPhonoenumber())
                .address(p.getAddress())
                .bio(p.getBio())
                .profileImagePath(p.getProfileImagePath())
                .build();
    }


}
