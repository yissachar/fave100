package com.google.web.bindery.requestfactory.shared.messages;

public class RequestMessageAutoBean extends com.google.web.bindery.autobean.shared.impl.AbstractAutoBean<com.google.web.bindery.requestfactory.shared.messages.RequestMessage> {
  private final com.google.web.bindery.requestfactory.shared.messages.RequestMessage shim = new com.google.web.bindery.requestfactory.shared.messages.RequestMessage() {
    public java.lang.String getRequestFactory()  {
      java.lang.String toReturn = RequestMessageAutoBean.this.getWrapped().getRequestFactory();
      return toReturn;
    }
    public java.lang.String getVersion()  {
      java.lang.String toReturn = RequestMessageAutoBean.this.getWrapped().getVersion();
      return toReturn;
    }
    public java.util.List getInvocations()  {
      java.util.List toReturn = RequestMessageAutoBean.this.getWrapped().getInvocations();
      if (toReturn != null) {
        if (RequestMessageAutoBean.this.isWrapped(toReturn)) {
          toReturn = RequestMessageAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.ListAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public java.util.List getOperations()  {
      java.util.List toReturn = RequestMessageAutoBean.this.getWrapped().getOperations();
      if (toReturn != null) {
        if (RequestMessageAutoBean.this.isWrapped(toReturn)) {
          toReturn = RequestMessageAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.ListAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public void setInvocations(java.util.List value)  {
      RequestMessageAutoBean.this.getWrapped().setInvocations(value);
      RequestMessageAutoBean.this.set("setInvocations", value);
    }
    public void setOperations(java.util.List value)  {
      RequestMessageAutoBean.this.getWrapped().setOperations(value);
      RequestMessageAutoBean.this.set("setOperations", value);
    }
    public void setRequestFactory(java.lang.String value)  {
      RequestMessageAutoBean.this.getWrapped().setRequestFactory(value);
      RequestMessageAutoBean.this.set("setRequestFactory", value);
    }
    public void setVersion(java.lang.String version)  {
      RequestMessageAutoBean.this.getWrapped().setVersion(version);
      RequestMessageAutoBean.this.set("setVersion", version);
    }
    @Override public boolean equals(Object o) {
      return this == o || getWrapped().equals(o);
    }
    @Override public int hashCode() {
      return getWrapped().hashCode();
    }
    @Override public String toString() {
      return getWrapped().toString();
    }
  };
  { com.google.gwt.core.client.impl.WeakMapping.set(shim, com.google.web.bindery.autobean.shared.AutoBean.class.getName(), this); }
  public RequestMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory) {super(factory);}
  public RequestMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory, com.google.web.bindery.requestfactory.shared.messages.RequestMessage wrapped) {
    super(wrapped, factory);
  }
  public com.google.web.bindery.requestfactory.shared.messages.RequestMessage as() {return shim;}
  public Class<com.google.web.bindery.requestfactory.shared.messages.RequestMessage> getType() {return com.google.web.bindery.requestfactory.shared.messages.RequestMessage.class;}
  @Override protected com.google.web.bindery.requestfactory.shared.messages.RequestMessage createSimplePeer() {
    return new com.google.web.bindery.requestfactory.shared.messages.RequestMessage() {
      private final com.google.web.bindery.autobean.shared.Splittable data = com.google.web.bindery.requestfactory.shared.messages.RequestMessageAutoBean.this.data;
      public java.lang.String getRequestFactory()  {
        return (java.lang.String) getOrReify("F");
      }
      public java.lang.String getVersion()  {
        return (java.lang.String) getOrReify("V");
      }
      public java.util.List getInvocations()  {
        return (java.util.List) getOrReify("I");
      }
      public java.util.List getOperations()  {
        return (java.util.List) getOrReify("O");
      }
      public void setInvocations(java.util.List value)  {
        setProperty("I", value);
      }
      public void setOperations(java.util.List value)  {
        setProperty("O", value);
      }
      public void setRequestFactory(java.lang.String value)  {
        setProperty("F", value);
      }
      public void setVersion(java.lang.String version)  {
        setProperty("V", version);
      }
    };
  }
  @Override protected void traverseProperties(com.google.web.bindery.autobean.shared.AutoBeanVisitor visitor, com.google.web.bindery.autobean.shared.impl.AbstractAutoBean.OneShotContext ctx) {
    com.google.web.bindery.autobean.shared.impl.AbstractAutoBean bean;
    Object value;
    com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext propertyContext;
    com.google.web.bindery.requestfactory.shared.messages.RequestMessage as = as();
    value = as.getRequestFactory();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(RequestMessageAutoBean.this, "F"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("F", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("F", value, propertyContext);
    value = as.getVersion();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(RequestMessageAutoBean.this, "V"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("V", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("V", value, propertyContext);
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getInvocations());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(RequestMessageAutoBean.this, "I"),
      new Class<?>[] {java.util.List.class, com.google.web.bindery.requestfactory.shared.messages.InvocationMessage.class},
      new int[] {1, 0}
    );
    if (visitor.visitCollectionProperty("I", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitCollectionProperty("I", bean, propertyContext);
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getOperations());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(RequestMessageAutoBean.this, "O"),
      new Class<?>[] {java.util.List.class, com.google.web.bindery.requestfactory.shared.messages.OperationMessage.class},
      new int[] {1, 0}
    );
    if (visitor.visitCollectionProperty("O", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitCollectionProperty("O", bean, propertyContext);
  }
}
