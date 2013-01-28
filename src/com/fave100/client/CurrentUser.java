package com.fave100.client;

import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.requestfactory.AppUserProxy;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;

public class CurrentUser {

	private AppUserProxy	appUser;

	@Inject
	public CurrentUser(final EventBus eventBus) {
		CurrentUserChangedEvent.register(eventBus,
				new CurrentUserChangedEvent.Handler() {
					@Override
					public void onCurrentUserChanged(
							final CurrentUserChangedEvent event) {
						setAppUser(event.getUser());
					}
				});
	}

	public AppUserProxy getAppUser() {
		return appUser;
	}

	public void setAppUser(final AppUserProxy appUser) {
		this.appUser = appUser;
	}

}
