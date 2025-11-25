package com.propertyservice.Redis;


import java.io.Serializable;

public class PropertyNotification implements Serializable {

    private long propertyId;
    private String propertyName;
    private String gstNumber;
    private String status; // PENDING / APPROVED

    public PropertyNotification() {}

    public PropertyNotification(long propertyId, String propertyName, String gstNumber, String status) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.gstNumber = gstNumber;
        this.status = status;
    }

    public long getPropertyId() { return propertyId; }
    public void setPropertyId(long propertyId) { this.propertyId = propertyId; }

    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

