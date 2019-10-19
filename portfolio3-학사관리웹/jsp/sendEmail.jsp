<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import= "java.util.ArrayList"%>
<%@ page import="javax.mail.Transport" %>
<%@ page import="javax.mail.Message" %>
<%@ page import="javax.mail.Address" %>
<%@ page import="javax.mail.internet.InternetAddress" %>
<%@ page import="javax.mail.internet.MimeMessage" %>
<%@ page import="javax.mail.Session" %>
<%@ page import="javax.mail.Authenticator" %>
<%@ page import="java.util.Properties" %>
<%@ page import="DatabaseUtil.Gmail" %>
<%@ page import="java.io.File" %>
<%@ page import= "MaterialPost.*" %>
<%@ page import= "Course.*" %>
<%@ page import= "User.*" %>
<%@ page import= "File.*" %>
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
	ArrayList<StudentDTO> studentList=new ArrayList<>();
	try{
		String[] sid=request.getParameterValues("sid");
		
		for(int i=0;i<sid.length;i++){
			StudentDTO studentDTO=new UserDAO().getStudent(sid[i]);
			studentList.add(studentDTO);
		}
		if(session.getAttribute("userID")!=null){
			userID=(String)session.getAttribute("userID");
		}
		if(session.getAttribute("userDivision")!=null){
			userDivision=(String)session.getAttribute("userDivision");
		}
	}catch(Exception error){
		out.println("<script>");
		out.println("alert('메일전송할 학생을 체크하십시오')");
		out.println("history.back()");
		out.println("</script>");
	}
	
	
%>
	<section class="container">
		
	<div class="row">
		<form action="emailSendAction.jsp" method="post" enctype="multipart/form-data">
			<table class="table table-stiped table-hover" style="text-align : center; Border:1px solid  #dddddd">
				<thead>
					<tr>
						<td style="width : 1280px;"><input type="text" class="form-control" placeholder="Title" name="pTitle" maxlength="50" > </td>
					</tr>
				</thead>
				<tbody>
					
					<tr>
						<td><textarea class="form-control" placeholder="Content" name="pContent" maxlength="4096" style="height:350px;" ></textarea> </td>
					</tr>
					<tr>
						<td><input type="file" class="form-control" name="file1"> </td>
					</tr>
					<tr>
						<td><input type="file" class="form-control" name="file2"> </td>
					</tr>
					<tr>
						<td><input type="file" class="form-control" name="file3"> </td>
					</tr>
					
				</tbody>
			</table>
			<%
				for(int i=0;i<studentList.size();i++){
					StudentDTO studentDTO=studentList.get(i);
			%>
					<input type="hidden" name="userEmail" value=<%=studentDTO.getSemail() %>>
					
			<%
				}
			%>
			<input type="submit" class="btn btn-primary pull-right" value="메일전송">
		</form>
	</div>
	<br>
	<div class="row">
			<table class="table table-stiped table-hover" style="text-align : center; Border:1px solid  #dddddd">
				<thead>
					<tr>
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
						for(int i=0;i<studentList.size();i++){
							StudentDTO studentDTO=studentList.get(i);
					%>
						<tr>
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
		</div>
			
	</section>
</body>
</html>