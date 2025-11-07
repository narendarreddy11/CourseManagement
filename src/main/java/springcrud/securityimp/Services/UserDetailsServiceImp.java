package springcrud.securityimp.Services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springcrud.securityimp.Repo.UserRepo;
import springcrud.securityimp.model.User;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    private final UserRepo userRepo;

    public UserDetailsServiceImp(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username).orElseThrow();
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name().replace("ROLE_", "")) // Spring expects "USER", "ADMIN"
                .build();
    }
}
