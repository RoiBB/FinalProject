package com.roi.todo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.roi.todo.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	private static TaskListAdapter taskListAdapter;
	private static ArrayList<Task> taskArrayList;
	private static TaskListModel taskListModel;
	
	private static final int TASK_SIMPLE = 100;
	
	private Geocoder geocoder;
	private LocationManager locationManager;
	
	private GoogleAnalytics googleAnalytics;
	private Tracker tracker;
	  
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        
        ListView listView = (ListView) findViewById(R.id.main_item_list);
        
        taskListModel = TaskListModel.getInstance(this);
        taskArrayList = taskListModel.getAllTasks();
        
        taskListAdapter = new TaskListAdapter(this,taskArrayList);
        listView.setAdapter(taskListAdapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
        		Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
        		Task editTask = taskListAdapter.getItem(position);
        		
        		intent.putExtra(CreateTaskActivity.TASK_ID,String.valueOf(editTask.getId()));
        		intent.putExtra(CreateTaskActivity.TASK_POSITION,String.valueOf(position));
        		
        		startActivityForResult(intent, TASK_SIMPLE);
			}
		});
        
        addDailyTasks();
        
        geocoder = new Geocoder(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        EasyTracker.getInstance().setContext(this);
        googleAnalytics = GoogleAnalytics.getInstance(this);
        tracker = googleAnalytics.getTracker("UA-37847166-1");
    }
    
    public void addDailyTasks()
    {
    	Intent intent = new Intent(this, AddTasksService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY, pendingIntent);
    }
    
    public void goToCreateTaskActivity(View view)
    {
    	Intent intent = new Intent(this, CreateTaskActivity.class);
    	startActivityForResult(intent, TASK_SIMPLE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	if (requestCode == TASK_SIMPLE)
    	{
	    	if (resultCode == RESULT_OK)
	    	{
	    		String taskId = data.getStringExtra(CreateTaskActivity.TASK_ID);
	    		String taskDescription = data.getStringExtra(CreateTaskActivity.TASK_DESCRIPTION);
	    		String taskAddress = data.getStringExtra(CreateTaskActivity.TASK_ADDRESS);
	    		
	    		String yearStr = data.getStringExtra(CreateTaskActivity.YEAR);
	    		String monthStr = data.getStringExtra(CreateTaskActivity.MONTH);
	    		String dayStr = data.getStringExtra(CreateTaskActivity.DAY);
	    		String hourOfDayStr = data.getStringExtra(CreateTaskActivity.HOUR_OF_DAY);
	    		String minuteStr = data.getStringExtra(CreateTaskActivity.MINUTE);
	    		
	    		Task newTask;
	    		
	    		if (taskId != null)
	    		{
	    			newTask = taskListModel.getTask(Integer.parseInt(taskId));
	    			newTask.setDescription(taskDescription);
	    		}
	    		else
	    		{
	    			newTask = new Task(taskListModel.getMaxIdTask()+1, taskDescription);
	    		}
	    		
	    		if (yearStr != null && monthStr != null && dayStr != null && hourOfDayStr != null && minuteStr != null)
	    		{
	    			newTask.setMinute(Integer.parseInt(minuteStr));
	    			newTask.setHour(Integer.parseInt(hourOfDayStr));
	    			newTask.setDay(Integer.parseInt(dayStr));
	    			newTask.setMonth(Integer.parseInt(monthStr));
	    			newTask.setYear(Integer.parseInt(yearStr));
	    			
	    			GregorianCalendar dateToRemind = new GregorianCalendar(Integer.parseInt(yearStr),Integer.parseInt(monthStr),Integer.parseInt(dayStr),Integer.parseInt(hourOfDayStr),Integer.parseInt(minuteStr));
	    		
		    		Intent intentBroadcast = new Intent("com.roi.todo.reminder_broadcast");
		    		
		    		intentBroadcast.putExtra(CreateTaskActivity.TASK_ID, String.valueOf(newTask.getId()));
					intentBroadcast.putExtra(CreateTaskActivity.TASK_DESCRIPTION, taskDescription);
					
	    			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, newTask.getId(), intentBroadcast, 0);
	    			
	    			AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
	    			alarmManager.set(AlarmManager.RTC_WAKEUP, dateToRemind.getTimeInMillis(), pendingIntent);
	    		}
	    		
	    		if (taskAddress != null)
	    		{
	    			try 
	    			{
						List<Address> addresses = geocoder.getFromLocationName(taskAddress, 1);
						
						Address address = addresses.get(0);
						
						double latitude = address.getLatitude();
						double longitude = address.getLongitude();
						
						Intent intent = new Intent("com.roi.todo.location_broadcast");
						intent.putExtra(CreateTaskActivity.TASK_ID, String.valueOf(newTask.getId()));
						intent.putExtra(CreateTaskActivity.TASK_DESCRIPTION, taskDescription);
						
						PendingIntent proximityIntent = PendingIntent.getBroadcast(this, newTask.getId(), intent, 0);
						locationManager.addProximityAlert(latitude, longitude, 2000, 5000, proximityIntent);
						
						newTask.setAddress(taskAddress);
					} 
	    			catch (IOException e) 
	    			{
						e.printStackTrace();
					}
	    		}
	    		
	    		if (taskId != null)
	    		{
	    			taskListModel.updateTask(newTask);
	    			int position = Integer.parseInt(data.getStringExtra(CreateTaskActivity.TASK_POSITION));
	    			
	    			taskArrayList.remove(position);
	    			Toast.makeText(this, "Task was updated.", Toast.LENGTH_SHORT).show();
	    			
	    			tracker.sendEvent("ui_action", "button_press", "update_button", (long) 2);
	    		}
	    		else
	    		{
	    			taskListModel.addTask(newTask);
	    			Toast.makeText(this, "New task just added.", Toast.LENGTH_SHORT).show();
	    			
	    			tracker.sendEvent("ui_action", "button_press", "add_button", (long) 1);
	    		}
	    		
	    		taskArrayList.add(0, newTask);
	    		taskListAdapter.notifyDataSetChanged();
	    	}
	    	
	    	if (resultCode == RESULT_CANCELED)
	    	{
	    		Toast.makeText(this, "Please enter description.", Toast.LENGTH_SHORT).show();
	    	}
    	}
	}
    
    @Override
    public void onStart() 
    {
      super.onStart();
      EasyTracker.getInstance().activityStart(this);
      tracker.sendView("/TaskListPage");

    }

    @Override
    public void onStop() 
    {
      super.onStop();
      EasyTracker.getInstance().activityStop(this);
    }

}