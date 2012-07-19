package com.fave100.client.requestfactory;

public class AppUserRequestImpl extends com.google.web.bindery.requestfactory.shared.impl.AbstractRequestContext implements com.fave100.client.requestfactory.AppUserRequest {
  public AppUserRequestImpl(com.google.web.bindery.requestfactory.shared.impl.AbstractRequestFactory requestFactory) {super(requestFactory, com.google.web.bindery.requestfactory.shared.impl.AbstractRequestContext.Dialect.STANDARD);}
  @com.google.web.bindery.autobean.shared.AutoBeanFactory.Category({com.google.web.bindery.requestfactory.shared.impl.EntityProxyCategory.class, com.google.web.bindery.requestfactory.shared.impl.ValueProxyCategory.class, com.google.web.bindery.requestfactory.shared.impl.BaseProxyCategory.class})
  @com.google.web.bindery.autobean.shared.AutoBeanFactory.NoWrap(com.google.web.bindery.requestfactory.shared.EntityProxyId.class)
  interface Factory extends com.google.web.bindery.autobean.shared.AutoBeanFactory {
    com.google.web.bindery.autobean.shared.AutoBean<com.fave100.client.requestfactory.AppUserProxy> com_fave100_client_requestfactory_AppUserProxy();
  }
  public static Factory FACTORY;
  @Override public Factory getAutoBeanFactory() {
    if (FACTORY == null) {
      FACTORY = com.google.gwt.core.client.GWT.create(Factory.class);
    }
    return FACTORY;
  }
  public  com.google.web.bindery.requestfactory.shared.Request<com.fave100.client.requestfactory.AppUserProxy> findAppUser(final java.lang.Long id) {
    class X extends com.google.web.bindery.requestfactory.shared.impl.AbstractRequest<com.fave100.client.requestfactory.AppUserProxy> implements com.google.web.bindery.requestfactory.shared.Request<com.fave100.client.requestfactory.AppUserProxy> {
      public X() { super(AppUserRequestImpl.this);}
      @Override public X with(String... paths) {super.with(paths); return this;}
      @Override protected com.google.web.bindery.requestfactory.shared.impl.RequestData makeRequestData() {
        return new com.google.web.bindery.requestfactory.shared.impl.RequestData("Jw6NYPjfNLifEbdY3OF3rv$oKXk=", new Object[] {id}, propertyRefs, com.fave100.client.requestfactory.AppUserProxy.class, null);
      }
    }
    X x = new X();
    addInvocation(x);
    return x;
  }
  public  com.google.web.bindery.requestfactory.shared.Request<java.lang.String> getLoginURL() {
    class X extends com.google.web.bindery.requestfactory.shared.impl.AbstractRequest<java.lang.String> implements com.google.web.bindery.requestfactory.shared.Request<java.lang.String> {
      public X() { super(AppUserRequestImpl.this);}
      @Override public X with(String... paths) {super.with(paths); return this;}
      @Override protected com.google.web.bindery.requestfactory.shared.impl.RequestData makeRequestData() {
        return new com.google.web.bindery.requestfactory.shared.impl.RequestData("SSwto846vBCAR0IgmBSLdhatBJ0=", new Object[] {}, propertyRefs, java.lang.String.class, null);
      }
    }
    X x = new X();
    addInvocation(x);
    return x;
  }
  public  com.google.web.bindery.requestfactory.shared.InstanceRequest<com.fave100.client.requestfactory.AppUserProxy, com.fave100.client.requestfactory.AppUserProxy> persist() {
    class X extends com.google.web.bindery.requestfactory.shared.impl.AbstractRequest<com.fave100.client.requestfactory.AppUserProxy> implements com.google.web.bindery.requestfactory.shared.InstanceRequest<com.fave100.client.requestfactory.AppUserProxy, com.fave100.client.requestfactory.AppUserProxy> {
      public X() { super(AppUserRequestImpl.this);}
      @Override public X with(String... paths) {super.with(paths); return this;}
      @Override protected com.google.web.bindery.requestfactory.shared.impl.RequestData makeRequestData() {
        return new com.google.web.bindery.requestfactory.shared.impl.RequestData("nD21tQVIDyzncjOoVyeX9oZ6PV0=", new Object[] {null}, propertyRefs, com.fave100.client.requestfactory.AppUserProxy.class, null);
      }
    }
    X x = new X();
    return x;
  }
}
