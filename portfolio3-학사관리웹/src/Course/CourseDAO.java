package Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import DatabaseUtil.DatabaseUtil;


public class CourseDAO {
	public ArrayList<CourseDTO> getLectureList(String id,String userDivision){
		String SQL="";
		if(userDivision.equals("student")) {
			SQL="select c.cid,c.cname from enroll as e, course as c where e.cid=c.cid and e.sid=?";
			//SQL="select cname from course where cid=?";
		}else {
			SQL="select c.cid,c.cname from teach as t, course as c where t.cid=c.cid and t.prid=?";
		}
		
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		ArrayList<CourseDTO> list=new ArrayList<>();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			pstmt.setString(1, id);
			rs=pstmt.executeQuery();
			while(rs.next()) {
				CourseDTO courseDTO=new CourseDTO();
				courseDTO.setCid(rs.getString(1));
				courseDTO.setCname(rs.getString(2));
				
				list.add(courseDTO);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList<CourseDTO> getAllLectureList(){
		String SQL="select cid,cname from course";
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ArrayList<CourseDTO> list=new ArrayList<>();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			rs=pstmt.executeQuery();
			while(rs.next()) {
				CourseDTO courseDTO=new CourseDTO();
				courseDTO.setCid(rs.getString(1));
				courseDTO.setCname(rs.getString(2));
				list.add(courseDTO);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
}
