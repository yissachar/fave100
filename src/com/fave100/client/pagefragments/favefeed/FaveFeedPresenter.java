package com.fave100.client.pagefragments.favefeed;

import java.util.List;

import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class FaveFeedPresenter extends
		PresenterWidget<FaveFeedPresenter.MyView> {

	public interface MyView extends View {
		void setFaveFeedContent(SafeHtml html);
	}

	private ApplicationRequestFactory requestFactory;
	
	@Inject
	public FaveFeedPresenter(final EventBus eventBus, final MyView view, 
			final ApplicationRequestFactory requestFactory) {
		super(eventBus, view);
		this.requestFactory = requestFactory;
	}
	
	@Override
	protected void onBind() {
		super.onBind();
		
	}
	
	@Override
	protected void onReveal() {
		super.onReveal();
		
		// Update the FaveFeed
		final Request<List<String>> faveFeedReq = requestFactory.appUserRequest().getFaveFeedForCurrentUser();
		faveFeedReq.fire(new Receiver<List<String>>() {
			@Override
			public void onSuccess(final List<String> faveFeed) {
				final SafeHtmlBuilder builder = new SafeHtmlBuilder();
				builder.appendHtmlConstant("<h4>");
				builder.appendEscaped("FaveFeed");
				builder.appendHtmlConstant("</h4>");
				if(faveFeed.size() > 0) {
					builder.appendHtmlConstant("<ul>");
					for(final String notification : faveFeed) {
						builder.appendHtmlConstant("<li>");					
						builder.appendHtmlConstant(notification);
						builder.appendHtmlConstant("</li>");
					}
					builder.appendHtmlConstant("</ul>");
				} else {
					builder.appendHtmlConstant("<p>");
					builder.appendEscaped("No recent activity.");
					builder.appendHtmlConstant("</p>");
				}
				//getView().setVisible(true);
				//getView().setHTML(builder.toSafeHtml());
				getView().setFaveFeedContent(builder.toSafeHtml());
			}
			@Override
			public void onFailure(final ServerFailure failure) {
				// TODO: What if the error isn't login error?
				//getView().getFaveFeed().setVisible(false);
				final SafeHtmlBuilder builder = new SafeHtmlBuilder();
				builder.appendHtmlConstant("<h4>");
				builder.appendEscaped("FaveFeed");
				builder.appendHtmlConstant("</h4>");
				final InlineHyperlink link = new InlineHyperlink();
				link.setTargetHistoryToken(NameTokens.login);
				link.setText("Please log in to view the FaveFeed");
				builder.appendHtmlConstant(link.toString());
				getView().setFaveFeedContent(builder.toSafeHtml());
			}
		});
	}
}
