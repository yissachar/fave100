package com.fave100.server.domain;

import javax.persistence.PrePersist;

import com.googlecode.objectify.annotation.Entity;

/**
 * A base entity that contains required version property and incrementor for
 * RequestFactory entities
 * 
 * @author yissachar.radcliffe
 * 
 */

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

	public void setVersion(final Integer version)
	{
		this.version = version;
	}
}
