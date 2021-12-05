package com.example.whatshistory.Models;

public class CallsModel {
    String number;
    String date;
    String countryIso;

    public String getCountryIso() {
        return countryIso;
    }

    public void setCountryIso(String countryIso) {
        this.countryIso = countryIso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    String time;
    String type;
    String duration;

    ///// this method is very important you must have to implement it.
    @Override
    public String toString() {
        return "\n" + "Name=" + time + "   Number=" + number + "   type=" + type + "   duration=" + duration;
    }

//    @Override
//    public int compareTo(CallsModel o) {
//        if(!getNumber().equals(o.getNumber())){
//            return 1;
//        }
//        return 0;
//    }
}
