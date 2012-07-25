package com.fave100.client.pages.myfave100;

import static com.google.gwt.query.client.GQuery.$;

import java.util.List;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveItemProxy;
import com.fave100.client.requestfactory.FaveItemRequest;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;

/**
 * DataGrid for user to view and edit their personal Fave100 list.
 * @author yissachar.radcliffe
 *
 */
public class FaveDataGrid extends DataGrid<FaveItemProxy>{
	
	// DataGrid StyleSheet override
	public interface DataGridResource extends DataGrid.Resources {
		@Source({ DataGrid.Style.DEFAULT_CSS, "DataGridOverride.css" })
		DataGrid.Style dataGridStyle();
	};
	
	private HandlerRegistration nativePreviewHandler;
	private ApplicationRequestFactory requestFactory;	
		
	public FaveDataGrid(final ApplicationRequestFactory requestFactory) {		
		super(0, (DataGridResource) GWT.create(DataGridResource.class));
		
		this.requestFactory = requestFactory;
		
		// Track name Column
		SafeHtmlCell linkCell = new SafeHtmlCell();
		Column<FaveItemProxy, SafeHtml> trackNameColumn = new Column<FaveItemProxy, SafeHtml>(linkCell) {
			@Override
			public SafeHtml getValue(FaveItemProxy faveItem) {
				// Create a link to the item in iTunes
				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				if(faveItem.getTrackViewUrl() != null && faveItem.getTrackViewUrl() != "") {
					sb.appendHtmlConstant("<a href='"+faveItem.getTrackViewUrl()+"'>"+faveItem.getTrackName()+"</a>");
				} else if(faveItem.getTrackName() != null){
					sb.appendHtmlConstant(faveItem.getTrackName());
				}
				return sb.toSafeHtml();
			}
		};
		trackNameColumn.setCellStyleNames("trackNameColumn");
		this.addColumn(trackNameColumn, "Name");
		
		// Artist Column
		TextColumn<FaveItemProxy> artistColumn = new TextColumn<FaveItemProxy>() {
			@Override
			public String getValue(FaveItemProxy object) {
				return object.getArtistName();
			}
		};
		artistColumn.setCellStyleNames("artistColumn");
		this.addColumn(artistColumn, "Artist");
		
		// Year Column
		TextColumn<FaveItemProxy> yearColumn = new TextColumn<FaveItemProxy>() {
			@Override
			public String getValue(FaveItemProxy object) {
				if(object.getReleaseYear() != null) {
					return object.getReleaseYear();
				} else {
					return null;
				}
			}
		};
		yearColumn.setCellStyleNames("yearColumn");
		this.addColumn(yearColumn, "Year");		
		
		// Delete Column
		ActionCell<FaveItemProxy> deleteButton = new ActionCell<FaveItemProxy>("Delete", new ActionCell.Delegate<FaveItemProxy>() {
		      @Override
		      public void execute(FaveItemProxy faveItem) {
		    	  // Delete the Fave Item
		    	  FaveItemRequest faveItemRequest = requestFactory.faveItemRequest();
		    	  Request<Void> deleteReq = faveItemRequest.removeFaveItemForCurrentUser(faveItem.getId());
		    	  deleteReq.fire(new Receiver<Void>() {
		    		  @Override
		    		  public void onSuccess(Void response) {
		    			  refreshFaveList();									
		    		  }								
		    	  });
		      }
	    });
		Column<FaveItemProxy, FaveItemProxy> deleteColumn = new Column<FaveItemProxy, FaveItemProxy>(deleteButton) {
			@Override
			public FaveItemProxy getValue(FaveItemProxy object) {
				return object;
			}
		};
		deleteColumn.setCellStyleNames("deleteColumn");
		this.addColumn(deleteColumn);
	}
	
	public void refreshFaveList() {
		//get the data from the datastore
		FaveItemRequest faveItemRequest = requestFactory.faveItemRequest();
		Request<List<FaveItemProxy>> allFaveItemsReq = faveItemRequest.getAllFaveItemsForCurrentUser();
		allFaveItemsReq.fire(new Receiver<List<FaveItemProxy>>() {	
			@Override
			public void onSuccess(List<FaveItemProxy> response) {
				setRowData(response);
			}
		});
	}
	
	public void startRanking() {
		//TODO: Switch over to plain GWT
		$(".faveList tbody tr").mousedown(new Function() {
			public boolean f(Event event) {
				// Remove mouse down listener immediately to prevent multiple mouse downs
				$(".faveList tbody tr").unbind("mousedown");
				GQuery $row = $(event.getCurrentEventTarget()).first();
				$(".faveList").addClass("unselectable");
				
				// Add a hidden row to act as a placeholder while the real row is moved
				$row.clone().css("visibility", "hidden").addClass("clonedHiddenRow").insertBefore($row);
				$row.addClass("draggedFaveListItem");
				
				nativePreviewHandler = Event.addNativePreviewHandler(new NativePreviewHandler() {
					@Override
					public void onPreviewNativeEvent(NativePreviewEvent event) {
						// Set the dragged row position to be equal to mouseY						
						GQuery $draggedFaveListItem = $(".draggedFaveListItem");
						int offsetMouseY = event.getNativeEvent().getClientY()-$(".faveList tbody").offset().top+Window.getScrollTop();
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
						}
						else if(draggedBottom > $next.offset().top+$next.height()) {
							$(".clonedHiddenRow").insertAfter($next);
						}
					}			
				});
				$(".faveList").live("mouseup", new Function() {
					public boolean f(Event event) {
						// Only allow one item to be added or we could end up with duplicate entries
						$(".draggedFaveListItem").first().insertAfter($(".clonedHiddenRow"));
						$(".draggedFaveListItem").removeClass("draggedFaveListItem");
						$(".faveList").removeClass("unselectable");
						$(".clonedHiddenRow").remove();
						//remove all listeners now that we are done with the drag
						if(nativePreviewHandler != null) {
							nativePreviewHandler.removeHandler();
							nativePreviewHandler = null;
						}						
						$(".faveList").unbind("mouseup mouseover");
						// Allow the user to rank more items 						
						startRanking();
						return true;
					}
				});
				return true;
			}
		});
	}
}
