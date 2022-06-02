package com.food_donation.model;

public class FoodModel {
    String donate_id, donor_id, food, type, qty, address, image, assign_to, posted_date, assign_volunteer;

    public FoodModel(String donate_id, String donor_id, String food, String type, String qty, String address, String image, String assign_to, String posted_date, String assign_volunteer) {
        this.donate_id = donate_id;
        this.donor_id = donor_id;
        this.food = food;
        this.type = type;
        this.qty = qty;
        this.address = address;
        this.image = image;
        this.assign_to = assign_to;
        this.posted_date = posted_date;
        this.assign_volunteer = assign_volunteer;
    }

    public String getDonate_id() {
        return donate_id;
    }

    public void setDonate_id(String donate_id) {
        this.donate_id = donate_id;
    }

    public String getPosted_date() {
        return posted_date;
    }

    public void setPosted_date(String posted_date) {
        this.posted_date = posted_date;
    }

    public String getDonor_id() {
        return donor_id;
    }

    public void setDonor_id(String donor_id) {
        this.donor_id = donor_id;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAssign_to() {
        return assign_to;
    }

    public void setAssign_to(String assign_to) {
        this.assign_to = assign_to;
    }

    public String getAssign_volunteer() {
        return assign_volunteer;
    }

    public void setAssign_volunteer(String assign_volunteer) {
        this.assign_volunteer = assign_volunteer;
    }
}
