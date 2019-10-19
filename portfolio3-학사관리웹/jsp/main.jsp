<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter"%>
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
%>
	<nav class="navbar navbar-expand-lg navbar-light bg-light">
		<a class="navbar-brand" href="index.jsp">유캠퍼스</a>
		<button class="navbar-toggler" type="button" data-toggle="collapse"
			data-target="#navbar">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div id="navbar" class="collapse navbar-collapse">
			<ul class="navbar-nav mr-auto">
				<li class="nav-item active">
					<a class="nav-link" href="index.jsp">메인</a>
				</li>
				<li class="nav-item">
					<a class="nav-link" href="lectureArchives.jsp">강의 자료실</a>
				</li>
				<%
					if(userDivision.equals("professor")){
				%>
					<li class="nav-item">
						<a class="nav-link" href="studentMange.jsp">수강생관리</a>
					</li>
				<%
					}else{
				%>
					<li class="nav-item">
						<a class="nav-link" href="lectureEnroll.jsp">강의등록</a>
					</li>	
				<%
					}
				%>
				<li class="nav-item">
					<a class="nav-link" href="logoutAction.jsp">로그아웃</a>
				</li>
				<li class="nav-item">
					<a class="nav-link" href="*"><%=userDivision %></a>
				</li>
			</ul>
			
		</div>
	</nav>
	
	
	
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>