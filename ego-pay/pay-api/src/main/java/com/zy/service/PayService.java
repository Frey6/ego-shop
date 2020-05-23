package com.zy.service;

import com.zy.domain.Pay;

import java.util.Map;

public interface PayService {
    String pay(Pay pay);

    boolean rsaCheckV1(Map<String, String> params);


//    boolean rsaCheckV1(Map<String, String> paramMap);
}
