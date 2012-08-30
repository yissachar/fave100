package com.fave100.client.pages.users;

import java.util.List;

/**
 * Interface to facilitate working with JSON results from iTunes *
 */

public interface ListResultOfSuggestion {
	
	Integer getResultCount();
	
	List<SuggestionResult> getResults();
	

}
