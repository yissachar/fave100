package com.fave100.client.pages.lists;

import com.fave100.client.pages.lists.widgets.favelist.FavelistPresenter;
import com.fave100.client.pages.lists.widgets.favelist.FavelistView;
import com.fave100.client.pages.lists.widgets.globallistdetails.GlobalListDetailsPresenter;
import com.fave100.client.pages.lists.widgets.globallistdetails.GlobalListDetailsView;
import com.fave100.client.pages.lists.widgets.listmanager.ListManagerPresenter;
import com.fave100.client.pages.lists.widgets.listmanager.ListManagerView;
import com.fave100.client.pages.lists.widgets.usersfollowing.UsersFollowingPresenter;
import com.fave100.client.pages.lists.widgets.usersfollowing.UsersFollowingView;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class ListModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		bindPresenter(ListPresenter.class, ListPresenter.MyView.class, ListView.class, ListPresenter.MyProxy.class);
		bindPresenterWidget(UsersFollowingPresenter.class, UsersFollowingPresenter.MyView.class, UsersFollowingView.class);
		bindSingletonPresenterWidget(FavelistPresenter.class, FavelistPresenter.MyView.class, FavelistView.class);
		bindSingletonPresenterWidget(ListManagerPresenter.class, ListManagerPresenter.MyView.class, ListManagerView.class);
		bindSingletonPresenterWidget(GlobalListDetailsPresenter.class, GlobalListDetailsPresenter.MyView.class, GlobalListDetailsView.class);
	}

}
