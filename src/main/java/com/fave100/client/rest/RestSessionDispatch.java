package com.fave100.client.rest;

import javax.inject.Inject;

import com.fave100.shared.Constants;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.rest.client.RestDispatchCall;
import com.gwtplatform.dispatch.rest.client.RestDispatchCallFactory;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;
import com.gwtplatform.dispatch.rest.shared.RestParameter;
import com.gwtplatform.dispatch.shared.DispatchRequest;

/*
 * A RestDispatcher that injects session info into each request.
 */
public class RestSessionDispatch implements RestDispatch {
	private final RestDispatchCallFactory callFactory;

	@Inject
	RestSessionDispatch(RestDispatchCallFactory callFactory) {
		this.callFactory = callFactory;
	}

	@Override
	public <A extends RestAction<R>, R> DispatchRequest execute(A action, AsyncCallback<R> callback) {
		// Inject the session id before making the call
		String sessionId = Cookies.getCookie(Constants.SESSION_HEADER);

		if (sessionId != null)
			action.getHeaderParams().add(new RestParameter(Constants.SESSION_HEADER, sessionId));

		RestDispatchCall<A, R> call = callFactory.create(action, callback);

		return call.execute();
	}
}
