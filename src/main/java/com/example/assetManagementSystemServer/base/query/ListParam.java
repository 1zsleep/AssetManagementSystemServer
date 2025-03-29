package com.example.assetManagementSystemServer.base.query;

import com.example.assetManagementSystemServer.enums.BucketType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListParam {
    private Integer limit = 10;
    private Integer offset = 0;
    private boolean count = true;
    private String filter;
    private BucketType bucketType;
}