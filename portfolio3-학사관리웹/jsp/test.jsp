<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="org.json.simple.*" %>
<%
		String name=null;
		String email=null;
		String content=null;
		name=request.getParameter("name");
		email=request.getParameter("email");
		content=request.getParameter("content");
		JSONObject jsonMain=new JSONObject();
		
		jsonMain.put("name",name);
		jsonMain.put("email",email);
		jsonMain.put("content",content);
		
		
		out.print(jsonMain);
		out.flush();
%>