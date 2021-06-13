package ie.cct.Garage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

public class Booking {
    @JsonIgnore
    private int id;
    private User user;
    private UserDetail detail;
    private Vehicle vehicle;
    private String serviceType;
    private LocalDate date;
    private String comment;
    private HashMap<String, Double> extraCost;
    private double cost;
    private String status;

    public Booking(){
        super();   // default constructor
    }

    public Booking(User user, UserDetail detail, Vehicle vehicle, String serviceType, HashMap<String, Double> extraCost, LocalDate date, String comment) {
        this.user = user;
        this.detail = detail;
        this.vehicle = vehicle;
        this.serviceType = serviceType;
        this.extraCost = extraCost;
        this.date = date;
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserDetail getDetail() {
        return detail;
    }

    public void setDetail(UserDetail detail) {
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

    public HashMap<String, Double> getExtraCost() {
        return extraCost;
    }

    public void setExtraCost(String part,double cost) {
        this.extraCost.put(part, cost);
    }

    public void addExtraCost(String part, double cost){
        if(this.extraCost.containsKey(part)){
            extraCost.put(part, extraCost.get(part) + cost);
        }
        else {
            extraCost.put(part, cost);
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

}
