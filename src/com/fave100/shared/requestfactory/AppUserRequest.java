package com.fave100.shared.requestfactory;

import java.util.List;

import com.fave100.server.domain.appuser.AppUser;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(AppUser.class)
public interface AppUserRequest extends RequestContext {
	Request<AppUserProxy> findAppUser(String username);

	Request<AppUserProxy> login(String username, String password);

	Request<Void> logout();

	Request<AppUserProxy> createAppUser(String username, String password, String email);

	Request<AppUserProxy> getLoggedInAppUser();

	Request<AppUserProxy> loginWithGoogle();

	Request<Boolean> isGoogleUserLoggedIn();

	Request<String> getGoogleLoginURL(String destinationURL);

	Request<AppUserProxy> createAppUserFromGoogleAccount(String username);

	Request<AppUserProxy> loginWithTwitter(String oauth_verifier);

	Request<AppUserProxy> loginWithFacebook(String code);

	Request<String> getTwitterAuthUrl(String redirectUrl);

	Request<AppUserProxy> createAppUserFromTwitterAccount(String username,
			String oauth_verifier);

	Request<String> getFacebookAuthUrl(String redirect);

	Request<AppUserProxy> createAppUserFromFacebookAccount(String username,
			String state, String code, String redirectUrl);

	Request<String> createBlobstoreUrl(String url);

	Request<UserInfoProxy> getCurrentUserSettings();

	Request<Void> followUser(String username);

	Request<Void> unfollowUser(String username);

	Request<List<AppUserProxy>> getFollowing(String username);

	Request<Boolean> setUserInfo(UserInfoProxy userInfo);

	Request<Boolean> emailPasswordResetToken(String username, String emailAddress);

	Request<Boolean> changePassword(String password, String token);
}