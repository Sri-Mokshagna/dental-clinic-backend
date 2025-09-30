package com.dentalclinic.DentalClinic.config;

import com.dentalclinic.DentalClinic.model.Role;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

   @Bean
   public CommandLineRunner createDefaultUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
       return args -> {
           // Create Admin user
           String adminUsername = "Admindental";
           String adminPassword = "Admin@07";

           if (userRepository.findByUsername(adminUsername).isEmpty()) {
               User admin = new User();
               admin.setUsername(adminUsername);
               admin.setFullName("System Administrator");
               admin.setEmail("admin@clinic.com");
               admin.setPhoneNumber("1234567890");
               admin.setPassword(passwordEncoder.encode(adminPassword));
               admin.setRole(Role.ADMIN);
               admin.setEnabled(true);

               userRepository.save(admin);
               System.out.println("✅ Admin account created: username='" + adminUsername + "', password='" + adminPassword + "'");
           } else {
               System.out.println("ℹ️ Admin account already exists.");
           }

           // Create Doctor user
           String doctorUsername = "doctor";
           String doctorPassword = "doctor";

           if (userRepository.findByUsername(doctorUsername).isEmpty()) {
               User doctor = new User();
               doctor.setUsername(doctorUsername);
               doctor.setFullName("Dr. John Smith");
               doctor.setEmail("doctor@clinic.com");
               doctor.setPhoneNumber("1234567891");
               doctor.setPassword(passwordEncoder.encode(doctorPassword));
               doctor.setRole(Role.DOCTOR);
               doctor.setEnabled(true);

               userRepository.save(doctor);
               System.out.println("✅ Doctor account created: username='" + doctorUsername + "', password='" + doctorPassword + "'");
           } else {
               System.out.println("ℹ️ Doctor account already exists.");
           }

           // Create Staff user
           String staffUsername = "staff";
           String staffPassword = "staff";

           if (userRepository.findByUsername(staffUsername).isEmpty()) {
               User staff = new User();
               staff.setUsername(staffUsername);
               staff.setFullName("Jane Doe");
               staff.setEmail("staff@clinic.com");
               staff.setPhoneNumber("1234567892");
               staff.setPassword(passwordEncoder.encode(staffPassword));
               staff.setRole(Role.STAFF);
               staff.setEnabled(true);

               userRepository.save(staff);
               System.out.println("✅ Staff account created: username='" + staffUsername + "', password='" + staffPassword + "'");
           } else {
               System.out.println("ℹ️ Staff account already exists.");
           }
       };
   }
}
