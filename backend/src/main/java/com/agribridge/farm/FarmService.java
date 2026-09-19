package com.agribridge.farm;

import com.agribridge.exception.ResourceNotFoundException;
import com.agribridge.farm.dto.FarmRequest;
import com.agribridge.farm.dto.FarmResponse;
import com.agribridge.user.User;
import com.agribridge.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Farm Management business logic. Every method resolves the acting user from
 * the authenticated principal's email and enforces that a farmer can only
 * read/update/delete farms they own - administrators are the only role that
 * can be extended to cross-owner access in a later phase (not implemented
 * in Week 2).
 */
@Service
public class FarmService {

    private final FarmRepository farmRepository;
    private final UserRepository userRepository;

    public FarmService(FarmRepository farmRepository, UserRepository userRepository) {
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
    }

    public FarmResponse createFarm(String ownerEmail, FarmRequest request) {
        User owner = resolveUser(ownerEmail);

        Farm farm = Farm.builder()
                .ownerId(owner.getId())
                .farmName(request.getFarmName())
                .location(request.getLocation())
                .landArea(request.getLandArea())
                .soilType(request.getSoilType())
                .build();

        return FarmResponse.fromEntity(farmRepository.save(farm));
    }

    public List<FarmResponse> listMyFarms(String ownerEmail) {
        User owner = resolveUser(ownerEmail);
        return farmRepository.findByOwnerId(owner.getId()).stream()
                .map(FarmResponse::fromEntity)
                .toList();
    }

    public FarmResponse getFarm(String ownerEmail, Long farmId) {
        User owner = resolveUser(ownerEmail);
        Farm farm = farmRepository.findByIdAndOwnerId(farmId, owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found: " + farmId));
        return FarmResponse.fromEntity(farm);
    }

    public FarmResponse updateFarm(String ownerEmail, Long farmId, FarmRequest request) {
        User owner = resolveUser(ownerEmail);
        Farm farm = farmRepository.findByIdAndOwnerId(farmId, owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found: " + farmId));

        farm.setFarmName(request.getFarmName());
        farm.setLocation(request.getLocation());
        farm.setLandArea(request.getLandArea());
        farm.setSoilType(request.getSoilType());

        return FarmResponse.fromEntity(farmRepository.save(farm));
    }

    public void deleteFarm(String ownerEmail, Long farmId) {
        User owner = resolveUser(ownerEmail);
        Farm farm = farmRepository.findByIdAndOwnerId(farmId, owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found: " + farmId));
        farmRepository.delete(farm);
    }

    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
