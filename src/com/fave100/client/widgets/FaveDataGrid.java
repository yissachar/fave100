package com.fave100.client.widgets;

import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveListItem;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.user.cellview.client.Column;
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
