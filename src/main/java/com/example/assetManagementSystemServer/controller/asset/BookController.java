package com.example.assetManagementSystemServer.controller.asset;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.entity.asset.Book;
import com.example.assetManagementSystemServer.service.asset.BookService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/book","/test/books"})
@AllArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public Items<Book> getBooks(ListParam listParam) {
        return bookService.list(listParam);
    }

    @PostMapping
    public void issueBook(@RequestBody long id) {
        bookService.issueBook(id);
    }
}
