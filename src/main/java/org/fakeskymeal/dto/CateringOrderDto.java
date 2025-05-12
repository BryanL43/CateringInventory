package org.fakeskymeal.dto;

import java.time.LocalDateTime;

public class CateringOrderDto extends BaseDto {
    int orderId;
    int flightId;
    int facilityId;
    LocalDateTime deliveryTime;

    // TODO: Implement private List<BeverageDto> beverages; for many-to-many

    public CateringOrderDto() {
        super();
    }

    @Override
    public int getId() {
        return orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
