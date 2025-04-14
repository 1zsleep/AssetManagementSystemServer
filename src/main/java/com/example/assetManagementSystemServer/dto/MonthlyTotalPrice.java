package com.example.assetManagementSystemServer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 用于接收每月总价统计结果
@Data
@AllArgsConstructor
public class MonthlyTotalPrice {
    private Integer year;       // 年份
    private Integer month;      // 月份
    private Double totalPrice;  // 当月总价
}