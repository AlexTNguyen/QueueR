package com.alextommy.queuer;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private final String name;
    private final String size;
    private final String time;

    public Customer(String name, String size, String time) {
        super();
        this.name = name;
        this.size = size;
        this.time = time;
    }

    public String getName() {
        return name;
    }


    public String getSize() {
        return size;
    }


    public String getTime() {
        return time;
    }

    public static List<Customer> makeData() {
        List<Customer> list = new ArrayList<>();
        list.add(new Customer("Alex", Integer.toString(2), "7:00 PM"));
        list.add(new Customer("Tommy", Integer.toString(4), "8:00 PM"));
        list.add(new Customer("Ming", Integer.toString(3), "9:00 PM"));
        return list;
    }

}