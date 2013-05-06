package com.fave100.client.pages.explore;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.explore.widgets.ExploreItem;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.ExploreResultProxy;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class ExplorePresenter extends BasePresenter<ExplorePresenter.MyView, ExplorePresenter.MyProxy> implements ExploreUiHandlers {

	public interface MyView extends BaseView, HasUiHandlers<ExploreUiHandlers> {
		void setExploreList(List<ExploreItem> list);

		void clearList();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.explore)
	public interface MyProxy extends ProxyPlace<ExplorePresenter> {
	}

	private ApplicationRequestFactory requestFactory;

	@Inject
	public ExplorePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		getView().setUiHandlers(this);
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		final Request<List<ExploreResultProxy>> exploreReq = requestFactory.exploreRequest().getExploreFeed();
		exploreReq.fire(new Receiver<List<ExploreResultProxy>>() {
			@Override
			public void onSuccess(final List<ExploreResultProxy> response) {
				if (response != null && response.size() > 0) {
					final List<ExploreItem> items = new ArrayList<ExploreItem>();
					for (final ExploreResultProxy result : response) {
						if (result != null) {
							final ExploreItem item = new ExploreItem(result);
							items.add(item);
						}
					}
					getView().setExploreList(items);
				}
				getProxy().manualReveal(ExplorePresenter.this);
			}
		});
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().clearList();
	}
}

interface ExploreUiHandlers extends UiHandlers {
}
