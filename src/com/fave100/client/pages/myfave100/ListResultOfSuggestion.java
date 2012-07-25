package com.fave100.client.pages.myfave100;

import java.util.List;


public interface ListResultOfSuggestion {
	
	Integer getResultCount();
	
	List<SuggestionResult> getResults();
	

}
