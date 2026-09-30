package com.agribridge.crop;

import com.agribridge.crop.dto.CropRequest;
import com.agribridge.crop.dto.CropResponse;
import com.agribridge.exception.ResourceNotFoundException;
import com.agribridge.farm.Farm;
import com.agribridge.farm.FarmRepository;
import com.agribridge.user.User;
import com.agribridge.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * WEEK 3 — Crop Management business logic (the core functionality selected
 * for this deliverable; see docs/week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md
 * for why this module was chosen).
 *
 * Follows the same ownership pattern established by FarmService in Week 2:
 * every operation resolves the acting user, then confirms the target Farm
 * belongs to that user before any Crop is created, read, updated or
 * deleted. A Crop is never accessible except through a Farm the caller owns.
 */
@Service
public class CropService {

    /** Allowed values for Crop.status. Kept as a small, explicit set rather
     *  than a JPA enum to match the existing Crop entity (Week 2), which
     *  stores status as a plain String column. */
    private static final Set<String> ALLOWED_STATUSES = Set.of("PLANNED", "GROWING", "HARVEST_READY", "COMPLETED");
    private static final String DEFAULT_STATUS = "PLANNED";

    private final CropRepository cropRepository;
    private final FarmRepository farmRepository;
    private final UserRepository userRepository;

    public CropService(CropRepository cropRepository, FarmRepository farmRepository, UserRepository userRepository) {
        this.cropRepository = cropRepository;
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
    }

    public CropResponse createCrop(String ownerEmail, CropRequest request) {
        User owner = resolveUser(ownerEmail);
        Farm farm = resolveOwnedFarm(request.getFarmId(), owner.getId());

        validateDates(request.getSowingDate(), request.getExpectedHarvestDate());
        String status = normalizeStatus(request.getStatus());

        Crop crop = Crop.builder()
                .farmId(farm.getId())
                .cropName(request.getCropName().trim())
                .cropType(request.getCropType())
                .sowingDate(request.getSowingDate())
                .expectedHarvestDate(request.getExpectedHarvestDate())
                .status(status)
                .build();

        return CropResponse.fromEntity(cropRepository.save(crop));
    }

    public List<CropResponse> listCropsForFarm(String ownerEmail, Long farmId) {
        User owner = resolveUser(ownerEmail);
        resolveOwnedFarm(farmId, owner.getId());

        return cropRepository.findByFarmId(farmId).stream()
                .map(CropResponse::fromEntity)
                .toList();
    }

    public CropResponse getCrop(String ownerEmail, Long cropId) {
        User owner = resolveUser(ownerEmail);
        Crop crop = resolveOwnedCrop(cropId, owner.getId());
        return CropResponse.fromEntity(crop);
    }

    public CropResponse updateCrop(String ownerEmail, Long cropId, CropRequest request) {
        User owner = resolveUser(ownerEmail);
        Crop crop = resolveOwnedCrop(cropId, owner.getId());

        // A crop cannot be reassigned to a different farm through an update
        // in this prototype - that would require re-validating ownership of
        // the new farm too, which is out of scope for Week 3.
        if (request.getFarmId() != null && !request.getFarmId().equals(crop.getFarmId())) {
            throw new IllegalArgumentException("Reassigning a crop to a different farm is not supported.");
        }

        validateDates(request.getSowingDate(), request.getExpectedHarvestDate());
        String status = normalizeStatus(request.getStatus());

        crop.setCropName(request.getCropName().trim());
        crop.setCropType(request.getCropType());
        crop.setSowingDate(request.getSowingDate());
        crop.setExpectedHarvestDate(request.getExpectedHarvestDate());
        crop.setStatus(status);

        return CropResponse.fromEntity(cropRepository.save(crop));
    }

    public void deleteCrop(String ownerEmail, Long cropId) {
        User owner = resolveUser(ownerEmail);
        Crop crop = resolveOwnedCrop(cropId, owner.getId());
        cropRepository.delete(crop);
    }

    // ------------------------------------------------------------------
    // Validation helpers - kept as small, direct methods (public static
    // where they have no dependency on repositories) so they can be unit
    // tested in isolation without mocking anything.
    // ------------------------------------------------------------------

    /**
     * Business rule: the expected harvest date, if provided, must not be
     * before the sowing date. Equal dates are treated as valid (a same-day
     * sowing-and-harvest boundary case, e.g. certain microgreens) - this is
     * the boundary case exercised by CropServiceTest.
     */
    public static void validateDates(LocalDate sowingDate, LocalDate expectedHarvestDate) {
        if (sowingDate != null && expectedHarvestDate != null && expectedHarvestDate.isBefore(sowingDate)) {
            throw new IllegalArgumentException("Expected harvest date cannot be before the sowing date.");
        }
    }

    /**
     * Validates and normalizes the crop status. A blank/null status
     * defaults to PLANNED; any non-blank value must match one of the
     * allowed statuses (case-insensitive).
     */
    public static String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return DEFAULT_STATUS;
        }
        String normalized = status.trim().toUpperCase();
        if (!ALLOWED_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException(
                    "Invalid crop status: '" + status + "'. Allowed values: " + ALLOWED_STATUSES);
        }
        return normalized;
    }

    // ------------------------------------------------------------------
    // Ownership resolution helpers
    // ------------------------------------------------------------------

    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private Farm resolveOwnedFarm(Long farmId, Long ownerId) {
        return farmRepository.findByIdAndOwnerId(farmId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found: " + farmId));
    }

    /** Loads a crop and confirms it belongs to a farm owned by ownerId, without ever revealing whether the crop exists to a non-owner. */
    private Crop resolveOwnedCrop(Long cropId, Long ownerId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found: " + cropId));

        boolean ownsParentFarm = farmRepository.findByIdAndOwnerId(crop.getFarmId(), ownerId).isPresent();
        if (!ownsParentFarm) {
            throw new ResourceNotFoundException("Crop not found: " + cropId);
        }
        return crop;
    }
}
