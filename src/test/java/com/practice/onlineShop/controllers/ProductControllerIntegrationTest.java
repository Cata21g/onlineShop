package com.practice.onlineShop.controllers;

import com.practice.onlineShop.entities.Address;
import com.practice.onlineShop.entities.Product;
import com.practice.onlineShop.entities.User;
import com.practice.onlineShop.enums.Currencies;
import com.practice.onlineShop.enums.Roles;
import com.practice.onlineShop.repositories.ProductRepository;
import com.practice.onlineShop.repositories.UserRepository;
import com.practice.onlineShop.vos.ProductVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {
    @TestConfiguration
    static class ProductControllerIntegrationTextContextConfiguration {
        @Bean
        public RestTemplate restTemplateForPatch() {
            return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        }
    }


    public static final String LOCALHOST = "http://localhost:";
    @LocalServerPort
    private int port;

    @Autowired
    private ProductController productController;

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private RestTemplate restTemplateForPatch;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void contextLoads() {
        assertThat(productController).isNotNull();
    }

    @Test
    public void addProduct_whenUserIsAdmin_shouldStoreTheProduct() {
        productRepository.deleteAll();
        User userEntity = new User();
        userEntity.setFirstname("adminFirstName");
        Collection<Roles> roles = new ArrayList<>();
        roles.add(Roles.ADMIN);

        userEntity.setRoles(roles);
        Address address = new Address();
        address.setCity("Bucuresti");
        address.setStreet("aWonderfulStreet");
        address.setNumber(2);
        address.setZipcode("123");

        userEntity.setAddress(address);

        userRepository.save(userEntity);

        ProductVO productVO = new ProductVO();
        productVO.setCode("aProductCode");
        productVO.setPrice(100);
        productVO.setCurrencies(Currencies.RON);
        productVO.setStock(12);
        productVO.setDescription("A product desciption!");
        productVO.setValid(true);


        testRestTemplate.postForEntity("http://localhost:" + port + "/product/" + userEntity.getId(), productVO, Void.class);


        Iterable<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);

        Product product = products.iterator().next();

        assertThat(product.getCode()).isEqualTo(productVO.getCode());

    }

    @Test
    public void addProduct_whenUserIsNotInDb_shouldThrowInvalidCustomerIdException() {

        ProductVO productVO = new ProductVO();
        productVO.setCode("aProductCode");
        productVO.setPrice(100);
        productVO.setCurrencies(Currencies.RON);
        productVO.setStock(12);
        productVO.setDescription("A product desciption!");
        productVO.setValid(true);


        ResponseEntity<String> response = testRestTemplate.postForEntity("http://localhost:" + port + "/product/123", productVO, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Id-ul trimis este invalid.");

    }

    @Test
    public void addProduct_whenUserIsNOTAdmin_shouldThrowInvalidOperationException() {
        User userEntity = saveUserWithRole(Roles.CLIENT);

        ProductVO productVO = new ProductVO();
        productVO.setCode("aProductCode");
        productVO.setPrice(100);
        productVO.setCurrencies(Currencies.RON);
        productVO.setStock(12);
        productVO.setDescription("A product desciption!");
        productVO.setValid(true);

        ResponseEntity<String> response = testRestTemplate.postForEntity(LOCALHOST + port + "/product/" + userEntity.getId(), productVO, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatiune");

    }

    private User saveUserWithRole(Roles role) {
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

    @Test
    public void getProductByCode_whenCodeIsPresentInDb_shouldReturnTheProduct() {


//        Product product2 = new Product();
//        product.setCode("aWonderfulCode2");
//        product.setCurrencies(Currencies.RON);
//        product.setPrice(100L);
//        product.setStock(1);
//        product.setDescription("a description");
//        product.setValid(true);

        Product product = storeTwoProductsInDatabase("aWonderfulCode", "aWonderfulCode2");

        ProductVO productResponse = testRestTemplate.getForObject(LOCALHOST + port + "/product/" + product.getCode(), ProductVO.class);
        assertThat(productResponse.getCode()).isEqualTo(product.getCode());

    }

    private Product storeTwoProductsInDatabase(String code1, String code2) {
        Product product = generateProduct(code1);
        Product product2 = generateProduct(code2);

        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        products.add(product2);

        productRepository.saveAll(products);
        return product;
    }

    private Product generateProduct(String productCode) {
        Product product = new Product();
        product.setCode(productCode);
        product.setCurrencies(Currencies.RON);
        product.setPrice(100L);
        product.setStock(1);
        product.setDescription("a description");
        product.setValid(true);
        return product;
    }

    @Test
    public void fetProductVyCode_shouldReturnErrorMessage_whenProductCodeIsNotPresent() {
        String response = testRestTemplate.getForObject(LOCALHOST + port + "/product/12321", String.class);

        assertThat(response).isEqualTo("Codul produsului trimis este invalid.");
    }

    @Test
    public void getProducts() {
        productRepository.deleteAll();
        storeTwoProductsInDatabase("aWonderfulCodexx", "aWonderfulCode2xx");

        ProductVO[] products = testRestTemplate.getForObject(LOCALHOST + port + "/product", ProductVO[].class);
        assertThat(products).hasSize(2);
        assertThat(products[0].getCode()).contains("aWonderfulCodexx");
        assertThat(products[1].getCode()).contains("aWonderfulCode2xx");
    }

    @Test
    public void updateProduct() {
        Product product = generateProduct("aProduct100");
        productRepository.save(product);

        User user = saveUserWithRole(Roles.ADMIN);// poate fi testat si pt cand  utilizatorul este EDITOR
        ProductVO productVO = new ProductVO();
        productVO.setCode(product.getCode());
        productVO.setCurrencies(Currencies.EUR);
        productVO.setPrice(200L);
        productVO.setStock(200);
        productVO.setDescription("a description");
        productVO.setValid(false);


        testRestTemplate.put(LOCALHOST + port + "/product/" + user.getId(), productVO);

        Optional<Product> updateProduct = productRepository.findByCode(productVO.getCode());

        assertThat(updateProduct.get().getDescription()).isEqualTo(productVO.getDescription());
        assertThat(updateProduct.get().getCurrencies()).isEqualTo(productVO.getCurrencies());
        assertThat(updateProduct.get().getPrice()).isEqualTo(productVO.getPrice());
        assertThat(updateProduct.get().getStock()).isEqualTo(productVO.getStock());
        assertThat(updateProduct.get().isValid()).isEqualTo(productVO.isValid());

    }

    @Test
    public void deleteProduct_whenUserIsAdmin_shouldDeleteTheProduct() {
        Product product = generateProduct("aProductForDelete");
        productRepository.save(product);

        testRestTemplate.delete(LOCALHOST + port + "/product/" + product.getCode() + "/1");
        assertThat(productRepository.findByCode(product.getCode())).isNotPresent();

    }

    @Test
    public void addStock_whenAddingStockToAnItemByAdmin_shouldBeSavedInDB() {
        Product product = generateProduct("aProductForAddingStock");
        productRepository.save(product);

        User user = saveUserWithRole(Roles.ADMIN);

        restTemplateForPatch.exchange(LOCALHOST + port + "/product/" + product.getCode() + "/3/" + user.getId(),
                HttpMethod.PATCH, HttpEntity.EMPTY, Void.class);
        Product productFromDb = productRepository.findByCode(product.getCode()).get();

        assertThat(productFromDb.getStock()).isEqualTo(4);
    }
}