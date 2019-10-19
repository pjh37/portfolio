<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import= "java.util.ArrayList"%>
<%@ page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>
<%@ page import="com.oreilly.servlet.MultipartRequest" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Enumeration" %>
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
<body>
<%
	int pid=new MaterialDAO().getNext();
	String directory=application.getRealPath("/upload/")+pid+"";
	String savePath=directory;
	File targetDir=new File(savePath);
	if(!targetDir.exists()){
		targetDir.mkdirs();
	}
	int maxSize=1024*1024*100;
	String encoding="UTF-8";
	MultipartRequest multipartRequest=new MultipartRequest(request,directory,maxSize,encoding,
		new DefaultFileRenamePolicy());
	Enumeration fileNames=multipartRequest.getFileNames();
	
	while(fileNames.hasMoreElements()){
		String parameter=(String)fileNames.nextElement();;
		String fileName=multipartRequest.getOriginalFileName(parameter);
		String fileRealName=multipartRequest.getFilesystemName(parameter);
		if(fileName==null)continue;
		new FileDAO().upload(pid, fileName, fileRealName);
	}
	
	String userID=null;
	String title=null;
	String content=null;
	String cid=null;
	if(session.getAttribute("userID")!=null){
		userID=(String)session.getAttribute("userID");
	}
	if(userID==null){
		out.println("<script>");
		out.println("location.href='login.jsp'");
		out.println("</script>");
	}else{
		if(multipartRequest.getParameter("pTitle")==null){
			out.println("<script>");
			out.println("alert('입력이 안 된 사항이 있습니다.')");
			out.println("history.back()");
			out.println("</script>");
		}else{
			MaterialDAO materialDAO=new MaterialDAO();
			title=multipartRequest.getParameter("pTitle");
			cid=(String)session.getAttribute("cid");
			content=multipartRequest.getParameter("pContent");
			int result=materialDAO.write(title, content, cid);
			if(result==-1){
				out.println("<script>");
				out.println("alert('글쓰기에 실패했습니다.')");
				out.println("history.back()");
				out.println("</script>");
			}else{
				out.println("<script>");
				out.println("location.href='lectureArchives.jsp'");
				out.println("</script>");
			}
		}
	}
%>

</body>
</html>