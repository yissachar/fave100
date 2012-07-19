package com.fave100.client.requestfactory;

public class ApplicationRequestFactoryImpl extends com.google.web.bindery.requestfactory.gwt.client.impl.AbstractClientRequestFactory implements com.fave100.client.requestfactory.ApplicationRequestFactory {
  @com.google.web.bindery.autobean.shared.AutoBeanFactory.Category({com.google.web.bindery.requestfactory.shared.impl.EntityProxyCategory.class, com.google.web.bindery.requestfactory.shared.impl.ValueProxyCategory.class, com.google.web.bindery.requestfactory.shared.impl.BaseProxyCategory.class})
  @com.google.web.bindery.autobean.shared.AutoBeanFactory.NoWrap(com.google.web.bindery.requestfactory.shared.EntityProxyId.class)
  interface Factory extends com.google.web.bindery.autobean.shared.AutoBeanFactory {
    com.google.web.bindery.autobean.shared.AutoBean<com.google.web.bindery.requestfactory.shared.EntityProxy> com_google_web_bindery_requestfactory_shared_EntityProxy();
    com.google.web.bindery.autobean.shared.AutoBean<com.fave100.client.requestfactory.AppUserProxy> com_fave100_client_requestfactory_AppUserProxy();
    com.google.web.bindery.autobean.shared.AutoBean<com.fave100.client.requestfactory.FaveItemProxy> com_fave100_client_requestfactory_FaveItemProxy();
  }
  public static Factory FACTORY;
  @Override public Factory getAutoBeanFactory() {
    if (FACTORY == null) {
      FACTORY = com.google.gwt.core.client.GWT.create(Factory.class);
    }
    return FACTORY;
  }
  public com.fave100.client.requestfactory.AppUserRequestImpl appUserRequest() {
    return new com.fave100.client.requestfactory.AppUserRequestImpl(this);
  }
  public com.fave100.client.requestfactory.FaveItemRequestImpl faveItemRequest() {
    return new com.fave100.client.requestfactory.FaveItemRequestImpl(this);
  }
  private static final java.util.HashMap<String, Class<?>> tokensToTypes = new java.util.HashMap<String, Class<?>>();
  private static final java.util.HashMap<Class<?>, String> typesToTokens = new java.util.HashMap<Class<?>, String>();
  private static final java.util.HashSet<Class<?>> entityProxyTypes = new java.util.HashSet<Class<?>>();
  private static final java.util.HashSet<Class<?>> valueProxyTypes = new java.util.HashSet<Class<?>>();
  static {
    tokensToTypes.put("w1Qg$YHpDaNcHrR5HZ$23y518nA=", com.google.web.bindery.requestfactory.shared.EntityProxy.class);
    typesToTokens.put(com.google.web.bindery.requestfactory.shared.EntityProxy.class, "w1Qg$YHpDaNcHrR5HZ$23y518nA=");
    entityProxyTypes.add(com.google.web.bindery.requestfactory.shared.EntityProxy.class);
    tokensToTypes.put("fgtdGHxB5OEBQEtPsPS5V7TkHWs=", com.fave100.client.requestfactory.AppUserProxy.class);
    typesToTokens.put(com.fave100.client.requestfactory.AppUserProxy.class, "fgtdGHxB5OEBQEtPsPS5V7TkHWs=");
    entityProxyTypes.add(com.fave100.client.requestfactory.AppUserProxy.class);
    tokensToTypes.put("82QpBIZ2Thda3j4h9ELHeAmdfBE=", com.fave100.client.requestfactory.FaveItemProxy.class);
    typesToTokens.put(com.fave100.client.requestfactory.FaveItemProxy.class, "82QpBIZ2Thda3j4h9ELHeAmdfBE=");
    entityProxyTypes.add(com.fave100.client.requestfactory.FaveItemProxy.class);
  }
  @Override public String getFactoryTypeToken() {
    return "com.fave100.client.requestfactory.ApplicationRequestFactory";
  }
  @Override protected Class getTypeFromToken(String typeToken) {
    return tokensToTypes.get(typeToken);
  }
  @Override protected String getTypeToken(Class type) {
    return typesToTokens.get(type);
  }
  @Override public boolean isEntityType(Class<?> type) {
    return entityProxyTypes.contains(type);
  }
  @Override public boolean isValueType(Class<?> type) {
    return valueProxyTypes.contains(type);
  }
}
