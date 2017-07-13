package up6.biz.folder;

import java.io.IOException;
import java.sql.SQLException;

import up6.FileResumerPart;
import up6.PathTool;
import up6.biz.PathUuidBuilder;
import up6.model.FileInf;

/**
 * uuid模式会在服务端创建文件夹层级结构，所有文件以原始名称命名。
 * 不会检查重复文件，服务器会保存重复文件。
 * @author Administrator
 *
 */
public class fd_uuid_appender extends fd_appender
{
	public fd_uuid_appender()
	{
		this.pb = new PathUuidBuilder();
	}

    public void save() throws IOException, SQLException
    {   
        this.m_root.pathSvr = this.pb.genFolder(this.m_root.uid, this.m_root.nameLoc);
        PathTool.createDirectory(this.m_root.pathSvr);

        super.save();
        
        //创建目录
        for(FileInf fd : this.m_root.folders)
        {
        	PathTool.createDirectory(fd.pathSvr);
        }
        //创建文件
        for(FileInf f : this.m_root.files)
        {
    		FileResumerPart fr = new FileResumerPart();
    		fr.CreateFile(f.pathSvr);		
        }
    }
    protected void get_md5s(){}//不查询重复文件
    protected void get_md5_files() { }//不查询重复文件
    protected void check_files(){}
}
