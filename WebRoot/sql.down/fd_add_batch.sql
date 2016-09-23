create or replace procedure fd_add_batch(
 f_count in number	/*文件总数*/
,uid    in number		/*用户ID*/
,f_ids  out clob
)as
i number;
fCount number;
id_cur number;
begin
  i := 0;
  f_ids := '0';
  fCount := f_count + 1;

  /*批量添加文件*/
  while(i<fCount) loop
    insert into down_files(f_idSvr,f_uid) values(SEQ_dn_f_idSvr.nextval,uid);
    f_ids := concat( f_ids,',');
    select SEQ_dn_f_idSvr.Currval into id_cur from dual;
    f_ids := concat( f_ids,id_cur );
    i := i + 1;
  end loop;
  f_ids := substr(f_ids,3,length(f_ids)-2);/*删除0,*/
end;
