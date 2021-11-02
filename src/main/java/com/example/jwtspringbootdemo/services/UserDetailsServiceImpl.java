package com.example.jwtspringbootdemo.services;

import com.example.jwtspringbootdemo.entity.User;
import com.example.jwtspringbootdemo.responsitory.UserReponsitory;
import com.example.jwtspringbootdemo.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserDetailsServiceImpl {

    @Autowired
    UserReponsitory userReponsitory;

    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userReponsitory.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found with username: "+ username));
        return UserDetailsImpl.build(user);
    }


}
