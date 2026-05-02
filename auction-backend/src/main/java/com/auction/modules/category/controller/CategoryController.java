package com.auction.modules.category.controller;

import com.auction.common.result.Result;
import com.auction.modules.category.entity.AuctionCategory;
import com.auction.modules.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<AuctionCategory>> list() {
        return Result.ok(categoryService.listActive());
    }
}
