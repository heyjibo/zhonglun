package org.example.zhonglundemo2.controller;

import org.example.zhonglundemo2.entity.Customer;
import org.example.zhonglundemo2.entity.Merchant;
import org.example.zhonglundemo2.repository.CustomerRepository;
import org.example.zhonglundemo2.repository.MerchantRepository;
import org.example.zhonglundemo2.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final MerchantService merchantService;

    @GetMapping("/customers")
    public ResponseEntity<Page<Customer>> getAllCustomers(Pageable pageable) {
        return ResponseEntity.ok(customerRepository.findAll(pageable));
    }

    @GetMapping("/merchants")
    public ResponseEntity<Page<Merchant>> getAllMerchants(Pageable pageable) {
        return ResponseEntity.ok(merchantRepository.findAll(pageable));
    }

    @PatchMapping("/merchants/{id}/approve")
    public ResponseEntity<Void> approveMerchant(
            @PathVariable Long id,
            @RequestParam boolean approved) {
        merchantService.approveMerchant(id, approved);
        return ResponseEntity.ok().build();
    }
}
