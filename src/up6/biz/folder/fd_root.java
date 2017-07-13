package up6.biz.folder;

import java.util.List;

import up6.model.xdb_files;

public class fd_root extends xdb_files{
    public List<xdb_files> folders;
    public List<xdb_files> files;
    
    public fd_root()
    {
    	this.fdTask = true;
    }
}
