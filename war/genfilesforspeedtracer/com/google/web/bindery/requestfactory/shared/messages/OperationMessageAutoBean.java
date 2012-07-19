package com.google.web.bindery.requestfactory.shared.messages;

public class OperationMessageAutoBean extends com.google.web.bindery.autobean.shared.impl.AbstractAutoBean<com.google.web.bindery.requestfactory.shared.messages.OperationMessage> {
  private final com.google.web.bindery.requestfactory.shared.messages.OperationMessage shim = new com.google.web.bindery.requestfactory.shared.messages.OperationMessage() {
    public com.google.web.bindery.requestfactory.shared.WriteOperation getOperation()  {
      com.google.web.bindery.requestfactory.shared.WriteOperation toReturn = OperationMessageAutoBean.this.getWrapped().getOperation();
      return toReturn;
    }
    public com.google.web.bindery.requestfactory.shared.messages.IdMessage.Strength getStrength()  {
      com.google.web.bindery.requestfactory.shared.messages.IdMessage.Strength toReturn = OperationMessageAutoBean.this.getWrapped().getStrength();
      return toReturn;
    }
    public int getClientId()  {
      int toReturn = OperationMessageAutoBean.this.getWrapped().getClientId();
      return toReturn;
    }
    public int getSyntheticId()  {
      int toReturn = OperationMessageAutoBean.this.getWrapped().getSyntheticId();
      return toReturn;
    }
    public java.lang.String getServerId()  {
      java.lang.String toReturn = OperationMessageAutoBean.this.getWrapped().getServerId();
      return toReturn;
    }
    public java.lang.String getTypeToken()  {
      java.lang.String toReturn = OperationMessageAutoBean.this.getWrapped().getTypeToken();
      return toReturn;
    }
    public java.lang.String getVersion()  {
      java.lang.String toReturn = OperationMessageAutoBean.this.getWrapped().getVersion();
      return toReturn;
    }
    public java.util.Map getPropertyMap()  {
      java.util.Map toReturn = OperationMessageAutoBean.this.getWrapped().getPropertyMap();
      if (toReturn != null) {
        if (OperationMessageAutoBean.this.isWrapped(toReturn)) {
          toReturn = OperationMessageAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.MapAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public void setClientId(int value)  {
      OperationMessageAutoBean.this.getWrapped().setClientId(value);
      OperationMessageAutoBean.this.set("setClientId", value);
    }
    public void setOperation(com.google.web.bindery.requestfactory.shared.WriteOperation value)  {
      OperationMessageAutoBean.this.getWrapped().setOperation(value);
      OperationMessageAutoBean.this.set("setOperation", value);
    }
    public void setPropertyMap(java.util.Map map)  {
      OperationMessageAutoBean.this.getWrapped().setPropertyMap(map);
      OperationMessageAutoBean.this.set("setPropertyMap", map);
    }
    public void setServerId(java.lang.String value)  {
      OperationMessageAutoBean.this.getWrapped().setServerId(value);
      OperationMessageAutoBean.this.set("setServerId", value);
    }
    public void setStrength(com.google.web.bindery.requestfactory.shared.messages.IdMessage.Strength value)  {
      OperationMessageAutoBean.this.getWrapped().setStrength(value);
      OperationMessageAutoBean.this.set("setStrength", value);
    }
    public void setSyntheticId(int value)  {
      OperationMessageAutoBean.this.getWrapped().setSyntheticId(value);
      OperationMessageAutoBean.this.set("setSyntheticId", value);
    }
    public void setTypeToken(java.lang.String value)  {
      OperationMessageAutoBean.this.getWrapped().setTypeToken(value);
      OperationMessageAutoBean.this.set("setTypeToken", value);
    }
    public void setVersion(java.lang.String version)  {
      OperationMessageAutoBean.this.getWrapped().setVersion(version);
      OperationMessageAutoBean.this.set("setVersion", version);
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
  public OperationMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory) {super(factory);}
  public OperationMessageAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory, com.google.web.bindery.requestfactory.shared.messages.OperationMessage wrapped) {
    super(wrapped, factory);
  }
  public com.google.web.bindery.requestfactory.shared.messages.OperationMessage as() {return shim;}
  public Class<com.google.web.bindery.requestfactory.shared.messages.OperationMessage> getType() {return com.google.web.bindery.requestfactory.shared.messages.OperationMessage.class;}
  @Override protected com.google.web.bindery.requestfactory.shared.messages.OperationMessage createSimplePeer() {
    return new com.google.web.bindery.requestfactory.shared.messages.OperationMessage() {
      private final com.google.web.bindery.autobean.shared.Splittable data = com.google.web.bindery.requestfactory.shared.messages.OperationMessageAutoBean.this.data;
      public com.google.web.bindery.requestfactory.shared.WriteOperation getOperation()  {
        return (com.google.web.bindery.requestfactory.shared.WriteOperation) getOrReify("O");
      }
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
      public java.lang.String getVersion()  {
        return (java.lang.String) getOrReify("V");
      }
      public java.util.Map getPropertyMap()  {
        return (java.util.Map) getOrReify("P");
      }
      public void setClientId(int value)  {
        setProperty("C", value);
      }
      public void setOperation(com.google.web.bindery.requestfactory.shared.WriteOperation value)  {
        setProperty("O", value);
      }
      public void setPropertyMap(java.util.Map map)  {
        setProperty("P", map);
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
      public void setVersion(java.lang.String version)  {
        setProperty("V", version);
      }
    };
  }
  @Override protected void traverseProperties(com.google.web.bindery.autobean.shared.AutoBeanVisitor visitor, com.google.web.bindery.autobean.shared.impl.AbstractAutoBean.OneShotContext ctx) {
    com.google.web.bindery.autobean.shared.impl.AbstractAutoBean bean;
    Object value;
    com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext propertyContext;
    com.google.web.bindery.requestfactory.shared.messages.OperationMessage as = as();
    value = as.getOperation();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(OperationMessageAutoBean.this, "O"),
      com.google.web.bindery.requestfactory.shared.WriteOperation.class
    );
    if (visitor.visitValueProperty("O", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("O", value, propertyContext);
    value = as.getStrength();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(OperationMessageAutoBean.this, "R"),
      com.google.web.bindery.requestfactory.shared.messages.IdMessage.Strength.class
    );
    if (visitor.visitValueProperty("R", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("R", value, propertyContext);
    value = as.getClientId();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(OperationMessageAutoBean.this, "C"),
      int.class
    );
    if (visitor.visitValueProperty("C", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("C", value, propertyContext);
    value = as.getSyntheticId();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(OperationMessageAutoBean.this, "Y"),
      int.class
    );
    if (visitor.visitValueProperty("Y", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("Y", value, propertyContext);
    value = as.getServerId();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(OperationMessageAutoBean.this, "S"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("S", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("S", value, propertyContext);
    value = as.getTypeToken();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(OperationMessageAutoBean.this, "T"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("T", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("T", value, propertyContext);
    value = as.getVersion();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(OperationMessageAutoBean.this, "V"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("V", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("V", value, propertyContext);
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getPropertyMap());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(OperationMessageAutoBean.this, "P"),
      new Class<?>[] {java.util.Map.class, java.lang.String.class, com.google.web.bindery.autobean.shared.Splittable.class},
      new int[] {2, 0, 0}
    );
    if (visitor.visitMapProperty("P", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitMapProperty("P", bean, propertyContext);
  }
}
