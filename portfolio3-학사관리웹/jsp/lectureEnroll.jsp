<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import= "java.util.ArrayList"%>
<%@ page import= "Course.*" %>
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
	ArrayList<CourseDTO> lectureList=new ArrayList<>();
	CourseDAO courseDAO=new CourseDAO();
	lectureList=courseDAO.getAllLectureList();
	
	ArrayList<CourseDTO> lectureList2=new ArrayList<>();
	CourseDAO courseDAO2=new CourseDAO();
	lectureList2=courseDAO2.getLectureList(userID, userDivision);
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
	<section class="container">
		<div class="row">
			<form action="lectureEnrollAction.jsp" method="post" style="width : 1280px">
			<table class="table table-stiped table-hover" style="text-align : center; Border:1px solid  #dddddd">
				<thead>
					<tr>
						<th style="background-color: #eeeeee; text-align:center;">과목</th>
						<th style="background-color: #eeeeee; text-align:center;">등록</th>
						<th style="background-color: #eeeeee; text-align:center;">취소</th>
					</tr>
				</thead>
				<tbody>
					<%
						for(int i=0;i<lectureList.size();i++){
							CourseDTO course=lectureList.get(i);
					%>
						<tr>
							<td><%=course.getCname() %></td>
							<td><input type='checkbox' name='enrollCid' value=<%=course.getCid() %> /></td>
							<td><input type='checkbox' name='cancelCid' value=<%=course.getCid() %> /></td>
						</tr>
					<%
						}
					%>
				</tbody>
			</table>
				<input type="submit" value="확인" class="btn btn-primary pull-right" >
			</form>
		</div>
		<br>
		<br>
		<br>
		
		<div class="row">
			<table class="table table-stiped table-hover" style="text-align : center; Border:1px solid  #dddddd">
				<thead>
					<tr>
						<th style="background-color: #eeeeee; text-align:center;">현재 수강중인 과목</th>
						
					</tr>
				</thead>
				<tbody>
					<%
						for(int i=0;i<lectureList2.size();i++){
							CourseDTO course=lectureList2.get(i);
					%>
						<tr>
							<td><%=course.getCname() %></td>
						</tr>
					<%
						}
					%>
				</tbody>
			</table>
		</div>
		
	</section>
	
	
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>