package com.dentalclinic.DentalClinic.service;


import java.util.Optional;

import com.dentalclinic.DentalClinic.model.Role;

public interface RoleService {
    Role save(Role role);
    Optional<Role> findByName(String name);
}
