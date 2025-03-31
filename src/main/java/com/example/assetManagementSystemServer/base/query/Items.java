package com.example.assetManagementSystemServer.base.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果记录类
 */
public record Items<T>(List<T> items, Long total) {

}