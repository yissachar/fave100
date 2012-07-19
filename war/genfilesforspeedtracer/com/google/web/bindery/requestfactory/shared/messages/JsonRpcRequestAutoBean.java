package com.google.web.bindery.requestfactory.shared.messages;

public class JsonRpcRequestAutoBean extends com.google.web.bindery.autobean.shared.impl.AbstractAutoBean<com.google.web.bindery.requestfactory.shared.messages.JsonRpcRequest> {
  private final com.google.web.bindery.requestfactory.shared.messages.JsonRpcRequest shim = new com.google.web.bindery.requestfactory.shared.messages.JsonRpcRequest() {
    public int getId()  {
      int toReturn = JsonRpcRequestAutoBean.this.getWrapped().getId();
      return toReturn;
    }
    public java.lang.String getApiVersion()  {
      java.lang.String toReturn = JsonRpcRequestAutoBean.this.getWrapped().getApiVersion();
      return toReturn;
    }
    public java.lang.String getMethod()  {
      java.lang.String toReturn = JsonRpcRequestAutoBean.this.getWrapped().getMethod();
      return toReturn;
    }
    public java.lang.String getVersion()  {
      java.lang.String toReturn = JsonRpcRequestAutoBean.this.getWrapped().getVersion();
      return toReturn;
    }
    public java.util.Map getParams()  {
      java.util.Map toReturn = JsonRpcRequestAutoBean.this.getWrapped().getParams();
      if (toReturn != null) {
        if (JsonRpcRequestAutoBean.this.isWrapped(toReturn)) {
          toReturn = JsonRpcRequestAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.MapAutoBean(getFactory(), toReturn).as();
        }
      }
      return toReturn;
    }
    public void setApiVersion(java.lang.String version)  {
      JsonRpcRequestAutoBean.this.getWrapped().setApiVersion(version);
      JsonRpcRequestAutoBean.this.set("setApiVersion", version);
    }
    public void setId(int id)  {
      JsonRpcRequestAutoBean.this.getWrapped().setId(id);
      JsonRpcRequestAutoBean.this.set("setId", id);
    }
    public void setMethod(java.lang.String method)  {
      JsonRpcRequestAutoBean.this.getWrapped().setMethod(method);
      JsonRpcRequestAutoBean.this.set("setMethod", method);
    }
    public void setParams(java.util.Map params)  {
      JsonRpcRequestAutoBean.this.getWrapped().setParams(params);
      JsonRpcRequestAutoBean.this.set("setParams", params);
    }
    public void setVersion(java.lang.String version)  {
      JsonRpcRequestAutoBean.this.getWrapped().setVersion(version);
      JsonRpcRequestAutoBean.this.set("setVersion", version);
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
  public JsonRpcRequestAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory) {super(factory);}
  public JsonRpcRequestAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory, com.google.web.bindery.requestfactory.shared.messages.JsonRpcRequest wrapped) {
    super(wrapped, factory);
  }
  public com.google.web.bindery.requestfactory.shared.messages.JsonRpcRequest as() {return shim;}
  public Class<com.google.web.bindery.requestfactory.shared.messages.JsonRpcRequest> getType() {return com.google.web.bindery.requestfactory.shared.messages.JsonRpcRequest.class;}
  @Override protected com.google.web.bindery.requestfactory.shared.messages.JsonRpcRequest createSimplePeer() {
    return new com.google.web.bindery.requestfactory.shared.messages.JsonRpcRequest() {
      private final com.google.web.bindery.autobean.shared.Splittable data = com.google.web.bindery.requestfactory.shared.messages.JsonRpcRequestAutoBean.this.data;
      public int getId()  {
        java.lang.Integer toReturn = getOrReify("id");
        return toReturn == null ? 0 : toReturn;
      }
      public java.lang.String getApiVersion()  {
        return (java.lang.String) getOrReify("apiVersion");
      }
      public java.lang.String getMethod()  {
        return (java.lang.String) getOrReify("method");
      }
      public java.lang.String getVersion()  {
        return (java.lang.String) getOrReify("jsonrpc");
      }
      public java.util.Map getParams()  {
        return (java.util.Map) getOrReify("params");
      }
      public void setApiVersion(java.lang.String version)  {
        setProperty("apiVersion", version);
      }
      public void setId(int id)  {
        setProperty("id", id);
      }
      public void setMethod(java.lang.String method)  {
        setProperty("method", method);
      }
      public void setParams(java.util.Map params)  {
        setProperty("params", params);
      }
      public void setVersion(java.lang.String version)  {
        setProperty("jsonrpc", version);
      }
    };
  }
  @Override protected void traverseProperties(com.google.web.bindery.autobean.shared.AutoBeanVisitor visitor, com.google.web.bindery.autobean.shared.impl.AbstractAutoBean.OneShotContext ctx) {
    com.google.web.bindery.autobean.shared.impl.AbstractAutoBean bean;
    Object value;
    com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext propertyContext;
    com.google.web.bindery.requestfactory.shared.messages.JsonRpcRequest as = as();
    value = as.getId();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(JsonRpcRequestAutoBean.this, "id"),
      int.class
    );
    if (visitor.visitValueProperty("id", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("id", value, propertyContext);
    value = as.getApiVersion();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(JsonRpcRequestAutoBean.this, "apiVersion"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("apiVersion", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("apiVersion", value, propertyContext);
    value = as.getMethod();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(JsonRpcRequestAutoBean.this, "method"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("method", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("method", value, propertyContext);
    value = as.getVersion();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(JsonRpcRequestAutoBean.this, "jsonrpc"),
      java.lang.String.class
    );
    if (visitor.visitValueProperty("jsonrpc", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("jsonrpc", value, propertyContext);
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getParams());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(JsonRpcRequestAutoBean.this, "params"),
      new Class<?>[] {java.util.Map.class, java.lang.String.class, com.google.web.bindery.autobean.shared.Splittable.class},
      new int[] {2, 0, 0}
    );
    if (visitor.visitMapProperty("params", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitMapProperty("params", bean, propertyContext);
  }
}
