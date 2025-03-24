package com.example.assetManagementSystemServer.base.query;


import java.util.List;

/**
 * 分页结果记录类
 *
 * @param items Getter方法
 */

public record Items<T>(List<T> items, Long total) {

}