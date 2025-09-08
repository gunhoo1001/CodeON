select *
from tbl_board;

select *
from tbl_member;

desc tbl_board
ALTER TABLE tbl_board MODIFY (FK_BOARD_TYPE_SEQ NULL);
ALTER TABLE tbl_board MODIFY (FK_MEMBER_SEQ NULL);
ALTER TABLE tbl_board MODIFY (BOARD_TITLE NULL);
ALTER TABLE tbl_board MODIFY (BOARD_CONTENT NULL);
commit;

SELECT sequence_name
FROM user_sequences
WHERE sequence_name LIKE '%BOARD%';

select *
from tbl_board
CREATE SEQUENCE   board_category_seq  START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

SELECT sequence_name
FROM user_sequences
WHERE sequence_name LIKE '%BOARD%';

SELECT sequence_name
FROM user_sequences;

select *
from tbl_board


INSERT INTO TBL_BOARD
  (Board_Seq, fk_Board_Type_Seq, fk_Member_Seq, board_Title, board_Content, board_Regdate, board_Readcount)
VALUES
  (TBL_BOARD_SEQ.nextval, 1, 1, '테스트 제목', '테스트 내용', SYSDATE, 0);

CREATE TABLE TBL_BOARD_CATEGORY (
    board_category_seq  NUMBER        NOT NULL,
    board_category_name VARCHAR2(100) NOT NULL,
    CONSTRAINT PK_TBL_BOARD_CATEGORY PRIMARY KEY (board_category_seq)
);
-- 게시판 유형
CREATE TABLE TBL_BOARD_TYPE (
    board_type_seq  NUMBER        NOT NULL,
    board_type_name VARCHAR2(100) NOT NULL,
    CONSTRAINT PK_TBL_BOARD_TYPE PRIMARY KEY (board_type_seq)
);

desc tbl_board
desc tbl_board_type
desc tbl_board_category

ALTER TABLE tbl_BOARD DROP CONSTRAINT FK_BOARD_TYPE;


drop table tbl_board


delete from tbl_board_type
where BOARD_type_SEQ= 1



select *
from tbl_board

select *
from tbl_member

SELECT BOARD_SEQ.NEXTVAL FROM DUAL;
desc tbl_board

desc tbl_board

desc tbl_board_type



select *
from tbl_member



select *
from tbl_board_type

desc tbl_board_type

select *
from tbl_board

INSERT INTO TBL_BOARD_TYPE (BOARD_TYPE_SEQ, BOARD_TYPE_NAME)
VALUES (1, '부서게시판');  
INSERT INTO TBL_BOARD_TYPE (BOARD_TYPE_SEQ, BOARD_TYPE_NAME)
VALUES (0, '사내게시판');  
INSERT INTO TBL_BOARD_TYPE (BOARD_TYPE_SEQ, BOARD_TYPE_NAME)
VALUES (2, '경조사');  
INSERT INTO TBL_BOARD_TYPE (BOARD_TYPE_SEQ, BOARD_TYPE_NAME)
VALUES (1, '공지사항');  


INSERT INTO tbl_board_category (board_category_seq, board_category_name)
VALUES (0, '공지사항');  
INSERT INTO tbl_board_category (board_category_seq, board_category_name)
VALUES (1, '일반');  
INSERT INTO tbl_board_category (board_category_seq, board_category_name)
VALUES (2, '경조사');  


update TBL_BOARD_TYPE
set BOARD_TYPE_NAME='사내게시판'
where BOARD_TYPE_SEQ = 0

select *
from tbl_member

desc tbl_board
SELECT sequence_name FROM user_sequences;

commit
SELECT * FROM TBL_BOARD_TYPE;

desc tbl_board_type

desc tbl_member

SELECT * FROM TBL_BOARD_TYPE WHERE BOARD_TYPE_SEQ = 1;


ALTER TABLE BOARD DROP CONSTRAINT FK_BOARD_TYPE; -- FK 이름은 실제 조회 결과에 맞게 변경
ALTER TABLE BOARD DROP CONSTRAINT FK_BOARD_MEMBER;



CREATE TABLE TBL_BOARD (
    board_seq         NUMBER        NOT NULL,
    fk_board_type_seq NUMBER        ,
    fk_member_seq     NUMBER        ,
    board_title       VARCHAR2(200) NOT NULL,
    board_content     CLOB          NOT NULL,
    board_regdate     DATE          DEFAULT SYSDATE,
    board_readcount   NUMBER        DEFAULT 0,
    CONSTRAINT PK_TBL_BOARD PRIMARY KEY (board_seq),
    CONSTRAINT FK_TBL_BOARD_TYPE_TO_TBL_BOARD FOREIGN KEY (fk_board_type_seq) REFERENCES TBL_BOARD_TYPE (board_type_seq),
    CONSTRAINT FK_TBL_MEMBER_TO_TBL_BOARD FOREIGN KEY (fk_member_seq) REFERENCES TBL_MEMBER (member_seq)
);

SELECT member_seq, member_userid, member_pwd, member_name
FROM TBL_MEMBER 
WHERE member_userid = 'lee';

select *
from tbl_board_category
desc tbl_board_category

select *
from tbl_board_type


select *
from tbl_member

select *
from tbl_member

desc tbl_member
desc tbl_comment
desc tbl_board



CREATE TABLE TBL_COMMENT (
    comment_seq      NUMBER            NOT NULL,
    fk_board_seq     NUMBER            NOT NULL,
    fk_member_seq    NUMBER            NOT NULL,
    comment_content  VARCHAR2(1000)    NOT NULL,
    comment_regdate  DATE DEFAULT SYSDATE,
    CONSTRAINT PK_TBL_COMMENT PRIMARY KEY (comment_seq),
    CONSTRAINT FK_COMMENT_BOARD FOREIGN KEY (fk_board_seq) REFERENCES TBL_BOARD(board_seq), 
    CONSTRAINT FK_COMMENT_MEMBER FOREIGN KEY (fk_member_seq) REFERENCES TBL_MEMBER(member_seq)
        
);

--Table TBL_COMMENT이(가) 생성되었습니다.

CREATE TABLE TBL_REPLY (
    reply_seq      NUMBER ,
    fk_comment_seq NUMBER NOT NULL,     -- 어느 댓글의 답글인지
    fk_member_seq  NUMBER NOT NULL,
    reply_content  VARCHAR2(1000) NOT NULL,
    reply_regdate  DATE DEFAULT SYSDATE,
    CONSTRAINT PK_TBL_REPLY PRIMARY KEY (reply_seq),
    CONSTRAINT FK_REPLY_COMMENT FOREIGN KEY (fk_comment_seq) REFERENCES TBL_COMMENT(comment_seq),
    CONSTRAINT FK_REPLY_MEMBER FOREIGN KEY (fk_member_seq) REFERENCES TBL_MEMBER(member_seq)
        
);

--Table TBL_REPLY이(가) 생성되었습니다.


CREATE SEQUENCE SEQ_REPLY
START WITH 1
INCREMENT BY 1
NOCACHE;
--Sequence SEQ_REPLY이(가) 생성되었습니다.


commit;

CREATE TABLE TBL_REACTION (
    reaction_seq   NUMBER          NOT NULL,
    fk_board_seq   NUMBER          NOT NULL,
    fk_member_seq  NUMBER          NOT NULL,
    reaction_type  VARCHAR2(30)    NOT NULL,
    reaction_date  DATE DEFAULT SYSDATE,
    CONSTRAINT PK_TBL_REACTION PRIMARY KEY (reaction_seq),
    CONSTRAINT FK_REACTION_BOARD FOREIGN KEY (fk_board_seq)
        REFERENCES TBL_BOARD(board_seq),
    CONSTRAINT FK_REACTION_MEMBER FOREIGN KEY (fk_member_seq)
        REFERENCES TBL_MEMBER(member_seq)
);
--Table TBL_REACTION이(가) 생성되었습니다.

-- 댓글 PK 시퀀스
CREATE SEQUENCE SEQ_TBL_COMMENT
    START WITH 1
    INCREMENT BY 1
    NOCACHE;
--Sequence SEQ_TBL_COMMENT이(가) 생성되었습니다.
CREATE SEQUENCE seq_comment
START WITH 1
INCREMENT BY 1
NOCACHE;


commit
drop sequence seq_tbl_comment
-- 리액션 PK 시퀀스
CREATE SEQUENCE SEQ_TBL_REACTION
    START WITH 1
    INCREMENT BY 1
    NOCACHE;
--Sequence SEQ_TBL_REACTION이(가) 생성되었습니다.

desc tbl_comment

commit

select *

from tbl_comment

select *
from
tbl_member

INSERT INTO TBL_COMMENT (
    comment_seq, fk_board_seq, fk_member_seq, comment_content
) VALUES (
    SEQ_TBL_COMMENT.NEXTVAL,  -- 시퀀스로 PK 생성
    47,                      -- 글 번호
    202510020,                     -- 회원 번호
    '좋은 글이네요'
);
commit

select *
from tbl_comment


desc tbl_comment
SELECT seq_comment.NEXTVAL FROM dual;

select *
from tbl_reaction




--추천 테이블---
CREATE TABLE tbl_recommend (
    recommend_seq     NUMBER PRIMARY KEY,              -- 추천 PK
    fk_board_seq      NUMBER NOT NULL,                 -- 게시글 번호
    fk_member_seq     NUMBER NOT NULL,                 -- 추천한 회원 번호
    CONSTRAINT fk_recommend_board FOREIGN KEY (fk_board_seq) 
        REFERENCES tbl_board(board_seq) ON DELETE CASCADE,
    CONSTRAINT fk_recommend_member FOREIGN KEY (fk_member_seq) 
        REFERENCES tbl_member(member_seq) ON DELETE CASCADE,
    CONSTRAINT uq_board_member UNIQUE (fk_board_seq, fk_member_seq) -- 같은 글 중복 추천 방지
);

select * from tbl_comment
drop table tbl_board_recommend
------------------- **** >>> Spring Boot Security <<< **** -------------------


