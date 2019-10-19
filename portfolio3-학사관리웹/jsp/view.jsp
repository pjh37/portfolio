<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import= "java.util.ArrayList"%>
<%@ page import="java.io.File" %>
<%@ page import= "MaterialPost.*" %>
<%@ page import= "Course.*" %>
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
<script>
	function submitAction(){
		document.getElementById('postlist').submit();
	}
	
</script>
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
		out.println("<script>");
		out.println("location.href='login.jsp'");
		out.println("</script>");
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
				<li class="nav-item dropdown">
					<a class="nav-link dropdown-toggle" id="dropdown"
					data-toggle="dropdown"> 회원관리 </a>
					<div class="dropdown-menu" aria-labelledby="dropdown">
						<a class="dropdown-item" href="login.jsp">로그인</a>
						<a class="dropdown-item" href="join.jsp">회원가입</a>
					</div>
				</li>
			</ul>
			
		</div>
	</nav>
	<section class="container">
		<div class="row">
		<table class="table table-stiped table-hover" style="text-align : left; Border:1px solid  #dddddd">
			<tbody>
				<tr>
					<td>과목정보:[]</td>
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
							<%
								for(int i=0;i<lectureList.size();i++){
									CourseDTO course=lectureList.get(i);
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
			<table class="table table-stiped table-hover" style="text-align : center; Border:1px solid  #dddddd">
				<thead>
					<tr>
						<th style="background-color: #eeeeee; text-align:center;" colspan="3"><%=materialDTO.getPtitle() %></th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>글번호 : <%=materialDTO.getPid() %></td>
						<td>등록일 : <%=materialDTO.getDate() %></td>
						<td>조회수 : <%=materialDTO.getHit() %></td>
					</tr>
					<tr style="height : 300px;">
						<td colspan="3" style="text-align : left;"><%=materialDTO.getPcontent() %></td>
					</tr>
					<tr>
						<td colspan="3" style="text-align : left;">첨부파일<br>
						<%
							ArrayList<FileDTO> filelist=new FileDAO().getList(materialDTO.getPid());
							String directory=application.getRealPath("/upload/")+materialDTO.getPid();
							String files[]=new File(directory).list();
							for(FileDTO file: filelist){
								out.write("<a href=\""+request.getContextPath()+"/downloadAction?file="+
										java.net.URLEncoder.encode(file.getFileRealName(),"UTF-8")+"&pid="+materialDTO.getPid()+"\">"+file.getFileName()+"</a><br>");
							}
							
						%>		
						</td>
						
					</tr>
				</tbody>
			</table>
				
			<a href="lectureArchives.jsp" class="btn btn-primary pull-right">목록</a>
		</div>
	</section>
	
	
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>