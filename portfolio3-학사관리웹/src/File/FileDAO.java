package File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import DatabaseUtil.DatabaseUtil;
import File.FileDTO;

public class FileDAO {
	
	public int upload(int pid,String fileName,String fileRealName) {
		String SQL="insert into file values(?,?,?)";
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try {
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(SQL);
			pstmt.setInt(1, pid);
			pstmt.setString(2, fileName);
			pstmt.setString(3, fileRealName);
			return pstmt.executeUpdate();
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
	public ArrayList<FileDTO> getList(int pid){
		String SQL="SELECT * FROM file where pid="+"'"+pid+"'";
		Connection conn=null;
		ArrayList<FileDTO> list=new ArrayList<>();
		try {
			conn=DatabaseUtil.getConnection();
			PreparedStatement pstmt=conn.prepareStatement(SQL);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()) {
				FileDTO file=new FileDTO(rs.getString(1),rs.getString(2),rs.getString(3));
				list.add(file);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
}
