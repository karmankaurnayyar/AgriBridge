package com.agribridge.farm;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    List<Farm> findByOwnerId(Long ownerId);

    Optional<Farm> findByIdAndOwnerId(Long id, Long ownerId);
}
