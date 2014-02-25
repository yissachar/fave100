package com.fave100.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fave100.client.generated.entities.FollowingResult;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.generated.services.AppuserService;
import com.fave100.client.rest.RestSessionDispatch;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.rest.shared.RestAction;

/**
 * Stores common server requests and their results in order to prevent constant trips to the server for unchanged data.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class RequestCache {

	enum RequestType {
		GOOGLE_LOGIN,
		FACEBOOK_LOGIN,
		FOLLOWING_CURRENT_USER
	}

	private RestSessionDispatch _dispatcher;
	private AppuserService _appUserService;

	private Map<RequestType, Boolean> _runningRequests = new HashMap<RequestType, Boolean>();
	private Map<RequestType, Object> _callbacks = new HashMap<RequestType, Object>();
	private Map<RequestType, Object> _results = new HashMap<RequestType, Object>();

	@Inject
	public RequestCache(RestSessionDispatch dispatcher, AppuserService appUserService) {
		_dispatcher = dispatcher;
		_appUserService = appUserService;
	}

	/**
	 * Allows manually clearing of results when it is known that they are stale
	 * 
	 * @param request
	 */
	public void clearRequestCache(final RequestType request) {
		_results.remove(request);
	}

	public void getGoogleUrl(final String redirect, final AsyncCallback<String> callback) {
		getLoginUrl(RequestType.GOOGLE_LOGIN, redirect, callback);
	}

	public void getFacebookUrl(final String redirect, final AsyncCallback<String> callback) {
		getLoginUrl(RequestType.FACEBOOK_LOGIN, redirect, callback);
	}

	public void getFollowingForCurrentUser(final String username, final AsyncCallback<FollowingResult> callback) {
		final RequestType request = RequestType.FOLLOWING_CURRENT_USER;
		final FollowingResult followingUsers = (FollowingResult)_results.get(request);
		final List<AsyncCallback<FollowingResult>> callbacks = getOrCreateCallbacks(request);
		final boolean reqRunning = (_runningRequests.get(request) != null) ? _runningRequests.get(request) : false;
		// Add the callback to list of callbacks to notify		
		callbacks.add(callback);
		// If we already have the following users, return		
		if (followingUsers != null) {
			for (final AsyncCallback<FollowingResult> gCallback : callbacks) {
				gCallback.onSuccess(followingUsers);
			}
			callbacks.clear();
			return;
		}

		// If there is no existing request, create one
		if (!reqRunning) {
			_runningRequests.put(request, true);
			_dispatcher.execute(_appUserService.getFollowing(username, 0), new AsyncCallback<FollowingResult>() {

				@Override
				public void onFailure(Throwable caught) {
					_runningRequests.put(request, false);
					// Clean all callbacks
					callbacks.clear();
				}

				@Override
				public void onSuccess(FollowingResult followingResult) {
					_runningRequests.put(request, false);
					_results.put(request, followingResult);
					for (final AsyncCallback<FollowingResult> gCallback : callbacks) {
						gCallback.onSuccess(followingResult);
					}
					callbacks.clear();
				}
			});
		}
	}

	private void getLoginUrl(final RequestType request, final String redirect, final AsyncCallback<String> callback) {
		StringResult result = ((StringResult)_results.get(request));
		final String loginUrl = result != null ? result.getValue() : null;
		final List<AsyncCallback<String>> callbacks = getOrCreateCallbacks(request);
		final boolean reqRunning = (_runningRequests.get(request) != null) ? _runningRequests.get(request) : false;
		// Add the callback to list of callbacks to notify		
		callbacks.add(callback);
		// If we already have the loginUrl, return		
		if (loginUrl != null) {
			for (final AsyncCallback<String> gCallback : callbacks) {
				gCallback.onSuccess(loginUrl);
			}
			callbacks.clear();
			return;
		}

		// If there is no existing request, create one
		if (!reqRunning) {
			_runningRequests.put(request, true);
			RestAction<StringResult> loginUrlReq = null;
			switch (request) {
				case GOOGLE_LOGIN:
					loginUrlReq = _appUserService.getGoogleLoginURL(redirect);
					break;
				case FACEBOOK_LOGIN:
					loginUrlReq = _appUserService.getFacebookAuthUrl(redirect);
					break;
				default:
					// No request to fire
					return;
			}

			_dispatcher.execute(loginUrlReq, new AsyncCallback<StringResult>() {

				@Override
				public void onFailure(Throwable caught) {
					_runningRequests.put(request, false);
					// Clean all callbacks
					callbacks.clear();
				}

				@Override
				public void onSuccess(StringResult url) {
					_runningRequests.put(request, false);
					_results.put(request, url);
					for (final AsyncCallback<String> gCallback : callbacks) {
						gCallback.onSuccess(url.getValue());
					}
					callbacks.clear();
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
