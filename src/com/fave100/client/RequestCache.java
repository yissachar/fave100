package com.fave100.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fave100.shared.requestfactory.AppUserProxy;
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
		FACEBOOK_LOGIN,
		FOLLOWING_USERS
	}

	private ApplicationRequestFactory _requestFactory;

	private Map<RequestType, Boolean> _runningRequests = new HashMap<RequestType, Boolean>();
	private Map<RequestType, Object> _callbacks = new HashMap<RequestType, Object>();
	private Map<RequestType, Object> _results = new HashMap<RequestType, Object>();

	@Inject
	public RequestCache(final ApplicationRequestFactory requestFactory) {
		_requestFactory = requestFactory;
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

	public void getFollowingUsers(final AsyncCallback<List<AppUserProxy>> callback) {
		final RequestType request = RequestType.FOLLOWING_USERS;
		@SuppressWarnings("unchecked")
		final List<AppUserProxy> followingUsers = (List<AppUserProxy>)_results.get(request);
		final List<AsyncCallback<List<AppUserProxy>>> callbacks = getOrCreateCallbacks(request);
		final boolean reqRunning = (_runningRequests.get(request) != null) ? _runningRequests.get(request) : false;
		// Add the callback to list of callbacks to notify		
		callbacks.add(callback);
		// If we already have the following users, return		
		if (followingUsers != null) {
			for (final AsyncCallback<List<AppUserProxy>> gCallback : callbacks) {
				gCallback.onSuccess(followingUsers);
			}
			callbacks.clear();
			return;
		}

		// If there is no existing request, create one
		if (reqRunning == false) {
			_runningRequests.put(request, true);
			final Request<List<AppUserProxy>> followingUserReq = _requestFactory.appUserRequest().getFollowingForCurrentUser();
			// If there is no existing request, create one
			if (reqRunning == false) {
				_runningRequests.put(request, true);

				followingUserReq.fire(new Receiver<List<AppUserProxy>>() {
					@Override
					public void onSuccess(final List<AppUserProxy> users) {
						_runningRequests.put(request, false);
						_results.put(request, users);
						for (final AsyncCallback<List<AppUserProxy>> gCallback : callbacks) {
							gCallback.onSuccess(users);
						}
						callbacks.clear();
					}

					@Override
					public void onFailure(final ServerFailure failure) {
						_runningRequests.put(request, false);
						// Clean all callbacks
						callbacks.clear();
					}
				});
			}
		}
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
					_runningRequests.put(request, false);
					_results.put(request, url);
					for (final AsyncCallback<String> gCallback : callbacks) {
						gCallback.onSuccess(url);
						callbacks.remove(gCallback);
					}
				}

				@Override
				public void onFailure(final ServerFailure failure) {
					_runningRequests.put(request, false);
					// Clean all callbacks
					for (final AsyncCallback<String> gCallback : callbacks) {
						callbacks.remove(gCallback);
					}
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
