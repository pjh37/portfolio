<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ page import="User.UserDAO" %>
 <%@ page import="java.io.PrintWriter" %>
 <% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%
		String userID=null;
		String userPassword=null;
		String userDivision=null;
		if(session.getAttribute("userID")!=null){
			userID=(String)session.getAttribute("userID");
		}
		
		if(request.getParameter("userID")!=null){
			userID=request.getParameter("userID");
		}
		if(request.getParameter("userPassword")!=null){
			userPassword=request.getParameter("userPassword");
		}
		if(request.getParameter("userDivision")!=null){
			userDivision=request.getParameter("userDivision");
		}
		UserDAO userDAO=new UserDAO();
		int result=userDAO.login(userID, userPassword,userDivision);
		if(result==1){
			session.setAttribute("userID", userID);
			session.setAttribute("userDivision", userDivision);
			PrintWriter script=response.getWriter();
			script.println("<script>");
			script.println("location.href='main.jsp'");
			script.println("</script>");
		}else if(result==0){
			PrintWriter script=response.getWriter();
			script.println("<script>");
			script.println("alert('비밀번호가 틀립니다')");
			script.println("history.back()");
			script.println("</script>");
		}else if(result==-1){
			PrintWriter script=response.getWriter();
			script.println("<script>");
			script.println("alert('아이디가 틀립니다')");
			script.println("history.back()");
			script.println("</script>");
		}else if(result==-2){
			PrintWriter script=response.getWriter();
			script.println("<script>");
			script.println("alert('데이터베이스 오류가 발생했습니다')");
			script.println("history.back()");
			script.println("</script>");
		}
	%>
</body>
</html>