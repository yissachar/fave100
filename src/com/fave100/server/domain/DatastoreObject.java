package com.fave100.server.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.OnSave;

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
	@OnSave
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
