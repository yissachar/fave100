package com.fave100.client.pages.myfave100;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class MyFave100View_BinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, com.fave100.client.pages.myfave100.MyFave100View>, com.fave100.client.pages.myfave100.MyFave100View.Binder {

  interface Template extends SafeHtmlTemplates {
    @Template("")
    SafeHtml html1();
     
    @Template("<span id='{0}'></span> <h1>Add songs</h1> <span id='{1}'></span> <span id='{2}'></span>")
    SafeHtml html2(String arg0, String arg1, String arg2);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final com.fave100.client.pages.myfave100.MyFave100View owner) {

    com.fave100.client.pages.myfave100.MyFave100View_BinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (com.fave100.client.pages.myfave100.MyFave100View_BinderImpl_GenBundle) GWT.create(com.fave100.client.pages.myfave100.MyFave100View_BinderImpl_GenBundle.class);
    com.fave100.client.pages.myfave100.MyFave100View_BinderImpl_GenCss_style style = clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style();
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.HTMLPanel topBar = new com.google.gwt.user.client.ui.HTMLPanel(template.html1().asString());
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.SuggestBox itemInputBox = owner.itemInputBox;
    java.lang.String domId2 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.cellview.client.DataGrid faveList = (com.google.gwt.user.cellview.client.DataGrid) GWT.create(com.google.gwt.user.cellview.client.DataGrid.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html2(domId0, domId1, domId2).asString());

    faveList.setStyleName("" + style.faveList() + "");

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    com.google.gwt.user.client.Element domId2Element = com.google.gwt.dom.client.Document.get().getElementById(domId2).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(topBar, domId0Element);
    f_HTMLPanel1.addAndReplaceElement(itemInputBox, domId1Element);
    f_HTMLPanel1.addAndReplaceElement(faveList, domId2Element);


    owner.faveList = faveList;
    owner.topBar = topBar;
    clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style().ensureInjected();

    return f_HTMLPanel1;
  }
}
