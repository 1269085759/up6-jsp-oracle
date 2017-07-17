--创建自动编号列
--DROP SEQUENCE SEQ_dn_fd_id
CREATE SEQUENCE SEQ_dn_fd_id 
       MINVALUE 1
       START WITH 1
       NOMAXVALUE
       INCREMENT BY 1
       NOCYCLE
       CACHE 30
