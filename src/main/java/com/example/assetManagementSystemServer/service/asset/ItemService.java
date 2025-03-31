package com.example.assetManagementSystemServer.service.asset;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.entity.asset.Item;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.repository.asset.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 资产项服务类 - 负责资产项的创建、库存调整等业务逻辑
 * 继承自通用基础服务类 BaseService，提供基础的 CRUD 操作
 */
@Service // 声明为 Spring 的服务组件
@RequiredArgsConstructor // Lombok 注解，自动生成包含 final 字段的构造函数
public class ItemService extends BaseService<Item, Long> {
    private final ItemRepository itemRepository;

    /**
     * 获取具体的 Repository 实现
     * @return 资产项数据库操作接口
     */
    @Override
    protected ItemRepository getRepository() {
        return itemRepository;
    }

    /**
     * 保存资产项（包含库存初始化逻辑）
     * @Transactional 注解保证方法的事务性（原子性操作）
     * @param item 要保存的资产项对象
     * @return 保存后的资产项对象
     */
    @Transactional
    public Item saveItem(Item item) {
        // 初始化库存设置：如果未设置当前库存，默认设为0表示需要手动管理
        if (item.getCurrentStock() == null) {
            item.setCurrentStock(0);
        }
        return itemRepository.save(item);
    }

    /**
     * 调整库存数量（支持增减操作）
     * @Transactional 注解保证方法的事务性（原子性操作）
     * @param itemId 目标资产项ID
     * @param delta 库存变化量（正数表示增加，负数表示减少）
     * @return 调整后的最新库存量
     * @throws BusinessException 当资产项不存在或库存不足时抛出业务异常
     */
    @Transactional
    public int adjustStock(Long itemId, int delta) {
        // 1. 验证资产项是否存在
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ResponseStatusEnum.ITEM_NOT_FOUND));

        // 2. 计算新的库存量
        int newStock = item.getCurrentStock() + delta;

        // 3. 库存不足校验（防止库存变为负数）
        if (newStock < 0) {
            throw new BusinessException(ResponseStatusEnum.INSUFFICIENT_INVENTORY);
        }

        // 4. 更新库存并持久化
        item.setCurrentStock(newStock);
        itemRepository.save(item);

        return newStock;
    }


}
