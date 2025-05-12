package org.fakeskymeal.dto;

public class InventoryDto extends BaseDto {
    int inventoryId;
    int facilityId;
    String name;
    String unit; // The universal measurement for said inventory

    public InventoryDto() {
        super();
    }

    @Override
    public int getId() {
        return inventoryId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
