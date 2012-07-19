package com.google.web.bindery.requestfactory.shared.messages;

public class IdMessageAutoBean extends com.google.web.bindery.autobean.shared.impl.AbstractAutoBean<com.google.web.bindery.requestfactory.shared.messages.IdMessage> {
  private final com.google.web.bindery.requestfactory.shared.messages.IdMessage shim = new com.google.web.bindery.requestfactory.shared.messages.IdMessage() {
    public com.google.web.bindery.requestfactory.shared.messages.IdMessage.Strength getStrength()  {
      com.google.web.bindery.requestfactory.shared.messages.IdMessage.Strength toReturn = IdMessageAutoBean.this.getWrapped().getStrength();
      return toReturn;
    }
    public int getClientId()  {
      int toReturn = IdMessageAutoBean.this.getWrapped().getClientId();
      return toReturn;
    }
    public int getSyntheticId()  {
      int toReturn = IdMessageAutoBean.this.getWrapped().getSyntheticId();
      return toReturn;
    }
    public java.lang.String getServerId()  {
      java.lang.String toReturn = IdMessageAutoBean.this.getWrapped().getServerId();
      return toReturn;
    }
    public java.lang.String getTypeToken()  {
      java.lang.String toReturn = IdMessageAutoBean.this.getWrapped().getTypeToken();
      return toReturn;
    }
    public void setClientId(int value)  {
      IdMessageAutoBean.this.getWrapped().setClientId(value);
      IdMessageAutoBean.this.set("setClientId", value);
    }
    public void setServerId(java.lang.String value)  {
      IdMessageAutoBean.this.getWrapped().setServerId(value);
      IdMessageAutoBean.this.set("setServerId", value);
    }
    public void setStrength(com.google.web.bindery.requestfactory.shared.messages.IdMessage.Strength value)  {
      IdMessageAutoBean.this.getWrapped().setStrength(value);
      IdMessageAutoBean.this.set("setStrength", value);
    }
    public void setSyntheticId(int value)  {
      IdMessageAutoBean.this.getWrapped().setSyntheticId(value);
      IdMessageAutoBean.this.set("setSyntheticId", value);
    }
    public void setTypeToken(java.lang.String value)  {
      IdMessageAutoBean.this.getWrapped().setTypeToken(value);
      IdMessageAutoBean.this.set("setTypeToken", value);
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
  public IdMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory) {super(factory);}
  public IdMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory, com.google.web.bindery.requestfactory.shared.messages.IdMessage wrapped) {
    super(wrapped, factory);
  }
  public com.google.web.bindery.requestfactory.shared.messages.IdMessage as() {return shim;}
  public Class<com.google.web.bindery.requestfactory.shared.messages.IdMessage> getType() {return com.google.web.bindery.requestfactory.shared.messages.IdMessage.class;}
  @Override protected com.google.web.bindery.requestfactory.shared.messages.IdMessage createSimplePeer() {
    return new com.google.web.bindery.requestfactory.shared.messages.IdMessage() {
      private final com.google.web.bindery.autobean.shared.Splittable data = com.google.web.bindery.requestfactory.shared.messages.IdMessageAutoBean.this.data;
      public com.google.web.bindery.requestfactory.shared.messages.IdMessage.Strength getStrength()  {
        return (com.google.web.bindery.requestfactory.shared.messages.IdMessage.Strength) getOrReify("R");
      }
      public int getClientId()  {
        java.lang.Integer toReturn = getOrReify("C");
        return toReturn == null ? 0 : toReturn;
      }
      public int getSyntheticId()  {
        java.lang.Integer toReturn = getOrReify("Y");
        return toReturn == null ? 0 : toReturn;
      }
      public java.lang.String getServerId()  {
        return (java.lang.String) getOrReify("S");
      }
      public java.lang.String getTypeToken()  {
        return (java.lang.String) getOrReify("T");
      }
      public void setClientId(int value)  {
        setProperty("C", value);
      }
      public void setServerId(java.lang.String value)  {
        setProperty("S", value);
      }
      public void setStrength(com.google.web.bindery.requestfactory.shared.messages.IdMessage.Strength value)  {
        setProperty("R", value);
      }
      public void setSyntheticId(int value)  {
        setProperty("Y", value);
      }
      public void setTypeToken(java.lang.String value)  {
        setProperty("T", value);
      }
    };
  }
  @Override protected void traverseProperties(com.google.web.bindery.autobean.shared.AutoBeanVisitor visitor, com.google.web.bindery.autobean.shared.impl.AbstractAutoBean.OneShotContext ctx) {
    com.google.web.bindery.autobean.shared.impl.AbstractAutoBean bean;
    Object value;
    com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext propertyContext;
    com.google.web.bindery.requestfactory.shared.messages.IdMessage as = as();
    value = as.getStrength();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(IdMessageAutoBean.this, "R"),
      com.google.web.bindery.requestfactory.shared.messages.IdMessage.Strength.class
    );
    if (visitor.visitValueProperty("R", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("R", value, propertyContext);
    value = as.getClientId();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(IdMessageAutoBean.this, "C"),
      int.class
    );
    if (visitor.visitValueProperty("C", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("C", value, propertyContext);
    value = as.getSyntheticId();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(IdMessageAutoBean.this, "Y"),
      int.class
    );
    if (visitor.visitValueProperty("Y", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("Y", value, propertyContext);
    value = as.getServerId();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(IdMessageAutoBean.this, "S"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("S", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("S", value, propertyContext);
    value = as.getTypeToken();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(IdMessageAutoBean.this, "T"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("T", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("T", value, propertyContext);
  }
}
