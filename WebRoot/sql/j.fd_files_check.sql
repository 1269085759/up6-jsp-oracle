/*
-- =============================================
-- Author:    zysoft
-- Create date: 2017-04-10
-- Description: 批量检查文件是否存在，提供给文件夹上传使用
-- =============================================
*/
--
create or replace procedure fd_files_check(  
 l_md5 in ARRAY_MD5--md5列表，项目需要引入nls_charset12.jar，否则字符串元素为空。
 ,files out sys_refcursor)
is
begin
	open files for select * from up6_files where f_md5 in( select column_value from table( l_md5 ) );	  
end;