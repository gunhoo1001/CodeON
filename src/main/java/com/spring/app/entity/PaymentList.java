// com.spring.app.entity.PaymentList.java
package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "TBL_PAYMENT_LIST")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentList {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PAYMENT_LIST_GEN")
    @SequenceGenerator(name = "SEQ_PAYMENT_LIST_GEN", sequenceName = "SEQ_PAYMENT_LIST", allocationSize = 1)
    @Column(name = "payment_list_seq")
    private Long paymentListSeq;

    @Column(name = "fk_draft_seq", nullable = false)
    private Long fkDraftSeq;

    @Column(name = "payment_list_regdate", nullable = false)
    private LocalDate regdate;

    @Column(name = "payment_list_price", nullable = false)
    private Long price;

    @Column(name = "payment_list_content", length = 500)
    private String content;
}
