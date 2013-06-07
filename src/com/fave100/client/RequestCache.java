package com.fave100.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * Stores common RequestFactory requests and their results in order to prevent constant trips to the server for unchanged data.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class RequestCache {

	enum RequestType {
		GOOGLE_LOGIN,
		FACEBOOK_LOGIN
	}

	private ApplicationRequestFactory _requestFactory;

	private Map<RequestType, Boolean> _runningRequests = new HashMap<RequestType, Boolean>();
	private Map<RequestType, Object> _callbacks = new HashMap<RequestType, Object>();
	private Map<RequestType, Object> _results = new HashMap<RequestType, Object>();

	@Inject
	public RequestCache(final ApplicationRequestFactory requestFactory) {
		_requestFactory = requestFactory;
	}

	public void getGoogleUrl(final String redirect, final AsyncCallback<String> callback) {
		getLoginUrl(RequestType.GOOGLE_LOGIN, redirect, callback);
	}

	public void getFacebookUrl(final String redirect, final AsyncCallback<String> callback) {
		getLoginUrl(RequestType.FACEBOOK_LOGIN, redirect, callback);
	}

	private void getLoginUrl(final RequestType request, final String redirect, final AsyncCallback<String> callback) {
		final String loginUrl = ((String)_results.get(request));
		final List<AsyncCallback<String>> callbacks = getOrCreateCallbacks(request);
		final boolean reqRunning = (_runningRequests.get(request) != null) ? _runningRequests.get(request) : false;
		// Add the callback to list of callbacks to notify		
		callbacks.add(callback);
		// If we already have the loginUrl, return		
		if (loginUrl != null) {
			for (final AsyncCallback<String> gCallback : callbacks) {
				gCallback.onSuccess(loginUrl);
				callbacks.remove(callback);
			}
			return;
		}

		// If there is no existing request, create one
		if (reqRunning == false) {
			_runningRequests.put(request, true);
			Request<String> loginUrlReq = null;
			switch (request) {
				case GOOGLE_LOGIN:
					loginUrlReq = _requestFactory.appUserRequest().getGoogleLoginURL(redirect);
					break;
				case FACEBOOK_LOGIN:
					loginUrlReq = _requestFactory.appUserRequest().getFacebookAuthUrl(redirect);
					break;
				default:
					// No request to fire
					return;
			}

			loginUrlReq.fire(new Receiver<String>() {
				@Override
				public void onSuccess(final String url) {
					for (final AsyncCallback<String> gCallback : callbacks) {
						gCallback.onSuccess(url);
						callbacks.remove(gCallback);
					}
					_runningRequests.put(request, false);
				}

				@Override
				public void onFailure(final ServerFailure failure) {
					// Clean all callbacks
					for (final AsyncCallback<String> gCallback : callbacks) {
						callbacks.remove(gCallback);
					}
					_runningRequests.put(request, false);
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	private <T> List<AsyncCallback<T>> getOrCreateCallbacks(final RequestType request) {
		List<AsyncCallback<T>> callbacks = (List<AsyncCallback<T>>)_callbacks.get(request);
		if (callbacks == null) {
			_callbacks.put(request, new ArrayList<AsyncCallback<T>>());
			callbacks = (List<AsyncCallback<T>>)_callbacks.get(request);
		}
		return callbacks;
	}
}
