package org.example.zhonglundemo2.service.impl;

import org.example.zhonglundemo2.dto.LoginRequest;
import org.example.zhonglundemo2.dto.LoginResponse;
import org.example.zhonglundemo2.dto.RegisterRequest;
import org.example.zhonglundemo2.entity.Customer;
import org.example.zhonglundemo2.entity.Merchant;
import org.example.zhonglundemo2.enums.UserRole;
import org.example.zhonglundemo2.exception.BusinessException;
import org.example.zhonglundemo2.exception.ResourceNotFoundException;
import org.example.zhonglundemo2.repository.CustomerRepository;
import org.example.zhonglundemo2.repository.MerchantRepository;
import org.example.zhonglundemo2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;

    @Override
    @Transactional
    public void registerCustomer(RegisterRequest request) {
        if (customerRepository.findByEmail(request.getEmail()).isPresent() ||
                merchantRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Email already in use");
        }

        Customer customer = new Customer();
        customer.setEmail(request.getEmail());
        customer.setPassword(request.getPassword());
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void registerMerchant(RegisterRequest request) {
        if (customerRepository.findByEmail(request.getEmail()).isPresent() ||
                merchantRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Email already in use");
        }

        Merchant merchant = new Merchant();
        merchant.setEmail(request.getEmail());
        merchant.setPassword(request.getPassword());
        merchant.setBusinessName(request.getBusinessName());
        merchant.setContactPerson(request.getContactPerson());
        merchant.setPhone(request.getPhone());
        merchant.setAddress(request.getAddress());
        merchant.setApproved(false);
        merchantRepository.save(merchant);
    }

    @Override
    @Transactional
    public void registerAdmin(RegisterRequest request) {
        throw new BusinessException("Admin registration is not allowed through this interface");
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (customer != null && customer.getPassword().equals(request.getPassword())) {
            return new LoginResponse(
                    customer.getId(),
                    customer.getEmail(),
                    customer.getName(),
                    UserRole.CUSTOMER,
                    "dummy-token"
            );
        }


        Merchant merchant = merchantRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (merchant.getPassword().equals(request.getPassword())) {
            return new LoginResponse(
                    merchant.getId(),
                    merchant.getEmail(),
                    merchant.getBusinessName(),
                    UserRole.MERCHANT,
                    "dummy-token"
            );
        }

        throw new BusinessException("Invalid password");
    }
}
