package likelion.devbreak.service;

import likelion.devbreak.domain.User;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class GlobalService {
    private final UserRepository userRepository;

    public GlobalService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User findUser(CustomUserDetails customUserDetails){
        User user = userRepository.findById(customUserDetails.getId())
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        return user;
    }
}
