package emul.java.util;

public class MapAutoBean extends com.google.web.bindery.autobean.shared.impl.AbstractAutoBean<java.util.Map> {
  private final java.util.Map shim = new java.util.Map() {
    public boolean isEmpty()  {
      boolean toReturn = MapAutoBean.this.getWrapped().isEmpty();
      return toReturn;
    }
    public java.lang.Object get(java.lang.Object key)  {
      java.lang.Object toReturn = MapAutoBean.this.getWrapped().get(key);
      if (toReturn != null) {
        if (MapAutoBean.this.isWrapped(toReturn)) {
          toReturn = MapAutoBean.this.getFromWrapper(toReturn);
        } else {
        }
      }
      MapAutoBean.this.call("get", toReturn, key);
      return toReturn;
    }
    public java.lang.Object put(java.lang.Object key,java.lang.Object value)  {
      java.lang.Object toReturn = MapAutoBean.this.getWrapped().put(key,value);
      if (toReturn != null) {
        if (MapAutoBean.this.isWrapped(toReturn)) {
          toReturn = MapAutoBean.this.getFromWrapper(toReturn);
        } else {
        }
      }
      MapAutoBean.this.call("put", toReturn, key,value);
      return toReturn;
    }
    public java.lang.Object remove(java.lang.Object key)  {
      java.lang.Object toReturn = MapAutoBean.this.getWrapped().remove(key);
      if (toReturn != null) {
        if (MapAutoBean.this.isWrapped(toReturn)) {
          toReturn = MapAutoBean.this.getFromWrapper(toReturn);
        } else {
        }
      }
      MapAutoBean.this.call("remove", toReturn, key);
      return toReturn;
    }
    public boolean containsKey(java.lang.Object key)  {
      boolean toReturn = MapAutoBean.this.getWrapped().containsKey(key);
      MapAutoBean.this.call("containsKey", toReturn, key);
      return toReturn;
    }
    public boolean containsValue(java.lang.Object value)  {
      boolean toReturn = MapAutoBean.this.getWrapped().containsValue(value);
      MapAutoBean.this.call("containsValue", toReturn, value);
      return toReturn;
    }
    public int size()  {
      int toReturn = MapAutoBean.this.getWrapped().size();
      MapAutoBean.this.call("size", toReturn );
      return toReturn;
    }
    public java.util.Collection values()  {
      java.util.Collection toReturn = MapAutoBean.this.getWrapped().values();
      if (toReturn != null) {
        if (MapAutoBean.this.isWrapped(toReturn)) {
          toReturn = MapAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.CollectionAutoBean(getFactory(), toReturn).as();
        }
      }
      MapAutoBean.this.call("values", toReturn );
      return toReturn;
    }
    public java.util.Set keySet()  {
      java.util.Set toReturn = MapAutoBean.this.getWrapped().keySet();
      if (toReturn != null) {
        if (MapAutoBean.this.isWrapped(toReturn)) {
          toReturn = MapAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.SetAutoBean(getFactory(), toReturn).as();
        }
      }
      MapAutoBean.this.call("keySet", toReturn );
      return toReturn;
    }
    public java.util.Set entrySet()  {
      java.util.Set toReturn = MapAutoBean.this.getWrapped().entrySet();
      if (toReturn != null) {
        if (MapAutoBean.this.isWrapped(toReturn)) {
          toReturn = MapAutoBean.this.getFromWrapper(toReturn);
        } else {
          toReturn = new emul.java.util.SetAutoBean(getFactory(), toReturn).as();
        }
      }
      MapAutoBean.this.call("entrySet", toReturn );
      return toReturn;
    }
    public void clear()  {
      MapAutoBean.this.getWrapped().clear();
      MapAutoBean.this.call("clear", null );
    }
    public void putAll(java.util.Map t)  {
      MapAutoBean.this.getWrapped().putAll(t);
      MapAutoBean.this.call("putAll", null, t);
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
  public MapAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory, java.util.Map wrapped) {
    super(wrapped, factory);
  }
  public java.util.Map as() {return shim;}
  public Class<java.util.Map> getType() {return java.util.Map.class;}
  @Override protected void traverseProperties(com.google.web.bindery.autobean.shared.AutoBeanVisitor visitor, com.google.web.bindery.autobean.shared.impl.AbstractAutoBean.OneShotContext ctx) {
    com.google.web.bindery.autobean.shared.impl.AbstractAutoBean bean;
    Object value;
    com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext propertyContext;
    java.util.Map as = as();
    value = as.isEmpty();
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(MapAutoBean.this, "empty"),
      boolean.class
    );
    if (visitor.visitValueProperty("empty", value, propertyContext)) {
    }
    visitor.endVisitValueProperty("empty", value, propertyContext);
  }
}
