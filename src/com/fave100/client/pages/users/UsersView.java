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
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class UsersView extends ViewWithUiHandlers<UsersUiHandlers>
	implements UsersPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, UsersView> {
	}

	@UiField(provided = true) SuggestBox			songSuggestBox;
	@UiField(provided = true) NonpersonalFaveList 	userFaveList;
	@UiField(provided = true) PersonalFaveList 		personalFaveList;
	@UiField HTMLPanel 								faveListContainer;
	@UiField Button 								twitterButton;
	@UiField HTMLPanel 								socialContainer;
	@UiField InlineHyperlink 						editProfileButton;
	//@UiField Button 								followButton;
	@UiField Image 									avatar;
	@UiField SpanElement 							username;

	@Inject
	public UsersView(final Binder binder, final ApplicationRequestFactory requestFactory) {
		userFaveList = new NonpersonalFaveList(requestFactory);
		personalFaveList = new PersonalFaveList(requestFactory);
		final MusicSuggestionOracle suggestions = new MusicSuggestionOracle();
		songSuggestBox = new SongSuggestBox(suggestions);
		songSuggestBox.getElement().setAttribute("placeholder",
				"Search songs...");
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiField HTMLPanel topBar;
	//@UiField HTMLPanel faveFeed;


	@Override
	public void setInSlot(final Object slot, final Widget content) {
		if(slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();
			if(content != null) {
				topBar.add(content);
			}
		}/* Currently not using FaveFeed
		if(slot == UsersPresenter.FAVE_FEED_SLOT) {
			faveFeed.clear();
			if(content != null) {
				faveFeed.add(content);
			}
		}*/
		super.setInSlot(slot, content);
	}

	@UiHandler("twitterButton")
	void onTwitterButtonClicked(final ClickEvent event) {
		getUiHandlers().shareTwitter();
	}

	@UiHandler("songSuggestBox")
	void onItemSelected(final SelectionEvent<Suggestion> event) {
		// Look up the selected song in the song map and add it to user list
		getUiHandlers().songSelected(
				((SongSuggestBox) songSuggestBox).getFromSuggestionMap(event
						.getSelectedItem().getReplacementString()));
		songSuggestBox.setValue("");
	}

	/*@UiHandler("followButton")
	void onFollowButtonClicked(final ClickEvent event) {
		getUiHandlers().follow();
	}*/

	/*@Override
	public void setFollowed() {
		followButton.setHTML("Following");
		followButton.setEnabled(false);
	}

	@Override
	public void setUnfollowed() {
		followButton.setHTML("Follow");
		followButton.setEnabled(true);
	}*/

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
