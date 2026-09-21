package com.Kalakriti.Kalakriti.dto;
import com.Kalakriti.Kalakriti.entity.OrderStatus;
public class UpdateOrderStatusRequest {

    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status){
        this.status = status;
    }
}
