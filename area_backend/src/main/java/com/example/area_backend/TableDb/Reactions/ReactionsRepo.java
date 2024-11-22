package com.example.area_backend.TableDb.Reactions;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.area_backend.TableDb.Actions.ActionsTable;

import java.util.List;

public interface ReactionsRepo extends JpaRepository<ReactionsTable, Long>
{
    List<ReactionsTable> findByActionTable(ActionsTable actionTable);
}
