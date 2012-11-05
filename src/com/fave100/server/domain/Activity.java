package com.fave100.server.domain;

import java.util.Date;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Activity {
	
	@Id private Long id;
	@Index private String username;
	@Load private Ref<Song> song;
	private Transaction transactionType;
	private int previousLocation;
	private int newLocation;
	@Index private Date timestamp;
	
	public enum Transaction {
		FAVE_ADDED, FAVE_REMOVED, FAVE_POSITION_CHANGED, FOLLOWED, FOLLOWED_BY;
	}
	
	public Activity() {}
	
	public Activity(final String username, final Transaction transactionType) {
		this.setUsername(username);
		this.transactionType = transactionType;
		this.timestamp = new Date();
	}
	
	
	
	// Getters and Setters

	public Transaction getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(final Transaction transactionType) {
		this.transactionType = transactionType;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public Ref<Song> getSong() {
		return song;
	}

	public void setSong(final Ref<Song> song) {
		this.song = song;
	}

	public int getPreviousLocation() {
		return previousLocation;
	}

	public void setPreviousLocation(final int previousLocation) {
		this.previousLocation = previousLocation;
	}

	public int getNewLocation() {
		return newLocation;
	}

	public void setNewLocation(final int newLocation) {
		this.newLocation = newLocation;
	}	

}
