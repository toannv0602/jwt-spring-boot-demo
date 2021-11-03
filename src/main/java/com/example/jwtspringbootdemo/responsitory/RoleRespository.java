package com.example.jwtspringbootdemo.responsitory;

import com.example.jwtspringbootdemo.eenum.EnumRole;
import com.example.jwtspringbootdemo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRespository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(EnumRole name);
}
