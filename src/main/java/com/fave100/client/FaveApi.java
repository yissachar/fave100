package com.fave100.client;

import com.fave100.client.generated.services.RestServiceFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.DispatchRequest;

/**
 * A GWT client for the Fave100 API.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class FaveApi {

	private RestDispatchAsync _dispatcher;
	private RestServiceFactory _restServiceFactory;

	@Inject
	public FaveApi(RestDispatchAsync dispatcher, RestServiceFactory restServiceFactory) {
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;
	}

	/**
	 * Calls a Fave100 API and performs the callback on completion
	 * 
	 * @param action The Fave100 API to call
	 * @param callback The callback to perform on completion
	 * @return a reference to the API request
	 */
	public <A extends RestAction<R>, R> DispatchRequest call(A action, AsyncCallback<R> callback) {
		return _dispatcher.execute(action, callback);
	}

	/**
	 * @return The available Fave100 API endpoints
	 */
	public RestServiceFactory service() {
		return _restServiceFactory;
	}

}
