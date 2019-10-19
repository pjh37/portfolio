<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import= "java.util.ArrayList"%>
<%@ page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>
<%@ page import="com.oreilly.servlet.MultipartRequest" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import= "Course.*" %>
<%@ page import= "MaterialPost.*" %>
<%@ page import= "User.*" %>
<%@ page import= "File.*" %>
<%@ page import= "DatabaseUtil.*" %>
<% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="css/bootstrap.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.3/umd/popper.min.js"></script>
<title>u-Campus</title>
<style type="text/css">
	a,a:hover{
		color:#000000;
		text-decoration:none;
	}
</style>
<script>
	function submitAction(){
		document.getElementById('postlist').submit();
	}
	function submidControl(index){
		if(index==1){
			document.myform.action="sendEmail.jsp";
		}else if(index=2){
			document.myform.action="sendSms.jsp";
		}
		document.myform.submit();
	}
</script>
</head>

<body>
<%
	String userID=null;
	String userDivision=null;
	String selectedLectureID=null;
	
	if(session.getAttribute("userID")!=null){
		userID=(String)session.getAttribute("userID");
	}
	if(session.getAttribute("userDivision")!=null){
		userDivision=(String)session.getAttribute("userDivision");
	}
	if(request.getParameter("posts")!=null){
		selectedLectureID=request.getParameter("posts");
	}else{
		selectedLectureID="1";
	}
	ArrayList<CourseDTO> lectureList=new ArrayList<>();
	CourseDAO courseDAO=new CourseDAO();
	lectureList=courseDAO.getLectureList(userID, userDivision);
	
	ArrayList<MaterialDTO> materialList=new ArrayList<>();
	MaterialDAO materialDAO=new MaterialDAO();
	materialList=materialDAO.getMaterialList(selectedLectureID);
	UserDAO userDAO=new UserDAO();
	ArrayList<String> sidList=new ArrayList<>();
	sidList=userDAO.sidList(userID,selectedLectureID);
	
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
		<table class="table table-stiped table-hover" style="text-align : left; Border:1px solid  #dddddd">
			<tbody>
				<tr>
					<td>과목정보</td>
				</tr>
				<tr>
					<td style="width: 25%">
						<select class="form-control">
							<option>2019년도 1학기</option>
						</select>
					</td>
					<td style="width: 75%">
						<form method="GET" action="studentMange.jsp" id="postlist">
							<select class="form-control" onChange="submitAction()" name="posts">
							<option selected="selected">과목을 선택해주세요</option>
							
							<%
								for(int i=0;i<lectureList.size();i++){
									CourseDTO course=lectureList.get(i);
									if(selectedLectureID==null){
							%>
									<option  value=<%=course.getCid() %>>[학부]<%=course.getCname() %></option>
							<% 
									continue;
									}
							%>
							<% 
									if(selectedLectureID.equals(course.getCid())){
							%>
									<option selected="selected" value=<%=course.getCid() %>>[학부]<%=course.getCname() %></option>
							<%
									}else{	
							%>
									<option  value=<%=course.getCid() %>>[학부]<%=course.getCname() %></option>
							<%
									}
								}
							%>
							</select>
						</form>
					</td>
				</tr>
			</tbody>
		</table>
			
		</div>
		
		<a role="button" href="write.jsp" class="btn btn-secondary">글쓰기</a>
		
		<br>
		<br>
		<div class="row">
			<form name="myform" method="post" style="width : 1280px" >
			<table class="table table-stiped table-hover" style="text-align : center; Border:1px solid  #dddddd">
				<thead>
					<tr>
						<th style="background-color: #eeeeee; text-align:center;">선택</th>
						<th style="background-color: #eeeeee; text-align:center;">No</th>
						<th style="background-color: #eeeeee; text-align:center;">소속</th>
						<th style="background-color: #eeeeee; text-align:center;">학년</th>
						<th style="background-color: #eeeeee; text-align:center;">학번</th>
						<th style="background-color: #eeeeee; text-align:center;">이름</th>
						<th style="background-color: #eeeeee; text-align:center;">이메일</th>
					</tr>
					
				</thead>
				<tbody>
					
					<%
						//studentList.size()//studentList.get(i);action="sendEmail.jsp"
					
						for(int i=0;i<sidList.size();i++){
							StudentDTO studentDTO=new UserDAO().getStudent(sidList.get(i));
							
					%>
						<tr>
							<td><input type='checkbox' name='sid' value=<%=studentDTO.getSid() %> /></td>
							<td><%=i+1 %></td>
							<td><%=studentDTO.getMajor() %></td>
							<td><%=studentDTO.getGrade() %> </td>
							<td><%=studentDTO.getSid() %> </td>
							<td><%=studentDTO.getSname() %> </td>
							<td><%=studentDTO.getSemail() %></td>
						</tr>
					<% 
						}
					%>
					
				</tbody>
				
			</table>
				
			<input type="button" id="mail" onClick="submidControl(1)" class="btn btn-secondary pull-right"  value="메일쓰기">
			<input type="button" id="SMS" onClick="submidControl(2)" class="btn btn-secondary pull-right"  value="SMS">
			</form>
			
		</div>
		
	</section>
	
	
	
	
	
	
	
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>