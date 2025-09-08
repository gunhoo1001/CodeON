// com.spring.app.entity.Payment.java
package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_PAYMENT")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id
    @Column(name = "draft_seq")
    private Long draftSeq;

    @Column(name = "payment_title", nullable = false, length = 100)
    private String paymentTitle;

    @Lob
    @Column(name="PAYMENT_CONTENT", columnDefinition="CLOB")
    private String paymentContent;
    
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;
}
