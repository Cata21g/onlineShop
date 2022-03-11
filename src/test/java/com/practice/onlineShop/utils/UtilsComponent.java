package com.practice.onlineShop.utils;

import com.practice.onlineShop.entities.Address;
import com.practice.onlineShop.entities.Product;
import com.practice.onlineShop.entities.User;
import com.practice.onlineShop.enums.Currencies;
import com.practice.onlineShop.enums.Roles;
import com.practice.onlineShop.repositories.ProductRepository;
import com.practice.onlineShop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class UtilsComponent {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    public static final String LOCALHOST = "http://localhost:";

    //se salveaza in baza de date dupa terminarea metodei la return   @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User saveUserWithRole(Roles role) {
        User userEntity = new User();
        userEntity.setFirstname("adminFirstName");
        Collection<Roles> roles = new ArrayList<>();
        roles.add(role);

        userEntity.setRoles(roles);
        Address address = new Address();
        address.setCity("Bucuresti");
        address.setStreet("aWonderfulStreet");
        address.setNumber(2);
        address.setZipcode("123");

        userEntity.setAddress(address);

        userRepository.save(userEntity);
        return userEntity;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product storeTwoProductsInDatabase(String code1, String code2) {
        Product product = generateProduct(code1);
        Product product2 = generateProduct(code2);

        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        products.add(product2);

        productRepository.saveAll(products);
        return product;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product generateProduct(String productCode) {
        Product product = new Product();
        product.setCode(productCode);
        product.setCurrencies(Currencies.RON);
        product.setPrice(100L);
        product.setStock(1);
        product.setDescription("a description");
        product.setValid(true);
        return product;
    }
}
