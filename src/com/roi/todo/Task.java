package com.roi.todo;


public class Task 
{
	private int id;
	private String description;
	private int isChecked;
	private String address;
	private int minute;
	private int hour;
	private int day;
	private int month;
	private int year;
	
	public Task() {
		
		this.id = 0;
		this.description = "";
		this.isChecked = 1;
		this.address = "";
		this.minute = 0;
		this.hour = 0;
		this.day = 0;
		this.month = 0;
		this.year = 0;
	}
	
	public Task(int id, String description) {
		
		this.id = id;
		this.description = description;
		this.isChecked = 1;
		this.address = "";
		this.minute = 0;
		this.hour = 0;
		this.day = 0;
		this.month = 0;
		this.year = 0;
	}
	
	public Task(int id, String description, int isChecked, String address,
			int minute, int hour, int day, int month, int year) 
	{
		this.id = id;
		this.description = description;
		this.isChecked = isChecked;
		this.address = address;
		this.minute = minute;
		this.hour = hour;
		this.day = day;
		this.month = month;
		this.year = year;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public int isChecked() {
		return isChecked;
	}

	public void setChecked(int isChecked) {
		this.isChecked = isChecked;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == this) 
		{
            return true;
        }
		
        if (other == null || other.getClass() != this.getClass())
        {
            return false;
        }

        Task otherTask = (Task) other;
		
		return otherTask.id == id;
		
		
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", description=" + description + "]";
	}
	
	
}
