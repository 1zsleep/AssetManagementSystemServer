package com.example.assetManagementSystemServer.dto;

import com.example.assetManagementSystemServer.enums.RoleEnum;
import lombok.*;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int id;
    private String userName;
    private RoleEnum role;
    private Date createdAt;
    private boolean status;

    public UserDTO(int id, String userName, Date createdAt, RoleEnum role, boolean status) {
        this.id = id;
        this.userName = userName;
        this.createdAt = createdAt;
        this.role = role;
        this.status = status;
    }

}
