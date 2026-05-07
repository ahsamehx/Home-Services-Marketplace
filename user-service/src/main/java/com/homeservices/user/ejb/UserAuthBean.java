package com.homeservices.user.ejb;

import jakarta.ejb.Stateless;
import org.springframework.stereotype.Component;
import com.homeservices.user.entity.User;
import com.homeservices.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Stateless
@Component
public class UserAuthBean {

    @Autowired
    private UserRepository userRepository;

    public User authenticate(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new Exception("User not found");
        }
        if (!user.getPassword().equals(password)) {
            throw new Exception("Invalid password");
        }
        if (!user.isActive()) {
            throw new Exception("User account is inactive");
        }
        return user;
    }

    public User registerUser(String username, String password, String userType, String profession) throws Exception {
        if (userRepository.findByUsername(username) != null) {
            throw new Exception("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setUserType(userType);
        user.setProfession(profession);
        user.setWalletBalance(0.0);
        user.setActive(true);

        return userRepository.save(user);
    }

    public User getUserById(Long userId) throws Exception {
        return userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
    }
}
