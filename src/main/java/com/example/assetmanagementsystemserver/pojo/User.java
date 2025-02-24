package com.example.assetmanagementsystemserver.pojo;

import com.example.assetmanagementsystemserver.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;


@Data
@ToString(callSuper = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    //GenerationType.IDENTITY 表示主键由数据库自动生成（通常是自增列）
    @GeneratedValue(strategy = GenerationType.IDENTITY)//
    private int id;

    //nullable = false 表示该列不允许为 null。
    //unique = true 表示该列的值必须唯一。
    @Column(name = "user_name",nullable = false, unique = true)
    private String userName;

    @Column(name = "user_password",nullable = false)
    private String userPassword;

    //这个注解用于将枚举类型映射到数据库列。
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEnum role;

    @Column(nullable = false)
    private Date createdAt;

}
