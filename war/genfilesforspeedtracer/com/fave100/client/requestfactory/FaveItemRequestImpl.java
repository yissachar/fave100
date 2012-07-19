package com.fave100.client.requestfactory;

public class FaveItemRequestImpl extends com.google.web.bindery.requestfactory.shared.impl.AbstractRequestContext implements com.fave100.client.requestfactory.FaveItemRequest {
  public FaveItemRequestImpl(com.google.web.bindery.requestfactory.shared.impl.AbstractRequestFactory requestFactory) {super(requestFactory, com.google.web.bindery.requestfactory.shared.impl.AbstractRequestContext.Dialect.STANDARD);}
  @com.google.web.bindery.autobean.shared.AutoBeanFactory.Category({com.google.web.bindery.requestfactory.shared.impl.EntityProxyCategory.class, com.google.web.bindery.requestfactory.shared.impl.ValueProxyCategory.class, com.google.web.bindery.requestfactory.shared.impl.BaseProxyCategory.class})
  @com.google.web.bindery.autobean.shared.AutoBeanFactory.NoWrap(com.google.web.bindery.requestfactory.shared.EntityProxyId.class)
  interface Factory extends com.google.web.bindery.autobean.shared.AutoBeanFactory {
    com.google.web.bindery.autobean.shared.AutoBean<com.fave100.client.requestfactory.FaveItemProxy> com_fave100_client_requestfactory_FaveItemProxy();
  }
  public static Factory FACTORY;
  @Override public Factory getAutoBeanFactory() {
    if (FACTORY == null) {
      FACTORY = com.google.gwt.core.client.GWT.create(Factory.class);
    }
    return FACTORY;
  }
  public  com.google.web.bindery.requestfactory.shared.Request<com.fave100.client.requestfactory.FaveItemProxy> findFaveItem(final java.lang.Long id) {
    class X extends com.google.web.bindery.requestfactory.shared.impl.AbstractRequest<com.fave100.client.requestfactory.FaveItemProxy> implements com.google.web.bindery.requestfactory.shared.Request<com.fave100.client.requestfactory.FaveItemProxy> {
      public X() { super(FaveItemRequestImpl.this);}
      @Override public X with(String... paths) {super.with(paths); return this;}
      @Override protected com.google.web.bindery.requestfactory.shared.impl.RequestData makeRequestData() {
        return new com.google.web.bindery.requestfactory.shared.impl.RequestData("7p63a6sAZB$VlRSF$vbfhBf6eLk=", new Object[] {id}, propertyRefs, com.fave100.client.requestfactory.FaveItemProxy.class, null);
      }
    }
    X x = new X();
    addInvocation(x);
    return x;
  }
  public  com.google.web.bindery.requestfactory.shared.Request<java.util.List<com.fave100.client.requestfactory.FaveItemProxy>> getAllFaveItemsForUser() {
    class X extends com.google.web.bindery.requestfactory.shared.impl.AbstractRequest<java.util.List<com.fave100.client.requestfactory.FaveItemProxy>> implements com.google.web.bindery.requestfactory.shared.Request<java.util.List<com.fave100.client.requestfactory.FaveItemProxy>> {
      public X() { super(FaveItemRequestImpl.this);}
      @Override public X with(String... paths) {super.with(paths); return this;}
      @Override protected com.google.web.bindery.requestfactory.shared.impl.RequestData makeRequestData() {
        return new com.google.web.bindery.requestfactory.shared.impl.RequestData("df0DKwCbgaZ5Y84jFFa42wbClVY=", new Object[] {}, propertyRefs, java.util.List.class, com.fave100.client.requestfactory.FaveItemProxy.class);
      }
    }
    X x = new X();
    addInvocation(x);
    return x;
  }
  public  com.google.web.bindery.requestfactory.shared.InstanceRequest<com.fave100.client.requestfactory.FaveItemProxy, com.fave100.client.requestfactory.FaveItemProxy> persist() {
    class X extends com.google.web.bindery.requestfactory.shared.impl.AbstractRequest<com.fave100.client.requestfactory.FaveItemProxy> implements com.google.web.bindery.requestfactory.shared.InstanceRequest<com.fave100.client.requestfactory.FaveItemProxy, com.fave100.client.requestfactory.FaveItemProxy> {
      public X() { super(FaveItemRequestImpl.this);}
      @Override public X with(String... paths) {super.with(paths); return this;}
      @Override protected com.google.web.bindery.requestfactory.shared.impl.RequestData makeRequestData() {
        return new com.google.web.bindery.requestfactory.shared.impl.RequestData("6utjAQR3HGGVtfCaNTL$dvhbxQ8=", new Object[] {null}, propertyRefs, com.fave100.client.requestfactory.FaveItemProxy.class, null);
      }
    }
    X x = new X();
    return x;
  }
  public  com.google.web.bindery.requestfactory.shared.InstanceRequest<com.fave100.client.requestfactory.FaveItemProxy, java.lang.Void> remove() {
    class X extends com.google.web.bindery.requestfactory.shared.impl.AbstractRequest<java.lang.Void> implements com.google.web.bindery.requestfactory.shared.InstanceRequest<com.fave100.client.requestfactory.FaveItemProxy, java.lang.Void> {
      public X() { super(FaveItemRequestImpl.this);}
      @Override public X with(String... paths) {super.with(paths); return this;}
      @Override protected com.google.web.bindery.requestfactory.shared.impl.RequestData makeRequestData() {
        return new com.google.web.bindery.requestfactory.shared.impl.RequestData("ivJOvWAq6$29ItJ4N4qK878smig=", new Object[] {null}, propertyRefs, java.lang.Void.class, null);
      }
    }
    X x = new X();
    return x;
  }
  public  com.google.web.bindery.requestfactory.shared.Request<java.lang.Void> removeFaveItem(final java.lang.Long long1) {
    class X extends com.google.web.bindery.requestfactory.shared.impl.AbstractRequest<java.lang.Void> implements com.google.web.bindery.requestfactory.shared.Request<java.lang.Void> {
      public X() { super(FaveItemRequestImpl.this);}
      @Override public X with(String... paths) {super.with(paths); return this;}
      @Override protected com.google.web.bindery.requestfactory.shared.impl.RequestData makeRequestData() {
        return new com.google.web.bindery.requestfactory.shared.impl.RequestData("Fpm1058HQvb767D1qxlMOHtfzR4=", new Object[] {long1}, propertyRefs, java.lang.Void.class, null);
      }
    }
    X x = new X();
    addInvocation(x);
    return x;
  }
}
