package com.bariscan.sigorta_hatirlatici.service;

import com.bariscan.sigorta_hatirlatici.dto.userDtos.ChangePassDto;
import com.bariscan.sigorta_hatirlatici.dto.userDtos.UserDto;
import com.bariscan.sigorta_hatirlatici.entity.User;
import com.bariscan.sigorta_hatirlatici.exceptions.NotFoundException;
import com.bariscan.sigorta_hatirlatici.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
@Log4j2
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createNewUser(UserDto userDto) {
        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .enabled(true)
                .roles(null)
                .build();

        log.trace("User " + user.getEmail() + " was created on " + new Date());
        return userRepository.save(user);
    }

    public String changePassword(ChangePassDto changePassDto) throws NotFoundException {
        User user = userRepository.findById(changePassDto.getId()).orElseThrow(() -> new NotFoundException("User not found"));

        if(!changePassDto.getNewPass().equals(changePassDto.getNewPassAgain())){
            log.trace("User " + user.getEmail() + " tried to change password on " + new Date());
            return "Passwords are not same.";
        }
        if(passwordEncoder.matches(changePassDto.getNewPass(),user.getPassword())){
            user.setPassword(passwordEncoder.encode(changePassDto.getNewPass()));
            userRepository.save(user);
            log.trace("User " + user.getEmail() + " changed password on " + new Date());
            return "Password changed.";
        }else{
            log.trace("User " + user.getEmail() + " tried to change password on " + new Date());
            return "Password is not correct.";
        }
    }

    public Optional<User> findUserById(Long id){
        return userRepository.findById(id);
    }
}
