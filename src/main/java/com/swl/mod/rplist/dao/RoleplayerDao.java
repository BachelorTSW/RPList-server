package com.swl.mod.rplist.dao;

import com.swl.mod.rplist.model.Roleplayer;
import com.swl.mod.rplist.model.RoleplayersInSameInstance;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Repository
public interface RoleplayerDao extends Neo4jRepository<Roleplayer, Long> {

    @Transactional(readOnly = true)
    Roleplayer findByPlayerId(Long playerId);

    @Transactional(readOnly = true)
    List<Roleplayer> findAllByPlayerIdIn(Iterable<Long> playerIds);

    @Transactional
    void deleteByPlayerId(Long playerId);

    @Transactional(readOnly = true)
    @Query("MATCH (r:Roleplayer)-[:IN_SAME_INSTANCE*..10]-(x:Roleplayer) RETURN r as root, collect(distinct x) as inSameInstance;")
    List<RoleplayersInSameInstance> getRoleplayersInSameInstance();

    @Transactional(readOnly = true)
    @Query("MATCH (r:Roleplayer) WHERE not( (r)-[:IN_SAME_INSTANCE]-() ) RETURN r;")
    List<Roleplayer> getRoleplayersInEmptyInstances();

    @Transactional(readOnly = true)
    List<Roleplayer> findAllByIdleOutAtBefore(Instant idleThreshold);

}
