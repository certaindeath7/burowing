package com.example.burowing2.Models;

public class Items {

    public String title, image, description, pid, price;

    //Constructor
    public Items(){}

    public String getPid() {
        return pid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Items(String title, String image, String description, String pid, String price)
    {
        this.title = title;
        this.image= image;
        this.description = description;
        this.pid = pid;
        this.price = price;
    }

    public Items(String title, String image, String description, String price)
    {
        this.title = title;
        this.image= image;
        this.description = description;
        this.price = price;
    }
    //getters and setters declaration

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
