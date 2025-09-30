package com.dentalclinic.DentalClinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dentalclinic.DentalClinic.model.Role;
import com.dentalclinic.DentalClinic.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);
    Optional<User> findByPhoneNumber(String phoneNumber);
}
