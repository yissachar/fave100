package com.fave100.client.gin;

import com.google.gwt.core.client.GWT;

public class ClientGinjectorImpl implements com.fave100.client.gin.ClientGinjector {
  public com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.about.AboutPresenter> getAboutPresenter() {
    return get_Key$type$com$google$gwt$inject$client$AsyncProvider$com$fave100$client$pages$about$AboutPresenter$$_annotation$$none$$();
  }
  
  public com.google.gwt.event.shared.EventBus getEventBus() {
    return get_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$();
  }
  
  public com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.home.HomePresenter> getHomePresenter() {
    return get_Key$type$com$google$gwt$inject$client$AsyncProvider$com$fave100$client$pages$home$HomePresenter$$_annotation$$none$$();
  }
  
  public com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.myfave100.MyFave100Presenter> getMyFave100Presenter() {
    return get_Key$type$com$google$gwt$inject$client$AsyncProvider$com$fave100$client$pages$myfave100$MyFave100Presenter$$_annotation$$none$$();
  }
  
  public com.gwtplatform.mvp.client.proxy.PlaceManager getPlaceManager() {
    return get_Key$type$com$gwtplatform$mvp$client$proxy$PlaceManager$_annotation$$none$$();
  }
  
  
  /**
   * Binding for com.fave100.client.pages.home.HomePresenter declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:123)
   */
  private native void com$gwtplatform$mvp$client$HandlerContainerImpl_automaticBind_methodInjection(com.gwtplatform.mvp.client.HandlerContainerImpl injectee, com.gwtplatform.mvp.client.AutobindDisable _0) /*-{
    injectee.@com.gwtplatform.mvp.client.HandlerContainerImpl::automaticBind(Lcom/gwtplatform/mvp/client/AutobindDisable;)(_0);
  }-*/;
  
  private native void com$fave100$client$pages$home$HomePresenter_topBar_fieldInjection(com.fave100.client.pages.home.HomePresenter injectee, com.fave100.client.pagefragments.TopBarPresenter value) /*-{
    injectee.@com.fave100.client.pages.home.HomePresenter::topBar = value;
  }-*/;
  
  private void memberInject_Key$type$com$fave100$client$pages$home$HomePresenter$_annotation$$none$$(com.fave100.client.pages.home.HomePresenter injectee) {
    com$gwtplatform$mvp$client$HandlerContainerImpl_automaticBind_methodInjection(injectee, get_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$());
    com$fave100$client$pages$home$HomePresenter_topBar_fieldInjection(injectee, get_Key$type$com$fave100$client$pagefragments$TopBarPresenter$_annotation$$none$$());
    
  }
  
  private com.fave100.client.pages.home.HomePresenter com$fave100$client$pages$home$HomePresenter_HomePresenter_methodInjection(com.google.gwt.event.shared.EventBus _0, com.fave100.client.pages.home.HomePresenter.MyView _1, com.fave100.client.pages.home.HomePresenter.MyProxy _2) {
    return new com.fave100.client.pages.home.HomePresenter(_0, _1, _2);
  }
  
  private com.fave100.client.pages.home.HomePresenter create_Key$type$com$fave100$client$pages$home$HomePresenter$_annotation$$none$$() {
    com.fave100.client.pages.home.HomePresenter result = com$fave100$client$pages$home$HomePresenter_HomePresenter_methodInjection(get_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$(), get_Key$type$com$fave100$client$pages$home$HomePresenter$MyView$_annotation$$none$$(), get_Key$type$com$fave100$client$pages$home$HomePresenter$MyProxy$_annotation$$none$$());
    memberInject_Key$type$com$fave100$client$pages$home$HomePresenter$_annotation$$none$$(result);
    return result;
  }
  
  private com.fave100.client.pages.home.HomePresenter singleton_Key$type$com$fave100$client$pages$home$HomePresenter$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:123)
   */
  private com.fave100.client.pages.home.HomePresenter get_Key$type$com$fave100$client$pages$home$HomePresenter$_annotation$$none$$() {
    if (singleton_Key$type$com$fave100$client$pages$home$HomePresenter$_annotation$$none$$ == null) {
      singleton_Key$type$com$fave100$client$pages$home$HomePresenter$_annotation$$none$$ = create_Key$type$com$fave100$client$pages$home$HomePresenter$_annotation$$none$$();
    }
    return singleton_Key$type$com$fave100$client$pages$home$HomePresenter$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.fave100.client.pages.about.AboutPresenter$MyProxy declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:125)
   */
  private void memberInject_Key$type$com$fave100$client$pages$about$AboutPresenter$MyProxy$_annotation$$none$$(com.fave100.client.pages.about.AboutPresenter.MyProxy injectee) {
    
  }
  
  private com.fave100.client.pages.about.AboutPresenter.MyProxy create_Key$type$com$fave100$client$pages$about$AboutPresenter$MyProxy$_annotation$$none$$() {
    Object created = GWT.create(com.fave100.client.pages.about.AboutPresenter.MyProxy.class);
    assert created instanceof com.fave100.client.pages.about.AboutPresenter.MyProxy;
    com.fave100.client.pages.about.AboutPresenter.MyProxy result = (com.fave100.client.pages.about.AboutPresenter.MyProxy) created;
    
    memberInject_Key$type$com$fave100$client$pages$about$AboutPresenter$MyProxy$_annotation$$none$$(result);
    return result;
  }
  
  private com.fave100.client.pages.about.AboutPresenter.MyProxy singleton_Key$type$com$fave100$client$pages$about$AboutPresenter$MyProxy$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:125)
   */
  private com.fave100.client.pages.about.AboutPresenter.MyProxy get_Key$type$com$fave100$client$pages$about$AboutPresenter$MyProxy$_annotation$$none$$() {
    if (singleton_Key$type$com$fave100$client$pages$about$AboutPresenter$MyProxy$_annotation$$none$$ == null) {
      singleton_Key$type$com$fave100$client$pages$about$AboutPresenter$MyProxy$_annotation$$none$$ = create_Key$type$com$fave100$client$pages$about$AboutPresenter$MyProxy$_annotation$$none$$();
    }
    return singleton_Key$type$com$fave100$client$pages$about$AboutPresenter$MyProxy$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor declared at:
   *   Implicit binding for Key[type=com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor, annotation=[none]]
   */
  private void memberInject_Key$type$com$gwtplatform$dispatch$client$DefaultSecurityCookieAccessor$_annotation$$none$$(com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor injectee) {
    
  }
  
  private com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor create_Key$type$com$gwtplatform$dispatch$client$DefaultSecurityCookieAccessor$_annotation$$none$$() {
    Object created = GWT.create(com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor.class);
    assert created instanceof com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor;
    com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor result = (com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor) created;
    
    memberInject_Key$type$com$gwtplatform$dispatch$client$DefaultSecurityCookieAccessor$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor declared at:
   *   Implicit binding for Key[type=com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor, annotation=[none]]
   */
  private com.gwtplatform.dispatch.client.DefaultSecurityCookieAccessor get_Key$type$com$gwtplatform$dispatch$client$DefaultSecurityCookieAccessor$_annotation$$none$$() {
    return create_Key$type$com$gwtplatform$dispatch$client$DefaultSecurityCookieAccessor$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.gwtplatform.mvp.client.RootPresenter$RootView declared at:
   *   Implicit binding for Key[type=com.gwtplatform.mvp.client.RootPresenter$RootView, annotation=[none]]
   */
  private void memberInject_Key$type$com$gwtplatform$mvp$client$RootPresenter$RootView$_annotation$$none$$(com.gwtplatform.mvp.client.RootPresenter.RootView injectee) {
    
  }
  
  private com.gwtplatform.mvp.client.RootPresenter.RootView create_Key$type$com$gwtplatform$mvp$client$RootPresenter$RootView$_annotation$$none$$() {
    Object created = GWT.create(com.gwtplatform.mvp.client.RootPresenter.RootView.class);
    assert created instanceof com.gwtplatform.mvp.client.RootPresenter.RootView;
    com.gwtplatform.mvp.client.RootPresenter.RootView result = (com.gwtplatform.mvp.client.RootPresenter.RootView) created;
    
    memberInject_Key$type$com$gwtplatform$mvp$client$RootPresenter$RootView$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.gwtplatform.mvp.client.RootPresenter$RootView declared at:
   *   Implicit binding for Key[type=com.gwtplatform.mvp.client.RootPresenter$RootView, annotation=[none]]
   */
  private com.gwtplatform.mvp.client.RootPresenter.RootView get_Key$type$com$gwtplatform$mvp$client$RootPresenter$RootView$_annotation$$none$$() {
    return create_Key$type$com$gwtplatform$mvp$client$RootPresenter$RootView$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.gwtplatform.mvp.client.AutobindDisable declared at:
   *   Implicit binding for Key[type=com.gwtplatform.mvp.client.AutobindDisable, annotation=[none]]
   */
  private void memberInject_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$(com.gwtplatform.mvp.client.AutobindDisable injectee) {
    
  }
  
  private native com.gwtplatform.mvp.client.AutobindDisable com$gwtplatform$mvp$client$AutobindDisable_AutobindDisable_methodInjection() /*-{
    return @com.gwtplatform.mvp.client.AutobindDisable::new()();
  }-*/;
  
  private com.gwtplatform.mvp.client.AutobindDisable create_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$() {
    com.gwtplatform.mvp.client.AutobindDisable result = com$gwtplatform$mvp$client$AutobindDisable_AutobindDisable_methodInjection();
    memberInject_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$(result);
    return result;
  }
  
  private com.gwtplatform.mvp.client.AutobindDisable singleton_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   Implicit binding for Key[type=com.gwtplatform.mvp.client.AutobindDisable, annotation=[none]]
   */
  private com.gwtplatform.mvp.client.AutobindDisable get_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$() {
    if (singleton_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$ == null) {
      singleton_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$ = create_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$();
    }
    return singleton_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.gwtplatform.mvp.client.proxy.TokenFormatter declared at:
   *   com.gwtplatform.mvp.client.gin.DefaultModule.configure(DefaultModule.java:48)
   */
  private com.gwtplatform.mvp.client.proxy.TokenFormatter create_Key$type$com$gwtplatform$mvp$client$proxy$TokenFormatter$_annotation$$none$$() {
    return get_Key$type$com$gwtplatform$mvp$client$proxy$ParameterTokenFormatter$_annotation$$none$$();
  }
  
  private com.gwtplatform.mvp.client.proxy.TokenFormatter singleton_Key$type$com$gwtplatform$mvp$client$proxy$TokenFormatter$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.DefaultModule.configure(DefaultModule.java:48)
   */
  private com.gwtplatform.mvp.client.proxy.TokenFormatter get_Key$type$com$gwtplatform$mvp$client$proxy$TokenFormatter$_annotation$$none$$() {
    if (singleton_Key$type$com$gwtplatform$mvp$client$proxy$TokenFormatter$_annotation$$none$$ == null) {
      singleton_Key$type$com$gwtplatform$mvp$client$proxy$TokenFormatter$_annotation$$none$$ = create_Key$type$com$gwtplatform$mvp$client$proxy$TokenFormatter$_annotation$$none$$();
    }
    return singleton_Key$type$com$gwtplatform$mvp$client$proxy$TokenFormatter$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.fave100.client.pages.home.HomePresenter$MyProxy declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:125)
   */
  private void memberInject_Key$type$com$fave100$client$pages$home$HomePresenter$MyProxy$_annotation$$none$$(com.fave100.client.pages.home.HomePresenter.MyProxy injectee) {
    
  }
  
  private com.fave100.client.pages.home.HomePresenter.MyProxy create_Key$type$com$fave100$client$pages$home$HomePresenter$MyProxy$_annotation$$none$$() {
    Object created = GWT.create(com.fave100.client.pages.home.HomePresenter.MyProxy.class);
    assert created instanceof com.fave100.client.pages.home.HomePresenter.MyProxy;
    com.fave100.client.pages.home.HomePresenter.MyProxy result = (com.fave100.client.pages.home.HomePresenter.MyProxy) created;
    
    memberInject_Key$type$com$fave100$client$pages$home$HomePresenter$MyProxy$_annotation$$none$$(result);
    return result;
  }
  
  private com.fave100.client.pages.home.HomePresenter.MyProxy singleton_Key$type$com$fave100$client$pages$home$HomePresenter$MyProxy$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:125)
   */
  private com.fave100.client.pages.home.HomePresenter.MyProxy get_Key$type$com$fave100$client$pages$home$HomePresenter$MyProxy$_annotation$$none$$() {
    if (singleton_Key$type$com$fave100$client$pages$home$HomePresenter$MyProxy$_annotation$$none$$ == null) {
      singleton_Key$type$com$fave100$client$pages$home$HomePresenter$MyProxy$_annotation$$none$$ = create_Key$type$com$fave100$client$pages$home$HomePresenter$MyProxy$_annotation$$none$$();
    }
    return singleton_Key$type$com$fave100$client$pages$home$HomePresenter$MyProxy$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.fave100.client.pagefragments.TopBarView declared at:
   *   Implicit binding for Key[type=com.fave100.client.pagefragments.TopBarView, annotation=[none]]
   */
  private void memberInject_Key$type$com$fave100$client$pagefragments$TopBarView$_annotation$$none$$(com.fave100.client.pagefragments.TopBarView injectee) {
    
  }
  
  private com.fave100.client.pagefragments.TopBarView com$fave100$client$pagefragments$TopBarView_TopBarView_methodInjection(com.fave100.client.pagefragments.TopBarView.Binder _0) {
    return new com.fave100.client.pagefragments.TopBarView(_0);
  }
  
  private com.fave100.client.pagefragments.TopBarView create_Key$type$com$fave100$client$pagefragments$TopBarView$_annotation$$none$$() {
    com.fave100.client.pagefragments.TopBarView result = com$fave100$client$pagefragments$TopBarView_TopBarView_methodInjection(get_Key$type$com$fave100$client$pagefragments$TopBarView$Binder$_annotation$$none$$());
    memberInject_Key$type$com$fave100$client$pagefragments$TopBarView$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.fave100.client.pagefragments.TopBarView declared at:
   *   Implicit binding for Key[type=com.fave100.client.pagefragments.TopBarView, annotation=[none]]
   */
  private com.fave100.client.pagefragments.TopBarView get_Key$type$com$fave100$client$pagefragments$TopBarView$_annotation$$none$$() {
    return create_Key$type$com$fave100$client$pagefragments$TopBarView$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for java.lang.String declared at:
   *   com.fave100.client.gin.ClientModule.configure(ClientModule.java:23)
   */
  private java.lang.String create_Key$type$java$lang$String$_annotation$$com$fave100$client$place$DefaultPlace$() {
    return "home";
  }
  
  
  /**
   * Binding for java.lang.String declared at:
   *   com.fave100.client.gin.ClientModule.configure(ClientModule.java:23)
   */
  private java.lang.String get_Key$type$java$lang$String$_annotation$$com$fave100$client$place$DefaultPlace$() {
    return create_Key$type$java$lang$String$_annotation$$com$fave100$client$place$DefaultPlace$();
  }
  
  
  
  /**
   * Binding for com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.home.HomePresenter> declared at:
   *   Implicit binding for Key[type=com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.home.HomePresenter>, annotation=[none]]
   */
  private com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.home.HomePresenter> create_Key$type$com$google$gwt$inject$client$AsyncProvider$com$fave100$client$pages$home$HomePresenter$$_annotation$$none$$() {
    return new com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.home.HomePresenter>() { 
        public void get(final com.google.gwt.user.client.rpc.AsyncCallback<com.fave100.client.pages.home.HomePresenter> callback) { 
          com.google.gwt.core.client.GWT.runAsync(new com.google.gwt.core.client.RunAsyncCallback() { 
            public void onSuccess() { 
              callback.onSuccess(get_Key$type$com$fave100$client$pages$home$HomePresenter$_annotation$$none$$()); 
            }
            public void onFailure(Throwable ex) { 
               callback.onFailure(ex); 
            } 
        }); 
        }
     };
    
  }
  
  
  /**
   * Binding for com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.home.HomePresenter> declared at:
   *   Implicit binding for Key[type=com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.home.HomePresenter>, annotation=[none]]
   */
  private com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.home.HomePresenter> get_Key$type$com$google$gwt$inject$client$AsyncProvider$com$fave100$client$pages$home$HomePresenter$$_annotation$$none$$() {
    return create_Key$type$com$google$gwt$inject$client$AsyncProvider$com$fave100$client$pages$home$HomePresenter$$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.fave100.client.place.ClientPlaceManager declared at:
   *   Implicit binding for Key[type=com.fave100.client.place.ClientPlaceManager, annotation=[none]]
   */
  private void memberInject_Key$type$com$fave100$client$place$ClientPlaceManager$_annotation$$none$$(com.fave100.client.place.ClientPlaceManager injectee) {
    
  }
  
  private com.fave100.client.place.ClientPlaceManager com$fave100$client$place$ClientPlaceManager_ClientPlaceManager_methodInjection(com.google.gwt.event.shared.EventBus _0, com.gwtplatform.mvp.client.proxy.TokenFormatter _1, java.lang.String _2) {
    return new com.fave100.client.place.ClientPlaceManager(_0, _1, _2);
  }
  
  private com.fave100.client.place.ClientPlaceManager create_Key$type$com$fave100$client$place$ClientPlaceManager$_annotation$$none$$() {
    com.fave100.client.place.ClientPlaceManager result = com$fave100$client$place$ClientPlaceManager_ClientPlaceManager_methodInjection(get_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$(), get_Key$type$com$gwtplatform$mvp$client$proxy$TokenFormatter$_annotation$$none$$(), get_Key$type$java$lang$String$_annotation$$com$fave100$client$place$DefaultPlace$());
    memberInject_Key$type$com$fave100$client$place$ClientPlaceManager$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.fave100.client.place.ClientPlaceManager declared at:
   *   Implicit binding for Key[type=com.fave100.client.place.ClientPlaceManager, annotation=[none]]
   */
  private com.fave100.client.place.ClientPlaceManager get_Key$type$com$fave100$client$place$ClientPlaceManager$_annotation$$none$$() {
    return create_Key$type$com$fave100$client$place$ClientPlaceManager$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.google.gwt.event.shared.EventBus declared at:
   *   com.gwtplatform.mvp.client.gin.DefaultModule.configure(DefaultModule.java:47)
   */
  private com.google.gwt.event.shared.EventBus create_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$() {
    return get_Key$type$com$google$gwt$event$shared$SimpleEventBus$_annotation$$none$$();
  }
  
  private com.google.gwt.event.shared.EventBus singleton_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.DefaultModule.configure(DefaultModule.java:47)
   */
  private com.google.gwt.event.shared.EventBus get_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$() {
    if (singleton_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$ == null) {
      singleton_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$ = create_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$();
    }
    return singleton_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.fave100.client.pages.myfave100.MyFave100View declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:124)
   */
  private void memberInject_Key$type$com$fave100$client$pages$myfave100$MyFave100View$_annotation$$none$$(com.fave100.client.pages.myfave100.MyFave100View injectee) {
    
  }
  
  private com.fave100.client.pages.myfave100.MyFave100View com$fave100$client$pages$myfave100$MyFave100View_MyFave100View_methodInjection(com.fave100.client.pages.myfave100.MyFave100View.Binder _0) {
    return new com.fave100.client.pages.myfave100.MyFave100View(_0);
  }
  
  private com.fave100.client.pages.myfave100.MyFave100View create_Key$type$com$fave100$client$pages$myfave100$MyFave100View$_annotation$$none$$() {
    com.fave100.client.pages.myfave100.MyFave100View result = com$fave100$client$pages$myfave100$MyFave100View_MyFave100View_methodInjection(get_Key$type$com$fave100$client$pages$myfave100$MyFave100View$Binder$_annotation$$none$$());
    memberInject_Key$type$com$fave100$client$pages$myfave100$MyFave100View$_annotation$$none$$(result);
    return result;
  }
  
  private com.fave100.client.pages.myfave100.MyFave100View singleton_Key$type$com$fave100$client$pages$myfave100$MyFave100View$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:124)
   */
  private com.fave100.client.pages.myfave100.MyFave100View get_Key$type$com$fave100$client$pages$myfave100$MyFave100View$_annotation$$none$$() {
    if (singleton_Key$type$com$fave100$client$pages$myfave100$MyFave100View$_annotation$$none$$ == null) {
      singleton_Key$type$com$fave100$client$pages$myfave100$MyFave100View$_annotation$$none$$ = create_Key$type$com$fave100$client$pages$myfave100$MyFave100View$_annotation$$none$$();
    }
    return singleton_Key$type$com$fave100$client$pages$myfave100$MyFave100View$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl declared at:
   *   Implicit binding for Key[type=com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl, annotation=[none]]
   */
  private void memberInject_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalyticsImpl$_annotation$$none$$(com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl injectee) {
    
  }
  
  private com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl create_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalyticsImpl$_annotation$$none$$() {
    Object created = GWT.create(com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl.class);
    assert created instanceof com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl;
    com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl result = (com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl) created;
    
    memberInject_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalyticsImpl$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl declared at:
   *   Implicit binding for Key[type=com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl, annotation=[none]]
   */
  private com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl get_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalyticsImpl$_annotation$$none$$() {
    return create_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalyticsImpl$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter declared at:
   *   Implicit binding for Key[type=com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter, annotation=[none]]
   */
  private void memberInject_Key$type$com$gwtplatform$mvp$client$proxy$ParameterTokenFormatter$_annotation$$none$$(com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter injectee) {
    
  }
  
  private com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter com$gwtplatform$mvp$client$proxy$ParameterTokenFormatter_ParameterTokenFormatter_methodInjection() {
    return new com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter();
  }
  
  private com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter create_Key$type$com$gwtplatform$mvp$client$proxy$ParameterTokenFormatter$_annotation$$none$$() {
    com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter result = com$gwtplatform$mvp$client$proxy$ParameterTokenFormatter_ParameterTokenFormatter_methodInjection();
    memberInject_Key$type$com$gwtplatform$mvp$client$proxy$ParameterTokenFormatter$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter declared at:
   *   Implicit binding for Key[type=com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter, annotation=[none]]
   */
  private com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter get_Key$type$com$gwtplatform$mvp$client$proxy$ParameterTokenFormatter$_annotation$$none$$() {
    return create_Key$type$com$gwtplatform$mvp$client$proxy$ParameterTokenFormatter$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.fave100.client.pages.myfave100.MyFave100View$Binder declared at:
   *   Implicit binding for Key[type=com.fave100.client.pages.myfave100.MyFave100View$Binder, annotation=[none]]
   */
  private void memberInject_Key$type$com$fave100$client$pages$myfave100$MyFave100View$Binder$_annotation$$none$$(com.fave100.client.pages.myfave100.MyFave100View.Binder injectee) {
    
  }
  
  private com.fave100.client.pages.myfave100.MyFave100View.Binder create_Key$type$com$fave100$client$pages$myfave100$MyFave100View$Binder$_annotation$$none$$() {
    Object created = GWT.create(com.fave100.client.pages.myfave100.MyFave100View.Binder.class);
    assert created instanceof com.fave100.client.pages.myfave100.MyFave100View.Binder;
    com.fave100.client.pages.myfave100.MyFave100View.Binder result = (com.fave100.client.pages.myfave100.MyFave100View.Binder) created;
    
    memberInject_Key$type$com$fave100$client$pages$myfave100$MyFave100View$Binder$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.fave100.client.pages.myfave100.MyFave100View$Binder declared at:
   *   Implicit binding for Key[type=com.fave100.client.pages.myfave100.MyFave100View$Binder, annotation=[none]]
   */
  private com.fave100.client.pages.myfave100.MyFave100View.Binder get_Key$type$com$fave100$client$pages$myfave100$MyFave100View$Binder$_annotation$$none$$() {
    return create_Key$type$com$fave100$client$pages$myfave100$MyFave100View$Binder$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.fave100.client.pages.about.AboutPresenter declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:123)
   */
  private native void com$gwtplatform$mvp$client$HandlerContainerImpl_automaticBind_methodInjection_(com.gwtplatform.mvp.client.HandlerContainerImpl injectee, com.gwtplatform.mvp.client.AutobindDisable _0) /*-{
    injectee.@com.gwtplatform.mvp.client.HandlerContainerImpl::automaticBind(Lcom/gwtplatform/mvp/client/AutobindDisable;)(_0);
  }-*/;
  
  private native void com$fave100$client$pages$about$AboutPresenter_topBar_fieldInjection(com.fave100.client.pages.about.AboutPresenter injectee, com.fave100.client.pagefragments.TopBarPresenter value) /*-{
    injectee.@com.fave100.client.pages.about.AboutPresenter::topBar = value;
  }-*/;
  
  private void memberInject_Key$type$com$fave100$client$pages$about$AboutPresenter$_annotation$$none$$(com.fave100.client.pages.about.AboutPresenter injectee) {
    com$gwtplatform$mvp$client$HandlerContainerImpl_automaticBind_methodInjection_(injectee, get_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$());
    com$fave100$client$pages$about$AboutPresenter_topBar_fieldInjection(injectee, get_Key$type$com$fave100$client$pagefragments$TopBarPresenter$_annotation$$none$$());
    
  }
  
  private com.fave100.client.pages.about.AboutPresenter com$fave100$client$pages$about$AboutPresenter_AboutPresenter_methodInjection(com.google.gwt.event.shared.EventBus _0, com.fave100.client.pages.about.AboutPresenter.MyView _1, com.fave100.client.pages.about.AboutPresenter.MyProxy _2) {
    return new com.fave100.client.pages.about.AboutPresenter(_0, _1, _2);
  }
  
  private com.fave100.client.pages.about.AboutPresenter create_Key$type$com$fave100$client$pages$about$AboutPresenter$_annotation$$none$$() {
    com.fave100.client.pages.about.AboutPresenter result = com$fave100$client$pages$about$AboutPresenter_AboutPresenter_methodInjection(get_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$(), get_Key$type$com$fave100$client$pages$about$AboutPresenter$MyView$_annotation$$none$$(), get_Key$type$com$fave100$client$pages$about$AboutPresenter$MyProxy$_annotation$$none$$());
    memberInject_Key$type$com$fave100$client$pages$about$AboutPresenter$_annotation$$none$$(result);
    return result;
  }
  
  private com.fave100.client.pages.about.AboutPresenter singleton_Key$type$com$fave100$client$pages$about$AboutPresenter$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:123)
   */
  private com.fave100.client.pages.about.AboutPresenter get_Key$type$com$fave100$client$pages$about$AboutPresenter$_annotation$$none$$() {
    if (singleton_Key$type$com$fave100$client$pages$about$AboutPresenter$_annotation$$none$$ == null) {
      singleton_Key$type$com$fave100$client$pages$about$AboutPresenter$_annotation$$none$$ = create_Key$type$com$fave100$client$pages$about$AboutPresenter$_annotation$$none$$();
    }
    return singleton_Key$type$com$fave100$client$pages$about$AboutPresenter$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.gwtplatform.dispatch.shared.SecurityCookieAccessor declared at:
   *   com.gwtplatform.dispatch.client.gin.DispatchAsyncModule.configure(DispatchAsyncModule.java:180)
   */
  private com.gwtplatform.dispatch.shared.SecurityCookieAccessor create_Key$type$com$gwtplatform$dispatch$shared$SecurityCookieAccessor$_annotation$$none$$() {
    return get_Key$type$com$gwtplatform$dispatch$client$DefaultSecurityCookieAccessor$_annotation$$none$$();
  }
  
  
  /**
   * Binding for com.gwtplatform.dispatch.shared.SecurityCookieAccessor declared at:
   *   com.gwtplatform.dispatch.client.gin.DispatchAsyncModule.configure(DispatchAsyncModule.java:180)
   */
  private com.gwtplatform.dispatch.shared.SecurityCookieAccessor get_Key$type$com$gwtplatform$dispatch$shared$SecurityCookieAccessor$_annotation$$none$$() {
    return create_Key$type$com$gwtplatform$dispatch$shared$SecurityCookieAccessor$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.fave100.client.pages.about.AboutPresenter$MyView declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:126)
   */
  private com.fave100.client.pages.about.AboutPresenter.MyView create_Key$type$com$fave100$client$pages$about$AboutPresenter$MyView$_annotation$$none$$() {
    return get_Key$type$com$fave100$client$pages$about$AboutView$_annotation$$none$$();
  }
  
  
  /**
   * Binding for com.fave100.client.pages.about.AboutPresenter$MyView declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:126)
   */
  private com.fave100.client.pages.about.AboutPresenter.MyView get_Key$type$com$fave100$client$pages$about$AboutPresenter$MyView$_annotation$$none$$() {
    return create_Key$type$com$fave100$client$pages$about$AboutPresenter$MyView$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.fave100.client.pagefragments.TopBarPresenter$MyView declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenterWidget(AbstractPresenterModule.java:265)
   */
  private com.fave100.client.pagefragments.TopBarPresenter.MyView create_Key$type$com$fave100$client$pagefragments$TopBarPresenter$MyView$_annotation$$none$$() {
    return get_Key$type$com$fave100$client$pagefragments$TopBarView$_annotation$$none$$();
  }
  
  
  /**
   * Binding for com.fave100.client.pagefragments.TopBarPresenter$MyView declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenterWidget(AbstractPresenterModule.java:265)
   */
  private com.fave100.client.pagefragments.TopBarPresenter.MyView get_Key$type$com$fave100$client$pagefragments$TopBarPresenter$MyView$_annotation$$none$$() {
    return create_Key$type$com$fave100$client$pagefragments$TopBarPresenter$MyView$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.gwtplatform.dispatch.client.ExceptionHandler declared at:
   *   com.gwtplatform.dispatch.client.gin.DispatchAsyncModule.configure(DispatchAsyncModule.java:179)
   */
  private com.gwtplatform.dispatch.client.ExceptionHandler create_Key$type$com$gwtplatform$dispatch$client$ExceptionHandler$_annotation$$none$$() {
    return get_Key$type$com$gwtplatform$dispatch$client$DefaultExceptionHandler$_annotation$$none$$();
  }
  
  
  /**
   * Binding for com.gwtplatform.dispatch.client.ExceptionHandler declared at:
   *   com.gwtplatform.dispatch.client.gin.DispatchAsyncModule.configure(DispatchAsyncModule.java:179)
   */
  private com.gwtplatform.dispatch.client.ExceptionHandler get_Key$type$com$gwtplatform$dispatch$client$ExceptionHandler$_annotation$$none$$() {
    return create_Key$type$com$gwtplatform$dispatch$client$ExceptionHandler$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.about.AboutPresenter> declared at:
   *   Implicit binding for Key[type=com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.about.AboutPresenter>, annotation=[none]]
   */
  private com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.about.AboutPresenter> create_Key$type$com$google$gwt$inject$client$AsyncProvider$com$fave100$client$pages$about$AboutPresenter$$_annotation$$none$$() {
    return new com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.about.AboutPresenter>() { 
        public void get(final com.google.gwt.user.client.rpc.AsyncCallback<com.fave100.client.pages.about.AboutPresenter> callback) { 
          com.google.gwt.core.client.GWT.runAsync(new com.google.gwt.core.client.RunAsyncCallback() { 
            public void onSuccess() { 
              callback.onSuccess(get_Key$type$com$fave100$client$pages$about$AboutPresenter$_annotation$$none$$()); 
            }
            public void onFailure(Throwable ex) { 
               callback.onFailure(ex); 
            } 
        }); 
        }
     };
    
  }
  
  
  /**
   * Binding for com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.about.AboutPresenter> declared at:
   *   Implicit binding for Key[type=com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.about.AboutPresenter>, annotation=[none]]
   */
  private com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.about.AboutPresenter> get_Key$type$com$google$gwt$inject$client$AsyncProvider$com$fave100$client$pages$about$AboutPresenter$$_annotation$$none$$() {
    return create_Key$type$com$google$gwt$inject$client$AsyncProvider$com$fave100$client$pages$about$AboutPresenter$$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.fave100.client.pages.home.HomeView declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:124)
   */
  private void memberInject_Key$type$com$fave100$client$pages$home$HomeView$_annotation$$none$$(com.fave100.client.pages.home.HomeView injectee) {
    
  }
  
  private com.fave100.client.pages.home.HomeView com$fave100$client$pages$home$HomeView_HomeView_methodInjection(com.fave100.client.pages.home.HomeView.Binder _0) {
    return new com.fave100.client.pages.home.HomeView(_0);
  }
  
  private com.fave100.client.pages.home.HomeView create_Key$type$com$fave100$client$pages$home$HomeView$_annotation$$none$$() {
    com.fave100.client.pages.home.HomeView result = com$fave100$client$pages$home$HomeView_HomeView_methodInjection(get_Key$type$com$fave100$client$pages$home$HomeView$Binder$_annotation$$none$$());
    memberInject_Key$type$com$fave100$client$pages$home$HomeView$_annotation$$none$$(result);
    return result;
  }
  
  private com.fave100.client.pages.home.HomeView singleton_Key$type$com$fave100$client$pages$home$HomeView$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:124)
   */
  private com.fave100.client.pages.home.HomeView get_Key$type$com$fave100$client$pages$home$HomeView$_annotation$$none$$() {
    if (singleton_Key$type$com$fave100$client$pages$home$HomeView$_annotation$$none$$ == null) {
      singleton_Key$type$com$fave100$client$pages$home$HomeView$_annotation$$none$$ = create_Key$type$com$fave100$client$pages$home$HomeView$_annotation$$none$$();
    }
    return singleton_Key$type$com$fave100$client$pages$home$HomeView$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.fave100.client.pages.home.HomeView$Binder declared at:
   *   Implicit binding for Key[type=com.fave100.client.pages.home.HomeView$Binder, annotation=[none]]
   */
  private void memberInject_Key$type$com$fave100$client$pages$home$HomeView$Binder$_annotation$$none$$(com.fave100.client.pages.home.HomeView.Binder injectee) {
    
  }
  
  private com.fave100.client.pages.home.HomeView.Binder create_Key$type$com$fave100$client$pages$home$HomeView$Binder$_annotation$$none$$() {
    Object created = GWT.create(com.fave100.client.pages.home.HomeView.Binder.class);
    assert created instanceof com.fave100.client.pages.home.HomeView.Binder;
    com.fave100.client.pages.home.HomeView.Binder result = (com.fave100.client.pages.home.HomeView.Binder) created;
    
    memberInject_Key$type$com$fave100$client$pages$home$HomeView$Binder$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.fave100.client.pages.home.HomeView$Binder declared at:
   *   Implicit binding for Key[type=com.fave100.client.pages.home.HomeView$Binder, annotation=[none]]
   */
  private com.fave100.client.pages.home.HomeView.Binder get_Key$type$com$fave100$client$pages$home$HomeView$Binder$_annotation$$none$$() {
    return create_Key$type$com$fave100$client$pages$home$HomeView$Binder$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics declared at:
   *   com.gwtplatform.mvp.client.gin.DefaultModule.configure(DefaultModule.java:50)
   */
  private com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics create_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalytics$_annotation$$none$$() {
    return get_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalyticsImpl$_annotation$$none$$();
  }
  
  private com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics singleton_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalytics$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.DefaultModule.configure(DefaultModule.java:50)
   */
  private com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics get_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalytics$_annotation$$none$$() {
    if (singleton_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalytics$_annotation$$none$$ == null) {
      singleton_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalytics$_annotation$$none$$ = create_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalytics$_annotation$$none$$();
    }
    return singleton_Key$type$com$gwtplatform$mvp$client$googleanalytics$GoogleAnalytics$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.fave100.client.pages.home.HomePresenter$MyView declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:126)
   */
  private com.fave100.client.pages.home.HomePresenter.MyView create_Key$type$com$fave100$client$pages$home$HomePresenter$MyView$_annotation$$none$$() {
    return get_Key$type$com$fave100$client$pages$home$HomeView$_annotation$$none$$();
  }
  
  
  /**
   * Binding for com.fave100.client.pages.home.HomePresenter$MyView declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:126)
   */
  private com.fave100.client.pages.home.HomePresenter.MyView get_Key$type$com$fave100$client$pages$home$HomePresenter$MyView$_annotation$$none$$() {
    return create_Key$type$com$fave100$client$pages$home$HomePresenter$MyView$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.myfave100.MyFave100Presenter> declared at:
   *   Implicit binding for Key[type=com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.myfave100.MyFave100Presenter>, annotation=[none]]
   */
  private com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.myfave100.MyFave100Presenter> create_Key$type$com$google$gwt$inject$client$AsyncProvider$com$fave100$client$pages$myfave100$MyFave100Presenter$$_annotation$$none$$() {
    return new com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.myfave100.MyFave100Presenter>() { 
        public void get(final com.google.gwt.user.client.rpc.AsyncCallback<com.fave100.client.pages.myfave100.MyFave100Presenter> callback) { 
          com.google.gwt.core.client.GWT.runAsync(new com.google.gwt.core.client.RunAsyncCallback() { 
            public void onSuccess() { 
              callback.onSuccess(get_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$_annotation$$none$$()); 
            }
            public void onFailure(Throwable ex) { 
               callback.onFailure(ex); 
            } 
        }); 
        }
     };
    
  }
  
  
  /**
   * Binding for com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.myfave100.MyFave100Presenter> declared at:
   *   Implicit binding for Key[type=com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.myfave100.MyFave100Presenter>, annotation=[none]]
   */
  private com.google.gwt.inject.client.AsyncProvider<com.fave100.client.pages.myfave100.MyFave100Presenter> get_Key$type$com$google$gwt$inject$client$AsyncProvider$com$fave100$client$pages$myfave100$MyFave100Presenter$$_annotation$$none$$() {
    return create_Key$type$com$google$gwt$inject$client$AsyncProvider$com$fave100$client$pages$myfave100$MyFave100Presenter$$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.gwtplatform.mvp.client.RootPresenter declared at:
   *   com.gwtplatform.mvp.client.gin.DefaultModule.configure(DefaultModule.java:49)
   */
  private native void com$gwtplatform$mvp$client$HandlerContainerImpl_automaticBind_methodInjection__(com.gwtplatform.mvp.client.HandlerContainerImpl injectee, com.gwtplatform.mvp.client.AutobindDisable _0) /*-{
    injectee.@com.gwtplatform.mvp.client.HandlerContainerImpl::automaticBind(Lcom/gwtplatform/mvp/client/AutobindDisable;)(_0);
  }-*/;
  
  private void memberInject_Key$type$com$gwtplatform$mvp$client$RootPresenter$_annotation$$none$$(com.gwtplatform.mvp.client.RootPresenter injectee) {
    com$gwtplatform$mvp$client$HandlerContainerImpl_automaticBind_methodInjection__(injectee, get_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$());
    
  }
  
  private com.gwtplatform.mvp.client.RootPresenter com$gwtplatform$mvp$client$RootPresenter_RootPresenter_methodInjection(com.google.gwt.event.shared.EventBus _0, com.gwtplatform.mvp.client.RootPresenter.RootView _1) {
    return new com.gwtplatform.mvp.client.RootPresenter(_0, _1);
  }
  
  private com.gwtplatform.mvp.client.RootPresenter create_Key$type$com$gwtplatform$mvp$client$RootPresenter$_annotation$$none$$() {
    com.gwtplatform.mvp.client.RootPresenter result = com$gwtplatform$mvp$client$RootPresenter_RootPresenter_methodInjection(get_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$(), get_Key$type$com$gwtplatform$mvp$client$RootPresenter$RootView$_annotation$$none$$());
    memberInject_Key$type$com$gwtplatform$mvp$client$RootPresenter$_annotation$$none$$(result);
    return result;
  }
  
  private com.gwtplatform.mvp.client.RootPresenter singleton_Key$type$com$gwtplatform$mvp$client$RootPresenter$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.DefaultModule.configure(DefaultModule.java:49)
   */
  private com.gwtplatform.mvp.client.RootPresenter get_Key$type$com$gwtplatform$mvp$client$RootPresenter$_annotation$$none$$() {
    if (singleton_Key$type$com$gwtplatform$mvp$client$RootPresenter$_annotation$$none$$ == null) {
      singleton_Key$type$com$gwtplatform$mvp$client$RootPresenter$_annotation$$none$$ = create_Key$type$com$gwtplatform$mvp$client$RootPresenter$_annotation$$none$$();
    }
    return singleton_Key$type$com$gwtplatform$mvp$client$RootPresenter$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.fave100.client.pages.about.AboutView$Binder declared at:
   *   Implicit binding for Key[type=com.fave100.client.pages.about.AboutView$Binder, annotation=[none]]
   */
  private void memberInject_Key$type$com$fave100$client$pages$about$AboutView$Binder$_annotation$$none$$(com.fave100.client.pages.about.AboutView.Binder injectee) {
    
  }
  
  private com.fave100.client.pages.about.AboutView.Binder create_Key$type$com$fave100$client$pages$about$AboutView$Binder$_annotation$$none$$() {
    Object created = GWT.create(com.fave100.client.pages.about.AboutView.Binder.class);
    assert created instanceof com.fave100.client.pages.about.AboutView.Binder;
    com.fave100.client.pages.about.AboutView.Binder result = (com.fave100.client.pages.about.AboutView.Binder) created;
    
    memberInject_Key$type$com$fave100$client$pages$about$AboutView$Binder$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.fave100.client.pages.about.AboutView$Binder declared at:
   *   Implicit binding for Key[type=com.fave100.client.pages.about.AboutView$Binder, annotation=[none]]
   */
  private com.fave100.client.pages.about.AboutView.Binder get_Key$type$com$fave100$client$pages$about$AboutView$Binder$_annotation$$none$$() {
    return create_Key$type$com$fave100$client$pages$about$AboutView$Binder$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.fave100.client.pages.myfave100.MyFave100Presenter declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:123)
   */
  private native void com$gwtplatform$mvp$client$HandlerContainerImpl_automaticBind_methodInjection___(com.gwtplatform.mvp.client.HandlerContainerImpl injectee, com.gwtplatform.mvp.client.AutobindDisable _0) /*-{
    injectee.@com.gwtplatform.mvp.client.HandlerContainerImpl::automaticBind(Lcom/gwtplatform/mvp/client/AutobindDisable;)(_0);
  }-*/;
  
  private native void com$fave100$client$pages$myfave100$MyFave100Presenter_topBar_fieldInjection(com.fave100.client.pages.myfave100.MyFave100Presenter injectee, com.fave100.client.pagefragments.TopBarPresenter value) /*-{
    injectee.@com.fave100.client.pages.myfave100.MyFave100Presenter::topBar = value;
  }-*/;
  
  private void memberInject_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$_annotation$$none$$(com.fave100.client.pages.myfave100.MyFave100Presenter injectee) {
    com$gwtplatform$mvp$client$HandlerContainerImpl_automaticBind_methodInjection___(injectee, get_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$());
    com$fave100$client$pages$myfave100$MyFave100Presenter_topBar_fieldInjection(injectee, get_Key$type$com$fave100$client$pagefragments$TopBarPresenter$_annotation$$none$$());
    
  }
  
  private com.fave100.client.pages.myfave100.MyFave100Presenter com$fave100$client$pages$myfave100$MyFave100Presenter_MyFave100Presenter_methodInjection(com.google.gwt.event.shared.EventBus _0, com.fave100.client.pages.myfave100.MyFave100Presenter.MyView _1, com.fave100.client.pages.myfave100.MyFave100Presenter.MyProxy _2) {
    return new com.fave100.client.pages.myfave100.MyFave100Presenter(_0, _1, _2);
  }
  
  private com.fave100.client.pages.myfave100.MyFave100Presenter create_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$_annotation$$none$$() {
    com.fave100.client.pages.myfave100.MyFave100Presenter result = com$fave100$client$pages$myfave100$MyFave100Presenter_MyFave100Presenter_methodInjection(get_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$(), get_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyView$_annotation$$none$$(), get_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyProxy$_annotation$$none$$());
    memberInject_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$_annotation$$none$$(result);
    return result;
  }
  
  private com.fave100.client.pages.myfave100.MyFave100Presenter singleton_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:123)
   */
  private com.fave100.client.pages.myfave100.MyFave100Presenter get_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$_annotation$$none$$() {
    if (singleton_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$_annotation$$none$$ == null) {
      singleton_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$_annotation$$none$$ = create_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$_annotation$$none$$();
    }
    return singleton_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.fave100.client.pages.myfave100.MyFave100Presenter$MyView declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:126)
   */
  private com.fave100.client.pages.myfave100.MyFave100Presenter.MyView create_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyView$_annotation$$none$$() {
    return get_Key$type$com$fave100$client$pages$myfave100$MyFave100View$_annotation$$none$$();
  }
  
  
  /**
   * Binding for com.fave100.client.pages.myfave100.MyFave100Presenter$MyView declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:126)
   */
  private com.fave100.client.pages.myfave100.MyFave100Presenter.MyView get_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyView$_annotation$$none$$() {
    return create_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyView$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry declared at:
   *   com.gwtplatform.dispatch.client.gin.DispatchAsyncModule.configure(DispatchAsyncModule.java:181)
   */
  private com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry create_Key$type$com$gwtplatform$dispatch$client$actionhandler$ClientActionHandlerRegistry$_annotation$$none$$() {
    return get_Key$type$com$gwtplatform$dispatch$client$actionhandler$DefaultClientActionHandlerRegistry$_annotation$$none$$();
  }
  
  private com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry singleton_Key$type$com$gwtplatform$dispatch$client$actionhandler$ClientActionHandlerRegistry$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.dispatch.client.gin.DispatchAsyncModule.configure(DispatchAsyncModule.java:181)
   */
  private com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry get_Key$type$com$gwtplatform$dispatch$client$actionhandler$ClientActionHandlerRegistry$_annotation$$none$$() {
    if (singleton_Key$type$com$gwtplatform$dispatch$client$actionhandler$ClientActionHandlerRegistry$_annotation$$none$$ == null) {
      singleton_Key$type$com$gwtplatform$dispatch$client$actionhandler$ClientActionHandlerRegistry$_annotation$$none$$ = create_Key$type$com$gwtplatform$dispatch$client$actionhandler$ClientActionHandlerRegistry$_annotation$$none$$();
    }
    return singleton_Key$type$com$gwtplatform$dispatch$client$actionhandler$ClientActionHandlerRegistry$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.gwtplatform.dispatch.shared.DispatchAsync declared at:
   *   protected com.gwtplatform.dispatch.shared.DispatchAsync com.gwtplatform.dispatch.client.gin.DispatchAsyncModule.provideDispatchAsync(com.gwtplatform.dispatch.client.ExceptionHandler,com.gwtplatform.dispatch.shared.SecurityCookieAccessor,com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry)
   */
  private native com.gwtplatform.dispatch.shared.DispatchAsync com$gwtplatform$dispatch$client$gin$DispatchAsyncModule_provideDispatchAsync_methodInjection(com.gwtplatform.dispatch.client.gin.DispatchAsyncModule injectee, com.gwtplatform.dispatch.client.ExceptionHandler _0, com.gwtplatform.dispatch.shared.SecurityCookieAccessor _1, com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry _2) /*-{
    return injectee.@com.gwtplatform.dispatch.client.gin.DispatchAsyncModule::provideDispatchAsync(Lcom/gwtplatform/dispatch/client/ExceptionHandler;Lcom/gwtplatform/dispatch/shared/SecurityCookieAccessor;Lcom/gwtplatform/dispatch/client/actionhandler/ClientActionHandlerRegistry;)(_0, _1, _2);
  }-*/;
  
  private com.gwtplatform.dispatch.shared.DispatchAsync create_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$() {
    return com$gwtplatform$dispatch$client$gin$DispatchAsyncModule_provideDispatchAsync_methodInjection(new com.gwtplatform.dispatch.client.gin.DispatchAsyncModule(), get_Key$type$com$gwtplatform$dispatch$client$ExceptionHandler$_annotation$$none$$(), get_Key$type$com$gwtplatform$dispatch$shared$SecurityCookieAccessor$_annotation$$none$$(), get_Key$type$com$gwtplatform$dispatch$client$actionhandler$ClientActionHandlerRegistry$_annotation$$none$$());
  }
  
  private com.gwtplatform.dispatch.shared.DispatchAsync singleton_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   protected com.gwtplatform.dispatch.shared.DispatchAsync com.gwtplatform.dispatch.client.gin.DispatchAsyncModule.provideDispatchAsync(com.gwtplatform.dispatch.client.ExceptionHandler,com.gwtplatform.dispatch.shared.SecurityCookieAccessor,com.gwtplatform.dispatch.client.actionhandler.ClientActionHandlerRegistry)
   */
  private com.gwtplatform.dispatch.shared.DispatchAsync get_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$() {
    if (singleton_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$ == null) {
      singleton_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$ = create_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$();
    }
    return singleton_Key$type$com$gwtplatform$dispatch$shared$DispatchAsync$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.fave100.client.pagefragments.TopBarView$Binder declared at:
   *   Implicit binding for Key[type=com.fave100.client.pagefragments.TopBarView$Binder, annotation=[none]]
   */
  private void memberInject_Key$type$com$fave100$client$pagefragments$TopBarView$Binder$_annotation$$none$$(com.fave100.client.pagefragments.TopBarView.Binder injectee) {
    
  }
  
  private com.fave100.client.pagefragments.TopBarView.Binder create_Key$type$com$fave100$client$pagefragments$TopBarView$Binder$_annotation$$none$$() {
    Object created = GWT.create(com.fave100.client.pagefragments.TopBarView.Binder.class);
    assert created instanceof com.fave100.client.pagefragments.TopBarView.Binder;
    com.fave100.client.pagefragments.TopBarView.Binder result = (com.fave100.client.pagefragments.TopBarView.Binder) created;
    
    memberInject_Key$type$com$fave100$client$pagefragments$TopBarView$Binder$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.fave100.client.pagefragments.TopBarView$Binder declared at:
   *   Implicit binding for Key[type=com.fave100.client.pagefragments.TopBarView$Binder, annotation=[none]]
   */
  private com.fave100.client.pagefragments.TopBarView.Binder get_Key$type$com$fave100$client$pagefragments$TopBarView$Binder$_annotation$$none$$() {
    return create_Key$type$com$fave100$client$pagefragments$TopBarView$Binder$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.fave100.client.pages.about.AboutView declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:124)
   */
  private void memberInject_Key$type$com$fave100$client$pages$about$AboutView$_annotation$$none$$(com.fave100.client.pages.about.AboutView injectee) {
    
  }
  
  private com.fave100.client.pages.about.AboutView com$fave100$client$pages$about$AboutView_AboutView_methodInjection(com.fave100.client.pages.about.AboutView.Binder _0) {
    return new com.fave100.client.pages.about.AboutView(_0);
  }
  
  private com.fave100.client.pages.about.AboutView create_Key$type$com$fave100$client$pages$about$AboutView$_annotation$$none$$() {
    com.fave100.client.pages.about.AboutView result = com$fave100$client$pages$about$AboutView_AboutView_methodInjection(get_Key$type$com$fave100$client$pages$about$AboutView$Binder$_annotation$$none$$());
    memberInject_Key$type$com$fave100$client$pages$about$AboutView$_annotation$$none$$(result);
    return result;
  }
  
  private com.fave100.client.pages.about.AboutView singleton_Key$type$com$fave100$client$pages$about$AboutView$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:124)
   */
  private com.fave100.client.pages.about.AboutView get_Key$type$com$fave100$client$pages$about$AboutView$_annotation$$none$$() {
    if (singleton_Key$type$com$fave100$client$pages$about$AboutView$_annotation$$none$$ == null) {
      singleton_Key$type$com$fave100$client$pages$about$AboutView$_annotation$$none$$ = create_Key$type$com$fave100$client$pages$about$AboutView$_annotation$$none$$();
    }
    return singleton_Key$type$com$fave100$client$pages$about$AboutView$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.google.gwt.event.shared.SimpleEventBus declared at:
   *   Implicit binding for Key[type=com.google.gwt.event.shared.SimpleEventBus, annotation=[none]]
   */
  private void memberInject_Key$type$com$google$gwt$event$shared$SimpleEventBus$_annotation$$none$$(com.google.gwt.event.shared.SimpleEventBus injectee) {
    
  }
  
  private com.google.gwt.event.shared.SimpleEventBus create_Key$type$com$google$gwt$event$shared$SimpleEventBus$_annotation$$none$$() {
    Object created = GWT.create(com.google.gwt.event.shared.SimpleEventBus.class);
    assert created instanceof com.google.gwt.event.shared.SimpleEventBus;
    com.google.gwt.event.shared.SimpleEventBus result = (com.google.gwt.event.shared.SimpleEventBus) created;
    
    memberInject_Key$type$com$google$gwt$event$shared$SimpleEventBus$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.google.gwt.event.shared.SimpleEventBus declared at:
   *   Implicit binding for Key[type=com.google.gwt.event.shared.SimpleEventBus, annotation=[none]]
   */
  private com.google.gwt.event.shared.SimpleEventBus get_Key$type$com$google$gwt$event$shared$SimpleEventBus$_annotation$$none$$() {
    return create_Key$type$com$google$gwt$event$shared$SimpleEventBus$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry declared at:
   *   Implicit binding for Key[type=com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry, annotation=[none]]
   */
  private void memberInject_Key$type$com$gwtplatform$dispatch$client$actionhandler$DefaultClientActionHandlerRegistry$_annotation$$none$$(com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry injectee) {
    
  }
  
  private com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry create_Key$type$com$gwtplatform$dispatch$client$actionhandler$DefaultClientActionHandlerRegistry$_annotation$$none$$() {
    Object created = GWT.create(com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry.class);
    assert created instanceof com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry;
    com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry result = (com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry) created;
    
    memberInject_Key$type$com$gwtplatform$dispatch$client$actionhandler$DefaultClientActionHandlerRegistry$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry declared at:
   *   Implicit binding for Key[type=com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry, annotation=[none]]
   */
  private com.gwtplatform.dispatch.client.actionhandler.DefaultClientActionHandlerRegistry get_Key$type$com$gwtplatform$dispatch$client$actionhandler$DefaultClientActionHandlerRegistry$_annotation$$none$$() {
    return create_Key$type$com$gwtplatform$dispatch$client$actionhandler$DefaultClientActionHandlerRegistry$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.gwtplatform.mvp.client.proxy.PlaceManager declared at:
   *   com.gwtplatform.mvp.client.gin.DefaultModule.configure(DefaultModule.java:51)
   */
  private com.gwtplatform.mvp.client.proxy.PlaceManager create_Key$type$com$gwtplatform$mvp$client$proxy$PlaceManager$_annotation$$none$$() {
    return get_Key$type$com$fave100$client$place$ClientPlaceManager$_annotation$$none$$();
  }
  
  private com.gwtplatform.mvp.client.proxy.PlaceManager singleton_Key$type$com$gwtplatform$mvp$client$proxy$PlaceManager$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.DefaultModule.configure(DefaultModule.java:51)
   */
  private com.gwtplatform.mvp.client.proxy.PlaceManager get_Key$type$com$gwtplatform$mvp$client$proxy$PlaceManager$_annotation$$none$$() {
    if (singleton_Key$type$com$gwtplatform$mvp$client$proxy$PlaceManager$_annotation$$none$$ == null) {
      singleton_Key$type$com$gwtplatform$mvp$client$proxy$PlaceManager$_annotation$$none$$ = create_Key$type$com$gwtplatform$mvp$client$proxy$PlaceManager$_annotation$$none$$();
    }
    return singleton_Key$type$com$gwtplatform$mvp$client$proxy$PlaceManager$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.fave100.client.pages.myfave100.MyFave100Presenter$MyProxy declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:125)
   */
  private void memberInject_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyProxy$_annotation$$none$$(com.fave100.client.pages.myfave100.MyFave100Presenter.MyProxy injectee) {
    
  }
  
  private com.fave100.client.pages.myfave100.MyFave100Presenter.MyProxy create_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyProxy$_annotation$$none$$() {
    Object created = GWT.create(com.fave100.client.pages.myfave100.MyFave100Presenter.MyProxy.class);
    assert created instanceof com.fave100.client.pages.myfave100.MyFave100Presenter.MyProxy;
    com.fave100.client.pages.myfave100.MyFave100Presenter.MyProxy result = (com.fave100.client.pages.myfave100.MyFave100Presenter.MyProxy) created;
    
    memberInject_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyProxy$_annotation$$none$$(result);
    return result;
  }
  
  private com.fave100.client.pages.myfave100.MyFave100Presenter.MyProxy singleton_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyProxy$_annotation$$none$$ = null;
  
  
  /**
   * Singleton bound at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:125)
   */
  private com.fave100.client.pages.myfave100.MyFave100Presenter.MyProxy get_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyProxy$_annotation$$none$$() {
    if (singleton_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyProxy$_annotation$$none$$ == null) {
      singleton_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyProxy$_annotation$$none$$ = create_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyProxy$_annotation$$none$$();
    }
    return singleton_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyProxy$_annotation$$none$$;
  }
  
  
  /**
   * Binding for com.gwtplatform.dispatch.client.DefaultExceptionHandler declared at:
   *   Implicit binding for Key[type=com.gwtplatform.dispatch.client.DefaultExceptionHandler, annotation=[none]]
   */
  private void memberInject_Key$type$com$gwtplatform$dispatch$client$DefaultExceptionHandler$_annotation$$none$$(com.gwtplatform.dispatch.client.DefaultExceptionHandler injectee) {
    
  }
  
  private com.gwtplatform.dispatch.client.DefaultExceptionHandler create_Key$type$com$gwtplatform$dispatch$client$DefaultExceptionHandler$_annotation$$none$$() {
    Object created = GWT.create(com.gwtplatform.dispatch.client.DefaultExceptionHandler.class);
    assert created instanceof com.gwtplatform.dispatch.client.DefaultExceptionHandler;
    com.gwtplatform.dispatch.client.DefaultExceptionHandler result = (com.gwtplatform.dispatch.client.DefaultExceptionHandler) created;
    
    memberInject_Key$type$com$gwtplatform$dispatch$client$DefaultExceptionHandler$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.gwtplatform.dispatch.client.DefaultExceptionHandler declared at:
   *   Implicit binding for Key[type=com.gwtplatform.dispatch.client.DefaultExceptionHandler, annotation=[none]]
   */
  private com.gwtplatform.dispatch.client.DefaultExceptionHandler get_Key$type$com$gwtplatform$dispatch$client$DefaultExceptionHandler$_annotation$$none$$() {
    return create_Key$type$com$gwtplatform$dispatch$client$DefaultExceptionHandler$_annotation$$none$$();
  }
  
  
  
  /**
   * Binding for com.fave100.client.pagefragments.TopBarPresenter declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenterWidget(AbstractPresenterModule.java:264)
   */
  private native void com$gwtplatform$mvp$client$HandlerContainerImpl_automaticBind_methodInjection____(com.gwtplatform.mvp.client.HandlerContainerImpl injectee, com.gwtplatform.mvp.client.AutobindDisable _0) /*-{
    injectee.@com.gwtplatform.mvp.client.HandlerContainerImpl::automaticBind(Lcom/gwtplatform/mvp/client/AutobindDisable;)(_0);
  }-*/;
  
  private void memberInject_Key$type$com$fave100$client$pagefragments$TopBarPresenter$_annotation$$none$$(com.fave100.client.pagefragments.TopBarPresenter injectee) {
    com$gwtplatform$mvp$client$HandlerContainerImpl_automaticBind_methodInjection____(injectee, get_Key$type$com$gwtplatform$mvp$client$AutobindDisable$_annotation$$none$$());
    
  }
  
  private com.fave100.client.pagefragments.TopBarPresenter com$fave100$client$pagefragments$TopBarPresenter_TopBarPresenter_methodInjection(com.google.gwt.event.shared.EventBus _0, com.fave100.client.pagefragments.TopBarPresenter.MyView _1) {
    return new com.fave100.client.pagefragments.TopBarPresenter(_0, _1);
  }
  
  private com.fave100.client.pagefragments.TopBarPresenter create_Key$type$com$fave100$client$pagefragments$TopBarPresenter$_annotation$$none$$() {
    com.fave100.client.pagefragments.TopBarPresenter result = com$fave100$client$pagefragments$TopBarPresenter_TopBarPresenter_methodInjection(get_Key$type$com$google$gwt$event$shared$EventBus$_annotation$$none$$(), get_Key$type$com$fave100$client$pagefragments$TopBarPresenter$MyView$_annotation$$none$$());
    memberInject_Key$type$com$fave100$client$pagefragments$TopBarPresenter$_annotation$$none$$(result);
    return result;
  }
  
  
  /**
   * Binding for com.fave100.client.pagefragments.TopBarPresenter declared at:
   *   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenterWidget(AbstractPresenterModule.java:264)
   */
  private com.fave100.client.pagefragments.TopBarPresenter get_Key$type$com$fave100$client$pagefragments$TopBarPresenter$_annotation$$none$$() {
    return create_Key$type$com$fave100$client$pagefragments$TopBarPresenter$_annotation$$none$$();
  }
  
  
  public ClientGinjectorImpl() {
    // Eager singleton bound at:
    //   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:125)
    get_Key$type$com$fave100$client$pages$about$AboutPresenter$MyProxy$_annotation$$none$$();
    // Eager singleton bound at:
    //   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:125)
    get_Key$type$com$fave100$client$pages$home$HomePresenter$MyProxy$_annotation$$none$$();
    // Eager singleton bound at:
    //   com.gwtplatform.mvp.client.gin.DefaultModule.configure(DefaultModule.java:49)
    get_Key$type$com$gwtplatform$mvp$client$RootPresenter$_annotation$$none$$();
    // Eager singleton bound at:
    //   com.gwtplatform.dispatch.client.gin.DispatchAsyncModule.configure(DispatchAsyncModule.java:181)
    get_Key$type$com$gwtplatform$dispatch$client$actionhandler$ClientActionHandlerRegistry$_annotation$$none$$();
    // Eager singleton bound at:
    //   com.gwtplatform.mvp.client.gin.AbstractPresenterModule.bindPresenter(AbstractPresenterModule.java:125)
    get_Key$type$com$fave100$client$pages$myfave100$MyFave100Presenter$MyProxy$_annotation$$none$$();
    
  }
  
}
