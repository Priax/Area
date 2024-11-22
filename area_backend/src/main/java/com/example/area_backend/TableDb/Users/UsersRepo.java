package com.example.area_backend.TableDb.Users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepo extends JpaRepository<UsersTable, Long>
{
    Optional<UsersTable> findByEmail(String email);
}
