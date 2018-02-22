package com.swl.mod.rplist.dao;

import com.swl.mod.rplist.model.Roleplayer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleplayerDao extends CrudRepository<Roleplayer, Long> {

    List<Roleplayer> findByInstanceId(String instanceId);

}
