package org.example.zhonglun.service;

import org.example.zhonglun.dto.request.CreateAddressRequest;
import org.example.zhonglun.entity.Address;

import java.util.List;

public interface AddressService {
    List<Address> getAddressesByCustomerId(Long customerId);
    Address createAddress(CreateAddressRequest request, Long customerId);
    Address updateAddress(Long addressId, CreateAddressRequest request, Long customerId);
    void deleteAddress(Long addressId, Long customerId);
}
