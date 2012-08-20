package com.fave100.client.widgets;

import com.fave100.client.pagefragments.SideNotification;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveListItem;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class FaveList extends FaveListBase{

	public FaveList(final ApplicationRequestFactory requestFactory) {
		super(requestFactory);
		
		cells.add(0, new HasCell<FaveListItem, SongProxy>() {
			private ActionCell<SongProxy> cell = new ActionCell<SongProxy>("+", new Delegate<SongProxy>() {
				@Override
				public void execute(SongProxy song) {
					AppUserRequest appUserRequest = requestFactory.appUserRequest();
					Request<Void> addFaveReq = appUserRequest.addFaveItemForCurrentUser(song.getId(), song);
					addFaveReq.fire(new Receiver<Void>() {
						@Override
						public void onSuccess(Void added) {
							SideNotification.show("Added!");												
						}
						
						@Override
						public void onFailure(ServerFailure failure) {
							SideNotification.show(failure.getMessage().replace("Server Error:", ""), true);
						}
					});
				}
			});

            @Override
            public Cell<SongProxy> getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<FaveListItem, SongProxy> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SongProxy getValue(FaveListItem object) {
				return (SongProxy) object;
			}			
		});
		
		createCellList();
	}	
}
