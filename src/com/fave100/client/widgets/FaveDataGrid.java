package com.fave100.client.widgets;

import static com.google.gwt.query.client.GQuery.$;

import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveListItem;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;

/**
 * DataGrid to display a list of FaveItems.
 * @author yissachar.radcliffe
 *
 */
public class FaveDataGrid extends FaveDataGridBase {
			
	public FaveDataGrid(final ApplicationRequestFactory requestFactory) {		
		super();
		
		// TODO: Handle what to do if user not logged in - don't show button, or show button
		// but then prompt to log in in hopes of getting new users
		ActionCell<SongProxy> addButton = new ActionCell<SongProxy>("+", new Delegate<SongProxy>() {
			@Override
			public void execute(SongProxy song) {
				AppUserRequest appUserRequest = requestFactory.appUserRequest();
				Request<Boolean> addFaveReq = appUserRequest.addFaveItemForCurrentUser(song.getId(), song);
				addFaveReq.fire(new Receiver<Boolean>() {
					@Override
					public void onSuccess(Boolean added) {
						GQuery $addedAlert = $(".addedAlert");
						if($addedAlert.length() == 0) {						
							$("<div>/div>").insertAfter($("div").first()).addClass("addedAlert");
							$addedAlert = $(".addedAlert");
						} 
						if(added) {
							$addedAlert.text("Added!");
						} else {
							$addedAlert.text("Please login");
						}
						$addedAlert.css("top", Window.getScrollTop()+80+"px");
						final int alertWidth = $addedAlert.outerWidth();
						$addedAlert.css("left",-alertWidth+"px");
						$addedAlert.animate("left:" + "0px", 300).delay(500, new Function() {
							public void f() {
								$(".addedAlert").animate("left:" + -alertWidth+"px", 300);
							}
						});
						
					}
				});
			}
		});
		
		Column<FaveListItem, SongProxy> addColumn = new Column<FaveListItem, SongProxy>(addButton) {	
			@Override
			public SongProxy getValue(FaveListItem object) {
				return (SongProxy) object;
			}
		};
		
		addColumn.setCellStyleNames("addColumn");
		this.insertColumn(0, addColumn);
				
				
		//Set the styles for this tableheader
		this.getTableHeadElement().setClassName("nonpersonalFaves");
	}
}
