package com.fave100.client.events.favelist;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * @author yissachar.radcliffe
 * 
 */
public class HideSideBarEvent extends Event<HideSideBarEvent.Handler> {

	public interface Handler {
		void onHideSideBar(HideSideBarEvent event);
	}

	private static final Type<HideSideBarEvent.Handler> TYPE = new Type<HideSideBarEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus, final HideSideBarEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	@Override
	public Type<HideSideBarEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onHideSideBar(this);
	}

}
