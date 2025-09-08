show user;

desc tbl_calendar;

SELECT calendar_type FROM tbl_calendar WHERE calendar_seq = 49;


select *  from tab;

select * from TBL_DEPARTMENT
;

select * from TBL_MEMBER
;

select * from TBL_CALENDAR
;

commit;

SELECT * FROM tbl_calendar WHERE calendar_seq = 49;

ALTER TABLE tbl_calendar    
DROP COLUMN calendar_type_name;

select *
from tbl_department;

select * from TBL_CALENDAR_BIG_CATEGORY;

select *
from TBL_CALENDAR_BIG_CATEGORY;

ALTER TABLE TBL_CALENDAR
ADD CALENDAR_TYPE VARCHAR2(20);

SELECT CALENDAR_TYPE FROM TBL_CALENDAR WHERE CALENDAR_SEQ = 51;


SELECT *
FROM tbl_calendar_small_category
ORDER BY tbl_calendar_small_category;


-- 1. 개인 대분류 seq 확인
SELECT big_category_seq 
FROM tbl_calendar_big_category
WHERE big_category_name LIKE '%내%';

-- 예를 들어 결과가 3이라고 가정

-- 2. 개인용 소분류가 없을 때만 "일반" 소분류 자동 생성
INSERT INTO tbl_calendar_small_category
    (small_category_seq, fk_big_category_seq, fk_member_seq, small_category_name)
SELECT SEQ_TBL_SMALL_CATEGORY.nextval, 3, NULL, '일반'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 
    FROM tbl_calendar_small_category
    WHERE fk_big_category_seq = 3
);
COMMIT;

INSERT INTO tbl_calendar_small_category (
    small_category_seq,
    fk_big_category_seq,
    fk_member_seq,
    small_category_name
) VALUES (
    SEQ_TBL_CALENDAR_SMALL_CATEGORY.NEXTVAL,
    3,                       -- "내 캘린더"
    1,                       -- 특정 유저(member_seq) / 공통이면 ADMIN 계정 등
    '공유자 선택'                    -- 기본 소분류명
);


COMMIT;


COMMIT;



SELECT sequence_name 
FROM user_sequences
WHERE sequence_name LIKE 'SEQ%';



SELECT calendar_seq, fk_big_category_seq, fk_small_category_seq,
       calendar_user, calendar_name
FROM tbl_calendar
WHERE calendar_name LIKE '%공유 캘린더%';

SELECT calendar_seq, fk_member_seq, calendar_user, calendar_type
FROM tbl_calendar
ORDER BY calendar_seq DESC;

INSERT INTO tbl_calendar_small_category (
    small_category_seq,
    fk_big_category_seq,
    fk_member_seq,
    small_category_name
) VALUES (
    SEQ_TBL_CALENDAR_SMALL_CATEGORY.NEXTVAL,
    (SELECT big_category_seq 
       FROM tbl_calendar_big_category 
      WHERE big_category_name = '공유 캘린더'),  -- 공유 대분류 참조
    1,                                    -- 공통 계정 or 관리자 계정
    '공유자 선택'                          -- 기본 소분류명
);

SELECT small_category_seq, fk_big_category_seq, small_category_name
FROM tbl_calendar_small_category
WHERE small_category_name LIKE '%공유%';


select *
from tbl_member

desc tbl_member