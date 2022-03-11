package com.practice.onlineShop.controllers;

import com.practice.onlineShop.entities.OrderItem;
import com.practice.onlineShop.entities.Orders;
import com.practice.onlineShop.entities.Product;
import com.practice.onlineShop.entities.User;
import com.practice.onlineShop.enums.Roles;
import com.practice.onlineShop.repositories.OrderRepository;
import com.practice.onlineShop.utils.UtilsComponent;
import com.practice.onlineShop.vos.OrderVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @TestConfiguration
    static class ProductControllerIntegrationTextContextConfiguration {
        @Bean
        public RestTemplate restTemplateForPatch() {
            return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        }
    }

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplateForPatch;

    @Autowired
    private UtilsComponent utilsComponent;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @Transactional
    public void addOrder_whenOrderIsValid_shouldAddItToDB() {
        User user = utilsComponent.saveUserWithRole(Roles.CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1", "code2");

        OrderVO orderVO = createOrderVo(user, product);

        testRestTemplate.postForEntity(UtilsComponent.LOCALHOST + port + "/order", orderVO, Void.class);


//        Iterable<Orders> ordersIterable = orderRepository.findAll();
//        if (!ordersIterable.iterator().hasNext()) {
//            assert false;
//        }
//        Orders order = ordersIterable.iterator().next();
//        assertThat(((List<OrderItem>) order.getOrderItems()).get(0).getProduct().getId()).isEqualTo(product.getId());

        List<Orders> ordersIterable = (List<Orders>) orderRepository.findAll();
        Optional<OrderItem> orderItemOptional = ordersIterable.stream()
                .map(orders -> (List<OrderItem>) orders.getOrderItems())
                .flatMap(List::stream)
                .filter(orderItem -> orderItem.getProduct().getId() == product.getId())
                .findFirst();
        assertThat(orderItemOptional).isPresent();

    }

    private OrderVO createOrderVo(User user, Product product) {
        OrderVO orderVO = new OrderVO();
        orderVO.setUserId((int) user.getId());
        Map<Integer, Integer> orderMap = new HashMap<>();
        orderMap.put((int) product.getId(), 1);
        orderVO.setProductsIdsToQuantity(orderMap);
        return orderVO;
    }

    @Test
    public void addOrder_whenRequestIsMadeByAdmin_shouldThrowAndException() {
        User user = utilsComponent.saveUserWithRole(Roles.ADMIN);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1ForAdmin", "code2ForAdmin");

        OrderVO orderVO = createOrderVo(user, product);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(UtilsComponent.LOCALHOST + port + "/order", orderVO, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatiune");


    }

    @Test
    public void addOrder_whenRequestIsMadeByExpeditor_shouldThrowAndException() {
        User user = utilsComponent.saveUserWithRole(Roles.EXPEDITOR);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1ForWxpeditor", "code2ForExpedito");

        OrderVO orderVO = createOrderVo(user, product);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(UtilsComponent.LOCALHOST + port + "/order", orderVO, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatiune");
    }

    @Test
    public void deliver_whenHavindAnOrderWichIsNotCanceled_shouldDeliverItByExpeditor() {
        User expeditor = utilsComponent.saveUserWithRole(Roles.EXPEDITOR);
        User client = utilsComponent.saveUserWithRole(Roles.CLIENT);

        Product product = utilsComponent.storeTwoProductsInDatabase("code1ForExpeditorForDeliver", "code2ForExpeditorFoDeliver");


        Orders orderWithProducts = generateOrderItems(product, client);

        orderRepository.save(orderWithProducts);

        restTemplateForPatch.exchange(UtilsComponent.LOCALHOST + port + "/order/" + orderWithProducts.getId() + "/" + expeditor.getId(),
                HttpMethod.PATCH, HttpEntity.EMPTY, Void.class);

        Orders orderFromDb = orderRepository.findById(orderWithProducts.getId()).get();
        assertThat(orderFromDb.isDelivered()).isTrue();
    }

    private Orders generateOrderItems(Product product, User user) {
        Orders order = new Orders();
        order.setUser(user);
        Collection<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = generateOrderItem(product);
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);
        return order;
    }

    private OrderItem generateOrderItem(Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(1);
        orderItem.setProduct(product);
        return orderItem;
    }

    @Test
    public void deliver_whenHavindAnOrderWichIsNotCanceled_shouldNOTDeliverItByAdmin() {
        User adminAsExpeditor = utilsComponent.saveUserWithRole(Roles.ADMIN);
        User client = utilsComponent.saveUserWithRole(Roles.CLIENT);

        Product product = utilsComponent.storeTwoProductsInDatabase("code1ForExpeditorForDeliverwithADMIN", "code2ForExpeditorFoDeliverWithaADMIN");


        Orders orderWithProducts = generateOrderItems(product, client);

        orderRepository.save(orderWithProducts);

        try {
            ResponseEntity<String> responseEntity = restTemplateForPatch.exchange(UtilsComponent.LOCALHOST + port + "/order/" + orderWithProducts.getId() + "/" + adminAsExpeditor.getId(),
                    HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException restClientException) {
            assertThat(restClientException.getMessage()).isEqualTo("400 : \"Utilizatorul nu are permisiunea de a executa aceasta operatiune\"");
        }

//        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
//        assertThat(responseEntity.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatiune");
    }

    @Test
    public void deliver_whenHavindAnOrderWichIsNotCanceled_shouldNOTDeliverItByClient() {
        User clientAsExpeditor = utilsComponent.saveUserWithRole(Roles.CLIENT);
        User client = utilsComponent.saveUserWithRole(Roles.CLIENT);

        Product product = utilsComponent.storeTwoProductsInDatabase("code1ForExpeditorForDeliverwithClient", "code2ForExpeditorFoDeliverWithClient");


        Orders orderWithProducts = generateOrderItems(product, client);

        orderRepository.save(orderWithProducts);

        try {
            ResponseEntity<String> responseEntity = restTemplateForPatch.exchange(UtilsComponent.LOCALHOST + port + "/order/" + orderWithProducts.getId() + "/" + clientAsExpeditor.getId(),
                    HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException restClientException) {
            assertThat(restClientException.getMessage()).isEqualTo("400 : \"Utilizatorul nu are permisiunea de a executa aceasta operatiune\"");
        }
    }

    @Test
    public void deliver_whenHavindAnOrderWichIsCanceled_shouldThrownAnException() {
        User expeditor = utilsComponent.saveUserWithRole(Roles.EXPEDITOR);
        User client = utilsComponent.saveUserWithRole(Roles.CLIENT);

        Product product = utilsComponent.storeTwoProductsInDatabase("code1ForExpeditorForForCanceledOrder",
                "code2ForExpeditorForCanceledOrder2");

        Orders orderWithProducts = generateOrderItems(product, client);
        orderWithProducts.setCanceled(true);
        orderRepository.save(orderWithProducts);

        try {
            ResponseEntity<String> responseEntity = restTemplateForPatch.exchange(UtilsComponent.LOCALHOST + port + "/order/" + orderWithProducts.getId() + "/" + expeditor.getId(),
                    HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException restClientException) {
            assertThat(restClientException.getMessage()).isEqualTo("400 : \"Comanda a fost anulata!\"");
        }
    }
    @Test
    public void cancel_whenValidOrder_shouldCancelIt(){
        User client = utilsComponent.saveUserWithRole(Roles.CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("productForCancelOrder1",
                "productForCancelOrder2");
        Orders orderWithProducts = generateOrderItems(product, client);
        orderRepository.save(orderWithProducts);
        restTemplateForPatch.exchange(UtilsComponent.LOCALHOST + port + "/order/cancel/" + orderWithProducts.getId() + "/" + client.getId(),
                HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        Orders orderFromDb = orderRepository.findById(orderWithProducts.getId()).get();
        assertThat(orderFromDb.isCanceled()).isTrue();

    }
    @Test
    public void cancel_whenOrderIsAlreadySent_shouldThrowAnException(){
        User client = utilsComponent.saveUserWithRole(Roles.CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("productForCancelOrder1",
                "productForCancelOrder2");
        Orders orderWithProducts = generateOrderItems(product, client);
        orderWithProducts.setDelivered(true);
        orderRepository.save(orderWithProducts);
        try{
        restTemplateForPatch.exchange(UtilsComponent.LOCALHOST + port + "/order/cancel/" + orderWithProducts.getId() + "/" + client.getId(),
                HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException restClientException) {
            assertThat(restClientException.getMessage()).isEqualTo("400 : \"Comanda a fost deja expediata!\"");
        }
        Orders orderFromDb = orderRepository.findById(orderWithProducts.getId()).get();
        assertThat(orderFromDb.isCanceled()).isTrue();

    }
    @Test
    public void cancel_whenOrderIsAdmin_shouldThrowAnException(){
        User admin = utilsComponent.saveUserWithRole(Roles.ADMIN);
        Product product = utilsComponent.storeTwoProductsInDatabase("productFoCancelOrder1ForAdmin",
                "productFoCancelOrder2ForAdmin");

        Orders orderWithProducts = generateOrderItems(product, admin);
        orderWithProducts.setCanceled(true);
        orderRepository.save(orderWithProducts);

        try {
            ResponseEntity<String> responseEntity = restTemplateForPatch.exchange(UtilsComponent.LOCALHOST + port + "/order/cancel/" + orderWithProducts.getId() + "/" + admin.getId(),
                    HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException restClientException) {
            assertThat(restClientException.getMessage()).isEqualTo("400 : \"Utilizatorul nu are permisiunea de a executa aceasta operatiune\"");
        }
    }


    @Test
    public void cancel_whenOrderIsAnExpeditor_shouldThrowAnException(){


    }

    @Test
    @Transactional
    public void return_whenOrderValid_shouldReturnIt(){
        User client = utilsComponent.saveUserWithRole(Roles.CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("productFoReturn1",
                "productForReturn2");
        Orders orderWithProducts = saveOrder(client, product);

        restTemplateForPatch.exchange(UtilsComponent.LOCALHOST + port + "/order/return/" + orderWithProducts.getId() + "/" + client.getId(),
                HttpMethod.PATCH, HttpEntity.EMPTY, Void.class);

        Orders orderFromDb = orderRepository.findById(orderWithProducts.getId()).get();
        assertThat(orderFromDb.isReturned()).isTrue();
        assertThat(((List<OrderItem>) orderFromDb.getOrderItems()).get(0).getProduct().getStock()).isEqualTo(product.getStock());

    }

    private Orders saveOrder(User client, Product product) {
        Orders orderWithProducts = generateOrderItems(product, client);
        orderWithProducts.setDelivered(true);
        orderRepository.save(orderWithProducts);
        return orderWithProducts;
    }

    @Test
    public void return_whenOrderIsNotDelivered_shouldThrowException(){

    }
    public void return_whenOrderIsCanceled_shouldThrowException(){

    }
    public void return_whenUserIsAdmin_shouldThrowException(){

    }
    public void return_whenUserIsExpeditor_shouldThrowException(){

    }

}