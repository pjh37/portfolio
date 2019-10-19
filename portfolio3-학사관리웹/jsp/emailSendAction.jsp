<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import= "java.util.ArrayList"%>
<%@ page import="javax.activation.DataHandler"%>
<%@ page import="javax.activation.FileDataSource"%>
<%@ page import="javax.activation.DataSource"%>
<%@ page import="javax.mail.Transport" %>
<%@ page import="javax.mail.Message" %>
<%@ page import="javax.mail.Multipart" %>
<%@ page import="javax.mail.Address" %>
<%@ page import="javax.mail.internet.InternetAddress" %>
<%@ page import="javax.mail.internet.MimeMessage" %>
<%@ page import="javax.mail.internet.MimeBodyPart" %>
<%@ page import="javax.mail.internet.MimeMultipart" %>
<%@ page import="javax.mail.Session" %>
<%@ page import="javax.mail.Authenticator" %>
<%@ page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>
<%@ page import="com.oreilly.servlet.MultipartRequest" %>
<%@ page import="java.util.Properties" %>
<%@ page import="DatabaseUtil.Gmail" %>
<%@ page import="java.io.File" %>
<%@ page import= "Course.*" %>
<%@ page import= "MaterialPost.*" %>
<%@ page import= "User.*" %>
<%@ page import="java.util.Enumeration" %>
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
	String directory=application.getRealPath("/upload/");
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
		
		
		
	String userID=null;
	String userEmail[]=multipartRequest.getParameterValues("userEmail");
	
	String title=null;
	String contents=null;
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
	if(multipartRequest.getParameter("pTitle")!=null){
		title=multipartRequest.getParameter("pTitle");
	}
	if(multipartRequest.getParameter("pContent")!=null){
		contents=multipartRequest.getParameter("pContent");
	}
	request.setCharacterEncoding("UTF-8");
	String host="http://localhost/jjjj1352/";
	String from="epictroll2018@gmail.com";
	for(int i=0;i<userEmail.length;i++){
		
		String to=userEmail[i];
		String subject=title;
		String content=contents;
		
		Properties p=new Properties();
		p.put("mail.smtp.user",from);
		p.put("mail.smtp.host","smtp.googlemail.com");
		p.put("mail.smtp.port","465");
		p.put("mail.smtp.starttls.enable","true");
		p.put("mail.smtp.auth","true");
		p.put("mail.smtp.debug","true");
		p.put("mail.smtp.socketFactory.port","465");
		p.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		p.put("mail.smtp.socketFactory.fallback","false");
		
		try{
			Authenticator auth=new Gmail();
			Session ses=Session.getInstance(p,auth);
			ses.setDebug(true);
			MimeMessage msg=new MimeMessage(ses);
			msg.setSubject(subject);
			Address fromAddr=new InternetAddress(from);
			msg.setFrom(fromAddr);
			Address toAddr=new InternetAddress(to);
			msg.addRecipient(Message.RecipientType.TO, toAddr);
			//msg.setContent(content,"text/html;charset=UTF-8");
			//
			MimeMultipart multipart = new MimeMultipart();
			MimeBodyPart bodyPart=null;
			
			while(fileNames.hasMoreElements()){
				String parameter=(String)fileNames.nextElement();;
				String fileName=multipartRequest.getOriginalFileName(parameter);
				String fileRealName=multipartRequest.getFilesystemName(parameter);
				if(fileName==null)continue;
				bodyPart=new MimeBodyPart();
				DataSource fds=new FileDataSource(savePath+"/"+fileName);
				bodyPart.setDataHandler(new DataHandler(fds));
				bodyPart.setFileName(fileName);
				multipart.addBodyPart(bodyPart);
			}	
			
			bodyPart=new MimeBodyPart();
			bodyPart.setContent(content,"text/html;charset=UTF-8");
			multipart.addBodyPart(bodyPart);
			msg.setContent(multipart);
			//
			Transport.send(msg);
			
		}catch(Exception e){
			e.printStackTrace();
			PrintWriter script=response.getWriter();
			script.println("<script>");
			script.println("alert('오류가 발생했습니다');");
			script.println("history.back();");
			script.println("</script>");
			script.close();
			return;
		}
	}
	out.println("<script>");
	out.println("location.href='studentMange.jsp'");
	out.println("</script>");
	
	%>



	
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>