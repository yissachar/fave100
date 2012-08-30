package com.fave100.client.pages.users;

/**
 * Interface to facilitate working with JSON results from iTunes *
 */
public interface SuggestionResult {
	String getWrapperType();
	//void setWrapperType(String wrapperType);
	String getKind();
	//void setKind(String kind);
	Integer getArtistId();
	//void setArtistId(Integer artistId);
	Integer getCollectionId();
	//void setcollectionId(Integer collectionId);
	Integer getTrackId();
	//void setTrackId(Integer trackId);
	String getArtistName();
	//void setArtistName(String artistName);
	String getCollectionName();
	//void setCollectionName(String collectionName);
	String getTrackName();
	//void setTrackName(String trackName);
	String getCollectionCensoredName();
	//void setCollectionCensoredName(String collectionCensoredName);
	String getTrackCensoredName();
	//void setTrackCensoredName(String trackCensoredName);
	String getArtistViewUrl();
	//void setArtistViewUrl(String artistViewUrl);
	String getCollectionViewUrl();
	//void setCollectionViewUrl(String collectionViewUrl);
	String getTrackViewUrl();
	//void settrackViewUrl(String trackViewUrl);
	String getPreviewUrl();
	//void setPreviewUrl(String previewUrl);
	String getArtworkUrl60();
	Double getCollectionPrice();
	//void setCollectionPrice(Double collectionPrice);
	Double getTrackPrice();
	//void setTrackPrice(Double trackPrice);
	String getReleaseDate();
	//void setReleaseDate(String releaseDate);
	String getCollectionExplicitness();
	//void setCollectionExplicitness(String collectionExplicitness);	
	String getTrackExplicitness();
	//void setTrackExplicitness(String trackExplicitness);
	Integer getDiscCount();
	//void setDiscCount(String discCount);
	Integer getDiscNumber();
	//void setDiscNumber(Integer discNumber);
	Integer getTrackCount();
	//void setTrackCount(Integer trackCount);
	Integer getTrackNumber();
	//void setTrackNumber(Integer trackNumber);
	Integer getTrackTimeMillis();
	//void setTrackTimeMillis(Integer trackTimeMillis);
	String getCountry();
	//void setCountry(String country);
	String getCurrency();
	//void setCurrecny(String currency);
	String getPrimaryGenreName();
	//void setPrimaryGenreName(String primaryGenreName);
	
}


