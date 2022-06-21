package com.czy.service;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

/**
 * @author Planifolia.Van
 * 消费者的服务类，在这他远程的调用生产者里的getTicket方法
 */
@Component
@Service
public class UserService {
    /**
     * 注意使用@Reference将远程的服务引入，然后我们需要要在远程服务相同的包下建一个对应的接口，不需要实现类
     */
    @Reference
    TicketService ticketService;
    public void getTicket(){
        System.out.println(ticketService.getTicket());
    }
}
