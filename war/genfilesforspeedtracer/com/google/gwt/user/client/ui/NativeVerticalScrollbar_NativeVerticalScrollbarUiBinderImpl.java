package com.google.gwt.user.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.dom.client.Element;

public class NativeVerticalScrollbar_NativeVerticalScrollbarUiBinderImpl implements UiBinder<com.google.gwt.dom.client.Element, com.google.gwt.user.client.ui.NativeVerticalScrollbar>, com.google.gwt.user.client.ui.NativeVerticalScrollbar.NativeVerticalScrollbarUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div class='{0}'><div class='{1}' id='{2}'> <div id='{3}'></div> </div></div>")
    SafeHtml html1(String arg0, String arg1, String arg2, String arg3);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.dom.client.Element createAndBindUi(final com.google.gwt.user.client.ui.NativeVerticalScrollbar owner) {

    com.google.gwt.user.client.ui.NativeVerticalScrollbar_NativeVerticalScrollbarUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (com.google.gwt.user.client.ui.NativeVerticalScrollbar_NativeVerticalScrollbarUiBinderImpl_GenBundle) GWT.create(com.google.gwt.user.client.ui.NativeVerticalScrollbar_NativeVerticalScrollbarUiBinderImpl_GenBundle.class);
    com.google.gwt.user.client.ui.NativeVerticalScrollbar_NativeVerticalScrollbarUiBinderImpl_GenCss_style style = clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style();
    com.google.gwt.dom.client.DivElement scrollable = null;
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.dom.client.DivElement contentDiv = null;
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.dom.client.DivElement f_div1 = (com.google.gwt.dom.client.DivElement) UiBinderUtil.fromHtml(template.html1("" + style.viewport() + "", "" + style.scrollable() + "", domId0, domId1).asString());


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
