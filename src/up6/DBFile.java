package up6;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import up6.model.FileInf;

import com.google.gson.Gson;

/*
 * 原型
*/
/**
 * @author Administrator
 * 更新记录：
 * 	2015-07-30 修复获取文件夹大小错误的问题。
 *
 */
public class DBFile {

	public DBFile()
	{
	}

	static public String GetAllUnComplete(int uid)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(" f_id");
		sb.append(",f_fdTask");
		sb.append(",f_nameLoc");
		sb.append(",f_nameSvr");
		sb.append(",f_pathLoc");
		sb.append(",f_pathSvr");
		sb.append(",f_pathRel");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(" from up6_files");
		sb.append(" where f_uid=? and f_deleted=0 and f_complete=0 and f_fdChild=0");

		ArrayList<FileInf> files = new ArrayList<FileInf>();
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setInt(1, uid);
			ResultSet r = db.ExecuteDataSet(cmd);
			while(r.next())
			{
				FileInf f 		= new FileInf();
				f.uid			= uid;
				f.id 			= r.getString(1);
				f.fdTask 		= r.getBoolean(2);				
				f.nameLoc 		= r.getString(3);
				f.nameSvr		= r.getString(4);
				f.pathLoc 		= r.getString(5);
				f.pathSvr 		= r.getString(6);
				f.pathRel 		= r.getString(7);
				f.md5 			= r.getString(8);
				f.lenLoc 		= r.getLong(9);
				f.sizeLoc 		= r.getString(10);
				f.offset 		= r.getLong(11);
				f.lenSvr 		= r.getLong(12);
				f.perSvr 		= r.getString(13);
				files.add(f);
				
			}
			r.close();
			cmd.close();
			cmd.getConnection().close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(files.size() < 1) return null;
		Gson g = new Gson();
	    return g.toJson( files );//bug:arrFiles为空时，此行代码有异常	
	}
	
	/// <summary>
	/// 根据文件MD5获取文件信息
	/// </summary>
	/// <param name="md5"></param>
	/// <param name="inf"></param>
	/// <returns></returns>
	public boolean exist_file(String md5, FileInf fileSvr)
	{
		boolean ret = false;
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(" f_id");
		sb.append(",f_uid");
		sb.append(",f_nameLoc");
		sb.append(",f_nameSvr");
		sb.append(",f_pathLoc");
		sb.append(",f_pathSvr");
		sb.append(",f_pathRel");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_complete"); 
		sb.append(",f_time");
		sb.append(",f_deleted");
		sb.append(" from up6_files");
		sb.append(" where f_md5='");
		sb.append(md5);
		sb.append("'and rownum<=1");
		sb.append(" order by to_number(f_lenSvr) DESC");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		//ResultSet r = db.ExecuteDataSet(sb.toString());
		try {
			ResultSet r = cmd.executeQuery();
			//cmd.setString(1, md5);
			//ResultSet r = db.ExecuteDataSet(cmd);
			if (r.next())
			{
				fileSvr.id 				= r.getString(1);
				fileSvr.uid 			= r.getInt(2);
				fileSvr.nameLoc 		= r.getString(3);
				fileSvr.nameSvr 		= r.getString(4);
				fileSvr.pathLoc 		= r.getString(5);
				fileSvr.pathSvr 		= r.getString(6);
				fileSvr.pathRel 		= r.getString(7);
				fileSvr.md5 			= r.getString(8);
				fileSvr.lenLoc 			= r.getLong(9);
				fileSvr.sizeLoc 		= r.getString(10);
				fileSvr.offset 			= r.getLong(11);
				fileSvr.lenSvr 			= r.getLong(12);
				fileSvr.perSvr 			= r.getString(13);
				fileSvr.complete 		= r.getBoolean(14);
				fileSvr.PostedTime 		= r.getDate(15);
				fileSvr.deleted 		= r.getBoolean(16);
				ret = true;
			}
			r.close();
			cmd.close();
			cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	/// <summary>
	/// 增加一条数据，并返回新增数据的ID
	/// 在ajax_create_fid.aspx中调用
	/// 文件名称，本地路径，远程路径，相对路径都使用原始字符串。
	/// d:\soft\QQ2012.exe
	/// </summary>
	public void Add(FileInf model)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("insert into up6_files(");
		sb.append(" f_id");
		sb.append(",f_pid");
		sb.append(",f_pidRoot");
		sb.append(",f_fdTask");
		sb.append(",f_fdChild");
		sb.append(",f_uid");
		sb.append(",f_pos");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_sizeLoc");
		sb.append(",f_nameLoc");
		sb.append(",f_nameSvr");
		sb.append(",f_pathLoc");
		sb.append(",f_pathSvr");
		sb.append(",f_pathRel");
		sb.append(",f_complete");
		
		sb.append(") values (");
		
		sb.append(" ?");//id
		sb.append(",?");//pid
		sb.append(",?");//pidRoot
		sb.append(",?");//fdTask
		sb.append(",?");//fdChild
		sb.append(",?");//uid
		sb.append(",?");//pos
		sb.append(",?");//md5
		sb.append(",?");//lenLoc		
		sb.append(",?");//lenSvr
		sb.append(",?");//perSvr
		sb.append(",?");//sizeLoc
		sb.append(",?");//nameLoc
		sb.append(",?");//nameSvr
		sb.append(",?");//pathLoc
		sb.append(",?");//pathSvr
		sb.append(",?");//pathRel
		sb.append(",?");//complete
		sb.append(") ");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		
		try {
			cmd.setString(1, model.id);
			cmd.setString(2, model.pid);
			cmd.setString(3, model.pidRoot);
			cmd.setBoolean(4, model.fdTask);
			cmd.setBoolean(5, model.fdChild);
			cmd.setInt(6, model.uid);
			cmd.setLong(7, model.offset);
			cmd.setString(8, model.md5);
			cmd.setLong(9, model.lenLoc);
			cmd.setLong(10, model.lenSvr);
			cmd.setString(11, model.perSvr);
			cmd.setString(12, model.sizeLoc);
			cmd.setString(13, model.nameLoc);
			cmd.setString(14, model.nameSvr);			
			cmd.setString(15, model.pathLoc);
			cmd.setString(16, model.pathSvr);
			cmd.setString(17, model.pathRel);
			cmd.setBoolean(18, model.complete);
		} catch (SQLException e) {e.printStackTrace();}

		db.ExecuteNonQuery(cmd);
	}

	/**
	 * 清空文件表，文件夹表数据。
	 */
	static public void Clear()
	{
		DbHelper db = new DbHelper();
		db.ExecuteNonQuery("truncate table up6_files");
		db.ExecuteNonQuery("truncate table up6_folders");
	}
	
	/**
	 * @param uid
	 * @param fid
	 */
	static public void fd_complete(String fid, String uid)
	{
		String sql = "begin ";
		sql += "update up6_files set f_perSvr='100%',f_lenSvr=f_lenLoc,f_complete=1 where f_id=? and f_uid=?;";
		sql += "update up6_folders set fd_complete=1 where fd_id=? and fd_uid=?;";
		sql += "update up6_files set f_perSvr='100%',f_lenSvr=f_lenLoc,f_complete=1 where f_pidRoot=?;";
		sql += "end;";
		
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		try {
			cmd.setString(1, fid);
			cmd.setInt(2, Integer.parseInt(uid));
			cmd.setString(3, fid);
			cmd.setInt(4, Integer.parseInt(uid));
			cmd.setString(5, fid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		db.ExecuteNonQuery(cmd);
	}

	/// <summary>
	/// 更新上传进度
	/// </summary>
	///<param name="uid">用户ID</param>
	///<param name="fid">文件ID</param>
	///<param name="filePos">文件位置，大小可能超过2G，所以需要使用long保存</param>
	///<param name="postedLength">已上传长度，文件大小可能超过2G，所以需要使用long保存</param>
	///<param name="postedPercent">已上传百分比</param>
	public boolean f_process(int uid,String f_id,long offset,long f_lenSvr,String f_perSvr)
	{
		String sql = "update up6_files set f_pos=?,f_lenSvr=?,f_perSvr=? where f_uid=? and f_id=?";
		
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		
		try 
		{
			cmd.setLong(1, offset);
			cmd.setLong(2, f_lenSvr);
			cmd.setString(3, f_perSvr);
			cmd.setInt(4, uid);
			cmd.setString(5, f_id);
		} catch (SQLException e) {e.printStackTrace();}

		db.ExecuteNonQuery(cmd);
		return true;
	}

	/// <summary>
	/// 上传完成。将所有相同MD5文件进度都设为100%
	/// </summary>
	public void UploadComplete(String md5)
	{
		String sql = "update up6_files set f_lenSvr=f_lenLoc,f_perSvr='100%',f_complete=1 where f_md5='"+md5+"'";
		DbHelper db = new DbHelper();
		//PreparedStatement cmd = db.GetCommand(sql);
		
		//cmd.setString(1, md5);
		//db.ExecuteNonQuery(cmd);//在部分环境中测试发现执行后没有效果。f_complete仍然为0
		db.ExecuteNonQuery(sql);
	}

	/// <summary>
	/// 删除一条数据，并不真正删除，只更新删除标识。
	/// </summary>
	/// <param name="uid"></param>
	/// <param name="fid"></param>
	public void Delete(int uid,String fid)
	{
		String sql = "update up6_files set f_deleted=1 where f_uid=? and f_id=?";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);

		try {
			cmd.setInt(1, uid);
			cmd.setString(2, fid);
			db.ExecuteNonQuery(cmd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据文件ID获取文件信息
	 * @param fid
	 * @param inf
	 * @return
	 */
	public boolean GetFileInfByFid(String fid,FileInf inf)
	{
		boolean ret = false;
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append("f_uid");
		sb.append(",f_nameLoc");
		sb.append(",f_nameSvr");
		sb.append(",f_pathLoc");
		sb.append(",f_pathSvr");
		sb.append(",f_pathRel");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_complete");
		sb.append(",f_time");
		sb.append(",f_deleted");
		sb.append(" from up6_files where f_id=? ");
		
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, fid);
			ResultSet r = db.ExecuteDataSet(cmd);

			if (r.next())
			{
				inf.id 				= fid;
				inf.uid 			= r.getInt(1);
				inf.nameLoc 		= r.getString(2);
				inf.nameSvr 		= r.getString(3);
				inf.pathLoc 		= r.getString(4);
				inf.pathSvr 		= r.getString(5);
				inf.pathRel 		= r.getString(6);
				inf.md5 			= r.getString(7);
				inf.lenLoc 			= r.getLong(8);
				inf.sizeLoc 		= r.getString(9);
	            inf.offset 			= r.getLong(10);
	            inf.lenSvr 			= r.getLong(11);
				inf.perSvr 			= r.getString(12);
				inf.complete 		= r.getBoolean(13);
				inf.PostedTime 		= r.getDate(14);
				inf.deleted 		= r.getBoolean(15);
				ret = true;
			}
			cmd.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}
}