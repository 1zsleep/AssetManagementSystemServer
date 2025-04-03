package com.example.assetManagementSystemServer.repository.asset;

import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.entity.asset.Book;

import java.util.List;

public interface BookRepository extends BaseRepository<Book, Long> {
    Book findByBookId(Long bookId);

    Book findFirstByBookId(Long bookId);

    Book findFirstByTitle(String title);
}
