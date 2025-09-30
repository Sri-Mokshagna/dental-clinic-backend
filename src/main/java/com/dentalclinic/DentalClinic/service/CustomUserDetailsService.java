package com.dentalclinic.DentalClinic.service;

import com.dentalclinic.DentalClinic.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User; // <-- Spring Security User

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        com.dentalclinic.DentalClinic.model.User userEntity = userRepository.findByUsername(identifier)
                .orElseGet(() -> 
                    // If not found, try phone number
                    userRepository.findByPhoneNumber(identifier)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with: " + identifier))
                );

        return org.springframework.security.core.userdetails.User.builder()
                .username(userEntity.getPhoneNumber() != null ? userEntity.getPhoneNumber() : userEntity.getUsername())
                .password(userEntity.getPassword())
                .disabled(!userEntity.isEnabled())
                .roles(userEntity.getRole() instanceof Enum ? 
                        ((Enum<?>) userEntity.getRole()).name() :
                        userEntity.getRole().toString())
                .build();
    }

}
