package com.google.web.bindery.requestfactory.shared.messages;

public class ResponseMessageAutoBean extends com.google.web.bindery.autobean.shared.impl.AbstractAutoBean<com.google.web.bindery.requestfactory.shared.messages.ResponseMessage> {
  private final com.google.web.bindery.requestfactory.shared.messages.ResponseMessage shim = new com.google.web.bindery.requestfactory.shared.messages.ResponseMessage() {
    public com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage getGeneralFailure()  {
      com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage toReturn = ResponseMessageAutoBean.this.getWrapped().getGeneralFailure();
      if (toReturn != null) {
        if (ResponseMessageAutoBean.this.isWrapped(toReturn)) {
          toReturn = ResponseMessageAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessageAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public java.lang.String getVersion()  {
      java.lang.String toReturn = ResponseMessageAutoBean.this.getWrapped().getVersion();
      return toReturn;
    }
    public java.util.List getInvocationResults()  {
      java.util.List toReturn = ResponseMessageAutoBean.this.getWrapped().getInvocationResults();
      if (toReturn != null) {
        if (ResponseMessageAutoBean.this.isWrapped(toReturn)) {
          toReturn = ResponseMessageAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.ListAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public java.util.List getOperations()  {
      java.util.List toReturn = ResponseMessageAutoBean.this.getWrapped().getOperations();
      if (toReturn != null) {
        if (ResponseMessageAutoBean.this.isWrapped(toReturn)) {
          toReturn = ResponseMessageAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.ListAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public java.util.List getViolations()  {
      java.util.List toReturn = ResponseMessageAutoBean.this.getWrapped().getViolations();
      if (toReturn != null) {
        if (ResponseMessageAutoBean.this.isWrapped(toReturn)) {
          toReturn = ResponseMessageAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.ListAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public java.util.List getStatusCodes()  {
      java.util.List toReturn = ResponseMessageAutoBean.this.getWrapped().getStatusCodes();
      if (toReturn != null) {
        if (ResponseMessageAutoBean.this.isWrapped(toReturn)) {
          toReturn = ResponseMessageAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.ListAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public void setGeneralFailure(com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage failure)  {
      ResponseMessageAutoBean.this.getWrapped().setGeneralFailure(failure);
      ResponseMessageAutoBean.this.set("setGeneralFailure", failure);
    }
    public void setInvocationResults(java.util.List value)  {
      ResponseMessageAutoBean.this.getWrapped().setInvocationResults(value);
      ResponseMessageAutoBean.this.set("setInvocationResults", value);
    }
    public void setOperations(java.util.List value)  {
      ResponseMessageAutoBean.this.getWrapped().setOperations(value);
      ResponseMessageAutoBean.this.set("setOperations", value);
    }
    public void setStatusCodes(java.util.List value)  {
      ResponseMessageAutoBean.this.getWrapped().setStatusCodes(value);
      ResponseMessageAutoBean.this.set("setStatusCodes", value);
    }
    public void setVersion(java.lang.String version)  {
      ResponseMessageAutoBean.this.getWrapped().setVersion(version);
      ResponseMessageAutoBean.this.set("setVersion", version);
    }
    public void setViolations(java.util.List value)  {
      ResponseMessageAutoBean.this.getWrapped().setViolations(value);
      ResponseMessageAutoBean.this.set("setViolations", value);
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
  public ResponseMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory) {super(factory);}
  public ResponseMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory, com.google.web.bindery.requestfactory.shared.messages.ResponseMessage wrapped) {
    super(wrapped, factory);
  }
  public com.google.web.bindery.requestfactory.shared.messages.ResponseMessage as() {return shim;}
  public Class<com.google.web.bindery.requestfactory.shared.messages.ResponseMessage> getType() {return com.google.web.bindery.requestfactory.shared.messages.ResponseMessage.class;}
  @Override protected com.google.web.bindery.requestfactory.shared.messages.ResponseMessage createSimplePeer() {
    return new com.google.web.bindery.requestfactory.shared.messages.ResponseMessage() {
      private final com.google.web.bindery.autobean.shared.Splittable data = com.google.web.bindery.requestfactory.shared.messages.ResponseMessageAutoBean.this.data;
      public com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage getGeneralFailure()  {
        return (com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage) getOrReify("F");
      }
      public java.lang.String getVersion()  {
        return (java.lang.String) getOrReify("V");
      }
      public java.util.List getInvocationResults()  {
        return (java.util.List) getOrReify("I");
      }
      public java.util.List getOperations()  {
        return (java.util.List) getOrReify("O");
      }
      public java.util.List getViolations()  {
        return (java.util.List) getOrReify("X");
      }
      public java.util.List getStatusCodes()  {
        return (java.util.List) getOrReify("S");
      }
      public void setGeneralFailure(com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage failure)  {
        setProperty("F", failure);
      }
      public void setInvocationResults(java.util.List value)  {
        setProperty("I", value);
      }
      public void setOperations(java.util.List value)  {
        setProperty("O", value);
      }
      public void setStatusCodes(java.util.List value)  {
        setProperty("S", value);
      }
      public void setVersion(java.lang.String version)  {
        setProperty("V", version);
      }
      public void setViolations(java.util.List value)  {
        setProperty("X", value);
      }
    };
  }
  @Override protected void traverseProperties(com.google.web.bindery.autobean.shared.AutoBeanVisitor visitor, com.google.web.bindery.autobean.shared.impl.AbstractAutoBean.OneShotContext ctx) {
    com.google.web.bindery.autobean.shared.impl.AbstractAutoBean bean;
    Object value;
    com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext propertyContext;
    com.google.web.bindery.requestfactory.shared.messages.ResponseMessage as = as();
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getGeneralFailure());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ResponseMessageAutoBean.this, "F"),
      com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage.class
    );
    if (visitor.visitReferenceProperty("F", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitReferenceProperty("F", bean, propertyContext);
    value = as.getVersion();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ResponseMessageAutoBean.this, "V"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("V", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("V", value, propertyContext);
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getInvocationResults());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ResponseMessageAutoBean.this, "I"),
      new Class<?>[] {java.util.List.class, com.google.web.bindery.autobean.shared.Splittable.class},
      new int[] {1, 0}
    );
    if (visitor.visitCollectionProperty("I", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitCollectionProperty("I", bean, propertyContext);
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getOperations());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ResponseMessageAutoBean.this, "O"),
      new Class<?>[] {java.util.List.class, com.google.web.bindery.requestfactory.shared.messages.OperationMessage.class},
      new int[] {1, 0}
    );
    if (visitor.visitCollectionProperty("O", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitCollectionProperty("O", bean, propertyContext);
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getViolations());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ResponseMessageAutoBean.this, "X"),
      new Class<?>[] {java.util.List.class, com.google.web.bindery.requestfactory.shared.messages.ViolationMessage.class},
      new int[] {1, 0}
    );
    if (visitor.visitCollectionProperty("X", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitCollectionProperty("X", bean, propertyContext);
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getStatusCodes());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ResponseMessageAutoBean.this, "S"),
      new Class<?>[] {java.util.List.class, java.lang.Boolean.class},
      new int[] {1, 0}
    );
    if (visitor.visitCollectionProperty("S", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitCollectionProperty("S", bean, propertyContext);
  }
}
