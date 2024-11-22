package com.example.area_backend.TableDb.Tokens;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.area_backend.TableDb.Users.UsersTable;

public interface TokensRepo extends JpaRepository<TokensTable, Long>
{
    Optional<TokensTable> findByAccessToken(String accessToken);
    Optional<List<TokensTable>> findByUsersTable(UsersTable userId);
    void deleteByAccessToken(String accessToken);
    void deleteByUsersTable(UsersTable userId);
}
