package com.swl.mod.rplist.dao;

import com.swl.mod.rplist.model.Roleplayer;
import com.swl.mod.rplist.model.RoleplayersInSameInstance;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface RoleplayerDao extends Neo4jRepository<Roleplayer, Long> {

    Roleplayer findByPlayerId(Long playerId);

    List<Roleplayer> findAllByPlayerIdIn(Iterable<Long> playerIds);

    void deleteByPlayerId(Long playerId);

    @Query("MATCH (r:Roleplayer)-[:IN_SAME_INSTANCE*..30]-(x:Roleplayer) RETURN r as root, collect(distinct x) as inSameInstance;")
    List<RoleplayersInSameInstance> getRoleplayersInSameInstance();

    @Query("MATCH (r:Roleplayer) WHERE not( (r)-[:IN_SAME_INSTANCE]-() ) RETURN r;")
    List<Roleplayer> getRoleplayersInEmptyInstances();

    List<Roleplayer> findAllByIdleOutAtBefore(Instant idleThreshold);

}
