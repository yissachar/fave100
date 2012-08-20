package com.fave100.client.widgets;

import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.pages.myfave100.MouseDownCell;
import com.fave100.client.requestfactory.FaveListItem;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

public class FaveListBase extends HTMLPanel{
	
	private CellList<FaveListItem> cellList;
	
	public FaveListBase() {
		super("");
		List<HasCell<FaveListItem, ?>> cells = new ArrayList<HasCell<FaveListItem,?>>();
		cells.add(new HasCell<FaveListItem, SafeHtml>() {
			private SafeHtmlCell cell = new SafeHtmlCell();

            @Override
            public SafeHtmlCell getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<FaveListItem, SafeHtml> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SafeHtml getValue(FaveListItem object) {
				SafeHtmlBuilder builder = new SafeHtmlBuilder();
				builder.appendHtmlConstant("<a href='"+object.getTrackViewUrl()+"'>"+object.getTrackName()+"</a>");
				return builder.toSafeHtml();
			}			
		});
		cells.add(new HasCell<FaveListItem, String>() {
			private TextCell cell = new TextCell();

            @Override
            public Cell<String> getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<FaveListItem, String> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getValue(FaveListItem object) {
				return object.getArtistName();
			}
		});
		cells.add(new HasCell<FaveListItem, String>() {
			MouseDownCell cell = new MouseDownCell(){
				@Override
				public void onBrowserEvent(Context context, Element parent, String value,
					NativeEvent event, ValueUpdater<String> valueUpdater) {	
					if(value == null) return;		
					super.onBrowserEvent(context, parent, value, event, valueUpdater);					
					if(event.getType().equals("mousedown")) {					
						//draggedRow = getRowElement(context.getIndex());
						//GQuery $row = $(draggedRow);
						//addStyleName("unselectable");
						GQuery $row = $(event.getEventTarget()).closest("div");
						
						// Add a hidden row to act as a placeholder while the real row is moved					
						$row.clone().css("visibility", "hidden").addClass("clonedHiddenRow").insertBefore($row);
						$row.addClass("draggedFaveListItem");
						
						$("body").mousemove(new Function() {
						public boolean f(Event event) {
								// Set the dragged row position to be equal to mouseY						
								GQuery $draggedFaveListItem = $(".draggedFaveListItem");
								int offsetMouseY = event.getClientY()-$(".faveList tbody").offset().top+Window.getScrollTop();
								int newPos = offsetMouseY-$draggedFaveListItem.height()/2;
								$draggedFaveListItem.css("top", newPos+"px");
								
								// Check if dragged row collides with row above or row below
								int draggedTop = $draggedFaveListItem.offset().top;
								int draggedBottom = draggedTop + $draggedFaveListItem.height();
								GQuery $clonedHiddenRow = $(".clonedHiddenRow");
								GQuery $previous = $clonedHiddenRow.prev();
								GQuery $next = $clonedHiddenRow.next();
								// Make sure we are not checking against the dragged row itself
								if($previous.hasClass("draggedFaveListItem")) $previous = $previous.prev();
								if($next.hasClass("draggedFaveListItem")) $next = $next.next();
								int previousBottom = $previous.offset().top+$previous.height();
								// Move the hidden row to the appropriate position
								if(draggedTop < previousBottom) {
									$(".clonedHiddenRow").insertBefore($previous);
								} else if(draggedBottom > $next.offset().top+$next.height()) {
									$(".clonedHiddenRow").insertAfter($next);
								}
								return true;
							}			
						});
					}
				}
			};

            @Override
            public Cell<String> getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<FaveListItem, String> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getValue(FaveListItem object) {
				return object.getReleaseYear();
			}
		});
		CompositeCell<FaveListItem> cell = new CompositeCell<FaveListItem>(cells);
		cellList = new FaveItemCellList(cell);
		this.add(cellList);
	}
	//TODO: should be list item
	public void setRowData(List<SongProxy> data) {
		cellList.setRowData(data);
	}
}
