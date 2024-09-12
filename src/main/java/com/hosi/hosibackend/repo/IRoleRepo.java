package com.hosi.hosibackend.repo;

import com.hosi.hosibackend.entity.Role;
import com.hosi.hosibackend.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepo extends JpaRepository<Role, Long> {
    Role findRoleByRole(RoleName role);
}
