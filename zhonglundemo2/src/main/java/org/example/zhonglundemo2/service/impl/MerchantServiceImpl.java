package org.example.zhonglundemo2.service.impl;

import org.example.zhonglundemo2.entity.Merchant;
import org.example.zhonglundemo2.exception.ResourceNotFoundException;
import org.example.zhonglundemo2.repository.MerchantRepository;
import org.example.zhonglundemo2.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {
    private final MerchantRepository merchantRepository;

    @Override
    @Transactional
    public void approveMerchant(Long id, boolean approved) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found with id: " + id));
        merchant.setApproved(approved);
        merchantRepository.save(merchant);
    }
}