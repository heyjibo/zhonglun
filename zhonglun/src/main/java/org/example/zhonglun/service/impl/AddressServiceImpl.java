package org.example.zhonglun.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.zhonglun.dto.request.CreateAddressRequest;
import org.example.zhonglun.entity.Address;
import org.example.zhonglun.exception.ResourceNotFoundException;
import org.example.zhonglun.repository.AddressRepository;
import org.example.zhonglun.repository.UserRepository;
import org.example.zhonglun.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Address> getAddressesByCustomerId(Long customerId) {
        return addressRepository.findByUserId(customerId);
    }

    @Override
    @Transactional
    public Address createAddress(CreateAddressRequest request, Long customerId) {
        userRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("User", "id", customerId));

        Address address = new Address();
        BeanUtils.copyProperties(request, address);
        address.setUserId(customerId);

        // 如果要设置为默认地址，需要将该用户其他地址的 isDefault 设为 0
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            addressRepository.clearDefaultByUserId(customerId);
        }

        log.info("为用户ID: {} 创建新地址", customerId);
        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public Address updateAddress(Long addressId, CreateAddressRequest request, Long customerId) {
        Address existingAddress = getAddressAndCheckOwnership(addressId, customerId);

        BeanUtils.copyProperties(request, existingAddress);

        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            addressRepository.clearDefaultByUserId(customerId);
        }

        log.info("用户ID: {} 更新地址ID: {}", customerId, addressId);
        return addressRepository.save(existingAddress);
    }

    @Override
    public void deleteAddress(Long addressId, Long customerId) {
        Address addressToDelete = getAddressAndCheckOwnership(addressId, customerId);
        addressRepository.delete(addressToDelete);
        log.info("用户ID: {} 删除了地址ID: {}", customerId, addressId);
    }

    private Address getAddressAndCheckOwnership(Long addressId, Long customerId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        if (!address.getUserId().equals(customerId)) {
            log.warn("安全警告：用户ID: {} 试图操作不属于自己的地址ID: {}", customerId, addressId);
            throw new AccessDeniedException("您无权操作此地址");
        }
        return address;
    }
}
