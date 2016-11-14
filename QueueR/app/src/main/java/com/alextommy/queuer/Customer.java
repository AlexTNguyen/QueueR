package com.alextommy.queuer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Customer {

    public String name;
    public int size;
    public Date checkin;
    public Date estimate;
    public int status;
    public String key;

    public Customer(){
    }

    public Customer(String name, int size) {
        this.name = name;
        this.size = size;
        checkin = new Date();
        estimate = null;
        status = 0;
        key = "";
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }



    public int getSize() {
        return size;
    }


    public Date getDate() {
        return checkin;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setSize(int size) {
        this.size = size;
    }


    public void setDate(Date date) {
        this.checkin = date;
    }


}