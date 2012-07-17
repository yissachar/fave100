package com.fave100.server.domain;

import javax.persistence.Id;
import javax.persistence.PrePersist;

import com.googlecode.objectify.annotation.Unindexed;

public class DatastoreObject
{
	@Unindexed
    @Id
    private Long id;
	@Unindexed
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
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }
}

