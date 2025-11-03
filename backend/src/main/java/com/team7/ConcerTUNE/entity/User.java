package com.team7.ConcerTUNE.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@AllArgsConstructor
class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(length = 200)
    private String password;

    @Column(length = 200)
    private String email;

    @Column(name = "phone_num", length = 20)
    private String phoneNum;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    @Column(length = 10)
    private String username;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Column(name = "isEnabled")
    private Boolean enabled = true;

    @Column(length = 48)
    private String provider;

    @Column(length = 500)
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String tags;

    @Enumerated(EnumType.STRING)
    @Column(length = 48, nullable = false)
    private AuthType auth = AuthType.USER;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = "ROLE_" + this.auth.name();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public boolean isEnabled() {return enabled;}

    @Builder
    public User(String email, String password, String username, AuthType auth,
                String phoneNum, String profileImageUrl, String providerId,
                Boolean enabled, String provider, String bio, String tags) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.auth = auth != null ? auth : AuthType.USER;
        this.phoneNum = phoneNum;
        this.profileImageUrl = profileImageUrl;
        this.providerId = providerId;
        this.enabled = enabled != null ? enabled : true;
        this.provider = provider;
        this.bio = bio;
        this.tags = tags;
    }
}