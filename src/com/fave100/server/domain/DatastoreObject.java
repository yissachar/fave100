package com.fave100.server.domain;

import javax.persistence.Id;
import javax.persistence.PrePersist;

public class DatastoreObject
{
    @Id
    private Long id;
    private Integer version = 0;
    
    /**
     * Auto-increment version # whenever persisted
     */
    @PrePersist
    void onPersist()
    {
        this.version++;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Integer getVersion()
    {// TODO: Do we actually need version for every (any?) entity, or is just taking up space?
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }
}

