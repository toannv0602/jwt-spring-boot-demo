package com.example.jwtspringbootdemo.controller;

import com.example.jwtspringbootdemo.dto.JwtResponse;
import com.example.jwtspringbootdemo.dto.LoginRequest;
import com.example.jwtspringbootdemo.dto.MessageResponse;
import com.example.jwtspringbootdemo.dto.SignupRequest;
import com.example.jwtspringbootdemo.eenum.EnumRole;
import com.example.jwtspringbootdemo.entity.Role;
import com.example.jwtspringbootdemo.entity.User;
import com.example.jwtspringbootdemo.responsitory.RoleRespository;
import com.example.jwtspringbootdemo.responsitory.UserReponsitory;
import com.example.jwtspringbootdemo.security.JwtUtils;
import com.example.jwtspringbootdemo.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserReponsitory userReponsitory;

    @Autowired
    RoleRespository roleRespository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Validated @RequestBody SignupRequest signupRequest){
        if(userReponsitory.existsByUsername(signupRequest.getUsername())){
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: username is already in use!"));

        }
        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null){
            Role userRole = roleRespository.findByName(EnumRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
            roles.add(userRole);
        }else{
            strRoles.forEach(role ->{
                switch (role){
                    case "admin":
                        Role adminRole  = roleRespository.findByName(EnumRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRespository.findByName(EnumRole.ROLE_MODERATOR)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found!"));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRespository.findByName(EnumRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userReponsitory.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }



}
