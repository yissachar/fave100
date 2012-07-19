package com.fave100.client.requestfactory;

public class FaveItemRequestImpl_FactoryImpl extends com.google.web.bindery.autobean.gwt.client.impl.AbstractAutoBeanFactory implements com.fave100.client.requestfactory.FaveItemRequestImpl.Factory {
  @Override protected void initializeCreatorMap(com.google.web.bindery.autobean.gwt.client.impl.JsniCreatorMap map) {
    map.add(com.fave100.client.requestfactory.FaveItemProxy.class, getConstructors_com_fave100_client_requestfactory_FaveItemProxy());
  }
  private native com.google.gwt.core.client.JsArray<com.google.gwt.core.client.JavaScriptObject> getConstructors_com_fave100_client_requestfactory_FaveItemProxy() /*-{
    return [
      @com.fave100.client.requestfactory.FaveItemProxyAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory::new(Lcom/google/web/bindery/autobean/shared/AutoBeanFactory;),
      @com.fave100.client.requestfactory.FaveItemProxyAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory::new(Lcom/google/web/bindery/autobean/shared/AutoBeanFactory;Lcom/fave100/client/requestfactory/FaveItemProxy;)
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
  public com.google.web.bindery.autobean.shared.AutoBean com_fave100_client_requestfactory_FaveItemProxy() {
    return new com.fave100.client.requestfactory.FaveItemProxyAutoBean_com_google_web_bindery_requestfactory_shared_impl_EntityProxyCategory_com_google_web_bindery_requestfactory_shared_impl_ValueProxyCategory_com_google_web_bindery_requestfactory_shared_impl_BaseProxyCategory(FaveItemRequestImpl_FactoryImpl.this);
  }
}
