package com.example.assetManagementSystemServer.repository.purchase;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.dto.MonthlyTotalPrice;
import com.example.assetManagementSystemServer.entity.purchase.PurchaseOrder;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseOrderRepository extends BaseRepository<PurchaseOrder, Long>{
    PurchaseOrder findFirstById(Long id);

    /**
     * 获取已归档状态的月度采购总金额统计
     * 使用原生SQL实现跨数据库兼容
     */
    @Query(nativeQuery = true, value = """
        SELECT
            EXTRACT(YEAR FROM po.purchase_date) AS year,
            EXTRACT(MONTH FROM po.purchase_date) AS month,
            SUM(po.total_price) AS total_price
        FROM purchase_order po
        WHERE po.status = '已归档'
        GROUP BY year, month
        ORDER BY year ASC, month ASC
        """)
    List<MonthlyTotalPrice> findMonthlyTotalPriceByArchivedStatus();
}
