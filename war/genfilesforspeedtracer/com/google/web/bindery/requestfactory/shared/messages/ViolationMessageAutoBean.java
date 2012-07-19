package com.google.web.bindery.requestfactory.shared.messages;

public class ViolationMessageAutoBean extends com.google.web.bindery.autobean.shared.impl.AbstractAutoBean<com.google.web.bindery.requestfactory.shared.messages.ViolationMessage> {
  private final com.google.web.bindery.requestfactory.shared.messages.ViolationMessage shim = new com.google.web.bindery.requestfactory.shared.messages.ViolationMessage() {
    public com.google.web.bindery.requestfactory.shared.messages.IdMessage getLeafBeanId()  {
      com.google.web.bindery.requestfactory.shared.messages.IdMessage toReturn = ViolationMessageAutoBean.this.getWrapped().getLeafBeanId();
      if (toReturn != null) {
        if (ViolationMessageAutoBean.this.isWrapped(toReturn)) {
          toReturn = ViolationMessageAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new com.google.web.bindery.requestfactory.shared.messages.IdMessageAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public com.google.web.bindery.requestfactory.shared.messages.IdMessage getRootBeanId()  {
      com.google.web.bindery.requestfactory.shared.messages.IdMessage toReturn = ViolationMessageAutoBean.this.getWrapped().getRootBeanId();
      if (toReturn != null) {
        if (ViolationMessageAutoBean.this.isWrapped(toReturn)) {
          toReturn = ViolationMessageAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new com.google.web.bindery.requestfactory.shared.messages.IdMessageAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public java.lang.String getMessage()  {
      java.lang.String toReturn = ViolationMessageAutoBean.this.getWrapped().getMessage();
      return toReturn;
    }
    public java.lang.String getMessageTemplate()  {
      java.lang.String toReturn = ViolationMessageAutoBean.this.getWrapped().getMessageTemplate();
      return toReturn;
    }
    public java.lang.String getPath()  {
      java.lang.String toReturn = ViolationMessageAutoBean.this.getWrapped().getPath();
      return toReturn;
    }
    public void setLeafBeanId(com.google.web.bindery.requestfactory.shared.messages.IdMessage id)  {
      ViolationMessageAutoBean.this.getWrapped().setLeafBeanId(id);
      ViolationMessageAutoBean.this.set("setLeafBeanId", id);
    }
    public void setMessage(java.lang.String value)  {
      ViolationMessageAutoBean.this.getWrapped().setMessage(value);
      ViolationMessageAutoBean.this.set("setMessage", value);
    }
    public void setMessageTemplate(java.lang.String value)  {
      ViolationMessageAutoBean.this.getWrapped().setMessageTemplate(value);
      ViolationMessageAutoBean.this.set("setMessageTemplate", value);
    }
    public void setPath(java.lang.String value)  {
      ViolationMessageAutoBean.this.getWrapped().setPath(value);
      ViolationMessageAutoBean.this.set("setPath", value);
    }
    public void setRootBeanId(com.google.web.bindery.requestfactory.shared.messages.IdMessage id)  {
      ViolationMessageAutoBean.this.getWrapped().setRootBeanId(id);
      ViolationMessageAutoBean.this.set("setRootBeanId", id);
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
  public ViolationMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory) {super(factory);}
  public ViolationMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory, com.google.web.bindery.requestfactory.shared.messages.ViolationMessage wrapped) {
    super(wrapped, factory);
  }
  public com.google.web.bindery.requestfactory.shared.messages.ViolationMessage as() {return shim;}
  public Class<com.google.web.bindery.requestfactory.shared.messages.ViolationMessage> getType() {return com.google.web.bindery.requestfactory.shared.messages.ViolationMessage.class;}
  @Override protected com.google.web.bindery.requestfactory.shared.messages.ViolationMessage createSimplePeer() {
    return new com.google.web.bindery.requestfactory.shared.messages.ViolationMessage() {
      private final com.google.web.bindery.autobean.shared.Splittable data = com.google.web.bindery.requestfactory.shared.messages.ViolationMessageAutoBean.this.data;
      public com.google.web.bindery.requestfactory.shared.messages.IdMessage getLeafBeanId()  {
        return (com.google.web.bindery.requestfactory.shared.messages.IdMessage) getOrReify("L");
      }
      public com.google.web.bindery.requestfactory.shared.messages.IdMessage getRootBeanId()  {
        return (com.google.web.bindery.requestfactory.shared.messages.IdMessage) getOrReify("R");
      }
      public java.lang.String getMessage()  {
        return (java.lang.String) getOrReify("M");
      }
      public java.lang.String getMessageTemplate()  {
        return (java.lang.String) getOrReify("T");
      }
      public java.lang.String getPath()  {
        return (java.lang.String) getOrReify("P");
      }
      public void setLeafBeanId(com.google.web.bindery.requestfactory.shared.messages.IdMessage id)  {
        setProperty("L", id);
      }
      public void setMessage(java.lang.String value)  {
        setProperty("M", value);
      }
      public void setMessageTemplate(java.lang.String value)  {
        setProperty("T", value);
      }
      public void setPath(java.lang.String value)  {
        setProperty("P", value);
      }
      public void setRootBeanId(com.google.web.bindery.requestfactory.shared.messages.IdMessage id)  {
        setProperty("R", id);
      }
    };
  }
  @Override protected void traverseProperties(com.google.web.bindery.autobean.shared.AutoBeanVisitor visitor, com.google.web.bindery.autobean.shared.impl.AbstractAutoBean.OneShotContext ctx) {
    com.google.web.bindery.autobean.shared.impl.AbstractAutoBean bean;
    Object value;
    com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext propertyContext;
    com.google.web.bindery.requestfactory.shared.messages.ViolationMessage as = as();
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getLeafBeanId());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ViolationMessageAutoBean.this, "L"),
      com.google.web.bindery.requestfactory.shared.messages.IdMessage.class
    );
    if (visitor.visitReferenceProperty("L", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitReferenceProperty("L", bean, propertyContext);
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getRootBeanId());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ViolationMessageAutoBean.this, "R"),
      com.google.web.bindery.requestfactory.shared.messages.IdMessage.class
    );
    if (visitor.visitReferenceProperty("R", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitReferenceProperty("R", bean, propertyContext);
    value = as.getMessage();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ViolationMessageAutoBean.this, "M"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("M", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("M", value, propertyContext);
    value = as.getMessageTemplate();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ViolationMessageAutoBean.this, "T"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("T", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("T", value, propertyContext);
    value = as.getPath();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ViolationMessageAutoBean.this, "P"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("P", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("P", value, propertyContext);
  }
}
