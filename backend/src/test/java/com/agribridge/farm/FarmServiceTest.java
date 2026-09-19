package com.agribridge.farm;

import com.agribridge.exception.ResourceNotFoundException;
import com.agribridge.farm.dto.FarmRequest;
import com.agribridge.farm.dto.FarmResponse;
import com.agribridge.user.Role;
import com.agribridge.user.User;
import com.agribridge.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FarmServiceTest {

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FarmService farmService;

    private User farmer;

    @BeforeEach
    void setUp() {
        farmer = User.builder()
                .id(1L)
                .name("Ravi Kumar")
                .email("ravi.kumar@example.com")
                .passwordHash("hashed")
                .role(Role.FARMER)
                .build();
    }

    @Test
    void createFarm_persistsFarmForResolvedOwner() {
        when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
        when(farmRepository.save(any(Farm.class))).thenAnswer(invocation -> {
            Farm f = invocation.getArgument(0);
            f.setId(10L);
            return f;
        });

        FarmRequest request = new FarmRequest();
        request.setFarmName("Green Valley Farm");
        request.setLocation("Hoshiarpur, Punjab");
        request.setLandArea(2.5);
        request.setSoilType("Loamy");

        FarmResponse response = farmService.createFarm(farmer.getEmail(), request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getOwnerId()).isEqualTo(farmer.getId());
        assertThat(response.getFarmName()).isEqualTo("Green Valley Farm");
        verify(farmRepository, times(1)).save(any(Farm.class));
    }

    @Test
    void getFarm_throwsWhenFarmNotOwnedByCaller() {
        when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
        when(farmRepository.findByIdAndOwnerId(99L, farmer.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> farmService.getFarm(farmer.getEmail(), 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void listMyFarms_returnsOnlyFarmsOwnedByCaller() {
        Farm farmA = Farm.builder().id(1L).ownerId(1L).farmName("Farm A").location("Loc A").landArea(1.0).build();
        Farm farmB = Farm.builder().id(2L).ownerId(1L).farmName("Farm B").location("Loc B").landArea(2.0).build();

        when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
        when(farmRepository.findByOwnerId(farmer.getId())).thenReturn(List.of(farmA, farmB));

        List<FarmResponse> result = farmService.listMyFarms(farmer.getEmail());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(FarmResponse::getFarmName).containsExactly("Farm A", "Farm B");
    }

    @Test
    void deleteFarm_removesFarmWhenOwnedByCaller() {
        Farm farm = Farm.builder().id(5L).ownerId(1L).farmName("Farm A").location("Loc A").landArea(1.0).build();

        when(userRepository.findByEmail(farmer.getEmail())).thenReturn(Optional.of(farmer));
        when(farmRepository.findByIdAndOwnerId(5L, farmer.getId())).thenReturn(Optional.of(farm));

        farmService.deleteFarm(farmer.getEmail(), 5L);

        verify(farmRepository, times(1)).delete(farm);
    }
}
