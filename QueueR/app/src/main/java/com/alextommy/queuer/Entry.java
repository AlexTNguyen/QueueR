package com.alextommy.queuer;

import java.util.Date;

/**
 * Created by bruh on 11/9/2016.
 */

public class Entry {
    public String name;
    public int size;
    public Date checkin;
    public Date estimate;
    public int status;

    public Entry () {

    }

    public Entry(String name, int size) {
        this.name = name;
        this.size = size;
        checkin = new Date();
        estimate = null;
        status = 0;
    }

    public String getName() {
        return name;
    }

}
