package com.myoungwon.web.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.myoungwon.web.entity.Notice;
import com.myoungwon.web.entity.NoticeView;

public class NoticeService {
	
	public int removeNoticeAll(int[] ids){
		
		return 0;
	}
	public int pubNoticeAll(int[] oids, int[] cids){
		List<String> oidsList = new ArrayList<>();
		for(int i=0; i<oids.length; i++) {
			oidsList.add(String.valueOf(oids[i]));
		}
		List<String> cidsList = new ArrayList<>();
		for(int i=0; i<cids.length; i++) {
			cidsList.add(String.valueOf(cids[i]));
		}
				
		return pubNoticeAll(oidsList, cidsList);
	}
	public int pubNoticeAll(List<String> oids, List<String> cids){
		
		String oidsCSV = String.join(",", oids);
		String cidsCSV = String.join(",", cids);
		
		return pubNoticeAll(oidsCSV, cidsCSV);
	}
	public int pubNoticeAll(String oidsCSV, String cidsCSV){
		int result = 0;
		String sqlOpen = String.format("update notice set pub=1 where id in (%s)", oidsCSV);
		String sqlClose = String.format("update notice set pub=0 where id in (%s)", cidsCSV);
		
		String url = "jdbc:oracle:thin:@192.168.0.5:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "12345");
			Statement stOpen = con.createStatement();
			result += stOpen.executeUpdate(sqlOpen);
			
			Statement stClose = con.createStatement();
			result += stClose.executeUpdate(sqlClose);
			
			stOpen.close();
			stClose.close();
			con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	public int insertNotice(Notice notice){
		int result = 0;
		
		String sql = "insert into notice(title, content, writer_id, pub, files, regdate) values(?, ?, ?, ?, ?, sysdate)";
		
		String url = "jdbc:oracle:thin:@192.168.0.5:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "12345");
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, notice.getTitle());
			pstmt.setString(2, notice.getContent());
			pstmt.setString(3, notice.getWriterId());
			pstmt.setBoolean(4, notice.getPub());
			pstmt.setString(5, notice.getFiles());
			
			result = pstmt.executeUpdate();
			
			pstmt.close();
			con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	public int deleteNotice(int id){
		
		return 0;
	}
	public int updateNotice(Notice notice){
		
		return 0;
	}
	public List<Notice> getNoticeNewestList(){
		
		return null;
	}
	
	public List<NoticeView> getNoticeList(){
		return getNoticeList("title", "", 1);
	}
	public List<NoticeView> getNoticeList(int page){
		return getNoticeList("title", "", page);
	}
	public List<NoticeView> getNoticeList(String field/*title, writer_id*/, String query/*A*/, int page){
		List<NoticeView> list = new ArrayList<>();

		String sql = "select * from (" + 
				"    select rownum num, n.* " + 
				"    from (select * from notice_view where " + field + " like ? order by regdate desc) n " + 
				") " + 
				"where num between ? and ?";
		// 1, 11, 21, 31,,,,, -> an = 1 + (page - 1) * 10
		// 10, 20, 30, 40,,,,,-> page * 10
		
		String url = "jdbc:oracle:thin:@192.168.0.5:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "12345");
			PreparedStatement pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, "%" + query + "%");
			pstmt.setInt(2, 1 + (page - 1) * 10);
			pstmt.setInt(3, page * 10);
			
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()){				
				int id = rs.getInt("id");	
				String title = rs.getString("title");
				Date regdate = rs.getDate("regdate");
				String writerId = rs.getString("writer_id");
				int hit = rs.getInt("hit");
				String files = rs.getString("files");
				boolean pub = rs.getBoolean("pub");
				int cmtCount = rs.getInt("cmt_count");
				
				// 인자 순서 주의 
				NoticeView notice = new NoticeView(id, title, regdate, writerId, hit, files,/*comment,*/pub, cmtCount);
				list.add(notice);
			}
				rs.close();
				pstmt.close();
				con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<NoticeView> getNoticePubList(String field, String query, int page) {
		List<NoticeView> list = new ArrayList<>();

		String sql = "select * from (" + 
				"    select rownum num, n.* " + 
				"    from (select * from notice_view where " + field + " like ? order by regdate desc) n " + 
				") " + 
				"where pub=1 and num between ? and ?";
		// 1, 11, 21, 31,,,,, -> an = 1 + (page - 1) * 10
		// 10, 20, 30, 40,,,,,-> page * 10
		
		String url = "jdbc:oracle:thin:@192.168.0.5:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "12345");
			PreparedStatement pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, "%" + query + "%");
			pstmt.setInt(2, 1 + (page - 1) * 10);
			pstmt.setInt(3, page * 10);
			
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()){				
				int id = rs.getInt("id");	
				String title = rs.getString("title");
				Date regdate = rs.getDate("regdate");
				String writerId = rs.getString("writer_id");
				int hit = rs.getInt("hit");
				String files = rs.getString("files");
				boolean pub = rs.getBoolean("pub");
				int cmtCount = rs.getInt("cmt_count");
				
				// 인자 순서 주의 
				NoticeView notice = new NoticeView(id, title, regdate, writerId, hit, files,/*comment,*/pub, cmtCount);
				list.add(notice);
			}
				rs.close();
				pstmt.close();
				con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;	
	}
	
	public int getNoticeCount() {
		return getNoticeCount("title", "");
	}
	public int getNoticeCount(String field, String query) {
		
		int count = 0;
		
		String sql = "select count(id) count from ("
					+ "	select rownum num, n.* "
					+ " from (select * from notice where "+field+" like ? order by regdate desc) n "  
					+ ") ";
		
		String url = "jdbc:oracle:thin:@192.168.0.5:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "12345");
			PreparedStatement pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, "%"+query+"%");
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {				
				count = rs.getInt("count");
			}
			
			rs.close();
			pstmt.close();
			con.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public Notice getNotice(int id) {
		Notice notice = null;
		
		String sql = "select * from notice where id=?";
		
		String url = "jdbc:oracle:thin:@192.168.0.5:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "12345");
			PreparedStatement pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()){				
				int nid = rs.getInt("id");	
				String title = rs.getString("title");
				Date regdate = rs.getDate("regdate");
				String writerId = rs.getString("writer_id");
				int hit = rs.getInt("hit");
				String files = rs.getString("files");
				String content = rs.getString("content");
				boolean pub = rs.getBoolean("pub");
				// 인자 순서 주의 
				notice = new Notice(nid, title, regdate, writerId, hit, files, content, pub);
				
			}
				rs.close();
				pstmt.close();
				con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return notice;
	}
	public Notice getNextNotice(int id) {
		
		Notice notice = null;
		
		String sql = "select * from notice " + 
				"where id = (" + 
				"    select id from notice " + 
				"    where regdate > (select regdate from notice where id = ?) " + 
				"    and rownum = 1" + 
				") ";
		
		String url = "jdbc:oracle:thin:@192.168.0.5:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "12345");
			PreparedStatement pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()){				
				int nid = rs.getInt("id");	
				String title = rs.getString("title");
				Date regdate = rs.getDate("regdate");
				String writerId = rs.getString("writer_id");
				int hit = rs.getInt("hit");
				String files = rs.getString("files");
				String content = rs.getString("content");
				boolean pub = rs.getBoolean("pub");
				// 인자 순서 주의 
				notice = new Notice(nid, title, regdate, writerId, hit, files, content, pub);
				
			}
				rs.close();
				pstmt.close();
				con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return notice;
	}
	public Notice getPrevNotice(int id) {
		
		Notice notice = null;
		
		String sql = "select * from notice " + 
				"where id = (select id from (select * from notice order by regdate desc) " + 
				"            where regdate < (select regdate from notice where id = ?) " + 
				"            and rownum = 1 " + 
				") ";
		
		String url = "jdbc:oracle:thin:@192.168.0.5:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "12345");
			PreparedStatement pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()){				
				int nid = rs.getInt("id");	
				String title = rs.getString("title");
				Date regdate = rs.getDate("regdate");
				String writerId = rs.getString("writer_id");
				int hit = rs.getInt("hit");
				String files = rs.getString("files");
				String content = rs.getString("content");
				boolean pub = rs.getBoolean("pub");
				// 인자 순서 주의 
				notice = new Notice(nid, title, regdate, writerId, hit, files, content, pub);
				
			}
				rs.close();
				pstmt.close();
				con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return notice;
	}
	public int deleteNoticeAll(int[] ids) {
		
		int result = 0;
		
		String params = "";
		for(int i=0; i<ids.length; i++) {
			params += ids[i];
			if(i < ids.length-1) {
				params += ",";
			}
		}
		
		String sql = "delete notice where id in ("+params+")";
		
		String url = "jdbc:oracle:thin:@192.168.0.5:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "12345");
			Statement st = con.createStatement();
			
			result = st.executeUpdate(sql);
			
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
