package com.fave100.client.pagefragments;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class TopBarView_BinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, com.fave100.client.pagefragments.TopBarView>, com.fave100.client.pagefragments.TopBarView.Binder {

  interface Template extends SafeHtmlTemplates {
    @Template("Fave100")
    SafeHtml html1();
     
    @Template("About")
    SafeHtml html2();
     
    @Template("My Fave100")
    SafeHtml html3();
     
    @Template("<div class='{0}'> <span id='{1}'></span> <ul> <li><span id='{2}'></span></li> <li><span id='{3}'>Log in</span></li> <li><span id='{4}'></span></li> </ul> </div>")
    SafeHtml html4(String arg0, String arg1, String arg2, String arg3, String arg4);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final com.fave100.client.pagefragments.TopBarView owner) {

    com.fave100.client.pagefragments.TopBarView_BinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (com.fave100.client.pagefragments.TopBarView_BinderImpl_GenBundle) GWT.create(com.fave100.client.pagefragments.TopBarView_BinderImpl_GenBundle.class);
    com.fave100.client.pagefragments.TopBarView_BinderImpl_GenCss_style style = clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style();
    com.fave100.client.place.NameTokens nameTokens = (com.fave100.client.place.NameTokens) GWT.create(com.fave100.client.place.NameTokens.class);
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.InlineHyperlink faveLogo = (com.google.gwt.user.client.ui.InlineHyperlink) GWT.create(com.google.gwt.user.client.ui.InlineHyperlink.class);
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.InlineHyperlink f_InlineHyperlink2 = (com.google.gwt.user.client.ui.InlineHyperlink) GWT.create(com.google.gwt.user.client.ui.InlineHyperlink.class);
    com.google.gwt.dom.client.SpanElement logInLink = null;
    java.lang.String domId2 = com.google.gwt.dom.client.Document.get().createUniqueId();
    java.lang.String domId3 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.InlineHyperlink f_InlineHyperlink3 = (com.google.gwt.user.client.ui.InlineHyperlink) GWT.create(com.google.gwt.user.client.ui.InlineHyperlink.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html4("" + style.topNav() + "", domId0, domId1, domId2, domId3).asString());

    faveLogo.setHTML(template.html1().asString());
    faveLogo.setTargetHistoryToken("" + nameTokens.getHome() + "");
    f_InlineHyperlink2.setHTML(template.html2().asString());
    f_InlineHyperlink2.setTargetHistoryToken("" + nameTokens.getAbout() + "");
    f_InlineHyperlink3.setHTML(template.html3().asString());
    f_InlineHyperlink3.setTargetHistoryToken("" + nameTokens.getMyfave100() + "");

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    logInLink = com.google.gwt.dom.client.Document.get().getElementById(domId2).cast();
    logInLink.removeAttribute("id");
    com.google.gwt.user.client.Element domId3Element = com.google.gwt.dom.client.Document.get().getElementById(domId3).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(faveLogo, domId0Element);
    f_HTMLPanel1.addAndReplaceElement(f_InlineHyperlink2, domId1Element);
    f_HTMLPanel1.addAndReplaceElement(f_InlineHyperlink3, domId3Element);


    owner.logInLink = logInLink;
    clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style().ensureInjected();

    return f_HTMLPanel1;
  }
}
