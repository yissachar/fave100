package com.fave100.client.events;

import com.fave100.shared.requestfactory.SongProxy;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This event indicates that song was selected from
 * a {@link SongSuggestBox}.
 * @author yissachar.radcliffe
 *
 */
public class SongSelectedEvent extends Event<SongSelectedEvent.Handler> {

    public interface Handler {
        void onSongSelected(SongSelectedEvent event);
    }

    private static final Type<SongSelectedEvent.Handler> TYPE =
        new Type<SongSelectedEvent.Handler>();

    public static HandlerRegistration register(final EventBus eventBus,
        final SongSelectedEvent.Handler handler) {
      return eventBus.addHandler(TYPE, handler);
    }

    private final SongProxy song;

    public SongSelectedEvent(final SongProxy song) {
        this.song = song;
    }

    @Override
    public Type<SongSelectedEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    public SongProxy getSong() {
        return song;
    }

    @Override
    protected void dispatch(final Handler handler) {
        handler.onSongSelected(this);
    }
}
