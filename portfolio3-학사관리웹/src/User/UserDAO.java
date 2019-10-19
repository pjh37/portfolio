package User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import DatabaseUtil.DatabaseUtil;

public class UserDAO {
	//로그인 데이터 접근
	public int login(String id,String password,String userDivision) {
		String SQL=null;
		if(userDivision.equals("student")) {
			SQL="SELECT spw FROM "+ userDivision +" WHERE sid=?";
		}else {
			SQL="select pw from "+ userDivision +" where prid=?";
		}
		
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try {
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			pstmt.setString(1, id);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				if(rs.getString(1).equals(password)) {
					return 1;//로그인성공
				}else {
					return 0;//로그인 실패
				}
			}
			return -2;//데이터 없음
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(conn!=null)conn.close();
				if(pstmt!=null)pstmt.close();
				if(rs!=null)rs.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return -2; //데이터베이스 오류		
	}
	//학생 or 교수의 자신이 수강하는 or 강의하는 목록 불러오기
	public ArrayList<StudentDTO> getStudentList(String prid,String cid){
		String SQL="select * from student where sid=any(select sid from enroll where cid=any(select cid from course where prid=?) and cid=?);";
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ArrayList<StudentDTO> studentList=new ArrayList<>();
		try {
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			pstmt.setString(1, prid);
			pstmt.setString(2, cid);
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				StudentDTO studentDTO=new StudentDTO();
				studentDTO.setSid(rs.getString(1));
				studentDTO.setSpw(rs.getString(2));
				studentDTO.setSname(rs.getString(3));
				studentDTO.setSemail(rs.getString(4));
				studentDTO.setMajor(rs.getString(5));
				studentDTO.setSphone(rs.getString(6));
				studentDTO.setGrade(rs.getString(7));
				studentList.add(studentDTO);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(conn!=null)conn.close();
				if(pstmt!=null)pstmt.close();
				if(rs!=null)rs.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return studentList;
	}
	public StudentDTO getStudent(String sid) {
		String SQL="select * from student where sid=?";
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StudentDTO studentDTO=new StudentDTO();
		try {
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			pstmt.setString(1, sid);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				studentDTO.setSid(rs.getString(1));
				studentDTO.setSpw(rs.getString(2));
				studentDTO.setSname(rs.getString(3));
				studentDTO.setSemail(rs.getString(4));
				studentDTO.setMajor(rs.getString(5));
				studentDTO.setSphone(rs.getString(6));
				studentDTO.setGrade(rs.getString(7));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(conn!=null)conn.close();
				if(pstmt!=null)pstmt.close();
				if(rs!=null)rs.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return studentDTO;
	}
	public ArrayList<String> sidList(String prid,String cid){
		String SQL="select sid from enroll where cid=any(select cid from course where prid=?) and cid=?";
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ArrayList<String> list=new ArrayList<String>();
		try {
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			pstmt.setString(1, prid);
			pstmt.setString(2, cid);
			rs=pstmt.executeQuery();
			while(rs.next()) {
				list.add(rs.getString(1));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(conn!=null)conn.close();
				if(pstmt!=null)pstmt.close();
				if(rs!=null)rs.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	public void enroll(String sid,String cid) {
		String SQL="insert into enroll values(?,?)";
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try {
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			pstmt.setString(1, sid);
			pstmt.setString(2, cid);
			pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(conn!=null)conn.close();
				if(pstmt!=null)pstmt.close();
				if(rs!=null)rs.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void cancel(String sid,String cid) {
		String SQL="delete from enroll where sid=? and cid=?";
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try {
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			pstmt.setString(1, sid);
			pstmt.setString(2, cid);
			pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(conn!=null)conn.close();
				if(pstmt!=null)pstmt.close();
				if(rs!=null)rs.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
