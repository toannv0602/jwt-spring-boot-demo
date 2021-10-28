package com.example.jwtspringbootdemo.entity;


import com.example.jwtspringbootdemo.eenum.EnumRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name ="roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, name = "name")
    private EnumRole name;

}
