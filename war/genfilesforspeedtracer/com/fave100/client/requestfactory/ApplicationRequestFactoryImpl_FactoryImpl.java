package com.fave100.client.requestfactory;

public class ApplicationRequestFactoryImpl_FactoryImpl extends com.google.web.bindery.autobean.gwt.client.impl.AbstractAutoBeanFactory implements com.fave100.client.requestfactory.ApplicationRequestFactoryImpl.Factory {
  @Override protected void initializeCreatorMap(com.google.web.bindery.autobean.gwt.client.impl.JsniCreatorMap map) {
    map.add(com.fave100.client.requestfactory.AppUserProxy.class, getConstructors_com_fave100_client_requestfactory_AppUserProxy());
    map.add(com.fave100.client.requestfactory.FaveItemProxy.class, getConstructors_com_fave100_client_requestfactory_FaveItemProxy());
    map.add(com.google.web.bindery.requestfactory.shared.EntityProxy.class, getConstructors_com_google_web_bindery_requestfactory_shared_EntityProxy());
  }
  private native com.google.gwt.core.client.JsArray<com.google.gwt.core.client.JavaScriptObject> getConstructors_com_fave100_client_requestfactory_AppUserProxy() /*-{
    return [
      @com.fave100.client.requestfactory.AppUserProxyAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory::new(Lcom/google/web/bindery/autobean/shared/AutoBeanFactory;),
      @com.fave100.client.requestfactory.AppUserProxyAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory::new(Lcom/google/web/bindery/autobean/shared/AutoBeanFactory;Lcom/fave100/client/requestfactory/AppUserProxy;)
    ];
  }-*/;
  private native com.google.gwt.core.client.JsArray<com.google.gwt.core.client.JavaScriptObject> getConstructors_com_fave100_client_requestfactory_FaveItemProxy() /*-{
    return [
      @com.fave100.client.requestfactory.FaveItemProxyAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory::new(Lcom/google/web/bindery/autobean/shared/AutoBeanFactory;),
      @com.fave100.client.requestfactory.FaveItemProxyAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory::new(Lcom/google/web/bindery/autobean/shared/AutoBeanFactory;Lcom/fave100/client/requestfactory/FaveItemProxy;)
    ];
  }-*/;
  private native com.google.gwt.core.client.JsArray<com.google.gwt.core.client.JavaScriptObject> getConstructors_com_google_web_bindery_requestfactory_shared_EntityProxy() /*-{
    return [
      @com.google.web.bindery.requestfactory.shared.EntityProxyAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory::new(Lcom/google/web/bindery/autobean/shared/AutoBeanFactory;),
      @com.google.web.bindery.requestfactory.shared.EntityProxyAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory::new(Lcom/google/web/bindery/autobean/shared/AutoBeanFactory;Lcom/google/web/bindery/requestfactory/shared/EntityProxy;)
    ];
  }-*/;
  private native com.google.gwt.core.client.JsArray<com.google.gwt.core.client.JavaScriptObject> getConstructors_com_google_web_bindery_requestfactory_shared_EntityProxyId() /*-{
    return [
      @com.google.web.bindery.requestfactory.shared.EntityProxyIdAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory::new(Lcom/google/web/bindery/autobean/shared/AutoBeanFactory;),
      @com.google.web.bindery.requestfactory.shared.EntityProxyIdAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory::new(Lcom/google/web/bindery/autobean/shared/AutoBeanFactory;Lcom/google/web/bindery/requestfactory/shared/EntityProxyId;)
    ];
  }-*/;
  @Override protected void initializeEnumMap() {
  }
  public com.google.web.bindery.autobean.shared.AutoBean com_fave100_client_requestfactory_AppUserProxy() {
    return new com.fave100.client.requestfactory.AppUserProxyAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory(ApplicationRequestFactoryImpl_FactoryImpl.this);
  }
  public com.google.web.bindery.autobean.shared.AutoBean com_fave100_client_requestfactory_FaveItemProxy() {
    return new com.fave100.client.requestfactory.FaveItemProxyAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory(ApplicationRequestFactoryImpl_FactoryImpl.this);
  }
  public com.google.web.bindery.autobean.shared.AutoBean com_google_web_bindery_requestfactory_shared_EntityProxy() {
    return new com.google.web.bindery.requestfactory.shared.EntityProxyAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory(ApplicationRequestFactoryImpl_FactoryImpl.this);
  }
}
