package File;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/downloadAction")
public class downloadAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid=request.getParameter("pid");
		String fileName=request.getParameter("file");
		String directory=this.getServletContext().getRealPath("/upload/")+pid;
		File file=new File(directory+"/"+fileName);
		String mimeType=getServletContext().getMimeType(file.toString());
		if(mimeType==null) {
			response.setContentType("application/octet-stream");
		}
		String downloadName=null;
		if(request.getHeader("user-agent").indexOf("MSIE")==-1) {
			downloadName=new String(fileName.getBytes("UTF-8"),"8859_1");
		}else{
			downloadName=new String(fileName.getBytes("EUC-KR"),"8859_1");
		}
		response.setHeader("Content-Disposition", "attachment;filename=\""+
		downloadName+"\";");
		FileInputStream fileInputStream=new FileInputStream(file);
		ServletOutputStream so=response.getOutputStream();
		byte[] bytes=new byte[1024];
		int data=0;
		while((data=(fileInputStream.read(bytes,0,bytes.length)))!=-1) {
			so.write(bytes,0,data);
		}
		
		so.flush();
		so.close();
		fileInputStream.close();
	}

	

}
