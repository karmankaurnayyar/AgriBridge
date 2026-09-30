package com.agribridge.crop;

import com.agribridge.crop.dto.CropRequest;
import com.agribridge.crop.dto.CropResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * WEEK 3 — implements the /api/crops contract that was documented as
 * "planned" in Week 2 (docs/api-documentation.md). Mirrors FarmController's
 * pattern: every endpoint requires authentication; writes are restricted to
 * FARMER/ADMIN.
 */
@RestController
@RequestMapping("/api/crops")
public class CropController {

    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @GetMapping
    public ResponseEntity<List<CropResponse>> listCrops(@AuthenticationPrincipal UserDetails principal,
                                                          @RequestParam Long farmId) {
        return ResponseEntity.ok(cropService.listCropsForFarm(principal.getUsername(), farmId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CropResponse> getCrop(@AuthenticationPrincipal UserDetails principal,
                                                 @PathVariable Long id) {
        return ResponseEntity.ok(cropService.getCrop(principal.getUsername(), id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FARMER','ADMIN')")
    public ResponseEntity<CropResponse> createCrop(@AuthenticationPrincipal UserDetails principal,
                                                    @Valid @RequestBody CropRequest request) {
        CropResponse response = cropService.createCrop(principal.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FARMER','ADMIN')")
    public ResponseEntity<CropResponse> updateCrop(@AuthenticationPrincipal UserDetails principal,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody CropRequest request) {
        return ResponseEntity.ok(cropService.updateCrop(principal.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('FARMER','ADMIN')")
    public ResponseEntity<Void> deleteCrop(@AuthenticationPrincipal UserDetails principal,
                                            @PathVariable Long id) {
        cropService.deleteCrop(principal.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
