package com.swl.mod.rplist.model;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Set;

@QueryResult
public class RoleplayersInSameInstance {
    public Roleplayer root;
    public Set<Roleplayer> inSameInstance;
}
