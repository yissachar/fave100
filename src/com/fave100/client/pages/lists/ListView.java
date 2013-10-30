package com.fave100.client.pages.lists;

import java.util.HashMap;
import java.util.Map;

import com.fave100.client.pages.BasePresenter;
import com.fave100.shared.Constants;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
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
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

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
	@UiField HTMLPanel globalListDetailsContainer;
	@UiField HTMLPanel listManager;
	@UiField FocusPanel followCTAcontainer;
	@UiField Label followCTA;
	@UiField HTMLPanel userPageFaveList;
	@UiField HTMLPanel followingContainer;
	@UiField HorizontalPanel userProfile;
	@UiField InlineHyperlink profileLink;
	@UiField Image avatar;
	@UiField InlineLabel username;
	@UiField HTMLPanel topBar;
	@UiField HTMLPanel songAutocomplete;
	@UiField HTMLPanel favelist;
	@UiField Label userNotFound;
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
		HTMLPanel slotPanel = getPanelForSlot(slot);

		if (slotPanel != null && content != null) {
			slotPanel.clear();
			slotPanel.add(content);
		}

		super.setInSlot(slot, content);
	}

	private HTMLPanel getPanelForSlot(Object slot) {
		Map<Type<RevealContentHandler<?>>, HTMLPanel> slotMap = new HashMap<Type<RevealContentHandler<?>>, HTMLPanel>();
		slotMap.put(BasePresenter.TOP_BAR_SLOT, topBar);
		slotMap.put(ListPresenter.AUTOCOMPLETE_SLOT, songAutocomplete);
		slotMap.put(ListPresenter.FAVELIST_SLOT, favelist);
		slotMap.put(ListPresenter.STARRED_LISTS_SLOT, followingContainer);
		slotMap.put(ListPresenter.LIST_MANAGER_SLOT, listManager);
		slotMap.put(ListPresenter.GLOBAL_LIST_DETAILS_SLOT, globalListDetailsContainer);

		return slotMap.get(slot);
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

	@UiHandler("mobileShowList")
	void onMobileShowListClick(final ClickEvent event) {
		setSelected(mobileShowList);
		userPageFaveList.setVisible(true);
		//shareButton.setVisible(false);
		followingContainer.setVisible(false);
		listManager.setVisible(true);
	}

	@UiHandler("mobileShowFollowing")
	void onMobileShowFollowingClick(final ClickEvent event) {
		setSelected(mobileShowFollowing);
		userPageFaveList.setVisible(false);
		//shareButton.setVisible(false);
		followingContainer.setVisible(true);
		listManager.setVisible(false);
	}

	private void setSelected(final Label label) {
		final String selected = "selected";
		mobileShowList.removeStyleName(selected);
		mobileShowFollowing.removeStyleName(selected);

		label.addStyleName(selected);
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
	}

	@Override
	public void showOtherPage() {
		userContainer.setVisible(true);
		userNotFound.setVisible(false);
		username.setVisible(true);
		profileLink.setVisible(false);
		songAutocomplete.setVisible(false);
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
				mobileShowFollowing.removeStyleName("selected");
			}

			if (mobileShowList.getStyleName().contains("selected")) {
				userPageFaveList.setVisible(true);
				followingContainer.setVisible(false);
			}
			else if (mobileShowFollowing.getStyleName().contains("selected")) {
				userPageFaveList.setVisible(false);
				followingContainer.setVisible(true);
			}

			else {
				setSelected(mobileShowList);
				userPageFaveList.setVisible(true);
				followingContainer.setVisible(false);
			}

			if (getUiHandlers().isOwnPage()) {
				mobileShowFollowing.setVisible(true);
				mobileShowList.setVisible(true);
			}
			else {
				mobileShowFollowing.setVisible(false);
				mobileShowList.setVisible(false);
			}
		}
		else {
			userPageFaveList.setVisible(true);
			followingContainer.setVisible(true);
		}
	}
}
