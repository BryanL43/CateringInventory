package org.fakeskymeal.dto;

public class FacilityDto extends BaseDto {
    int facilityId;
    String facilityName;
    String facilityLocation;

    public FacilityDto() {
        super();
    }

    @Override
    public int getId() {
        return facilityId;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityLocation() {
        return facilityLocation;
    }

    public void setFacilityLocation(String facilityLocation) {
        this.facilityLocation = facilityLocation;
    }
}
