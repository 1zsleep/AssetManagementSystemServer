package com.example.assetManagementSystemServer.config;

import com.example.assetManagementSystemServer.enums.BucketType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

/**
 * 存储路径配置类
 * 支持按存储桶类型和文件类别动态配置路径模板
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "cos.path")
public class StoragePathConfig {
    // 配置示例：
    // cos.path.public.template=public/{category}/{ownerId}/{date}/{uuid}_{filename}
    // cos.path.groups.template=groups/{category}/{ownerId}/{date}/{uuid}_{filename}
    // cos.path.private.template=private/{category}/{ownerId}/{date}/{uuid}_{filename}

    // Getter & Setter
    private Map<BucketType, String> templates = new EnumMap<>(BucketType.class);

    public String getTemplate(BucketType type) {
        return templates.getOrDefault(type, "fallback/{category}/{ownerId}/{date}/{uuid}_{filename}");
    }

}