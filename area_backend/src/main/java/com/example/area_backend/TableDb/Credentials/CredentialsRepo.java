package com.example.area_backend.TableDb.Credentials;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.area_backend.TableDb.Users.UsersTable;

@Repository
public interface CredentialsRepo extends JpaRepository<CredentialsTable, Long>
{
    Optional<CredentialsTable> findByUsersTable(UsersTable userId);
}
