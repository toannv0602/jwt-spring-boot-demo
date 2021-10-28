package com.example.jwtspringbootdemo.responsitory;

import com.example.jwtspringbootdemo.eenum.EnumRole;
import com.example.jwtspringbootdemo.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRespository {

    Optional<Role> findByName(EnumRole name);
}
