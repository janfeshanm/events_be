package com.sprocms.jprj.events.packages.auth.application;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.sprocms.jprj.events.packages.auth.domain.Role;
import com.sprocms.jprj.events.packages.auth.domain.User;
import com.sprocms.jprj.events.packages.auth.infrastructure.RoleRepository;
import com.sprocms.jprj.events.packages.auth.infrastructure.UserRepository;
import com.sprocms.jprj.events.packages.pckg.application.UsePackage;
import com.sprocms.jprj.events.packages.pckg.domain.IPackage;

@Service
public class UseAuth implements IPackage {
    @Autowired
    UsePackage usePackage;

    @PostConstruct
    public void postConstruct() {
        usePackage.addPackage(this);
    }

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtEncoder encoder;

    @Autowired
    EventsUserDetailsService userDetailsService;

    long expiry = 3600L;

    public User addUser(User user) {
        String code = bcryptEncoder.encode(user.getPassword()).toString();

        Role role = roleRepository.findByName("USER");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        User nu = new User(user.getUsername(), code, roleSet);
        // nu.setId(user.getId());
        userRepository.save(nu);

        return nu;
    }

    public String getToken(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        int n = authorities.size();
        GrantedAuthority ats[] = new GrantedAuthority[n];
        System.arraycopy(authorities.toArray(), 0, ats, 0, n);

        UserBuilder builder = null;
        builder = org.springframework.security.core.userdetails.User.withUsername(user.getUsername());
        builder.password(user.getPassword());
        builder.authorities(ats);

        UserDetails ud = builder.build();

        String roles = ud.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self").issuedAt(now).expiresAt(now.plusSeconds(expiry))
                .subject(ud.getUsername()).claim("roles", roles)
                .build();

        String token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return token;
    }

    public void prepare() {
        Role role = roleRepository.findByName("USER");
        if (role == null) {
            Role role1 = roleRepository.save(new Role("ADMIN", "Admin Desc ..."));
            Role role2 = roleRepository.save(new Role("USER", "User Desc ..."));
            System.out.println(role1);
            System.out.println(role2);
        }
        User user = userRepository.findByUsername("admin");
        if (user == null) {
            Set<Role> roleSet = new HashSet<>();
            role = roleRepository.findByName("ADMIN");
            roleSet.add(role);
            User uEvents = new User("admin",
                    "$2a$10$B7oehwud4KU/vz.Ft/ODf.VD1vQOSLw5Gque6OJ.h0h1NKQePQVt6", roleSet);
            uEvents.setRoles(roleSet);
            uEvents = userRepository.save(uEvents);
            Set<Role> user1RoleSet = new HashSet<>();
            role = roleRepository.findByName("USER");
            user1RoleSet.add(role);
            User uUser1 = new User("user1",
                    "$2a$10$wnWr4vYjh6x.8yMqPD7KVebjGmdV2CWsOLscMOiGas0FCVHLPgT5K", user1RoleSet);
            uUser1 = userRepository.save(uUser1);
        }
    }

    public boolean isUserExisted(String username) {
        return userRepository.existsByUsername(username);
    }
}
