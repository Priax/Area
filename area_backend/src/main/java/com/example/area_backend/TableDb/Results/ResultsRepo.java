package com.example.area_backend.TableDb.Results;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.area_backend.TableDb.Users.UsersTable;

public interface ResultsRepo extends JpaRepository<ResultsTable, Long>
{
    void deleteByUserTable(UsersTable usersTable);
}
