package com.example.ecommerce_backend.modules.shipping.service;

import com.example.ecommerce_backend.modules.shipping.dto.request.AddressRequest;
import com.example.ecommerce_backend.modules.shipping.dto.response.AddressResponse;
import com.example.ecommerce_backend.modules.shipping.entity.ShippingAddress;
import com.example.ecommerce_backend.modules.shipping.exception.AddressNotFoundException;
import com.example.ecommerce_backend.modules.shipping.repository.ShippingAddressRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingAddressServiceTest {

    @Mock
    private ShippingAddressRepository shippingAddressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShippingAddressService shippingAddressService;

    private User user;
    private ShippingAddress address;
    private ShippingAddress defaultAddress;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).uuid("user-uuid").email("test@test.com").build();
        address = ShippingAddress.builder()
                .id(1L).uuid("addr-uuid")
                .user(user)
                .recipientName("John Doe")
                .phone("1234567890")
                .addressLine1("123 Main St")
                .city("New York")
                .state("NY")
                .postalCode("10001")
                .country("USA")
                .isDefault(false)
                .build();
        defaultAddress = ShippingAddress.builder()
                .id(2L).uuid("default-uuid")
                .user(user)
                .recipientName("Jane Doe")
                .phone("9876543210")
                .addressLine1("456 Oak St")
                .city("Los Angeles")
                .state("CA")
                .postalCode("90001")
                .country("USA")
                .isDefault(true)
                .build();
    }

    // --- getAddresses ---

    @Test
    void getAddresses_shouldReturnList() {
        when(shippingAddressRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(address, defaultAddress));

        List<AddressResponse> result = shippingAddressService.getAddresses(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    void getAddresses_whenEmpty_shouldReturnEmptyList() {
        when(shippingAddressRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of());

        List<AddressResponse> result = shippingAddressService.getAddresses(1L);

        assertThat(result).isEmpty();
    }

    // --- getByUuid ---

    @Test
    void getByUuid_shouldReturnAddress() {
        when(shippingAddressRepository.findByUuid("addr-uuid")).thenReturn(Optional.of(address));

        AddressResponse result = shippingAddressService.getByUuid("addr-uuid", 1L);

        assertThat(result.getUuid()).isEqualTo("addr-uuid");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(shippingAddressRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingAddressService.getByUuid("nonexistent", 1L))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    void getByUuid_whenNotOwnedByUser_shouldThrow() {
        User otherUser = User.builder().id(2L).build();
        ShippingAddress otherAddress = ShippingAddress.builder()
                .id(3L).uuid("other-uuid")
                .user(otherUser)
                .recipientName("Other")
                .build();

        when(shippingAddressRepository.findByUuid("other-uuid")).thenReturn(Optional.of(otherAddress));

        assertThatThrownBy(() -> shippingAddressService.getByUuid("other-uuid", 1L))
                .isInstanceOf(AddressNotFoundException.class);
    }

    // --- create ---

    @Test
    void create_shouldCreateAddress() {
        AddressRequest request = new AddressRequest();
        request.setRecipientName("John Doe");
        request.setPhone("1234567890");
        request.setAddressLine1("123 Main St");
        request.setCity("New York");
        request.setState("NY");
        request.setPostalCode("10001");
        request.setCountry("USA");
        request.setDefault(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(shippingAddressRepository.save(any(ShippingAddress.class))).thenReturn(address);

        AddressResponse result = shippingAddressService.create(request, 1L);

        assertThat(result.getUuid()).isEqualTo("addr-uuid");
    }

    @Test
    void create_whenUserNotFound_shouldThrow() {
        AddressRequest request = new AddressRequest();
        request.setRecipientName("John Doe");
        request.setPhone("1234567890");
        request.setAddressLine1("123 Main St");
        request.setCity("New York");
        request.setState("NY");
        request.setPostalCode("10001");
        request.setCountry("USA");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingAddressService.create(request, 1L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void create_withDefaultTrue_shouldUnsetExistingDefault() {
        AddressRequest request = new AddressRequest();
        request.setRecipientName("New Default");
        request.setPhone("5555555555");
        request.setAddressLine1("789 Pine St");
        request.setCity("Chicago");
        request.setState("IL");
        request.setPostalCode("60601");
        request.setCountry("USA");
        request.setDefault(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(shippingAddressRepository.findByUserIdAndIsDefaultTrue(1L))
                .thenReturn(Optional.of(defaultAddress));
        when(shippingAddressRepository.save(any(ShippingAddress.class))).thenReturn(address);

        shippingAddressService.create(request, 1L);

        assertThat(defaultAddress.isDefault()).isFalse();
        verify(shippingAddressRepository, times(2)).save(any(ShippingAddress.class));
    }

    // --- update ---

    @Test
    void update_shouldUpdateAddress() {
        AddressRequest request = new AddressRequest();
        request.setRecipientName("John Updated");
        request.setPhone("1234567890");
        request.setAddressLine1("123 Main St");
        request.setCity("New York");
        request.setState("NY");
        request.setPostalCode("10001");
        request.setCountry("USA");
        request.setDefault(false);

        when(shippingAddressRepository.findByUuid("addr-uuid")).thenReturn(Optional.of(address));
        when(shippingAddressRepository.save(any(ShippingAddress.class))).thenReturn(address);

        AddressResponse result = shippingAddressService.update("addr-uuid", request, 1L);

        assertThat(result.getUuid()).isEqualTo("addr-uuid");
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        AddressRequest request = new AddressRequest();
        request.setRecipientName("John");
        request.setPhone("123");

        when(shippingAddressRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingAddressService.update("nonexistent", request, 1L))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    void update_whenNotOwned_shouldThrow() {
        User otherUser = User.builder().id(2L).build();
        ShippingAddress otherAddress = ShippingAddress.builder()
                .id(3L).uuid("other-uuid")
                .user(otherUser)
                .build();

        AddressRequest request = new AddressRequest();
        request.setRecipientName("John");
        request.setPhone("123");

        when(shippingAddressRepository.findByUuid("other-uuid")).thenReturn(Optional.of(otherAddress));

        assertThatThrownBy(() -> shippingAddressService.update("other-uuid", request, 1L))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    void update_withDefaultTrue_shouldUnsetExistingDefault() {
        AddressRequest request = new AddressRequest();
        request.setRecipientName("John Updated");
        request.setPhone("1234567890");
        request.setAddressLine1("123 Main St");
        request.setCity("New York");
        request.setState("NY");
        request.setPostalCode("10001");
        request.setCountry("USA");
        request.setDefault(true);

        when(shippingAddressRepository.findByUuid("addr-uuid")).thenReturn(Optional.of(address));
        when(shippingAddressRepository.findByUserIdAndIsDefaultTrue(1L))
                .thenReturn(Optional.of(defaultAddress));
        when(shippingAddressRepository.save(any(ShippingAddress.class))).thenReturn(address);

        shippingAddressService.update("addr-uuid", request, 1L);

        assertThat(defaultAddress.isDefault()).isFalse();
    }

    // --- setDefault ---

    @Test
    void setDefault_shouldSetDefaultAndUnsetOthers() {
        when(shippingAddressRepository.findByUuid("addr-uuid")).thenReturn(Optional.of(address));
        when(shippingAddressRepository.findByUserIdAndIsDefaultTrue(1L))
                .thenReturn(Optional.of(defaultAddress));
        when(shippingAddressRepository.save(any(ShippingAddress.class))).thenReturn(address);

        shippingAddressService.setDefault("addr-uuid", 1L);

        assertThat(address.isDefault()).isTrue();
        assertThat(defaultAddress.isDefault()).isFalse();
        verify(shippingAddressRepository, times(2)).save(any(ShippingAddress.class));
    }

    @Test
    void setDefault_whenNotFound_shouldThrow() {
        when(shippingAddressRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingAddressService.setDefault("nonexistent", 1L))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    void setDefault_whenNotOwned_shouldThrow() {
        User otherUser = User.builder().id(2L).build();
        ShippingAddress otherAddress = ShippingAddress.builder()
                .id(3L).uuid("other-uuid")
                .user(otherUser)
                .build();

        when(shippingAddressRepository.findByUuid("other-uuid")).thenReturn(Optional.of(otherAddress));

        assertThatThrownBy(() -> shippingAddressService.setDefault("other-uuid", 1L))
                .isInstanceOf(AddressNotFoundException.class);
    }

    // --- delete ---

    @Test
    void delete_shouldDeleteAddress() {
        when(shippingAddressRepository.findByUuid("addr-uuid")).thenReturn(Optional.of(address));

        shippingAddressService.delete("addr-uuid", 1L);

        verify(shippingAddressRepository).delete(address);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(shippingAddressRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingAddressService.delete("nonexistent", 1L))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    void delete_whenNotOwned_shouldThrow() {
        User otherUser = User.builder().id(2L).build();
        ShippingAddress otherAddress = ShippingAddress.builder()
                .id(3L).uuid("other-uuid")
                .user(otherUser)
                .build();

        when(shippingAddressRepository.findByUuid("other-uuid")).thenReturn(Optional.of(otherAddress));

        assertThatThrownBy(() -> shippingAddressService.delete("other-uuid", 1L))
                .isInstanceOf(AddressNotFoundException.class);
    }
}
