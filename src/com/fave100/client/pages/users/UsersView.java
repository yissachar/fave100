package com.fave100.client.pages.users;

import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.widgets.favelist.NonpersonalFaveList;
import com.fave100.client.widgets.favelist.PersonalFaveList;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
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

	@UiField(provided = true) NonpersonalFaveList 	userFaveList;
	@UiField(provided = true) PersonalFaveList 		personalFaveList;
	@UiField HTMLPanel 								faveListContainer;
	@UiField Button 								twitterButton;
	@UiField HTMLPanel 								socialContainer;
	@UiField InlineHyperlink 						editProfileButton;
	//@UiField Button 								followButton;
	@UiField Image 									avatar;
	@UiField SpanElement 							username;
	@UiField HTMLPanel 								topBar;
	@UiField HTMLPanel 								songAutocomplete;

	@Inject
	public UsersView(final Binder binder, final ApplicationRequestFactory requestFactory) {
		userFaveList = new NonpersonalFaveList(requestFactory);
		personalFaveList = new PersonalFaveList(requestFactory);
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final Widget content) {
		if(slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();
			if(content != null) {
				topBar.add(content);
			}
		}
		if(slot == UsersPresenter.AUTOCOMPLETE_SLOT) {
			songAutocomplete.clear();
			if(content != null) {
				songAutocomplete.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@UiHandler("twitterButton")
	void onTwitterButtonClicked(final ClickEvent event) {
		getUiHandlers().shareTwitter();
	}

	@Override
	public void setUserProfile(final AppUserProxy user) {
		avatar.setUrl(user.getAvatarImage());
		username.setInnerText(user.getUsername());
	}

	@Override
	public void setUserFaveList(final List<FaveItemProxy> faveList) {
		userFaveList.setRowData(faveList);
	}

	@Override
	public void refreshPersonalFaveList() {
		personalFaveList.refreshList();
	}

	@Override
	public void showOwnPage() {
		personalFaveList.setVisible(true);
		userFaveList.setVisible(false);
		socialContainer.setVisible(true);
		editProfileButton.setVisible(true);
		//faveFeed.setVisible(true);
		//followButton.setVisible(false);
		refreshPersonalFaveList();
	}

	@Override
	public void showOtherPage() {
		personalFaveList.setVisible(false);
		userFaveList.setVisible(true);
		socialContainer.setVisible(false);
		editProfileButton.setVisible(false);
		//followButton.setVisible(true);
		//faveFeed.setVisible(false);
	}

}
