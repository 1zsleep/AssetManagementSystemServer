package com.example.assetManagementSystemServer.entity.supplier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应商信息实体类
 */
@Getter
@Setter
@Entity
@Table(name = "suppliers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "supplier_name"),
                @UniqueConstraint(columnNames = "tax_number")
        })
public class Supplier {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 供应商全称（唯一）
     * 示例：XX科技有限公司
     */
    @Column(name = "supplier_name", nullable = false, length = 100)
    private String name;

    /**
     * 供应商简称
     * 示例：XX科技
     */
    @Column(name = "short_name", length = 50)
    private String shortName;

    /**
     * 状态
     * 合作中,合作结束,待审核,黑名单
     */
    @Column(nullable = false)
    private String status;

    /**
     * 供应商类型
     * 示例：生产商/代理商/服务商
     */
    @Column(name = "supplier_type", nullable = false, length = 20)
    private String type;

    /**
     * 主要联系人姓名
     * 示例：张三
     */
    @Column(name = "primary_contact", nullable = false, length = 50)
    private String primaryContact;

    /**
     * 联系电话
     * 示例：13800138000
     */
    @Column(name = "contact_phone", nullable = false, length = 20)
    private String phone;

    /**
     * 联系邮箱
     * 示例：contact@example.com
     */
    @Column(name = "contact_email", length = 100)
    private String email;

    /**
     * 详细地址
     * 示例：北京市海淀区XX路XX号
     */
    @Column(nullable = false, length = 200)
    private String address;

    /**
     * 行政区划代码
     * 示例：110101（关联地区编码表）
     */
    @Column(name = "region_code", length = 10)
    private String regionCode;

    /**
     * 统一社会信用代码/税号（唯一）
     * 示例：91330101XXXXX
     */
    @Column(name = "tax_number", nullable = false, length = 20)
    private String taxNumber;

    /**
     * 开户银行名称
     * 示例：中国银行北京分行
     */
    @Column(name = "bank_name", nullable = false, length = 50)
    private String bankName;

    /**
     * 银行账号（需加密存储）
     * 示例：622848******1234（加密后）
     */
    @Column(name = "bank_account", nullable = false, length = 30)
    private String bankAccount;

    /**
     * 开票类型
     * 0-普通发票，1-增值税专用发票
     */
    @Column(name = "invoice_type", nullable = false)
    private Integer invoiceType;

    /**
     * 合作开始日期
     * 格式：yyyy-MM-dd
     */
    @Column(name = "coop_start_date", nullable = false)
    private LocalDate startDate;

    /**
     * 合作结束日期
     * 格式：yyyy-MM-dd
     */
    @Column(name = "coop_end_date")
    private LocalDate endDate;

    /**
     * 供应商评分（0-5分）
     * 示例：4.50
     */
    @Column(precision = 3, scale = 2)
    private BigDecimal score;

    /**
     * 创建人ID（关联用户表）
     */
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    /**
     * 最后修改人ID
     */
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    /**
     * 创建时间（自动填充）
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 最后修改时间（自动更新）
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // 自动填充时间字段
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}