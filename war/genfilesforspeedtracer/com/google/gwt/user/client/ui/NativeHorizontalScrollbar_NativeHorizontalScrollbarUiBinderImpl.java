package com.google.gwt.user.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.dom.client.Element;

public class NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl implements UiBinder<com.google.gwt.dom.client.Element, com.google.gwt.user.client.ui.NativeHorizontalScrollbar>, com.google.gwt.user.client.ui.NativeHorizontalScrollbar.NativeHorizontalScrollbarUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div class='{0}'><div class='{1}' id='{2}'> <div class='{3}' id='{4}'></div> </div></div>")
    SafeHtml html1(String arg0, String arg1, String arg2, String arg3, String arg4);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.dom.client.Element createAndBindUi(final com.google.gwt.user.client.ui.NativeHorizontalScrollbar owner) {

    com.google.gwt.user.client.ui.NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (com.google.gwt.user.client.ui.NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenBundle) GWT.create(com.google.gwt.user.client.ui.NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenBundle.class);
    com.google.gwt.user.client.ui.NativeHorizontalScrollbar_NativeHorizontalScrollbarUiBinderImpl_GenCss_style style = clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style();
    com.google.gwt.dom.client.DivElement scrollable = null;
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.dom.client.DivElement contentDiv = null;
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.dom.client.DivElement f_div1 = (com.google.gwt.dom.client.DivElement) UiBinderUtil.fromHtml(template.html1("" + style.viewport() + "", "" + style.scrollable() + "", domId0, "" + style.content() + "", domId1).asString());


    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_div1);
    scrollable = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    scrollable.removeAttribute("id");
    contentDiv = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    contentDiv.removeAttribute("id");
    attachRecord0.detach();


    owner.contentDiv = contentDiv;
    owner.scrollable = scrollable;
    clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style().ensureInjected();

    return f_div1;
  }
}
