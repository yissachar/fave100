package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;

@Entity
@Index
public class Song extends DatastoreObject {
	@Id private String id;
	@Index private long score = 0;
	// iTunes results 
	private Integer trackId;
	private String wrapperType;
	private String kind;
	private Integer artistId;
	private Integer collectionId;	
	private String artistName;
	private String collectionName;
	private String trackName;
	private String collectionCensoredName;
	private String trackCensoredName;
	private String artistViewUrl;
	private String collectionViewUrl;
	private String trackViewUrl;
	private String previewUrl;
	private String artworkUrl60;
	private Double collectionPrice;
	private Double trackPrice;
	private String releaseDate;
	private String collectionExplicitness;	
	private String trackExplicitness;
	private Integer discCount;
	private Integer discNumber;
	private Integer trackCount;
	private Integer trackNumber;
	private Integer trackTimeMillis;
	private String country;
	private String currency;
	private String primaryGenreName;
	@IgnoreSave private String whyline;
	@IgnoreSave private int whylineScore;
	
	//TODO: Need to periodically update cache
	
	public static Song findSong(final Long id) {
		//return ofy().get(Song.class, id);
		return ofy().load().type(Song.class).id(id).get();
	}
	
	public void addScore(final int score) {
		this.score += score;
	}
	
	@OnLoad
	@SuppressWarnings("unused")	 
	private void onLoad(final Objectify ofy) {		 
		final List<Whyline> list =  ofy.load().type(Whyline.class)						
										.filter("song", Ref.create(Key.create(Song.class, getId())))
										.order("score")
										.limit(1)
										.list();
		if(list.size() > 0) {
			whyline = list.get(0).getWhyline();
		} else {
			whyline = "";
		}
		
	}
	/* Getters and setters */	
	
	public String getWrapperType() {
		return wrapperType;
	}

	public void setWrapperType(final String wrapperType) {
		this.wrapperType = wrapperType;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(final String kind) {
		this.kind = kind;
	}

	public Integer getArtistId() {
		return artistId;
	}

	public void setArtistId(final Integer artistId) {
		this.artistId = artistId;
	}

	public Integer getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(final Integer collectionId) {
		this.collectionId = collectionId;
	}

	public Integer getTrackId() {
		return trackId;
	}

	public void setTrackId(final Integer trackId) {
		this.trackId = trackId;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(final String artistName) {
		this.artistName = artistName;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(final String collectionName) {
		this.collectionName = collectionName;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(final String trackName) {
		this.trackName = trackName;
	}

	public String getCollectionCensoredName() {
		return collectionCensoredName;
	}

	public void setCollectionCensoredName(final String collectionCensoredName) {
		this.collectionCensoredName = collectionCensoredName;
	}

	public String getTrackCensoredName() {
		return trackCensoredName;
	}

	public void setTrackCensoredName(final String trackCensoredName) {
		this.trackCensoredName = trackCensoredName;
	}

	public String getArtistViewUrl() {
		return artistViewUrl;
	}

	public void setArtistViewUrl(final String artistViewUrl) {
		this.artistViewUrl = artistViewUrl;
	}

	public String getCollectionViewUrl() {
		return collectionViewUrl;
	}

	public void setCollectionViewUrl(final String collectionViewUrl) {
		this.collectionViewUrl = collectionViewUrl;
	}

	public String getTrackViewUrl() {
		return trackViewUrl;
	}

	public void setTrackViewUrl(final String trackViewUrl) {
		this.trackViewUrl = trackViewUrl;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(final String previewUrl) {
		this.previewUrl = previewUrl;
	}

	public Double getCollectionPrice() {
		return collectionPrice;
	}

	public void setCollectionPrice(final Double collectionPrice) {
		this.collectionPrice = collectionPrice;
	}

	public Double getTrackPrice() {
		return trackPrice;
	}

	public void setTrackPrice(final Double trackPrice) {
		this.trackPrice = trackPrice;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(final String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getCollectionExplicitness() {
		return collectionExplicitness;
	}

	public void setCollectionExplicitness(final String collectionExplicitness) {
		this.collectionExplicitness = collectionExplicitness;
	}

	public String getTrackExplicitness() {
		return trackExplicitness;
	}

	public void setTrackExplicitness(final String trackExplicitness) {
		this.trackExplicitness = trackExplicitness;
	}

	public Integer getDiscCount() {
		return discCount;
	}

	public void setDiscCount(final Integer discCount) {
		this.discCount = discCount;
	}

	public Integer getDiscNumber() {
		return discNumber;
	}

	public void setDiscNumber(final Integer discNumber) {
		this.discNumber = discNumber;
	}

	public Integer getTrackCount() {
		return trackCount;
	}

	public void setTrackCount(final Integer trackCount) {
		this.trackCount = trackCount;
	}

	public Integer getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(final Integer trackNumber) {
		this.trackNumber = trackNumber;
	}

	public Integer getTrackTimeMillis() {
		return trackTimeMillis;
	}

	public void setTrackTimeMillis(final Integer trackTimeMillis) {
		this.trackTimeMillis = trackTimeMillis;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(final String currency) {
		this.currency = currency;
	}

	public String getPrimaryGenreName() {
		return primaryGenreName;
	}

	public void setPrimaryGenreName(final String primaryGenreName) {
		this.primaryGenreName = primaryGenreName;
	}

	public long getScore() {
		return score;
	}

	public void setScore(final long score) {
		this.score = score;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getArtworkUrl60() {
		return artworkUrl60;
	}

	public void setArtworkUrl60(final String artworkUrl60) {
		this.artworkUrl60 = artworkUrl60;
	}

	public String getWhyline() {
		return whyline;
	}

	public void setWhyline(final String whyline) {
		this.whyline = whyline;
	}

	public int getWhylineScore() {
		return whylineScore;
	}

	public void setWhylineScore(final int whylineScore) {
		this.whylineScore = whylineScore;
	}

}
