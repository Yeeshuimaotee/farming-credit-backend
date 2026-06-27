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

    /**
     * 根据店主 ID 获取店铺信息
     */
    public Store getStoreByUserId(Long userId) {
        try {
            // 先尝试通过 owner_id 查询店铺 ID
            Long storeId = storeMapper.getStoreIdByOwnerId(userId);
            if (storeId != null) {
                // 通过 ID 查询店铺详情
                return storeMapper.selectById(storeId);
            }
        } catch (Exception e) {
            // 如果查询失败，返回 null
            return null;
        }
        return null;
    }

    /**
     * 根据店主 ID 获取店铺 ID（旧方法，保留兼容性）
     */
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