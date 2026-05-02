package com.auction.modules.category.service.impl;

import com.auction.modules.category.entity.AuctionCategory;
import com.auction.modules.category.mapper.AuctionCategoryMapper;
import com.auction.modules.category.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final AuctionCategoryMapper categoryMapper;

    @Override
    public List<AuctionCategory> listActive() {
        return categoryMapper.selectList(new LambdaQueryWrapper<AuctionCategory>()
                .eq(AuctionCategory::getStatus, 1).orderByAsc(AuctionCategory::getSort).orderByDesc(AuctionCategory::getId));
    }

    @Override
    public List<AuctionCategory> listAll() {
        return categoryMapper.selectList(new LambdaQueryWrapper<AuctionCategory>()
                .orderByAsc(AuctionCategory::getSort).orderByDesc(AuctionCategory::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuctionCategory create(AuctionCategory category) {
        if (category.getStatus() == null) category.setStatus(1);
        if (category.getParentId() == null) category.setParentId(0L);
        if (category.getSort() == null) category.setSort(0);
        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuctionCategory update(AuctionCategory category) {
        if (category.getId() == null) throw new IllegalArgumentException("分类ID不能为空");
        if (categoryMapper.selectById(category.getId()) == null) throw new IllegalArgumentException("分类不存在");
        categoryMapper.updateById(category);
        return categoryMapper.selectById(category.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
