package com.example.appgidritexmonitoring.entity;

import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeDescriptor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User extends AbsUUIDEntity implements UserDetails {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @ManyToOne(optional = false)
    private Role role;

    @OneToOne(fetch = FetchType.LAZY)
    private Attachment avatar;

    private boolean enabled = true;

    private LocalDateTime tokenIssuedAt;

    @Column(columnDefinition = "text")
    private String pages;

    @ManyToMany
    private Set<Reservoir> reservoirs;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.role.getPermission();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }


}
