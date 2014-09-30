package com.fave100.client.pages.lists;

import java.util.HashMap;
import java.util.Map;

import com.fave100.client.CurrentUser;
import com.fave100.client.Utils;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.pages.PageView;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.shared.Constants;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class ListView extends PageView<ListUiHandlers>
		implements ListPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, ListView> {
	}

	interface ListStyle extends GlobalStyle {
		String fixedSearch();

		String hoverSideBar();
	}

	@UiField ListStyle style;
	@UiField HTMLPanel userContainer;
	@UiField Panel slideOutBackground;
	@UiField HTMLPanel userPageSideBar;
	@UiField HTMLPanel faveListContainer;
	@UiField HTMLPanel globalListDetailsContainer;
	@UiField HTMLPanel listManager;
	@UiField Button followButton;
	@UiField HTMLPanel userPageFaveList;
	@UiField Panel listHeader;
	@UiField Anchor contributeCTA;
	@UiField Panel criticUrlPanel;
	@UiField Label criticUrlLabel;
	@UiField TextBox criticUrlInput;
	@UiField Button criticUrlButton;
	@UiField Anchor addSongLink;
	@UiField HTMLPanel followingContainer;
	@UiField FlowPanel userProfile;
	@UiField Hyperlink profileLink;
	@UiField Image avatar;
	@UiField Label username;
	@UiField HTMLPanel favelist;
	@UiField Label userNotFound;
	private boolean following;

	@Inject
	public ListView(final Binder binder) {
		widget = binder.createAndBindUi(this);

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				resize();
			}
		});

		RootPanel.get().addHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Element target = Element.as(event.getNativeEvent().getEventTarget());
				if (Utils.widgetContainsElement(globalListDetailsContainer, target)
						|| Utils.widgetContainsElement(slideOutBackground, target)) {
					hideSideBar();
				}

			}
		}, ClickEvent.getType());

		resize();
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		super.setInSlot(slot, content);

		HTMLPanel slotPanel = getPanelForSlot(slot);

		if (slotPanel != null && content != null) {
			slotPanel.clear();
			slotPanel.add(content);
		}
	}

	private HTMLPanel getPanelForSlot(Object slot) {
		Map<Type<RevealContentHandler<?>>, HTMLPanel> slotMap = new HashMap<Type<RevealContentHandler<?>>, HTMLPanel>();
		slotMap.put(ListPresenter.FAVELIST_SLOT, favelist);
		slotMap.put(ListPresenter.STARRED_LISTS_SLOT, followingContainer);
		slotMap.put(ListPresenter.LIST_MANAGER_SLOT, listManager);
		slotMap.put(ListPresenter.GLOBAL_LIST_DETAILS_SLOT, globalListDetailsContainer);

		return slotMap.get(slot);
	}

	@UiHandler("followButton")
	void onFollowButtonClick(final ClickEvent event) {
		getUiHandlers().followUser();
		if (!following) {
			followButton.removeStyleName("button-warning");
		}
	}

	@UiHandler("followButton")
	void onFollowButtonOver(final MouseOverEvent event) {
		if (following) {
			followButton.addStyleName("button-warning");
			followButton.setText("Unfollow");
		}
	}

	@UiHandler("followButton")
	void onFollowButtonOut(final MouseOutEvent event) {
		followButton.removeStyleName("button-warning");
		if (following) {
			followButton.setText("Following");
		}
	}

	@UiHandler("contributeCTA")
	void onContributeClick(ClickEvent event) {
		getUiHandlers().contributeToList();
	}

	@UiHandler("criticUrlButton")
	void onCriticUrlButtonClicked(ClickEvent event) {
		if (criticUrlLabel.isVisible()) {
			criticUrlLabel.setVisible(false);
			criticUrlInput.setVisible(true);
			criticUrlInput.setText(criticUrlLabel.getText());
			criticUrlButton.setText("Save");
		}
		else {
			criticUrlInput.setVisible(false);
			criticUrlLabel.setVisible(true);
			criticUrlLabel.setText(criticUrlInput.getText());
			criticUrlButton.setText("Edit");
			getUiHandlers().saveCriticUrl(criticUrlInput.getText());
		}
	}

	@UiHandler("addSongLink")
	void onAddSongClick(ClickEvent event) {
		getUiHandlers().showAddSongPrompt();
	}

	public native void nativeRenderShare() /*-{
		$wnd.FB.XFBML.parse();
		$wnd.twttr.widgets.load();
		$wnd.gapi.plusone.go();
	}-*/;

	@Override
	public void setPageDetails(final AppUser requestedUser, final CurrentUser currentUser) {

		if (requestedUser == null) {
			userProfile.setVisible(false);
		}
		else {
			userProfile.setVisible(true);
			avatar.setUrl(requestedUser.getAvatarImage());
			username.setText(requestedUser.getUsername());
			profileLink.setText(requestedUser.getUsername());
		}

		criticUrlPanel.setVisible(false);
		if (currentUser.isLoggedIn() && currentUser.equals(requestedUser)) {
			showOwnPage();
			if (currentUser.isCritic()) {
				criticUrlPanel.setVisible(true);
				criticUrlLabel.setVisible(true);
				criticUrlInput.setVisible(false);
				criticUrlButton.setText("Edit");
			}
		}
		else {
			showOtherPage();
		}

		resize();
		faveListContainer.getElement().setScrollTop(0);
	}

	@Override
	public void resize() {
		String height = (Window.getClientHeight() - Constants.TOP_BAR_HEIGHT) + "px";
		userPageSideBar.setHeight(height);
		slideOutBackground.setHeight(height);
		String listHeight = (Window.getClientHeight() - Constants.TOP_BAR_HEIGHT - listHeader.getOffsetHeight()) + "px";
		faveListContainer.getElement().getStyle().setProperty("maxHeight", listHeight);
	}

	private void showOwnPage() {
		userContainer.setVisible(true);
		userNotFound.setVisible(false);
		username.setVisible(false);
		profileLink.setVisible(true);
		contributeCTA.setVisible(false);
		addSongLink.setVisible(true);
	}

	private void showOtherPage() {
		userContainer.setVisible(true);
		userNotFound.setVisible(false);
		username.setVisible(true);
		profileLink.setVisible(false);
		contributeCTA.setVisible(true);
		addSongLink.setVisible(false);
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

		followButton.setVisible(show);

		if (following) {
			followButton.setText("Following");
		}
		else {
			followButton.setText("Follow");
		}
	}

	@Override
	public void toggleSideBar() {
		if (userContainer.getStyleName().contains(style.hoverSideBar())) {
			hideSideBar();
		}
		else {
			showSideBar();
		}
	}

	@Override
	public void hideSideBar() {
		userContainer.removeStyleName(style.hoverSideBar());
	}

	@Override
	public void showSideBar() {
		userContainer.addStyleName(style.hoverSideBar());
	}

	@Override
	public void setCriticUrl(String url) {
		criticUrlLabel.setText(url);
		criticUrlInput.setText(url);
	}
}
