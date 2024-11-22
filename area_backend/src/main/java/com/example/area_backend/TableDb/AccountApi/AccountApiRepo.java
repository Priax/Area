package com.example.area_backend.TableDb.AccountApi;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.area_backend.TableDb.Users.UsersTable;

public interface AccountApiRepo extends JpaRepository<AccountApiTable, Long>
{
    Optional<AccountApiTable> findByUsersTable(UsersTable user);
}
