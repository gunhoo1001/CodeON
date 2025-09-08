package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.spring.app.domain.SignlineDTO;

@Entity
@Table(name = "TBL_SIGNLINE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Signline {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "signlineSeqGen")
    @SequenceGenerator(name = "signlineSeqGen", sequenceName = "SIGNLINE_SEQ", allocationSize = 1)
    @Column(name = "signline_seq")
    private Long signlineSeq;

    @Column(name = "fk_member_seq", nullable = false)
    private Long fkMemberSeq;

    @Column(name = "signline_name", nullable = false, length = 100)
    private String signlineName;

    @Column(name = "regdate")
    private LocalDateTime regdate;

    @OneToMany(mappedBy = "signline", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lineOrder ASC")
    @Builder.Default
    private List<SignlineMember> members = new ArrayList<>();

    public void addMember(SignlineMember m) {
    	m.setSignline(this); 
    	this.members.add(m);
	}
    
    @PrePersist
    public void prePersist() {
      if (this.regdate == null) this.regdate = LocalDateTime.now();
    }
    
	public SignlineDTO toDTO() {
		
		return SignlineDTO.builder()
				.signlineSeq(this.signlineSeq)
				.fkMemberSeq(this.fkMemberSeq)
				.signlineName(this.signlineName)
				.regdate(this.regdate)
				.build();
		
	}
    
}
