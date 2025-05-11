package org.fakeskymeal.dto;

import java.time.LocalDateTime;

public class FlightDto extends BaseDto {
    int flightId;
    int airlineCompanyId;
    String flightNumber;
    LocalDateTime departureTime;
    LocalDateTime arrivalTime;

    public FlightDto() {
        super();
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getAirlineCompanyId() {
        return airlineCompanyId;
    }

    public void setAirlineCompanyId(int airlineCompanyId) {
        this.airlineCompanyId = airlineCompanyId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}