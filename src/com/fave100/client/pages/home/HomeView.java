package com.fave100.client.pages.home;

import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.widgets.UserThumbList;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class HomeView extends ViewImpl implements HomePresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, HomeView> {
	}

	@UiField HTMLPanel userThumbListPanel;
	//@UiField(provided = true) NonpersonalFaveList masterFaveList;
	private ApplicationRequestFactory requestFactory;

	@Inject
	public HomeView(final Binder binder, final ApplicationRequestFactory requestFactory) {
		//masterFaveList = new NonpersonalFaveList(requestFactory);
		widget = binder.createAndBindUi(this);
		this.requestFactory = requestFactory;
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	//@UiField HTMLPanel faveFeed;

	@Override
	public void setInSlot(final Object slot, final Widget content) {

		/* Currently not using FaveFeed
		if(slot == HomePresenter.FAVE_FEED_SLOT) {
			faveFeed.clear();
			if(content != null) {
				faveFeed.add(content);
			}
		}*/
		super.setInSlot(slot, content);
	}

	@Override
	public void addUserThumb(final AppUserProxy appUser) {
		final UserThumbList userThumbList = new UserThumbList(requestFactory, appUser);
		userThumbListPanel.add(userThumbList);
	}

	/*@Override
	public void updateMasterFaveList(final List<SongProxy> faveList) {
		masterFaveList.setRowData(faveList);
	}*/
}
