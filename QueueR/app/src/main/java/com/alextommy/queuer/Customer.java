package com.alextommy.queuer;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private String name;
    private String size;
    private String time;

    public Customer(String name, String size, String time) {
        super();
        this.name = name;
        this.size = size;
        this.time = time;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public static List<Customer> makeData() {
        List<Customer> list = new ArrayList<Customer>();
        list.add(new Customer("Alex", Integer.toString(2), "7:00 PM"));
        list.add(new Customer("Tommy", Integer.toString(4), "8:00 PM"));
        list.add(new Customer("Ming", Integer.toString(3), "9:00 PM"));
        return list;
    }

}