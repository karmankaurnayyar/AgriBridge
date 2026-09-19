package com.agribridge.harvest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** PLANNED FOR WEEK 3 — repository only for now; see docs/roadmap.md. */
public interface HarvestLotRepository extends JpaRepository<HarvestLot, Long> {

    List<HarvestLot> findByAvailabilityStatus(String availabilityStatus);

    List<HarvestLot> findByCropId(Long cropId);
}
