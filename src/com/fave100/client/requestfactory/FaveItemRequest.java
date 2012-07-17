package com.fave100.client.requestfactory;

import java.util.List;

import com.fave100.server.domain.FaveItem;
import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(FaveItem.class)
public interface FaveItemRequest extends RequestContext{
	
	Request<FaveItemProxy> findFaveItem(Long id);
	Request<List<FaveItemProxy>> getAllFaveItemsForUser();
	Request<Void> removeFaveItem(Long long1);
	InstanceRequest<FaveItemProxy, FaveItemProxy> persist();
	InstanceRequest<FaveItemProxy, Void> remove();

}
