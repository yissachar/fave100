package com.fave100.client.pagefragments;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TopBarView_BinderImpl_GenBundle_default_InlineClientBundleGenerator implements com.fave100.client.pagefragments.TopBarView_BinderImpl_GenBundle {
  private static TopBarView_BinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new TopBarView_BinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new com.fave100.client.pagefragments.TopBarView_BinderImpl_GenCss_style() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GEWCVAHB{background-color:" + ("#fff")  + ";height:" + ("60px")  + ";}.GEWCVAHB ul{list-style-type:" + ("none")  + ";float:" + ("left")  + ";}.GEWCVAHB li{display:" + ("inline")  + ";float:" + ("left")  + ";margin:" + ("7px")  + ";margin-top:" + ("15px")  + ";}")) : ((".GEWCVAHB{background-color:" + ("#fff")  + ";height:" + ("60px")  + ";}.GEWCVAHB ul{list-style-type:" + ("none")  + ";float:" + ("right")  + ";}.GEWCVAHB li{display:" + ("inline")  + ";float:" + ("right")  + ";margin:" + ("7px")  + ";margin-top:" + ("15px")  + ";}"));
      }
      public java.lang.String faveLogo(){
        return "GEWCVAHA";
      }
      public java.lang.String topNav(){
        return "GEWCVAHB";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static com.fave100.client.pagefragments.TopBarView_BinderImpl_GenCss_style get() {
      return style;
    }
  }
  public com.fave100.client.pagefragments.TopBarView_BinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.fave100.client.pagefragments.TopBarView_BinderImpl_GenCss_style style;
  
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
      case 'style': return this.@com.fave100.client.pagefragments.TopBarView_BinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
