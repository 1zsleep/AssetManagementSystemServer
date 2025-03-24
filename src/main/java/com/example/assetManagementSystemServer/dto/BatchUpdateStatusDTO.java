package com.example.assetManagementSystemServer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BatchUpdateStatusDTO {
    @NotNull(message = "用户ID列表不能为空")
    private List<Integer> ids;

    @NotNull(message = "状态值不能为空")
    private Boolean status;
}