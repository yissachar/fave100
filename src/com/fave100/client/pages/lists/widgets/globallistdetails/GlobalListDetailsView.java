package com.fave100.client.pages.lists.widgets.globallistdetails;

import java.util.List;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class GlobalListDetailsView extends ViewImpl implements GlobalListDetailsPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, GlobalListDetailsView> {
	}

	@UiField Label hashtagLabel;
	@UiField FlowPanel userAvatarsContainer;
	@UiField Label andXOthersLabel;

	@Inject
	public GlobalListDetailsView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInfo(final String hashtag, final List<String> avatars, final List<String> users, final int listCount) {
		hashtagLabel.setText(hashtag);

		userAvatarsContainer.clear();
		for (final String avatar : avatars) {
			final Image image = new Image(avatar);
			userAvatarsContainer.add(image);
		}

		final int others = listCount - users.size();
		if (others > 0) {
			andXOthersLabel.setText("and " + others + " others");
			andXOthersLabel.setVisible(true);
		}
		else {
			andXOthersLabel.setVisible(false);
		}
	}

	@Override
	public void show() {
		widget.setVisible(true);
	}

	@Override
	public void hide() {
		widget.setVisible(false);
	}
}
