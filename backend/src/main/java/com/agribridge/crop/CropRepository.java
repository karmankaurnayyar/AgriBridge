package com.agribridge.crop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** PLANNED FOR WEEK 3 — repository only for now; see docs/roadmap.md. */
public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findByFarmId(Long farmId);
}
