<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import= "java.util.ArrayList"%>
<%@ page import="java.io.File" %>
<%@ page import= "MaterialPost.*" %>
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
	String pid=null;
	String selectedLectureID=null;
	if(session.getAttribute("userID")!=null){
		userID=(String)session.getAttribute("userID");
	}
	if(userID==null){
		PrintWriter script=response.getWriter();
		script.println("<script>");
		script.println("<alert('로그인을 해주세요')>");
		script.println("location.href='userLogin.jsp'");
		script.println("</script>");
		script.close();
		return;
	}
	if(session.getAttribute("userDivision")!=null){
		userDivision=(String)session.getAttribute("userDivision");
	}
	if(request.getParameter("pid")!=null){
		pid=request.getParameter("pid");
	}
	if(request.getParameter("cid")!=null){
		selectedLectureID=request.getParameter("cid");
	}
	ArrayList<CourseDTO> lectureList=new ArrayList<>();
	CourseDAO courseDAO=new CourseDAO();
	lectureList=courseDAO.getLectureList(userID, userDivision);
	
	MaterialDTO materialDTO=new MaterialDAO().getPostContent(pid);
	
%>
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
						<form method="GET" action="lectureArchives.jsp?cid<%=selectedLectureID %>" id="postlist">
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
	<div class="row">
		<form action="writeAction.jsp" method="post" enctype="multipart/form-data">
			<table class="table table-stiped table-hover" style="text-align : center; Border:1px solid  #dddddd">
				<thead>
					<%
						session.setAttribute("cid", selectedLectureID);
					%>
					<tr>
						<td style="width : 1280px;"><input type="text" class="form-control" placeholder="글 제목" name="pTitle" maxlength="50" > </td>
					</tr>
				</thead>
				<tbody>
					
					<tr>
						<td><textarea class="form-control" placeholder="글 내용" name="pContent" maxlength="4096" style="height:350px;" ></textarea> </td>
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
					<tr>
						<td><input type="file" class="form-control" name="file4"> </td>
					</tr>
					<tr>
						<td><input type="file" class="form-control" name="file5"> </td>
					</tr>
				</tbody>
			</table>
			<input type="submit" class="btn btn-primary pull-right" value="글쓰기">
		</form>
	</div>
	</section>
</body>
</html>