package com.fave100.client.pagefragments.favefeed;

import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class FaveFeedView extends ViewImpl implements FaveFeedPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, FaveFeedView> {
	}

	@UiField InlineHTML panel;
	@UiField Label faveFeedTitle;
	@UiField InlineHTML activityPanel;
	@UiField Label faveFeedMessage;
	@UiField InlineHyperlink loginLink;

	@Inject
	public FaveFeedView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setFaveFeedContent(final List<String> activityList) {
		if (activityList != null) {
			activityPanel.setVisible(true);
			loginLink.setVisible(false);
			faveFeedTitle.setText("FaveFeed");
			if(activityList.size() > 0) {
				// Show the activity
				faveFeedMessage.setVisible(false);
				final SafeHtmlBuilder builder = new SafeHtmlBuilder();
				for(final String activity : activityList) {
					builder.appendHtmlConstant(activity);
					activityPanel.setHTML(builder.toSafeHtml());
				}
			} else {
				faveFeedMessage.setVisible(true);
				faveFeedMessage.setText("No recent activity");
			}
		} else {
			// Show default message
			faveFeedTitle.setText("Get your list on!");
			activityPanel.setVisible(false);
			String txt = "If you could only pick the cream of the crop, what ";
			txt += "song would you choose?";
			faveFeedMessage.setVisible(true);
			faveFeedMessage.setText(txt);
			loginLink.setVisible(true);
		}
	}
}
