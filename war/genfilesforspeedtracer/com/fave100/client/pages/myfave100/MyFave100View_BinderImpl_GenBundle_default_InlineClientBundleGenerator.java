package com.fave100.client.pages.myfave100;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class MyFave100View_BinderImpl_GenBundle_default_InlineClientBundleGenerator implements com.fave100.client.pages.myfave100.MyFave100View_BinderImpl_GenBundle {
  private static MyFave100View_BinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new MyFave100View_BinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new com.fave100.client.pages.myfave100.MyFave100View_BinderImpl_GenCss_style() {
      private boolean injected;
      public boolean ensureInjected() {
        if (!injected) {
          injected = true;
          com.google.gwt.dom.client.StyleInjector.inject(getText());
          return true;
        }
        return false;
      }
      public String getName() {
        return "style";
      }
      public String getText() {
        return (".faveList{margin:" + ("50px"+ " " +"auto")  + ";background-color:" + ("#fff")  + ";border:" + ("2px"+ " " +"solid"+ " " +"#5b5")  + ";width:" + ("740px")  + ";height:" + ("600px")  + ";}.faveList button{display:" + ("block")  + ";margin:" + ("0"+ " " +"auto")  + ";}");
      }
      public java.lang.String faveList(){
        return "faveList";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static com.fave100.client.pages.myfave100.MyFave100View_BinderImpl_GenCss_style get() {
      return style;
    }
  }
  public com.fave100.client.pages.myfave100.MyFave100View_BinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.fave100.client.pages.myfave100.MyFave100View_BinderImpl_GenCss_style style;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      style(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("style", style());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'style': return this.@com.fave100.client.pages.myfave100.MyFave100View_BinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
