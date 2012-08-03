package com.fave100.server.domain;


import javax.persistence.PrePersist;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class DatastoreObject
{
    private Integer version = 0;
    
    /**
     * Auto-increment version # whenever persisted
     */
    @PrePersist
    void onPersist()
    {
        this.version++;
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

