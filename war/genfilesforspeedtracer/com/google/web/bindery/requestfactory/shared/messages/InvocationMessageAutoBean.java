package com.google.web.bindery.requestfactory.shared.messages;

public class InvocationMessageAutoBean extends com.google.web.bindery.autobean.shared.impl.AbstractAutoBean<com.google.web.bindery.requestfactory.shared.messages.InvocationMessage> {
  private final com.google.web.bindery.requestfactory.shared.messages.InvocationMessage shim = new com.google.web.bindery.requestfactory.shared.messages.InvocationMessage() {
    public java.lang.String getOperation()  {
      java.lang.String toReturn = InvocationMessageAutoBean.this.getWrapped().getOperation();
      return toReturn;
    }
    public java.util.List getParameters()  {
      java.util.List toReturn = InvocationMessageAutoBean.this.getWrapped().getParameters();
      if (toReturn != null) {
        if (InvocationMessageAutoBean.this.isWrapped(toReturn)) {
          toReturn = InvocationMessageAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.ListAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public java.util.Set getPropertyRefs()  {
      java.util.Set toReturn = InvocationMessageAutoBean.this.getWrapped().getPropertyRefs();
      if (toReturn != null) {
        if (InvocationMessageAutoBean.this.isWrapped(toReturn)) {
          toReturn = InvocationMessageAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.SetAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public void setOperation(java.lang.String value)  {
      InvocationMessageAutoBean.this.getWrapped().setOperation(value);
      InvocationMessageAutoBean.this.set("setOperation", value);
    }
    public void setParameters(java.util.List value)  {
      InvocationMessageAutoBean.this.getWrapped().setParameters(value);
      InvocationMessageAutoBean.this.set("setParameters", value);
    }
    public void setPropertyRefs(java.util.Set value)  {
      InvocationMessageAutoBean.this.getWrapped().setPropertyRefs(value);
      InvocationMessageAutoBean.this.set("setPropertyRefs", value);
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
  public InvocationMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory) {super(factory);}
  public InvocationMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory, com.google.web.bindery.requestfactory.shared.messages.InvocationMessage wrapped) {
    super(wrapped, factory);
  }
  public com.google.web.bindery.requestfactory.shared.messages.InvocationMessage as() {return shim;}
  public Class<com.google.web.bindery.requestfactory.shared.messages.InvocationMessage> getType() {return com.google.web.bindery.requestfactory.shared.messages.InvocationMessage.class;}
  @Override protected com.google.web.bindery.requestfactory.shared.messages.InvocationMessage createSimplePeer() {
    return new com.google.web.bindery.requestfactory.shared.messages.InvocationMessage() {
      private final com.google.web.bindery.autobean.shared.Splittable data = com.google.web.bindery.requestfactory.shared.messages.InvocationMessageAutoBean.this.data;
      public java.lang.String getOperation()  {
        return (java.lang.String) getOrReify("O");
      }
      public java.util.List getParameters()  {
        return (java.util.List) getOrReify("P");
      }
      public java.util.Set getPropertyRefs()  {
        return (java.util.Set) getOrReify("R");
      }
      public void setOperation(java.lang.String value)  {
        setProperty("O", value);
      }
      public void setParameters(java.util.List value)  {
        setProperty("P", value);
      }
      public void setPropertyRefs(java.util.Set value)  {
        setProperty("R", value);
      }
    };
  }
  @Override protected void traverseProperties(com.google.web.bindery.autobean.shared.AutoBeanVisitor visitor, com.google.web.bindery.autobean.shared.impl.AbstractAutoBean.OneShotContext ctx) {
    com.google.web.bindery.autobean.shared.impl.AbstractAutoBean bean;
    Object value;
    com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext propertyContext;
    com.google.web.bindery.requestfactory.shared.messages.InvocationMessage as = as();
    value = as.getOperation();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(InvocationMessageAutoBean.this, "O"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("O", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("O", value, propertyContext);
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getParameters());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(InvocationMessageAutoBean.this, "P"),
      new Class<?>[] {java.util.List.class, com.google.web.bindery.autobean.shared.Splittable.class},
      new int[] {1, 0}
    );
    if (visitor.visitCollectionProperty("P", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitCollectionProperty("P", bean, propertyContext);
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getPropertyRefs());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(InvocationMessageAutoBean.this, "R"),
      new Class<?>[] {java.util.Set.class, java.lang.String.class},
      new int[] {1, 0}
    );
    if (visitor.visitCollectionProperty("R", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitCollectionProperty("R", bean, propertyContext);
  }
}
