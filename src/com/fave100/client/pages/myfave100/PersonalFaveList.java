package com.fave100.client.pages.myfave100;

import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveItemProxy;
import com.fave100.client.requestfactory.FaveListItem;
import com.fave100.client.widgets.FaveListBase;
import com.fave100.client.widgets.MouseClickCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;

public class PersonalFaveList extends FaveListBase {
	
	private Element draggedRow;
	private ApplicationRequestFactory requestFactory;
	private List<FaveListItem> clientFaveList = new ArrayList<FaveListItem>();
	
	public PersonalFaveList(final ApplicationRequestFactory requestFactory) {
		super(requestFactory);
		
		this.requestFactory = requestFactory;
		
		cells.add(0, new HasCell<FaveListItem, String>() {
			MouseDownCell cell = new MouseDownCell(){
				@Override
				public void onBrowserEvent(Context context, Element parent, String value,
					NativeEvent event, ValueUpdater<String> valueUpdater) {	
					if(value == null) return;		
					super.onBrowserEvent(context, parent, value, event, valueUpdater);					
					if(event.getType().equals("mousedown")) {	
						draggedRow = parent.getParentElement();
						GQuery $row = $(draggedRow);
						addStyleName("unselectable");						
						//GQuery $row = $(event.getEventTarget()).closest("div");
						
						// Add a hidden row to act as a placeholder while the real row is moved					
						$row.clone().css("visibility", "hidden").addClass("clonedHiddenRow").insertBefore($row);
						$row.addClass("draggedFaveListItem");
						
						setPos($row, event.getClientY());
						
						$("body").mousemove(new Function() {
							public boolean f(Event event) {
								// Set the dragged row position to be equal to mouseY						
								GQuery $draggedFaveListItem = $(".draggedFaveListItem");
								setPos($draggedFaveListItem, event.getClientY());
								
								int draggedTop = $draggedFaveListItem.offset().top;
								int draggedBottom = draggedTop + $draggedFaveListItem.outerHeight(true);
								// Check if dragged row collides with row above or row below								
								GQuery $clonedHiddenRow = $(".clonedHiddenRow");
								GQuery $previous = $clonedHiddenRow.prev();
								GQuery $next = $clonedHiddenRow.next();
								// Make sure we are not checking against the dragged row itself
								if($previous.hasClass("draggedFaveListItem")) $previous = $previous.prev();
								if($next.hasClass("draggedFaveListItem")) $next = $next.next();
								int previousBottom = $previous.offset().top+$previous.outerHeight(true);
								// Move the hidden row to the appropriate position
								if(draggedTop < previousBottom) {
									$(".clonedHiddenRow").insertBefore($previous);
								} else if(draggedBottom > $next.offset().top+$next.outerHeight(true)) {
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
				return clientFaveList.indexOf(object)+1+".";
			}
		});	
		
		// Delete button
		cells.add(new HasCell<FaveListItem, String>() {
			private MouseClickCell cell = new MouseClickCell(){
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
				    			refreshList();									
				    		}								
				    	});
					}
				}
			};

            @Override
            public MouseClickCell getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<FaveListItem, String> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getValue(FaveListItem object) {
				return "X";
			}			
		});
		
		// Mouse up handler
		RootPanel.get().addDomHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				// TODO: Switch over to plain GWT?
				if(draggedRow == null) return;
				GQuery $draggedItem = $(".draggedFaveListItem").first();
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
				$("body").unbind("mousemove");
				draggedRow = null;	
				// Reset the rows so that row numbers will update
				// TODO: There has to be a better way of doing this...				
				refreshList();
			}
			
		}, MouseUpEvent.getType());
		
		createCellList();
	}
	
	private void setPos(GQuery element, int mouseY) {		
		int newPos = mouseY-element.height()/2;		
		element.css("top", newPos+"px");
		// TODO: // If dragged row goes out of top or bottom bounds, stop it
		/*int draggedTop = element.offset().top;
		int draggedBottom = draggedTop + element.outerHeight(true);
		if(draggedTop >  $(".faveList").offset().top
			&& draggedBottom < $(".faveList").offset().top+$(".faveList").outerHeight(true)) {
				element.css("top", newPos+"px");
		}*/
	}
	
	public void refreshList() {
		//TODO: To reduce number of RPC calls, perhaps don't refresh list every change
		// instead, make changes locally on client by adding elements to DOM
		
		// Get the data from the datastore
		AppUserRequest appUserRequest = requestFactory.appUserRequest();		
		Request<List<FaveItemProxy>> currentUserReq = appUserRequest.getFaveItemsForCurrentUser();
		currentUserReq.fire(new Receiver<List<FaveItemProxy>>() {
			@Override
			public void onSuccess(List<FaveItemProxy> faveItems) {				
				if(faveItems != null) {
					clientFaveList.clear();
					clientFaveList.addAll(faveItems);
					setRowData(clientFaveList);
				}			
			}
		});
	}

}
