package com.practice.onlineShop.mappers;

import com.practice.onlineShop.entities.Product;
import com.practice.onlineShop.vos.ProductVO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toEntity(ProductVO productVO){
        if(productVO == null){
            return null;
        }

        Product product = new Product();
        product.setCode(productVO.getCode());
        product.setPrice(productVO.getPrice());
        product.setDescription(productVO.getDescription());
        product.setId(productVO.getId());
        product.setStock(productVO.getStock());
        product.setValid(productVO.isValid());
        product.setCurrencies(productVO.getCurrencies());

        return product;
    }
    public ProductVO toVO(Product product) {
        if(product == null){
            return null;
        }


        ProductVO productVO = new ProductVO();
        productVO.setCode(product.getCode());
        productVO.setPrice(product.getPrice());
        productVO.setDescription(product.getDescription());
        productVO.setId(product.getId());
        productVO.setStock(product.getStock());
        productVO.setValid(product.isValid());
        productVO.setCurrencies(product.getCurrencies());

        return productVO;
    }
}
