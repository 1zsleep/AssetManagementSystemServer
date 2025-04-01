package com.example.assetManagementSystemServer.entity.asset;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 书籍表实体类，对应数据库表 books
 */
@Entity
@Data
@Table(name = "books")
public class Book {

    /**
     * 书籍唯一标识 (INT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    /**
     * 国际标准书号（唯一）
     */
    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    /**
     * 书名
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * 作者
     */
    @Column(name = "author")
    private String author;

    /**
     * 出版社
     */
    @Column(name = "publisher")
    private String publisher;

    /**
     * 购买日期
     */
    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    /**
     * 总库存数量
     */
    @Column(name = "stock_quantity")
    private Integer stockQuantity;


}