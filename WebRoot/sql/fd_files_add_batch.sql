/*
-- =============================================
-- Author:    zysoft
-- Create date: 2016-07-31
-- Description:  一次性分配所有的文件和文件夹ID
-- =============================================
*/
CREATE TYPE ARRAY_INT AS TABLE OF NUMBER;          -- Array of integers
--
create or replace procedure fd_files_add_batch(  
   f_count in number      --文件总数，要单独增加一个文件夹
  ,fd_count in number      --文件夹总数
  ,f_ids out ARRAY_INT    --数组
  ,fd_ids out ARRAY_INT    --数组
)
as
  i number;
  id_cur number;
begin
  i := 0;
  f_ids := new ARRAY_INT();
  fd_ids := new ARRAY_INT();
  f_ids.extend(f_count);
  fd_ids.extend(fd_count);

  /*批量分配文件夹ID*/
  while i < fd_count loop  
    insert into up6_folders(fd_id,fd_pid) values(SEQ_fd_id.nextval,0);    
    select SEQ_fd_id.currval into id_cur from dual;
    i := i + 1;
    fd_ids(i) := id_cur;
  end loop;

  /*批量分配文件ID*/
  i := 0;
  while i < f_count loop  
    insert into up6_files(f_idSvr,f_pid) values(SEQ_f_idSvr.nextval,0);    
    select SEQ_f_idSvr.currval into id_cur from dual;
    i := i+1;    
    f_ids(i) := id_cur;
  end loop;    
end;