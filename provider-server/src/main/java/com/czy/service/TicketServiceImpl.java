package com.czy.service;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import java.util.Random;

/**
 * @author Planifolia.Van
 */
@Component
@Service
public class TicketServiceImpl implements TicketService{
    @Override
    public String getTicket() {
        Random random=new Random();
        return "拿到了第"+random.nextInt(100)+"张票";
    }
}
