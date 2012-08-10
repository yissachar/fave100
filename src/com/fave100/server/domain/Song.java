package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Index
public class Song extends DatastoreObject {
	@Id private Long id;
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
	
	public static Song findSong(Long id) {
		//return ofy().get(Song.class, id);
		return ofy().load().type(Song.class).id(id).get();
	}
	
	public void addScore(int score) {
		this.score += score;
	}
	
	/*public Song persist() {
		ofy().put(this);
		return this;
	}
	
	public void remove() {
		ofy().delete(this);
	}*/
	
	/* Getters and setters */	
	
	public String getWrapperType() {
		return wrapperType;
	}

	public void setWrapperType(String wrapperType) {
		this.wrapperType = wrapperType;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public Integer getArtistId() {
		return artistId;
	}

	public void setArtistId(Integer artistId) {
		this.artistId = artistId;
	}

	public Integer getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(Integer collectionId) {
		this.collectionId = collectionId;
	}

	public Integer getTrackId() {
		return trackId;
	}

	public void setTrackId(Integer trackId) {
		this.trackId = trackId;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public String getCollectionCensoredName() {
		return collectionCensoredName;
	}

	public void setCollectionCensoredName(String collectionCensoredName) {
		this.collectionCensoredName = collectionCensoredName;
	}

	public String getTrackCensoredName() {
		return trackCensoredName;
	}

	public void setTrackCensoredName(String trackCensoredName) {
		this.trackCensoredName = trackCensoredName;
	}

	public String getArtistViewUrl() {
		return artistViewUrl;
	}

	public void setArtistViewUrl(String artistViewUrl) {
		this.artistViewUrl = artistViewUrl;
	}

	public String getCollectionViewUrl() {
		return collectionViewUrl;
	}

	public void setCollectionViewUrl(String collectionViewUrl) {
		this.collectionViewUrl = collectionViewUrl;
	}

	public String getTrackViewUrl() {
		return trackViewUrl;
	}

	public void setTrackViewUrl(String trackViewUrl) {
		this.trackViewUrl = trackViewUrl;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	public Double getCollectionPrice() {
		return collectionPrice;
	}

	public void setCollectionPrice(Double collectionPrice) {
		this.collectionPrice = collectionPrice;
	}

	public Double getTrackPrice() {
		return trackPrice;
	}

	public void setTrackPrice(Double trackPrice) {
		this.trackPrice = trackPrice;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	public String getReleaseYear() {
		return releaseDate.substring(0, 4);
	}

	public String getCollectionExplicitness() {
		return collectionExplicitness;
	}

	public void setCollectionExplicitness(String collectionExplicitness) {
		this.collectionExplicitness = collectionExplicitness;
	}

	public String getTrackExplicitness() {
		return trackExplicitness;
	}

	public void setTrackExplicitness(String trackExplicitness) {
		this.trackExplicitness = trackExplicitness;
	}

	public Integer getDiscCount() {
		return discCount;
	}

	public void setDiscCount(Integer discCount) {
		this.discCount = discCount;
	}

	public Integer getDiscNumber() {
		return discNumber;
	}

	public void setDiscNumber(Integer discNumber) {
		this.discNumber = discNumber;
	}

	public Integer getTrackCount() {
		return trackCount;
	}

	public void setTrackCount(Integer trackCount) {
		this.trackCount = trackCount;
	}

	public Integer getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(Integer trackNumber) {
		this.trackNumber = trackNumber;
	}

	public Integer getTrackTimeMillis() {
		return trackTimeMillis;
	}

	public void setTrackTimeMillis(Integer trackTimeMillis) {
		this.trackTimeMillis = trackTimeMillis;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPrimaryGenreName() {
		return primaryGenreName;
	}

	public void setPrimaryGenreName(String primaryGenreName) {
		this.primaryGenreName = primaryGenreName;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
