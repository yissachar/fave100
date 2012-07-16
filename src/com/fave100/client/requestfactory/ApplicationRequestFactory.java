package com.fave100.client.requestfactory;

import com.google.web.bindery.requestfactory.shared.RequestFactory;

public interface ApplicationRequestFactory extends RequestFactory {
  
	FaveItemRequest faveItemRequest();
	// TODO other application entity type requests go here ...  
}
