package com.swl.mod.rplist.dao;

import com.swl.mod.rplist.model.Roleplayer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleplayerDao extends CrudRepository<Roleplayer, Long> {

    Roleplayer findByPlayerId(Long playerId);

    // Currently not supported by Redis
//    List<Roleplayer> findAllByPlayerIdIn(Iterable<Long> playerIds);

    List<Roleplayer> findAllByPlayfieldIdAndInstanceId(Integer playfieldId, Integer instanceId);

    // Currently not supported by Redis
//    int countAllByPlayfieldIdAndInstanceId(Integer playfieldId, Integer instanceId);

}
