package com.practice.onlineShop.aspects;

import com.practice.onlineShop.entities.User;
import com.practice.onlineShop.enums.Roles;
import com.practice.onlineShop.exceptions.InvalidCustomerIdExceptions;
import com.practice.onlineShop.exceptions.InvalidOperationException;
import com.practice.onlineShop.repositories.UserRepository;
import com.practice.onlineShop.vos.OrderVO;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Aspect
@Component
@AllArgsConstructor
public class SecurityAspect {

    private final UserRepository userRepository;

    @Pointcut("execution(* com.practice.onlineShop.services.ProductService.addProduct(..))")
    public void addProduct() {

    }

    @Pointcut("execution(* com.practice.onlineShop.services.ProductService.updateProduct(..))")
    public void updateProduct() {

    }

    @Pointcut("execution(* com.practice.onlineShop.services.ProductService.deleteProduct(..))")
    public void deleteProduct() {

    }

    @Pointcut("execution(* com.practice.onlineShop.services.ProductService.addStock(..))")
    public void addStockPointcut() {

    }
    @Pointcut("execution(* com.practice.onlineShop.services.OrderService.addOrder(..))")
    public void addOrderPointcut() {

    }

    @Pointcut("execution(* com.practice.onlineShop.services.OrderService.deliver(..))")
    public void deliverPointcut() {

    }

    @Pointcut("execution(* com.practice.onlineShop.services.OrderService.cancelOrder(..))")
    public void cancelOrderPointcut() {

    }

    @Pointcut("execution(* com.practice.onlineShop.services.OrderService.returnOrder(..))")
    public void returnOrderPointcut() {

    }


    @Before("com.practice.onlineShop.aspects.SecurityAspect.addProduct()")
    public void checkSecurityBeforeAddingProduct(JoinPoint joinPoint) throws InvalidCustomerIdExceptions, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);
        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdExceptions();
        }

        User user = userOptional.get();

        if (userIdNotAllowedToAddProduct(user.getRoles())) {
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }

    @Before("com.practice.onlineShop.aspects.SecurityAspect.deliverPointcut()")
    public void checkSecurityBeforeDeliverProduct(JoinPoint joinPoint) throws InvalidCustomerIdExceptions, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);
        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdExceptions();
        }

        User user = userOptional.get();

        if (userIdNotAllowedToDeliver(user.getRoles())) {
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }

    @Before("com.practice.onlineShop.aspects.LogAspect.returnOrderPointcut()")
    public void checkSecurityBeforeReturnigOrder(JoinPoint joinPoint) throws InvalidCustomerIdExceptions, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdExceptions();
        }


        User user = userOptional.get();

        if (userIsNotAllowedToReturnOrder(user.getRoles())) {
            throw new InvalidOperationException();
        }
    }

    private boolean userIsNotAllowedToReturnOrder(Collection<Roles> roles) {
        return !roles.contains(Roles.CLIENT);
    }

    @Before("com.practice.onlineShop.aspects.SecurityAspect.cancelOrderPointcut()")
    public void checkSecurityBeforeCancelingOrder(JoinPoint joinPoint) throws InvalidCustomerIdExceptions, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);
        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdExceptions();
        }

        User user = userOptional.get();

        if (userIdNotAllowedToCancel(user.getRoles())) {
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }

    private boolean userIdNotAllowedToCancel(Collection<Roles> roles) {
        return !roles.contains(Roles.CLIENT);
    }

    private boolean userIdNotAllowedToDeliver(Collection<Roles> roles) {
        return !roles.contains(Roles.EXPEDITOR);
    }


    private boolean userIdNotAllowedToAddProduct(Collection<Roles> roles) {
        return !roles.contains(Roles.ADMIN);
    }

    @Before("com.practice.onlineShop.aspects.SecurityAspect.deleteProduct()")
    public void checkSecurityBeforeDeletingAProduct(JoinPoint joinPoint) throws InvalidCustomerIdExceptions, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);
        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdExceptions();
        }

        User user = userOptional.get();

        if (userIdNotAllowedToDeleteProduct(user.getRoles())) {
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }

    @Before("com.practice.onlineShop.aspects.SecurityAspect.addOrderPointcut()")
    public void checkSecurityBeforeAddingOnOrder(JoinPoint joinPoint) throws InvalidCustomerIdExceptions, InvalidOperationException {
        OrderVO orderVO = (OrderVO) joinPoint.getArgs()[0];
        if (orderVO.getUserId() == null) {
            throw new InvalidCustomerIdExceptions();
        }
        Optional<User> userOptional = userRepository.findById(orderVO.getUserId().longValue());

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdExceptions();
        }

        User user = userOptional.get();

        if (userIsNotAllowedToAddAnOrder(user.getRoles())) {
            throw new InvalidOperationException();
        }

    }

    private boolean userIsNotAllowedToAddAnOrder(Collection<Roles> roles) {
        return !roles.contains(Roles.CLIENT);
    }

    private boolean userIdNotAllowedToDeleteProduct(Collection<Roles> roles) {
        return !roles.contains(Roles.ADMIN);
    }

    @Before("com.practice.onlineShop.aspects.SecurityAspect.updateProduct()")
    public void checkSecurityBeforeUpdatingProduct(JoinPoint joinPoint) throws InvalidCustomerIdExceptions, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdExceptions();
        }

        User user = userOptional.get();

        if (userIdNotAllowedToUpdateProduct(user.getRoles())) {
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }
    @Before("com.practice.onlineShop.aspects.SecurityAspect.addStockPointcut()")
    public void checkSecurityBeforeAddingStock(JoinPoint joinPoint) throws InvalidCustomerIdExceptions, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[2];
        Optional<User> userOptional = userRepository.findById(customerId);

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdExceptions();
        }

        User user = userOptional.get();

        if (userIsNotAllowedAddStock(user.getRoles())) {
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }

    private boolean userIsNotAllowedAddStock(Collection<Roles> roles) {
        return !roles.contains(Roles.ADMIN);

    }

    private boolean userIdNotAllowedToUpdateProduct(Collection<Roles> roles) {
        return !roles.contains(Roles.ADMIN) && !roles.contains(Roles.EDITOR);
    }
}
