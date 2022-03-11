package com.practice.onlineShop.mappers;

import com.practice.onlineShop.entities.OrderItem;
import com.practice.onlineShop.entities.Orders;
import com.practice.onlineShop.entities.Product;
import com.practice.onlineShop.entities.User;
import com.practice.onlineShop.exceptions.InvalidCustomerIdExceptions;
import com.practice.onlineShop.exceptions.InvalidOperationException;
import com.practice.onlineShop.exceptions.InvalidProductIdException;
import com.practice.onlineShop.exceptions.InvalidProductsException;
import com.practice.onlineShop.repositories.ProductRepository;
import com.practice.onlineShop.repositories.UserRepository;
import com.practice.onlineShop.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    public Orders toEntity(OrderVO orderVO) throws InvalidCustomerIdExceptions, InvalidProductsException, InvalidProductIdException {
        if (orderVO == null) {
            return null;
        }
        validateOrder(orderVO);

        Orders order = new Orders();

        Optional<User> userOptional = userRepository.findById(orderVO.getUserId().longValue());

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdExceptions();
        }

        order.setUser(userOptional.get());

        Map<Integer, Integer> productsIdsToQuantityMap = orderVO.getProductsIdsToQuantity();

        List<OrderItem> orderItemList = new ArrayList<>();
        for (Integer productId : productsIdsToQuantityMap.keySet()) {
            OrderItem orderItem = new OrderItem();
            Optional<Product> productOptional = productRepository.findById(productId.longValue());
            if (!productOptional.isPresent()) {
                throw new InvalidProductIdException();
            }
            orderItem.setProduct(productOptional.get());
            Map<Integer, Integer> productQuantity = orderVO.getProductsIdsToQuantity();
            orderItem.setQuantity(productQuantity.get(productId));
            orderItemList.add(orderItem);

        }
        order.setOrderItems(orderItemList);
        return order;
    }


    private void validateOrder(OrderVO orderVO) throws InvalidCustomerIdExceptions, InvalidProductsException {

        if (orderVO.getProductsIdsToQuantity().keySet().isEmpty()) {
            throw new InvalidProductsException();
        }
    }
}
