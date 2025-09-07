package com.sprocms.jprj.events.packages.auth.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sprocms.jprj.events.packages.auth.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    boolean existsById(String id);

    @Query("select count(p) = 1 from User p where username = ?1")
    boolean existsByUsername(String username);
}
