package com.fave100.client;

import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;

public class CurrentUser implements AppUserProxy {

	private AppUserProxy appUser;
	private String avatar = "";

	@Inject
	public CurrentUser(final EventBus eventBus) {
		CurrentUserChangedEvent.register(eventBus,
				new CurrentUserChangedEvent.Handler() {
					@Override
					public void onCurrentUserChanged(
							final CurrentUserChangedEvent event) {
						setAppUser(event.getUser());
						if (appUser != null) {
							avatar = appUser.getAvatarImage();
						}
					}
				});
	}

	public boolean isLoggedIn() {
		return appUser != null;
	}

	public void setAppUser(final AppUserProxy appUser) {
		this.appUser = appUser;
	}

	public void setAvatar(final String url) {
		avatar = url;
	}

	// Needed for RequestFactory
	@Override
	public EntityProxyId<?> stableId() {
		return appUser.stableId();
	}

	@Override
	public Integer getVersion() {
		return appUser.getVersion();
	}

	@Override
	public String getUsername() {
		return appUser.getUsername();
	}

	@Override
	public String getAvatarImage() {
		return avatar;
	}

	@Override
	public boolean equals(final Object obj) {
		return appUser.equals(obj);
	}
}
