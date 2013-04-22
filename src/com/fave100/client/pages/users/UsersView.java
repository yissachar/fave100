package com.fave100.client.pages.users;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.users.widgets.ShareButton;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class UsersView extends ViewWithUiHandlers<UsersUiHandlers>
		implements UsersPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, UsersView> {
	}

	interface UsersStyle extends CssResource {
		String fixedSearch();
	}

	@UiField UsersStyle style;
	@UiField HTMLPanel faveListContainer;
	@UiField HTMLPanel socialContainer;
	@UiField ShareButton shareButton;
	@UiField InlineHyperlink editProfileButton;
	@UiField Image avatar;
	@UiField SpanElement username;
	@UiField HTMLPanel topBar;
	@UiField HTMLPanel songAutocomplete;
	@UiField HTMLPanel favelist;

	@Inject
	public UsersView(final Binder binder, final ApplicationRequestFactory requestFactory) {
		;
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final Widget content) {
		if (slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();
			if (content != null) {
				topBar.add(content);
			}
		}
		if (slot == UsersPresenter.AUTOCOMPLETE_SLOT) {
			songAutocomplete.clear();
			if (content != null) {
				songAutocomplete.add(content);
			}
		}
		if (slot == UsersPresenter.FAVELIST_SLOT) {
			favelist.clear();
			if (content != null) {
				favelist.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@Override
	public void setUserProfile(final AppUserProxy user) {
		avatar.setUrl(user.getAvatarImage());
		username.setInnerText(user.getUsername());
	}

	@Override
	public void showOwnPage() {
		socialContainer.setVisible(true);
		editProfileButton.setVisible(true);
		songAutocomplete.setVisible(true);
	}

	@Override
	public void showOtherPage() {
		socialContainer.setVisible(false);
		editProfileButton.setVisible(false);
		songAutocomplete.setVisible(false);
	}

	@Override
	public String getFixedSearchStyle() {
		return style.fixedSearch();
	}
}
