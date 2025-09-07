package com.sprocms.jprj.events.packages.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.sprocms.jprj.events.packages.auth.domain.Role;
import com.sprocms.jprj.events.packages.auth.domain.User;
import com.sprocms.jprj.events.packages.auth.infrastructure.RoleRepository;
import com.sprocms.jprj.events.packages.auth.infrastructure.UserRepository;

public class AuthServicesTest {
    @InjectMocks
    EventsUserDetailsService userDetailsService;

    @InjectMocks
    UseAuth useAuth;

    @Mock
    private BCryptPasswordEncoder mockedPasswordEncoder;

    @Mock
    private UserRepository mockedUserRepository;

    @Mock
    private RoleRepository mockedRoleRepository;

    @Mock
    JwtEncoder mockedEncoder;

    @Mock
    Jwt mockedJwt;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        when(mockedJwt.getTokenValue()).thenReturn("someToken");
        when(mockedEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockedJwt);
        when(mockedPasswordEncoder.encode(any(String.class))).thenReturn("someEncryption");
    }

    @Test
    public void AddUser() {
        User us = useAuth.addUser(new User("testUser",
                "testUser", null));
        assertEquals(us.getUsername(), "testUser");
    }

    @Test
    public void LoadUserByUsername() {
        UserDetails ud = userDetailsService.loadUserByUsername("testUser");
        assertNull(ud);
        Role roleToReturnFromRepository = new Role("USER", "USER Role Desc");
        when(mockedRoleRepository.findByName("USER")).thenReturn(roleToReturnFromRepository);
        User userToReturnFromRepository = useAuth.addUser(new User("testUser",
                "testUser", null));
        when(mockedUserRepository.findByUsername("testUser")).thenReturn(userToReturnFromRepository);
        ud = userDetailsService.loadUserByUsername("testUser");
        assertEquals(ud.getUsername(), "testUser");
        assertEquals(ud.getAuthorities().size(), 1);
        ud.getAuthorities().forEach(role -> {
            assertEquals("ROLE_USER", role.getAuthority());
        });
    }

    @Test
    public void GetToken() {
        Role roleToReturnFromRepository = new Role("USER", "USER Role Desc");
        when(mockedRoleRepository.findByName("USER")).thenReturn(roleToReturnFromRepository);
        User userToReturnFromRepository = useAuth.addUser(new User("testUser",
                "testUser", null));
        when(mockedUserRepository.findByUsername("testUser")).thenReturn(userToReturnFromRepository);
        String token = useAuth.getToken("testUser");
        assertEquals(token, "someToken");
    }

    @Test
    public void Prepare() {
        when(mockedUserRepository.existsByUsername("Events")).thenReturn(true);
        useAuth.prepare();
        assertEquals(true, useAuth.isUserExisted("Events"));
    }
}
