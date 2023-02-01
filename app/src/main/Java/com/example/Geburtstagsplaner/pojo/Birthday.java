package com.example.Geburtstagsplaner.pojo;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class Birthday {
    private String name;
    private int year;
    private int month;
    private int day;

    public Birthday() {
        this.name = "";
        this.year = 0;
        this.month = 0;
        this.day = 0;
    }

    public Birthday(String name, int year, int month, int day) {
        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void setName(String name) {
        if (Pattern.matches("^[ a-zA-z_äÄöÖüÜß]+$", name)) {
            this.name = name;
        } else {
            this.name = "";
        }
    }

    private void setYear(int year) {
        this.year = year;
    }

    private void setMonth(int month) {
        this.month = month;
    }

    private void setDay(int day) {
        this.day = day;
    }

    public void setDate(int year, int month, int day) {
        Calendar calendarDate = Calendar.getInstance();
        Date today = new Date();
        calendarDate.setTime(today);
        Calendar date = Calendar.getInstance();
        date.set(year, month - 1, day);
        //compare current date (today) to date set
        //set all values 0 if date set is greater
        if (date.after(calendarDate)) {
            day = 0;
            month = 0;
            year = 0;
        }
        setYear(year);
        setMonth(month);
        setDay(day);
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getDate() {
        return day + "." + month + "." + year;
    }

    @Override
    public Birthday clone() {
        return new Birthday(this.name, this.year, this.month, this.day);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            if (obj != null) {
                return ((Birthday) obj).getName().equals(this.name) && ((Birthday) obj).getDate().equals(this.getDate());
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return false;
    }
}
