package com.fave100.client.pagefragments;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Window;

public class SideNotification {
	
	public static void show(String message) {
		SideNotification.show(message, false);
	}
	
	public static void show(String message, boolean error) {
		int delayTime = error ? 1500 : 900;
		SideNotification.show(message, error, delayTime);		
	}
	
	public static void show(String message, boolean error, int delay) {
		// TODO: Undo link
		GQuery $sideNotification = $(".sideNotification");
		if($sideNotification.length() == 0) {						
			$("<div>/div>").insertAfter($("div").first()).addClass("sideNotification");
			$sideNotification = $(".sideNotification");
		} 
		if(error) {
			$sideNotification.addClass("error");
		} else {
			$sideNotification.removeClass("error");
		}
		$sideNotification.text(message);
		
		$sideNotification.css("top", Window.getScrollTop()+80+"px");
		final int alertWidth = $sideNotification.outerWidth();
		$sideNotification.css("left",-alertWidth+"px");
		$sideNotification.animate("left:" + "-1px", 300).delay(delay, new Function() {
			public void f() {
				$(".sideNotification").animate("left:" + -alertWidth+"px", 300);
			}
		});
	}

}
