package com.fave100.client.pages.lists.widgets.favelist.widgets;

import com.fave100.client.generated.entities.FaveItem;
import com.fave100.client.pages.lists.widgets.favelist.FavelistUiHandlers;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.client.resources.img.ImageResources;
import com.fave100.client.widgets.helpbubble.HelpBubble;
import com.fave100.shared.Constants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class FavePickWidget extends Composite {

	private static Binder uiBinder = GWT.create(Binder.class);

	public interface Binder extends UiBinder<Widget, FavePickWidget> {
	}

	public interface FavePickWidgetStyle extends GlobalStyle {
		String hoverPanel();

		String rank();

		String rankThreeDigit();
	}

	@UiField HTMLPanel container;
	@UiField FavePickWidgetStyle style;
	@UiField SimplePanel rankPanel;
	@UiField Anchor song;
	@UiField InlineLabel artist;
	@UiField SimplePanel whyLinePanel;
	@UiField Panel hoverPanel;

	private EventBus _eventBus;
	private FaveItem _faveItem;
	private String _whyLine;
	private int _rank;
	private String _url;
	private final boolean _editable;
	private FavelistUiHandlers _uiHandlers;
	private FavePickRankInput _favePickRankInput;
	private FavePickWhyLineInput _favePickWhyLineInput;
	private HelpBubble whylineHelpBubble;
	private HelpBubble rankHelpBubble;
	private ImageResources resources = GWT.create(ImageResources.class);

	public FavePickWidget(final EventBus eventBus, final FaveItem item, final int rank, final boolean editable, final String url, final FavelistUiHandlers uiHandlers) {
		_eventBus = eventBus;
		_faveItem = item;
		_whyLine = _faveItem.getWhyline();
		_rank = rank;
		_editable = editable;
		_uiHandlers = uiHandlers;
		_url = url;

		initWidget(uiBinder.createAndBindUi(this));

		fillWidget();
	}

	private void fillWidget() {
		setupRankPanel();

		song.setText(getSong());
		song.setHref(_url);

		artist.setText(getArtist());

		setupWhyLinePanel();

		setupHoverPanel();
		hoverPanel.addStyleName(style.hoverPanel());
	}

	private void setupRankPanel() {
		rankPanel.clear();

		Widget rankWidget;

		if (_editable) {
			FavePickRankInput rankInput = new FavePickRankInput(this, _eventBus);
			_favePickRankInput = rankInput;
			rankWidget = rankInput;
		}
		else {
			Label rankText = new Label(Integer.toString(_rank));
			rankWidget = rankText;
		}

		rankWidget.addStyleName(style.rank());

		if (_rank >= Constants.MAX_ITEMS_PER_LIST) {
			rankWidget.addStyleName(style.rankThreeDigit());
		}
		else {
			rankWidget.removeStyleName(style.rankThreeDigit());
		}

		rankPanel.setWidget(rankWidget);
	}

	private void setupWhyLinePanel() {
		if (_editable) {
			_favePickWhyLineInput = new FavePickWhyLineInput(this);
			whyLinePanel.setWidget(_favePickWhyLineInput);
		}
		else {
			whyLinePanel.setWidget(new Label(getWhyLine()));
		}
	}

	private void setupHoverPanel() {
		hoverPanel.clear();
		if (_editable) {
			FavePickRerankPanel rerankPanel = new FavePickRerankPanel(this, _uiHandlers);
			hoverPanel.add(rerankPanel);
		}
		else {
			final Image addButton = new Image(resources.add());
			addButton.setTitle("Add to your Fave100");
			addButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					_uiHandlers.addSong(getSongID(), getSong(), getArtist(), false);
				}
			});
			hoverPanel.add(addButton);
		}
	}

	public void updateRank(final String rank) {
		if (rankHelpBubble != null) {
			rankHelpBubble.setVisible(false);
		}

		try {
			final int _currentRank = _rank;
			_rank = Integer.parseInt(rank);
			_uiHandlers.changeSongPosition(getSongID(), _currentRank - 1, _rank - 1);
		}
		catch (final NumberFormatException ex) {
			// Reset the the panel in the finally block
		}
		finally {
			setupRankPanel();
		}
	}

	public void updateWhyLine(String whyLine) {
		_whyLine = whyLine;
		_uiHandlers.editWhyline(getSongID(), whyLine);
	}

	public void showWhylineHelpBubble() {
		final String whylineText = "You can add an 80 character Why-Line here, explaining why this song is in your Fave100";
		whylineHelpBubble = new HelpBubble("Why-Line", whylineText, 400, HelpBubble.Direction.UP);
		container.add(whylineHelpBubble);
	}

	public void hideWhyLineHelpBubble() {
		if (whylineHelpBubble != null) {
			whylineHelpBubble.setVisible(false);
		}
	}

	public void showRankWhylineHelpBubble() {
		final String rankText = "You can change the rank of your songs here";
		rankHelpBubble = new HelpBubble("Rank", rankText, 300, HelpBubble.Direction.LEFT);
		container.add(rankHelpBubble);
		rankHelpBubble.setArrowPos(30);
	}

	public void focusWhyline() {
		_favePickWhyLineInput.focus();
	}

	public void focusRank() {
		_favePickRankInput.focus();
	}

	/* Getters and Setters */

	public int getRank() {
		return _rank;
	}

	public void setRank(final int rank) {
		_rank = rank;
		setupRankPanel();
	}

	public String getSongID() {
		return _faveItem.getSongID();
	}

	public String getSong() {
		return _faveItem.getSong();
	}

	public String getArtist() {
		return _faveItem.getArtist();
	}

	public String getWhyLine() {
		return _whyLine;
	}
}
