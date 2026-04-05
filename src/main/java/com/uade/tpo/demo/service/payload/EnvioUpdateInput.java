package com.uade.tpo.demo.service.payload;

import com.uade.tpo.demo.entity.ShipmentStatus;

import lombok.Data;

@Data
public class EnvioUpdateInput {
    private String address;
    private ShipmentStatus status;
    private String trackingNumber;
}
