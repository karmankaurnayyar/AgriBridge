package com.agribridge.farm;

import com.agribridge.farm.dto.FarmRequest;
import com.agribridge.farm.dto.FarmResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Implemented in Week 2: full CRUD for Farm Management, the "basic API
 * prototype" module for this deliverable. Every endpoint requires
 * authentication (see SecurityConfig); write operations are additionally
 * restricted to the FARMER and ADMIN roles via @PreAuthorize.
 */
@RestController
@RequestMapping("/api/farms")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @GetMapping
    public ResponseEntity<List<FarmResponse>> listFarms(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(farmService.listMyFarms(principal.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmResponse> getFarm(@AuthenticationPrincipal UserDetails principal,
                                                 @PathVariable Long id) {
        return ResponseEntity.ok(farmService.getFarm(principal.getUsername(), id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FARMER','ADMIN')")
    public ResponseEntity<FarmResponse> createFarm(@AuthenticationPrincipal UserDetails principal,
                                                     @Valid @RequestBody FarmRequest request) {
        FarmResponse response = farmService.createFarm(principal.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FARMER','ADMIN')")
    public ResponseEntity<FarmResponse> updateFarm(@AuthenticationPrincipal UserDetails principal,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody FarmRequest request) {
        return ResponseEntity.ok(farmService.updateFarm(principal.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('FARMER','ADMIN')")
    public ResponseEntity<Void> deleteFarm(@AuthenticationPrincipal UserDetails principal,
                                            @PathVariable Long id) {
        farmService.deleteFarm(principal.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
