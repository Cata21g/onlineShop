package com.practice.onlineShop.controllers;

import com.practice.onlineShop.exceptions.InvalidCustomerIdExceptions;
import com.practice.onlineShop.exceptions.InvalidProductCodeException;
import com.practice.onlineShop.services.ProductService;
import com.practice.onlineShop.vos.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/{customerId}")
    public void addProduct(@RequestBody ProductVO productVO, @PathVariable Long customerId) {// deoarece trimitem un body punem @RequestBody

        productService.addProduct(productVO, customerId);
    }

    //{productCode} productCode se afla intre acolade deoarece folosim @PathVariable
    @GetMapping("/{productCode}")
    public ProductVO getProduct(@PathVariable String productCode) throws InvalidProductCodeException {
        return productService.getProduct(productCode);
    }

    @GetMapping
    public ProductVO[] getProducts() {
        return productService.getProducts().toArray(new ProductVO[]{});
    }

    @PutMapping("/{customerId}")
    public void updateProduct(@RequestBody ProductVO productVO, @PathVariable Long customerId) throws InvalidProductCodeException {
        productService.updateProduct(productVO, customerId);
    }

    @DeleteMapping("/{productCode}/{customerId}")
    public void deleteProduct(@PathVariable String productCode, @PathVariable Long custtomerId) throws InvalidProductCodeException {
        productService.deleteProduct(productCode, custtomerId);
    }

    @PatchMapping("/{productCode}/{quantity}/{ustomerId}")
    public void addStock(@PathVariable String productCode, @PathVariable Integer quantity, @PathVariable Long customerId) throws InvalidProductCodeException {
        productService.addStock(productCode, quantity, customerId);
    }

}
