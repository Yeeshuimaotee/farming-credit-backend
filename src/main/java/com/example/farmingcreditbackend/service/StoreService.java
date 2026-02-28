package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.entity.Store;
import com.example.farmingcreditbackend.exception.BusinessException;
import com.example.farmingcreditbackend.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreMapper storeMapper;

    public Long getStoreIdByOwnerId(Long ownerId) {
        Long storeId = storeMapper.getStoreIdByOwnerId(ownerId);
        if (storeId == null) {
            throw new BusinessException("未找到店铺信息，请确认您已开通店铺");
        }
        return storeId;
    }

    public void createStore(Store store) {
        storeMapper.insert(store);
    }
}