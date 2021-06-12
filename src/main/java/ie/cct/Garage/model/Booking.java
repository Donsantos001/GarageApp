package ie.cct.Garage.model;

import java.time.LocalDate;
import java.util.Date;

public class Booking {
    private User user;
    private UserDetail detail;
    private Vehicle vehicle;
    private String serviceType;
    private LocalDate date;
    private String comment;
    private double cost;
    private String status;

    public Booking(User user, UserDetail detail, Vehicle vehicle, String serviceType, LocalDate date, String comment) {
        this.user = user;
        this.detail = detail;
        this.vehicle = vehicle;
        this.serviceType = serviceType;
        this.date = date;
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserDetail getUserDetail() {
        return detail;
    }

    public void setUserDetail(UserDetail detail) {
        this.detail = detail;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void addCost(double cost){
        this.cost += cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
