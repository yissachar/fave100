package com.google.web.bindery.requestfactory.shared.messages;

public class ServerFailureMessageAutoBean extends com.google.web.bindery.autobean.shared.impl.AbstractAutoBean<com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage> {
  private final com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage shim = new com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage() {
    public boolean isFatal()  {
      boolean toReturn = ServerFailureMessageAutoBean.this.getWrapped().isFatal();
      return toReturn;
    }
    public java.lang.String getExceptionType()  {
      java.lang.String toReturn = ServerFailureMessageAutoBean.this.getWrapped().getExceptionType();
      return toReturn;
    }
    public java.lang.String getMessage()  {
      java.lang.String toReturn = ServerFailureMessageAutoBean.this.getWrapped().getMessage();
      return toReturn;
    }
    public java.lang.String getStackTrace()  {
      java.lang.String toReturn = ServerFailureMessageAutoBean.this.getWrapped().getStackTrace();
      return toReturn;
    }
    public void setExceptionType(java.lang.String exceptionType)  {
      ServerFailureMessageAutoBean.this.getWrapped().setExceptionType(exceptionType);
      ServerFailureMessageAutoBean.this.set("setExceptionType", exceptionType);
    }
    public void setFatal(boolean significant)  {
      ServerFailureMessageAutoBean.this.getWrapped().setFatal(significant);
      ServerFailureMessageAutoBean.this.set("setFatal", significant);
    }
    public void setMessage(java.lang.String message)  {
      ServerFailureMessageAutoBean.this.getWrapped().setMessage(message);
      ServerFailureMessageAutoBean.this.set("setMessage", message);
    }
    public void setStackTrace(java.lang.String stackTrace)  {
      ServerFailureMessageAutoBean.this.getWrapped().setStackTrace(stackTrace);
      ServerFailureMessageAutoBean.this.set("setStackTrace", stackTrace);
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
  public ServerFailureMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory) {super(factory);}
  public ServerFailureMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory, com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage wrapped) {
    super(wrapped, factory);
  }
  public com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage as() {return shim;}
  public Class<com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage> getType() {return com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage.class;}
  @Override protected com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage createSimplePeer() {
    return new com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage() {
      private final com.google.web.bindery.autobean.shared.Splittable data = com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessageAutoBean.this.data;
      public boolean isFatal()  {
        java.lang.Boolean toReturn = getOrReify("F");
        return toReturn == null ? false : toReturn;
      }
      public java.lang.String getExceptionType()  {
        return (java.lang.String) getOrReify("X");
      }
      public java.lang.String getMessage()  {
        return (java.lang.String) getOrReify("M");
      }
      public java.lang.String getStackTrace()  {
        return (java.lang.String) getOrReify("S");
      }
      public void setExceptionType(java.lang.String exceptionType)  {
        setProperty("X", exceptionType);
      }
      public void setFatal(boolean significant)  {
        setProperty("F", significant);
      }
      public void setMessage(java.lang.String message)  {
        setProperty("M", message);
      }
      public void setStackTrace(java.lang.String stackTrace)  {
        setProperty("S", stackTrace);
      }
    };
  }
  @Override protected void traverseProperties(com.google.web.bindery.autobean.shared.AutoBeanVisitor visitor, com.google.web.bindery.autobean.shared.impl.AbstractAutoBean.OneShotContext ctx) {
    com.google.web.bindery.autobean.shared.impl.AbstractAutoBean bean;
    Object value;
    com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext propertyContext;
    com.google.web.bindery.requestfactory.shared.messages.ServerFailureMessage as = as();
    value = as.isFatal();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ServerFailureMessageAutoBean.this, "F"),
      boolean.class
    );
    if (visitor.visitValueProperty("F", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("F", value, propertyContext);
    value = as.getExceptionType();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ServerFailureMessageAutoBean.this, "X"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("X", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("X", value, propertyContext);
    value = as.getMessage();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ServerFailureMessageAutoBean.this, "M"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("M", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("M", value, propertyContext);
    value = as.getStackTrace();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(ServerFailureMessageAutoBean.this, "S"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("S", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("S", value, propertyContext);
  }
}
