package up6;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import up6.model.FileInf;

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
		ResultSet r = db.ExecuteDataSet(sb.toString());
		try {
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
			//cmd.close();			
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
}