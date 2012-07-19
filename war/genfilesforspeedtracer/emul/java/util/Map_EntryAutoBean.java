package emul.java.util;

public class Map_EntryAutoBean extends com.google.web.bindery.autobean.shared.impl.AbstractAutoBean<java.util.Map.Entry> {
  private final java.util.Map.Entry shim = new java.util.Map.Entry() {
    public java.lang.Object getKey()  {
      java.lang.Object toReturn = Map_EntryAutoBean.this.getWrapped().getKey();
      if (toReturn != null) {
        if (Map_EntryAutoBean.this.isWrapped(toReturn)) {
          toReturn = Map_EntryAutoBean.this.getFromWrapper(toReturn);
        } else {
        }
      }
      return toReturn;
    }
    public java.lang.Object getValue()  {
      java.lang.Object toReturn = Map_EntryAutoBean.this.getWrapped().getValue();
      if (toReturn != null) {
        if (Map_EntryAutoBean.this.isWrapped(toReturn)) {
          toReturn = Map_EntryAutoBean.this.getFromWrapper(toReturn);
        } else {
        }
      }
      return toReturn;
    }
    public java.lang.Object setValue(java.lang.Object value)  {
      Map_EntryAutoBean.this.getWrapped().setValue(value);
      Map_EntryAutoBean.this.set("setValue", value);
      return this;
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
  public Map_EntryAutoBean(com.google.web.bindery.autobean.shared.AutoBeanFactory factory, java.util.Map.Entry wrapped) {
    super(wrapped, factory);
  }
  public java.util.Map.Entry as() {return shim;}
  public Class<java.util.Map.Entry> getType() {return java.util.Map.Entry.class;}
  @Override protected void traverseProperties(com.google.web.bindery.autobean.shared.AutoBeanVisitor visitor, com.google.web.bindery.autobean.shared.impl.AbstractAutoBean.OneShotContext ctx) {
    com.google.web.bindery.autobean.shared.impl.AbstractAutoBean bean;
    Object value;
    com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext propertyContext;
    java.util.Map.Entry as = as();
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getKey());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter.beanSetter(Map_EntryAutoBean.this, "key"),
      java.lang.Object.class
    );
    if (visitor.visitReferenceProperty("key", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitReferenceProperty("key", bean, propertyContext);
    bean = (com.google.web.bindery.autobean.shared.impl.AbstractAutoBean) com.google.web.bindery.autobean.shared.AutoBeanUtils.getAutoBean(as.getValue());
    propertyContext = new com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext(
      as,
      setValueMethodReference(as),
      java.lang.Object.class
    );
    if (visitor.visitReferenceProperty("value", bean, propertyContext)) {
      if (bean != null) { bean.traverse(visitor, ctx); }
    }
    visitor.endVisitReferenceProperty("value", bean, propertyContext);
  }
  public static native com.google.web.bindery.autobean.gwt.client.impl.ClientPropertyContext.Setter setValueMethodReference(Object instance) /*-{
    return instance.@java.util.Map.Entry::setValue(Ljava/lang/Object;);
  }-*/;
}
