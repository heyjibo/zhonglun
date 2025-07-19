package org.example.zhonglundemo2.service;

import org.example.zhonglundemo2.dto.SalesDataDTO;

public interface SalesService {
    SalesDataDTO getSalesDataByMerchant(Long merchantId);
}