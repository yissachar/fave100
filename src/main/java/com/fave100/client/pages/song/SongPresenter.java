package com.fave100.client.pages.song;

import com.fave100.client.pages.PagePresenter;
import com.fave100.shared.place.NameTokens;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

/**
 * A song page that will display details about the song
 * 
 * @author yissachar.radcliffe
 * 
 */
public class SongPresenter extends
		PagePresenter<SongPresenter.MyView, SongPresenter.MyProxy> implements
		SongUiHandlers {

	public interface MyView extends View, HasUiHandlers<SongUiHandlers> {
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.song)
	public interface MyProxy extends ProxyPlace<SongPresenter> {
	}

	@Inject
	public SongPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy) {
		super(eventBus, view, proxy);
		getView().setUiHandlers(this);
	}

}

interface SongUiHandlers extends UiHandlers {
}
