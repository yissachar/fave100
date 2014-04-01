package com.fave100.server.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fave100.server.UrlBuilder;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;

@SuppressWarnings("serial")
public class ListRedirectServlet extends HttpServlet {

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse res)
			throws ServletException, IOException {

		int lastSlashPos = req.getRequestURI().lastIndexOf("/");
		if (lastSlashPos < 0)
			res.sendError(404);

		String listName = req.getRequestURI().substring(lastSlashPos + 1, req.getRequestURI().length());
		res.sendRedirect(new UrlBuilder(NameTokens.lists).with(PlaceParams.LIST_PARAM, listName).getUrl());

	}
}
