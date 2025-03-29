// src/main/java/com/example/assetManagementSystemServer/config/CosConfig.java
package com.example.assetManagementSystemServer.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosConfig {

    @Value("${cos.secretId}")
    private String secretId;

    @Value("${cos.secretKey}")
    private String secretKey;

    /**
     * 区域
     */
    @Value("${cos.region}")
    private String region;

    /**
     * 桶名
     */
    private String bucket;

    @Bean
    public COSClient cosClient() {
        return new COSClient(
                new BasicCOSCredentials(secretId, secretKey),
                new ClientConfig(new Region(region))
        );
    }
}