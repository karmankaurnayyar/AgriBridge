package com.agribridge.crop;

import com.agribridge.crop.dto.CropRequest;
import com.agribridge.crop.dto.CropResponse;
import com.agribridge.exception.ResourceNotFoundException;
import com.agribridge.farm.Farm;
import com.agribridge.farm.FarmRepository;
import com.agribridge.user.Role;
import com.agribridge.user.User;
import com.agribridge.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * WEEK 3 — Unit tests for CropService, the core functionality implemented
 * this week. Covers normal, boundary, invalid-input, null-input and
 * exception/business-rule-violation scenarios, per the Week 3 test plan in
 * docs/week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md (test case table
 * maps 1:1 to the @DisplayName TC-xx id of each test below).
 *
 * These tests were written and manually traced through carefully but could
 * not be executed with `mvn test` inside this development environment (no
 * Maven Central access) - see the Week 3 documentation, "Unit Testing
 * Methodology" and "Challenges Encountered", for the honest, non-fabricated
 * account of what was and was not actually run.
 */
@ExtendWith(MockitoExtension.class)
class CropServiceTest {

    @Mock private CropRepository cropRepository;
    @Mock private FarmRepository farmRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private CropService cropService;

    private User farmer;
    private Farm farm;

    @BeforeEach
    void setUp() {
        farmer = User.builder().id(1L).name("Ravi Kumar").email("ravi.kumar@example.com")
                .passwordHash("hashed").role(Role.FARMER).build();
        farm = Farm.builder().id(10L).ownerId(1L).farmName("Green Valley Farm")
                .location("Hoshiarpur").landArea(2.5).build();
    }

    // ------------------------------------------------------------------
    // Pure validation logic - no mocking required
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("validateDates() - business rule: harvest date must not precede sowing date")
    class ValidateDatesTests {

        @Test
        @DisplayName("TC-01 (Normal): sowing before harvest -> no exception")
        void normalCase_sowingBeforeHarvest_isValid() {
            LocalDate sowing = LocalDate.of(2026, 6, 1);
            LocalDate harvest = LocalDate.of(2026, 10, 1);
            assertThatCode(() -> CropService.validateDates(sowing, harvest)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("TC-02 (Boundary): sowing date equals harvest date -> valid (same-day boundary)")
        void boundaryCase_equalDates_isValid() {
            LocalDate sameDay = LocalDate.of(2026, 6, 1);
            assertThatCode(() -> CropService.validateDates(sameDay, sameDay)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("TC-03 (Business rule violation): harvest before sowing -> IllegalArgumentException")
        void invalid_harvestBeforeSowing_throws() {
            LocalDate sowing = LocalDate.of(2026, 10, 1);
            LocalDate harvest = LocalDate.of(2026, 6, 1);
            assertThatThrownBy(() -> CropService.validateDates(sowing, harvest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be before");
        }

        @Test
        @DisplayName("TC-04 (Null input): both dates null -> valid (dates are optional)")
        void nullInput_bothDatesNull_isValid() {
            assertThatCode(() -> CropService.validateDates(null, null)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("TC-05 (Null input): only sowing date provided -> valid")
        void nullInput_onlyHarvestDateNull_isValid() {
            assertThatCode(() -> CropService.validateDates(LocalDate.now(), null)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("normalizeStatus() - defaulting and allowed-value validation")
    class NormalizeStatusTests {

        @Test
        @DisplayName("TC-06 (Null input): null status defaults to PLANNED")
        void nullStatus_defaultsToPlanned() {
            assertThat(CropService.normalizeStatus(null)).isEqualTo("PLANNED");
        }

        @Test
        @DisplayName("TC-07 (Empty input): blank status defaults to PLANNED")
        void blankStatus_defaultsToPlanned() {
            assertThat(CropService.normalizeStatus("   ")).isEqualTo("PLANNED");
        }

        @Test
        @DisplayName("TC-08 (Normal): lower-case valid status is normalized to upper-case")
        void validLowerCaseStatus_isNormalized() {
            assertThat(CropService.normalizeStatus("growing")).isEqualTo("GROWING");
        }

        @Test
        @DisplayName("TC-09 (Invalid value): unrecognized status throws IllegalArgumentException")
        void invalidStatus_throws() {
            assertThatThrownBy(() -> CropService.normalizeStatus("ROTTEN"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid crop status");
        }
    }

    // ------------------------------------------------------------------
    // Service-level behavior - mocked repositories
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("createCrop()")
    class CreateCropTests {

        @Test
        @DisplayName("TC-10 (Normal): valid crop for an owned farm is created successfully")
        void createCrop_success() {
            when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
            when(farmRepository.findByIdAndOwnerId(10L, 1L)).thenReturn(Optional.of(farm));
            when(cropRepository.save(any(Crop.class))).thenAnswer(inv -> {
                Crop c = inv.getArgument(0);
                c.setId(100L);
                return c;
            });

            CropRequest request = new CropRequest();
            request.setFarmId(10L);
            request.setCropName("Wheat");
            request.setSowingDate(LocalDate.of(2026, 11, 1));
            request.setExpectedHarvestDate(LocalDate.of(2027, 3, 15));

            CropResponse response = cropService.createCrop(farmer.getEmail(), request);

            assertThat(response.getId()).isEqualTo(100L);
            assertThat(response.getFarmId()).isEqualTo(10L);
            assertThat(response.getCropName()).isEqualTo("Wheat");
            assertThat(response.getStatus()).isEqualTo("PLANNED");
            verify(cropRepository, times(1)).save(any(Crop.class));
        }

        @Test
        @DisplayName("TC-11 (Exception): farm does not exist / is not owned by caller -> ResourceNotFoundException")
        void createCrop_farmNotOwned_throwsNotFound() {
            when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
            when(farmRepository.findByIdAndOwnerId(999L, 1L)).thenReturn(Optional.empty());

            CropRequest request = new CropRequest();
            request.setFarmId(999L);
            request.setCropName("Wheat");

            assertThatThrownBy(() -> cropService.createCrop(farmer.getEmail(), request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Farm not found");

            verify(cropRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC-12 (Exception): authenticated principal does not resolve to a user -> ResourceNotFoundException")
        void createCrop_userNotFound_throwsNotFound() {
            when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

            CropRequest request = new CropRequest();
            request.setFarmId(10L);
            request.setCropName("Wheat");

            assertThatThrownBy(() -> cropService.createCrop("ghost@example.com", request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found");

            verifyNoInteractions(cropRepository);
        }

        @Test
        @DisplayName("TC-13 (Business rule violation): invalid date range is rejected before save")
        void createCrop_invalidDateRange_throwsAndDoesNotSave() {
            when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
            when(farmRepository.findByIdAndOwnerId(10L, 1L)).thenReturn(Optional.of(farm));

            CropRequest request = new CropRequest();
            request.setFarmId(10L);
            request.setCropName("Wheat");
            request.setSowingDate(LocalDate.of(2026, 10, 1));
            request.setExpectedHarvestDate(LocalDate.of(2026, 6, 1)); // before sowing

            assertThatThrownBy(() -> cropService.createCrop(farmer.getEmail(), request))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(cropRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getCrop()")
    class GetCropTests {

        @Test
        @DisplayName("TC-14 (Exception): crop belongs to a farm the caller does not own -> ResourceNotFoundException")
        void getCrop_ownedByAnotherUser_throwsNotFound() {
            Crop otherUsersCrop = Crop.builder().id(55L).farmId(999L).cropName("Rice").status("PLANNED").build();

            when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
            when(cropRepository.findById(55L)).thenReturn(Optional.of(otherUsersCrop));
            when(farmRepository.findByIdAndOwnerId(999L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cropService.getCrop(farmer.getEmail(), 55L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Crop not found");
        }

        @Test
        @DisplayName("TC-15 (Exception): crop id does not exist at all -> ResourceNotFoundException")
        void getCrop_nonexistentId_throwsNotFound() {
            when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
            when(cropRepository.findById(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cropService.getCrop(farmer.getEmail(), 404L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("TC-16 (Normal): existing, owned crop is returned")
        void getCrop_ownedCrop_returnsSuccessfully() {
            Crop crop = Crop.builder().id(55L).farmId(10L).cropName("Rice").status("GROWING").build();

            when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
            when(cropRepository.findById(55L)).thenReturn(Optional.of(crop));
            when(farmRepository.findByIdAndOwnerId(10L, 1L)).thenReturn(Optional.of(farm));

            CropResponse response = cropService.getCrop(farmer.getEmail(), 55L);

            assertThat(response.getCropName()).isEqualTo("Rice");
            assertThat(response.getStatus()).isEqualTo("GROWING");
        }
    }

    @Nested
    @DisplayName("updateCrop()")
    class UpdateCropTests {

        @Test
        @DisplayName("TC-17 (Normal): valid update persists new field values")
        void updateCrop_success() {
            Crop existing = Crop.builder().id(55L).farmId(10L).cropName("Rice").status("PLANNED").build();

            when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
            when(cropRepository.findById(55L)).thenReturn(Optional.of(existing));
            when(farmRepository.findByIdAndOwnerId(10L, 1L)).thenReturn(Optional.of(farm));
            when(cropRepository.save(any(Crop.class))).thenAnswer(inv -> inv.getArgument(0));

            CropRequest request = new CropRequest();
            request.setFarmId(10L);
            request.setCropName("Basmati Rice");
            request.setStatus("harvest_ready");

            CropResponse response = cropService.updateCrop(farmer.getEmail(), 55L, request);

            assertThat(response.getCropName()).isEqualTo("Basmati Rice");
            assertThat(response.getStatus()).isEqualTo("HARVEST_READY");
        }

        @Test
        @DisplayName("TC-18 (Business rule violation): reassigning farmId on update is rejected")
        void updateCrop_reassignFarm_throws() {
            Crop existing = Crop.builder().id(55L).farmId(10L).cropName("Rice").status("PLANNED").build();

            when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
            when(cropRepository.findById(55L)).thenReturn(Optional.of(existing));
            when(farmRepository.findByIdAndOwnerId(10L, 1L)).thenReturn(Optional.of(farm));

            CropRequest request = new CropRequest();
            request.setFarmId(999L); // different farm
            request.setCropName("Rice");

            assertThatThrownBy(() -> cropService.updateCrop(farmer.getEmail(), 55L, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Reassigning");

            verify(cropRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteCrop() and listCropsForFarm()")
    class DeleteAndListTests {

        @Test
        @DisplayName("TC-19 (Normal): delete removes an owned crop")
        void deleteCrop_success() {
            Crop existing = Crop.builder().id(55L).farmId(10L).cropName("Rice").status("PLANNED").build();

            when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
            when(cropRepository.findById(55L)).thenReturn(Optional.of(existing));
            when(farmRepository.findByIdAndOwnerId(10L, 1L)).thenReturn(Optional.of(farm));

            cropService.deleteCrop(farmer.getEmail(), 55L);

            verify(cropRepository, times(1)).delete(existing);
        }

        @Test
        @DisplayName("TC-20 (Exception): listing crops for a farm the caller does not own is rejected")
        void listCrops_farmNotOwned_throwsNotFound() {
            when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
            when(farmRepository.findByIdAndOwnerId(999L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cropService.listCropsForFarm(farmer.getEmail(), 999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(cropRepository);
        }

        @Test
        @DisplayName("TC-21 (Normal): listing crops for an owned farm returns all its crops")
        void listCrops_success() {
            Crop c1 = Crop.builder().id(1L).farmId(10L).cropName("Wheat").status("PLANNED").build();
            Crop c2 = Crop.builder().id(2L).farmId(10L).cropName("Rice").status("GROWING").build();

            when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
            when(farmRepository.findByIdAndOwnerId(10L, 1L)).thenReturn(Optional.of(farm));
            when(cropRepository.findByFarmId(10L)).thenReturn(List.of(c1, c2));

            List<CropResponse> result = cropService.listCropsForFarm(farmer.getEmail(), 10L);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(CropResponse::getCropName).containsExactly("Wheat", "Rice");
        }
    }
}
