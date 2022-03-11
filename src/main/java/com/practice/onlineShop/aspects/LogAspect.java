package com.practice.onlineShop.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.practice.onlineShop.controllers.ProductController.addProduct(..))")
    public void addProdcutPointcut() {

    }

    @Pointcut("execution(* com.practice.onlineShop.controllers.ProductController.updateProduct(..))")
    public void updateProdcutPointcut() {

    }
    @Pointcut("execution(* com.practice.onlineShop.controllers.OrderController.addOrder(..))")
    public void addOrderPointcut() {

    }

    @Pointcut("execution(* com.practice.onlineShop.controllers.OrderController.delivery(..))")
    public void deliverOrderPointcut() {

    }
    @Pointcut("execution(* com.practice.onlineShop.controllers.OrderController.returnOrder(..))")
    public void returnOrderPointcut() {

    }

    @Pointcut("execution(* com.practice.onlineShop.controllers.OrderController.cancelOrder(..))")
    public void cancelOrderPointcut() {

    }
    @Pointcut("execution(* com.practice.onlineShop.controllers.ProductController.addStock(..))")
    public void addStockPointcut() {

    }


//    @Around("com.practice.onlineShop.aspects.LogAspect.addProdcutPointcut()")
//    public void around(JoinPoint joinPoint){
//        System.out.println("In around aspect.");
//    }

    @Before("com.practice.onlineShop.aspects.LogAspect.addProdcutPointcut()")
    public void before(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date());
        System.out.println("ProductVO: " + joinPoint.getArgs()[0]);
        System.out.println("The user had id: " + joinPoint.getArgs()[1]);
    }

    @Before("com.practice.onlineShop.aspects.LogAspect.updateProdcutPointcut()")
    public void beforeUpdate(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date() + " for doing an update.");
        System.out.println("ProductVO: " + joinPoint.getArgs()[0]);
        System.out.println("The user had id: " + joinPoint.getArgs()[1]);
    }

    @Before("com.practice.onlineShop.aspects.LogAspect.addStockPointcut()")
    public void beforeAddingStock(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date() + " before adding stock.");
        System.out.println("Product code: " + joinPoint.getArgs()[0]);
        System.out.println("Quantity: " + joinPoint.getArgs()[1]);
        System.out.println("The user had id: " + joinPoint.getArgs()[2]);
    }

    @Before("com.practice.onlineShop.aspects.LogAspect.deliverOrderPointcut()")
    public void beforeDeliver(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date() + " for doing a deliver.");
        System.out.println("Order Id: " + joinPoint.getArgs()[0]);
        System.out.println("The user had id: " + joinPoint.getArgs()[1]);
    }
    @Before("com.practice.onlineShop.aspects.LogAspect.cancelOrderPointcut()")
    public void beforeCancel(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date() + " for doing a cancelation.");
        System.out.println("Order Id: " + joinPoint.getArgs()[0]);
        System.out.println("The user had id: " + joinPoint.getArgs()[1]);
    }

    @Before("com.practice.onlineShop.aspects.LogAspect.returnOrderPointcut()")
    public void beforeReturningOrder(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date() + " for doing a return.");
        System.out.println("Order Id: " + joinPoint.getArgs()[0]);
        System.out.println("The user had id: " + joinPoint.getArgs()[1]);
    }

    @After("com.practice.onlineShop.aspects.LogAspect.addProdcutPointcut()")
    public void after(JoinPoint joinPoint) {
        System.out.println("In after aspect at " + new Date());
    }

    @Before("com.practice.onlineShop.aspects.LogAspect.updateProdcutPointcut()")
    public void beforeAddingAnOrder(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date());
        System.out.println("OrderVO: " + joinPoint.getArgs()[0]);
    }


}
