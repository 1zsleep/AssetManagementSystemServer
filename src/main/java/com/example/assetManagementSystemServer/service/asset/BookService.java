package com.example.assetManagementSystemServer.service.asset;

import com.example.assetManagementSystemServer.base.service.BaseService;
import com.example.assetManagementSystemServer.entity.asset.Book;
import com.example.assetManagementSystemServer.entity.asset.UserAsset;
import com.example.assetManagementSystemServer.entity.user.User;
import com.example.assetManagementSystemServer.enums.AssetStatus;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.repository.asset.BookRepository;
import com.example.assetManagementSystemServer.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class BookService extends BaseService<Book, Long> {
    private final BookRepository bookRepository;
    private final UserService userService;
    private final UserAssetService userAssetService;
    @Override
    protected BookRepository getRepository() {
        return bookRepository;
    }
    public Book getBookById(Long id){
        return bookRepository.findFirstByBookId(id);
    }
    public Book getBookByTitle(String title){
        return bookRepository.findFirstByTitle(title);
    }

    @Transactional
    public void issueBook(Long id){
        Long currentUserId = userService.getCurrentUserId();
        User user = userService.getUserById(currentUserId);
        Book books = bookRepository.findByBookId(id);
        if (books.getStockQuantity() == null || books.getStockQuantity() < 1){
            throw new BusinessException(ResponseStatusEnum.INSUFFICIENT_INVENTORY);
        }



        UserAsset userAsset = new UserAsset();
        userAsset.setAssetType("Book");
        userAsset.setAssetId(id);
        userAsset.setAssetName(books.getTitle());
        userAsset.setQuantity(1);
        userAsset.setUserId(currentUserId);
        userAsset.setUserName(user.getUserName());
        userAsset.setStatus("申请中");
        userAssetService.saveUserAsset(userAsset);
    }


}
