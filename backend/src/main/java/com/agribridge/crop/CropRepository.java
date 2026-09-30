package com.agribridge.crop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** WEEK 3 — backs CropService/CropController (Crop Management, now implemented). */
public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findByFarmId(Long farmId);
}
