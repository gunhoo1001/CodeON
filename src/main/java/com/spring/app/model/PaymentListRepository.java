package com.spring.app.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.spring.app.entity.PaymentList;

public interface PaymentListRepository extends JpaRepository<PaymentList, Long> {
	List<PaymentList> findByFkDraftSeqOrderByRegdateAscPaymentListSeqAsc(Long fkDraftSeq);
}
