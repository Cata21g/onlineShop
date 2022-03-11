package com.practice.onlineShop.services;

import com.practice.onlineShop.entities.Product;
import com.practice.onlineShop.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {
    private final ProductRepository productRepository;
    public boolean isHavingEnoughStock(Integer productId, Integer quantity) {
        Product product = productRepository.findById(productId.longValue()).get();
//        if(product.getStock() >= quantity){
//            return true;
//        }
//        return false;
        return product.getStock() >= quantity;
    }
}
