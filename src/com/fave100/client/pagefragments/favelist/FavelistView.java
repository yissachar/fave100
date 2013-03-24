package com.fave100.client.pagefragments.favelist;

import static com.google.gwt.query.client.GQuery.$;

import java.util.List;

import com.fave100.client.pages.song.SongPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class FavelistView extends ViewWithUiHandlers<FavelistUiHandlers>
	implements FavelistPresenter.MyView {

	private final Widget	widget;

	public interface Binder extends UiBinder<Widget, FavelistView> {
	}

	interface FavelistStyle extends CssResource {
		String personalListItem();
		String rank();
		String detailsContainer();
		String songLink();
		String whyline();
	}

	@UiField FavelistStyle style;
	@UiField HTMLPanel favelist;
	private HTMLPanel draggedElement;

	@Inject
	public FavelistView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setList(final List<FaveItemProxy> list, final boolean personalList) {
		favelist.clear();

		for(int i = 0; i < list.size(); i++) {
			final FaveItemProxy faveItem = list.get(i);

			final HTMLPanel listItem = new HTMLPanel("");

			final Label rank = new Label(String.valueOf(i+1)+".");
			rank.getElement().addClassName(style.rank());
			listItem.add(rank);

			final HTMLPanel detailsContainer = new HTMLPanel("");
			detailsContainer.getElement().addClassName(style.detailsContainer());
			listItem.add(detailsContainer);

			final InlineHyperlink song = new InlineHyperlink();
			song.setText(faveItem.getSong());
			// TODO: Need better way of building links in views
			song.setTargetHistoryToken(NameTokens.song+";"+SongPresenter.ID_PARAM+"="+faveItem.getSongID());
			song.getElement().addClassName(style.songLink());
			detailsContainer.add(song);

			final Label artist = new Label(faveItem.getArtist());
			detailsContainer.add(artist);

			final Label whyline = new Label(faveItem.getWhyline());
			whyline.getElement().addClassName(style.whyline());
			detailsContainer.add(whyline);

			if(personalList) {
				// Special styles
				listItem.getElement().addClassName(style.personalListItem());



				/*favelist.addDomHandler(new MouseMoveHandler() {
					@Override
					public void onMouseMove(final MouseMoveEvent event) {
						if(draggedElement == null)
							return;
						DOM.setStyleAttribute(draggedElement.getElement(), "position", "absolute");
						DOM.setStyleAttribute(draggedElement.getElement(), "top", String.valueOf(event.getY())+"px");
						DOM.setStyleAttribute(draggedElement.getElement(), "width", "100%");
					}
				}, MouseMoveEvent.getType());*/

				// Delete button
				final Button deleteButton = new Button("X");
				deleteButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						getUiHandlers().removeSong(faveItem.getSongID());
					}
				});
				listItem.add(deleteButton);

				// Editable whyline, on click allows editing
				whyline.addStyleName("favelistWhyline");

				final TextBox whylineEditor = new TextBox();
				whylineEditor.getElement().addClassName(style.whyline());
				detailsContainer.add(whylineEditor);
				whylineEditor.setVisible(false);

				whyline.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						whyline.setVisible(false);
						whylineEditor.setValue(whyline.getText());
						whylineEditor.setVisible(true);
						whylineEditor.setFocus(true);
					}
				});

				whylineEditor.addBlurHandler(new BlurHandler() {
					@Override
					public void onBlur(final BlurEvent event) {
						whyline.setVisible(true);
						whylineEditor.setVisible(false);
						whyline.setText(whylineEditor.getValue());
						getUiHandlers().editWhyline(faveItem.getSongID(), whylineEditor.getValue());
					}
				});

				// Treat enter, tab or escape as a lose focus event
				whylineEditor.addKeyUpHandler(new KeyUpHandler() {
					@Override
					public void onKeyUp(final KeyUpEvent event) {
						if(KeyCodes.KEY_ENTER == event.getNativeKeyCode()
							|| KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()
							|| KeyCodes.KEY_TAB == event.getNativeKeyCode())
						{
							final NativeEvent blurEvent = Document.get().createBlurEvent();
							DomEvent.fireNativeEvent(blurEvent, whylineEditor);
						}
					}
				});

				// TODO: Switch all gQuery to pure GWT
				// Drag to rerank
				listItem.addDomHandler(new MouseDownHandler() {
					@Override
					public void onMouseDown(final MouseDownEvent event) {
						// Only listen for left button down
						if(event.getNativeButton() != NativeEvent.BUTTON_LEFT)
							return;
						// If songLink, button, or whyline is target, ignore
						if(isEventTarget(event, song) || isEventTarget(event, whyline)
							|| isEventTarget(event, whylineEditor) || isEventTarget(event, deleteButton))
						{
							return;
						}

						// Drag the element
						draggedElement = listItem;
							final GQuery $row = $(draggedElement);
							favelist.addStyleName("unselectable");

							// Add a hidden row to act as a placeholder while the real row is moved
							$row.clone().css("visibility", "hidden").addClass("clonedHiddenRow").insertBefore($row);
							$row.addClass("draggedFaveListItem");

							setPos($row, event.getClientY()+Window.getScrollTop());

							$("body").mousemove(new Function() {
								@Override
								public boolean f(final Event event) {
									// Set the dragged row position to be equal to mouseY
									final GQuery $draggedFaveListItem = $(".draggedFaveListItem");
									setPos($draggedFaveListItem, event.getClientY()+Window.getScrollTop());

									final int draggedTop = $draggedFaveListItem.offset().top;
									final int draggedBottom = draggedTop + $draggedFaveListItem.outerHeight(true);
									// Check if dragged row collides with row above or row below
									final GQuery $clonedHiddenRow = $(".clonedHiddenRow");
									GQuery $previous = $clonedHiddenRow.prev();
									GQuery $next = $clonedHiddenRow.next();
									final int prevHeight = $previous.outerHeight(true);
									final int nextHeight = $next.outerHeight(true);
									// Make sure we are not checking against the dragged row itself
									if($previous.hasClass("draggedFaveListItem")) $previous = $previous.prev();
									if($next.hasClass("draggedFaveListItem")) $next = $next.next();
									final int previousBottom = $previous.offset().top+$previous.outerHeight(true);
									// Move the hidden row to the appropriate position
									if(draggedTop < previousBottom-prevHeight/2) {
										$(".clonedHiddenRow").insertBefore($previous);
									} else if(draggedBottom > $next.offset().top+nextHeight/2){//$next.outerHeight(true)) {
										$(".clonedHiddenRow").insertAfter($next);
									}
									return true;
								}
							});
						}
					//}
				}, MouseDownEvent.getType());

				// Mouse up handler for change position
				RootPanel.get().addDomHandler(new MouseUpHandler() {
					@Override
					public void onMouseUp(final MouseUpEvent event) {
						if(draggedElement == null) return;
						final GQuery $draggedItem = $(".draggedFaveListItem").first();
						// Get the index of the row being dragged
						final int currentIndex = $draggedItem.parent().children().not(".clonedHiddenRow").index(draggedElement.getElement());
						// Insert the dragged row back into the table at the correct position
						$draggedItem.first().insertAfter($(".clonedHiddenRow"));
						// Get the new index
						final int newIndex = $draggedItem.parent().children().not(".clonedHiddenRow").index(draggedElement.getElement());
						// Rank on the server
						if(currentIndex != newIndex) {
							// Don't bother doing anything if the indices are the same
							getUiHandlers().changeSongPosition(currentIndex, newIndex);
						}
						//remove all drag associated items now that we are done with the drag
			    	  	$draggedItem.removeClass("draggedFaveListItem");
						favelist.removeStyleName("unselectable");
						$(".clonedHiddenRow").remove();
						$("body").unbind("mousemove");
						draggedElement = null;
					}

				}, MouseUpEvent.getType());
			} else {
				// Non-personal list, add an "Add" button
				final Button addButton = new Button("+");
				addButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						getUiHandlers().addSong(faveItem.getSongID());
					}
				});
				listItem.add(addButton);
			}
			favelist.add(listItem);
		}
	}

	private boolean isEventTarget(final MouseEvent<?> event, final Widget widget) {
		if(event.getClientX() >= widget.getAbsoluteLeft()
				&& event.getClientX() <= widget.getAbsoluteLeft() + widget.getOffsetWidth()
				&& event.getClientY() >= widget.getAbsoluteTop()
				&& event.getClientY() <= widget.getAbsoluteTop() + widget.getOffsetHeight())
		{
			return true;
		}
		return false;
	}

	private void setPos(final GQuery element, final int mouseY) {
		final int clonedHeight = $(".clonedHiddenRow").outerHeight(true);
		final int elementHeight = element.outerHeight(true);
		final int newPos = mouseY-(elementHeight);
		final int draggedBottom = newPos + elementHeight;
		final int faveListTop = ($(".faveList").offset().top-(clonedHeight/2));
		final int faveListBottom = faveListTop+$(".faveList").outerHeight(true);//-clonedHeight/2;

		// If dragged row goes out of top or bottom bounds, stop it
		if(newPos <  faveListTop) {
			// Element is above the favelist, make it at the favelist height
			element.css("top", faveListTop+"px");
		} else if(draggedBottom > faveListBottom) {
			// Element is below the favelist, set it at favelist bottom
			element.css("top", faveListBottom-elementHeight+"px");
		} else {
			// Element is neither above or below faveList, position it correctly
			element.css("top", newPos+"px");
		}
	}
}
