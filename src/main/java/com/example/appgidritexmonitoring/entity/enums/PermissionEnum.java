package com.example.appgidritexmonitoring.entity.enums;

import org.springframework.security.core.GrantedAuthority;

public enum PermissionEnum implements GrantedAuthority {
    ;


    @Override
    public String getAuthority() {
        return name();
    }



}
