package MaterialPost;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Course.CourseDTO;
import DatabaseUtil.DatabaseUtil;

public class MaterialDAO {
	//강의자료 목록 가져오기
	public ArrayList<MaterialDTO> getMaterialList(String cid){
		String SQL="select * from materialpost as m, course as c where m.cid=c.cid and c.cid=?";
		ArrayList<MaterialDTO> list=new ArrayList<>();
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			pstmt.setString(1,cid);
			rs=pstmt.executeQuery();
			while(rs.next()) {
				MaterialDTO materialDTO=new MaterialDTO();
				materialDTO.setPid(rs.getInt(1));
				materialDTO.setPtitle(rs.getString(2));
				materialDTO.setPcontent(rs.getString(3));
				materialDTO.setHit(rs.getInt(4));
				materialDTO.setDate(rs.getString(5));
				materialDTO.setCid(rs.getString(6));
				list.add(materialDTO);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public MaterialDTO getPostContent(String pid) {
		String SQL="select * from materialpost where pid=?";
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		MaterialDTO materialDTO=new MaterialDTO();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			pstmt.setString(1,pid);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				materialDTO.setPid(rs.getInt(1));
				materialDTO.setPtitle(rs.getString(2));
				materialDTO.setPcontent(rs.getString(3));
				materialDTO.setHit(rs.getInt(4));
				materialDTO.setDate(rs.getString(5));
				materialDTO.setCid(rs.getString(6));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return materialDTO;
	}
	public int getNext() {
		String SQL="SELECT pid FROM materialpost ORDER BY pid DESC";
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			
			rs=pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1)+1;
			}
			return 1;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public String getDate() {
		String SQL="SELECT NOW()";
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				return rs.getString(1);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public int write(String title,String content,String cid) {
		String SQL="INSERT INTO materialpost VALUES (?,?,?,?,?,?)";
		Connection conn=null;
		PreparedStatement pstmt=null;
		
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext());
			pstmt.setString(2, title);
			pstmt.setString(3, content);
			pstmt.setInt(4, 0);
			pstmt.setString(5, getDate());
			pstmt.setString(6, cid);
			
			return pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
