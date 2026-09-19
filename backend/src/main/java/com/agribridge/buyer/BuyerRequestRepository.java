package com.agribridge.buyer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** PLANNED FOR WEEK 4 — repository only for now; see docs/roadmap.md. */
public interface BuyerRequestRepository extends JpaRepository<BuyerRequest, Long> {

    List<BuyerRequest> findByStatus(String status);

    List<BuyerRequest> findByBuyerId(Long buyerId);
}
