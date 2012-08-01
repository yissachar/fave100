package com.fave100.client.pages.myfave100;

import static com.google.gwt.query.client.GQuery.$;

import java.util.List;

import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveItemProxy;
import com.fave100.client.widgets.FaveDataGrid;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;

/**
 * DataGrid for user to view and edit their personal Fave100 list.
 * @author yissachar.radcliffe
 *
 */
public class UserFaveDataGrid extends FaveDataGrid{	
	
	private HandlerRegistration nativePreviewHandler;
	private ApplicationRequestFactory requestFactory;
	private TableRowElement draggedRow;
		
	public UserFaveDataGrid(final ApplicationRequestFactory requestFactory) {		
		super();
		
		this.requestFactory = requestFactory;
		
		// Drag handler column		
		MouseDownCell dragHandlerCell = new MouseDownCell(){
			@Override
			public void onBrowserEvent(Context context, Element parent, String value,
				NativeEvent event, ValueUpdater<String> valueUpdater) {	
				if(value == null) return;		
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
				// TODO: Switch over to plain GWT
				// TODO: implement on server-side (currently only reranks on client, not persistent)
				if(event.getType().equals("mousedown")) {					
					draggedRow = getRowElement(context.getIndex());
					GQuery $row = $(draggedRow);
					addStyleName("unselectable");
					
					// Add a hidden row to act as a placeholder while the real row is moved					
					$row.clone().css("visibility", "hidden").addClass("clonedHiddenRow").insertBefore($row);
					$row.addClass("draggedFaveListItem");
					
					nativePreviewHandler = Event.addNativePreviewHandler(new NativePreviewHandler() {
						@Override
						public void onPreviewNativeEvent(NativePreviewEvent event) {
//					$("body").mousemove(new Function() {
//						public boolean f(Event event) {
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
							} else if(draggedBottom > $next.offset().top+$next.height()) {
								$(".clonedHiddenRow").insertAfter($next);
							}
							//return true;
						}			
					});
				}
			}
		};
		this.addDomHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				// TODO: Switch over to plain GWT
				if(draggedRow == null) return;
				GQuery $draggedItem = $(draggedRow);
				// Get the index of the row being dragged
				int currentIndex = $draggedItem.parent().children().not(".clonedHiddenRow").index(draggedRow);
				// Insert the dragged row back into the table at the correct position
				$draggedItem.first().insertAfter($(".clonedHiddenRow"));
				// Get the new index
				int newIndex = $draggedItem.parent().children().not(".clonedHiddenRow").index(draggedRow);						
				// Rank on the server
				if(currentIndex != newIndex) {
					// Don't bother doing anything if the indices are the same
					AppUserRequest appUserRequest = requestFactory.appUserRequest();
		    	  	Request<Void> rankReq = appUserRequest.rerankFaveItemForCurrentUser(currentIndex, newIndex);
		    	  	rankReq.fire();
				}	    	  	
				//remove all drag associated items now that we are done with the drag
	    	  	$draggedItem.removeClass("draggedFaveListItem");
				removeStyleName("unselectable");
				$(".clonedHiddenRow").remove();
				draggedRow = null;				
				if(nativePreviewHandler != null) {
					nativePreviewHandler.removeHandler();
					nativePreviewHandler = null;
				}
			}
			
		}, MouseUpEvent.getType());
		
		Column<FaveItemProxy, String> dragHandlerColumn = new Column<FaveItemProxy, String>(dragHandlerCell) {
			@Override
			public String getValue(FaveItemProxy object) {
				return "^";
			}
			
		};
		dragHandlerColumn.setCellStyleNames("dragHandlerColumn");
		this.insertColumn(0, dragHandlerColumn, "");		
		
		// Delete Column
		MouseClickCell deleteButton = new MouseClickCell(){
			@Override
			public void onBrowserEvent(Context context, Element parent, String value,
				NativeEvent event, ValueUpdater<String> valueUpdater) {	
				if(value == null) return;		
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
				if(event.getType().equals("click")) {
					// Delete the Fave Item
			    	AppUserRequest appUserRequest = requestFactory.appUserRequest();
			    	Request<Void> deleteReq = appUserRequest.removeFaveItemForCurrentUser(context.getIndex());
			    	deleteReq.fire(new Receiver<Void>() {
			    		@Override
			    		public void onSuccess(Void response) {
			    			refreshFaveList();									
			    		}								
			    	});
				}
			}
		};
		Column<FaveItemProxy, String> deleteColumn = new Column<FaveItemProxy, String>(deleteButton) {
			@Override
			public String getValue(FaveItemProxy object) {
				return "Delete";
			}
		};
		deleteColumn.setCellStyleNames("deleteColumn");
		this.addColumn(deleteColumn);
	}
	public void refreshFaveList() {
		//TODO: To reduce number of RPC calls, perhaps don't refresh list every change
		// instead, make changes locally on client by adding elements to DOM
		// Get the data from the datastore
		AppUserRequest appUserRequest = requestFactory.appUserRequest();		
		Request<List<FaveItemProxy>> currentUserReq = appUserRequest.getAllSongsForCurrentUser();
		currentUserReq.fire(new Receiver<List<FaveItemProxy>>() {
			@Override
			public void onSuccess(List<FaveItemProxy> faveItems) {				
				if(faveItems != null) setRowData(faveItems);
				resizeFaveList();				
			}
		});
	}
}
