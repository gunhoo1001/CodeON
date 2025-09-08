// SmallCategoryDTO.java
package com.spring.app.calendar.domain;

public class SmallCategoryDTO {
    private int smallCategorySeq;
    private int fkBigCategorySeq;
    private int fkMemberSeq;
    private String smallCategoryName;

    // getter/setter
    public int getSmallCategorySeq() {
        return smallCategorySeq;
    }
    public void setSmallCategorySeq(int smallCategorySeq) {
        this.smallCategorySeq = smallCategorySeq;
    }
    public int getFkBigCategorySeq() {
        return fkBigCategorySeq;
    }
    public void setFkBigCategorySeq(int fkBigCategorySeq) {
        this.fkBigCategorySeq = fkBigCategorySeq;
    }
    public int getFkMemberSeq() {
        return fkMemberSeq;
    }
    public void setFkMemberSeq(int fkMemberSeq) {
        this.fkMemberSeq = fkMemberSeq;
    }
    public String getSmallCategoryName() {
        return smallCategoryName;
    }
    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }
}
