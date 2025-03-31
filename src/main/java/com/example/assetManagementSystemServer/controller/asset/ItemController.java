package com.example.assetManagementSystemServer.controller.asset;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.entity.asset.Item;
import com.example.assetManagementSystemServer.service.asset.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Items<Item> listItems(ListParam listParam) {
        return itemService.list(listParam);
    }

    @PostMapping
    public Item saveItem(@RequestBody Item item) {
        return itemService.saveItem(item);
    }

    @PutMapping
    public int adjustStock(Long itemId, int delta) {
        return itemService.adjustStock(itemId, delta);
    }
}
