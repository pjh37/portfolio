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
	
</script>
 
        <script type="text/javascript">
            function setPhoneNumber(val) {
                var numList = val.split("-");
                document.smsForm.sphone1.value = numList[0];
                document.smsForm.sphone2.value = numList[1];
            if(numList[2] != undefined){
                    document.smsForm.sphone3.value = numList[2];
        }
            }

            function loadJSON() {
                var data_file = "/calljson.jsp";
                var http_request = new XMLHttpRequest();
                try {
                    // Opera 8.0+, Firefox, Chrome, Safari
                    http_request = new XMLHttpRequest();
                } catch (e) {
                    // Internet Explorer Browsers
                    try {
                        http_request = new ActiveXObject("Msxml2.XMLHTTP");

                    } catch (e) {

                        try {
                            http_request = new ActiveXObject("Microsoft.XMLHTTP");
                        } catch (e) {
                            // Eror
                            alert("지원하지 않는브라우저!");
                            return false;
                        }

                    }
                }
                http_request.onreadystatechange = function() {
                    if (http_request.readyState == 4) {
                        // Javascript function JSON.parse to parse JSON data
                        var jsonObj = JSON.parse(http_request.responseText);
                        if (jsonObj['result'] == "Success") {
                            var aList = jsonObj['list'];
                            var selectHtml = "<select name=\"sendPhone\" onchange=\"setPhoneNumber(this.value)\">";
                            selectHtml += "<option value='' selected>발신번호를 선택해주세요</option>";
                            for (var i = 0; i < aList.length; i++) {
                                selectHtml += "<option value=\"" + aList[i] + "\">";
                                selectHtml += aList[i];
                                selectHtml += "</option>";
                            }
                            selectHtml += "</select>";
                            document.getElementById("sendPhoneList").innerHTML = selectHtml;
                        }
                    }
                }

                http_request.open("GET", data_file, true);
                http_request.send();
            }
        </script>

</head>
<body onload="loadJSON()">
<%
	String userID=null;
	String userDivision=null;
	ArrayList<StudentDTO> studentList=new ArrayList<>();
	String receivers="";
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
	
	for(int i=0;i<studentList.size();i++){
		StudentDTO studentDTO=studentList.get(i);
		receivers+=studentDTO.getSphone()+",";
	}
	receivers=receivers.substring(0,(receivers.length()-1));
	
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
			 <form method="post" name="smsForm" action="sendSmsAction.jsp">
       		 	<input type="hidden" name="action" value="go">
      		 	<textarea name="msg" class="form-control" cols="30" rows="10" style="width:455px;" placeholder="내용입력"></textarea>
      		 	<br>
      		 	<input type="text" class="form-control" name="rphone" value="<%=receivers %>" placeholder="받는번호">
       		 	<br>
       		 	<input type="hidden" name="sphone1" value="010">
       		 	<input type="hidden" name="sphone2" value="2881">
        		<input type="hidden" name="sphone3" value="8634">
        		<input type="submit" class="btn btn-primary" value="전송">
   			 </form>
		</div>
		<div class="row">
			<table class="table table-stiped table-hover" style="text-align : center; Border:1px solid  #dddddd">
				<thead>
					<tr>
						<th style="background-color: #eeeeee; text-align:center;">No</th>
						<th style="background-color: #eeeeee; text-align:center;">학번</th>
						<th style="background-color: #eeeeee; text-align:center;">이름</th>
						<th style="background-color: #eeeeee; text-align:center;">핸드폰</th>
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
							<td><%=studentDTO.getSid() %></td>
							<td><%=studentDTO.getSname() %> </td>
							<td><%=studentDTO.getSphone() %> </td>
							<td><%=studentDTO.getSemail() %></td>
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
            