package com.fave100.shared.requestfactory;

import java.util.List;

import com.fave100.server.domain.SearchResult;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(SearchResult.class)
public interface SearchResultProxy extends ValueProxy {

	List<SongProxy> getResults();

	int getTotal();

}
