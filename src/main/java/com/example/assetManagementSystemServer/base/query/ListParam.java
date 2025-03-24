package com.example.assetManagementSystemServer.base.query;

import lombok.Data;

@Data
public class ListParam {
    private Integer limit = 10;
    private Integer offset = 0;
    private boolean count = true;
    private String filter;
}