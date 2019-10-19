<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import= "java.util.ArrayList"%>
<%@ page import= "Course.*" %>
<%@ page import= "User.*" %>
<% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="css/bootstrap.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.3/umd/popper.min.js"></script>
<title>u-Campus</title>
</head>
<body>
<%
	String userID=null;
	String userDivision=null;
	if(session.getAttribute("userID")!=null){
		userID=(String)session.getAttribute("userID");
	}else{
		response.sendRedirect("login.jsp");
	}
	if(session.getAttribute("userDivision")!=null){
		userDivision=(String)session.getAttribute("userDivision");
	}
	try{
		if(request.getParameterValues("enrollCid")!=null){
			String[] enrollCid=request.getParameterValues("enrollCid");
			for(int i=0;i<enrollCid.length;i++){
				new UserDAO().enroll(userID,enrollCid[i]);
			}
		}
		if(request.getParameterValues("cancelCid")!=null){
			String[] cancelCid=request.getParameterValues("cancelCid");
			for(int i=0;i<cancelCid.length;i++){
				new UserDAO().cancel(userID,cancelCid[i]);
			}
		}
		out.println("<script>");
		response.sendRedirect("lectureEnroll.jsp");
		out.println("</script>");
	}catch(Exception error){
		out.println("<script>");
		out.println("alert('메일전송할 학생을 체크하십시오')");
		out.println("history.back()");
		out.println("</script>");
	}
	
%>
	
	
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>