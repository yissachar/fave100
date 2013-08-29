package com.fave100.client.pages.lists;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.lists.widgets.sharebutton.ShareButton;
import com.fave100.shared.Constants;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ListView extends ViewWithUiHandlers<ListUiHandlers>
		implements ListPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, ListView> {
	}

	interface ListStyle extends CssResource {
		String fixedSearch();
	}

	@UiField ListStyle style;
	@UiField HTMLPanel userContainer;
	@UiField HTMLPanel faveListContainer;
	@UiField Label hashtagLabel;
	@UiField HTMLPanel socialContainer;
	@UiField HTMLPanel listManager;
	@UiField FocusPanel followCTAcontainer;
	@UiField Label followCTA;
	@UiField HTMLPanel userPageFaveList;
	@UiField HTMLPanel followingContainer;
	@UiField ShareButton shareButton;
	@UiField HorizontalPanel userProfile;
	@UiField InlineHyperlink profileLink;
	@UiField Image avatar;
	@UiField InlineLabel username;
	@UiField HTMLPanel topBar;
	@UiField HTMLPanel songAutocomplete;
	@UiField HTMLPanel favelist;
	@UiField Label userNotFound;
	@UiField Label mobileShowShare;
	@UiField Label mobileShowList;
	@UiField Label mobileShowFollowing;
	private boolean following;

	@Inject
	public ListView(final Binder binder, final ApplicationRequestFactory requestFactory) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();
			if (content != null) {
				topBar.add(content);
			}
		}
		if (slot == ListPresenter.AUTOCOMPLETE_SLOT) {
			songAutocomplete.clear();
			if (content != null) {
				songAutocomplete.add(content);
			}
		}
		if (slot == ListPresenter.FAVELIST_SLOT) {
			favelist.clear();
			if (content != null) {
				favelist.add(content);
			}
		}

		if (slot == ListPresenter.STARRED_LISTS_SLOT) {
			followingContainer.clear();
			if (content != null) {
				followingContainer.add(content);
			}
		}

		if (slot == ListPresenter.LIST_MANAGER_SLOT) {
			listManager.clear();
			if (content != null) {
				listManager.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@UiHandler("followCTAcontainer")
	void onStarClick(final ClickEvent event) {
		getUiHandlers().followUser();
		if (!following) {
			followCTA.removeStyleName("button-warning");
		}
	}

	@UiHandler("followCTA")
	void onFollowOver(final MouseOverEvent event) {
		if (following) {
			followCTA.addStyleName("button-warning");
			followCTA.setText("Unfollow");
		}
	}

	@UiHandler("followCTA")
	void onFollowOut(final MouseOutEvent event) {
		followCTA.removeStyleName("button-warning");
		if (following) {
			followCTA.setText("Following");
		}
	}

	@UiHandler("mobileShowShare")
	void onMobileShowShareClick(final ClickEvent event) {
		setSelected(mobileShowShare);
		userPageFaveList.setVisible(false);
		shareButton.setVisible(true);
		followingContainer.setVisible(false);
	}

	@UiHandler("mobileShowList")
	void onMobileShowListClick(final ClickEvent event) {
		setSelected(mobileShowList);
		userPageFaveList.setVisible(true);
		shareButton.setVisible(false);
		followingContainer.setVisible(false);
	}

	@UiHandler("mobileShowFollowing")
	void onMobileShowFollowingClick(final ClickEvent event) {
		setSelected(mobileShowFollowing);
		userPageFaveList.setVisible(false);
		shareButton.setVisible(false);
		followingContainer.setVisible(true);
	}

	private void setSelected(final Label label) {
		final String selected = "selected";
		mobileShowList.removeStyleName(selected);
		mobileShowShare.removeStyleName(selected);
		mobileShowFollowing.removeStyleName(selected);

		label.addStyleName(selected);
	}

	@Override
	public void renderSharing(final String username) {
		shareButton.setSharingUrls(username);
		nativeRenderShare();
	}

	public native void nativeRenderShare() /*-{
		$wnd.FB.XFBML.parse();
		$wnd.twttr.widgets.load();
		$wnd.gapi.plusone.go();
	}-*/;

	@Override
	public void setUserProfile(final AppUserProxy user) {
		if (user == null) {
			userProfile.setVisible(false);
		}
		else {
			userProfile.setVisible(true);
			avatar.setUrl(user.getAvatarImage());
			username.setText(user.getUsername());
			profileLink.setText(user.getUsername());
		}
	}

	@Override
	public void showOwnPage() {
		userContainer.setVisible(true);
		userNotFound.setVisible(false);
		username.setVisible(false);
		profileLink.setVisible(true);
		songAutocomplete.setVisible(true);
		shareButton.setTwitterMessage("Check out my Fave100 songs: ");
	}

	@Override
	public void showOtherPage() {
		userContainer.setVisible(true);
		userNotFound.setVisible(false);
		username.setVisible(true);
		profileLink.setVisible(false);
		songAutocomplete.setVisible(false);

		shareButton.setTwitterMessage("Check out " + username.getText() + "'s Fave100 songs: ");
	}

	@Override
	public String getFixedSearchStyle() {
		return style.fixedSearch();
	}

	@Override
	public void showUserNotFound() {
		userContainer.setVisible(false);
		userNotFound.setVisible(true);
	}

	@Override
	public void setFollowCTA(final boolean show, final boolean following) {
		this.following = following;

		if (show) {
			followCTAcontainer.setVisible(true);
		}
		else {
			followCTAcontainer.setVisible(false);
		}

		if (following) {
			followCTA.setText("Following");
		}
		else {
			followCTA.setText("Follow");
		}
	}

	@Override
	public void setMobileView(final boolean reset) {
		if (Window.getClientWidth() <= Constants.MOBILE_WIDTH_PX) {
			if (reset) {
				mobileShowList.removeStyleName("selected");
				mobileShowShare.removeStyleName("selected");
				mobileShowFollowing.removeStyleName("selected");
			}

			if (mobileShowList.getStyleName().contains("selected")) {
				userPageFaveList.setVisible(true);
				shareButton.setVisible(false);
				followingContainer.setVisible(false);
			}
			else if (mobileShowShare.getStyleName().contains("selected")) {
				userPageFaveList.setVisible(false);
				shareButton.setVisible(true);
				followingContainer.setVisible(false);
			}
			else if (mobileShowFollowing.getStyleName().contains("selected")) {
				userPageFaveList.setVisible(false);
				shareButton.setVisible(false);
				followingContainer.setVisible(true);
			}

			else {
				setSelected(mobileShowList);
				userPageFaveList.setVisible(true);
				shareButton.setVisible(false);
				followingContainer.setVisible(false);
			}
		}
		else {
			userPageFaveList.setVisible(true);
			shareButton.setVisible(true);
			followingContainer.setVisible(true);
		}
	}

	@Override
	public void setHashtagLabel(final String hashtag) {
		hashtagLabel.setText(hashtag);
	}

	@Override
	public void setHashtagVisible(final boolean show) {
		hashtagLabel.setVisible(show);
	}
}
