<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="css/bootstrap.css">
<title>u-Campus</title>
</head>
<body>
<%
	String userID=null;
	String userDivision=null;
	if(session.getAttribute("userID")!=null){
		userID=(String)session.getAttribute("userID");
	}
	if(session.getAttribute("userDivision")!=null){
		userDivision=(String)session.getAttribute("userDivision");
	}
	if(userID!=null){
		out.println("<script>");
		out.println("location.href='main.jsp';");
		out.println("</script>");
	}
	
%>
	<script>
		location.href="login.jsp";
	</script>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>