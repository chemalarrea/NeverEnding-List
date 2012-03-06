package com.pxr.tutorials.neverendinglist;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;


public class NeverEndingList extends ListActivity {
	
	//how many to load on reaching the bottom
	int itemsPerPage = 15;
	boolean loadingMore = false;
	
	
	
	
	ArrayList<String> myListItems;
	ArrayAdapter<String> adapter;
	
	//For test data :-)
	Calendar d = Calendar.getInstance();
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listplaceholder);
        
		
		//This will hold the new items
        myListItems = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myListItems);
        
		
			
		
		//add the footer before adding the adapter, else the footer will nod load!
		View footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listfooter, null, false);
		this.getListView().addFooterView(footerView);
		
		//Set the adapter
		this.setListAdapter(adapter);
		
		
		//Here is where the magic happens
		this.getListView().setOnScrollListener(new OnScrollListener(){
			
			//useless here, skip!
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			//dumdumdum			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				
				//what is the bottom iten that is visible
				int lastInScreen = firstVisibleItem + visibleItemCount;				
				
				//is the bottom item visible & not loading more already ? Load more !
				if((lastInScreen == totalItemCount) && !(loadingMore)){					
					Thread thread =  new Thread(null, loadMoreListItems);
			        thread.start();
				}
			}
		});
		
		
		//Load the first 15 items
		Thread thread =  new Thread(null, loadMoreListItems);
        thread.start();
    
    }
    
	
    //Runnable to load the items 
    private Runnable loadMoreListItems = new Runnable() {			
		@Override
		public void run() {
			//Set flag so we cant load new items 2 at the same time
			loadingMore = true;
			
			//Reset the array that holds the new items
	    	myListItems = new ArrayList<String>();
	    	
			//Simulate a delay, delete this on a production environment!
	    	try { Thread.sleep(1000);
			} catch (InterruptedException e) {}
			
			//Get 15 new listitems
	    	for (int i = 0; i < itemsPerPage; i++) {		
				
				//Fill the item with some bogus information
	        	myListItems.add("Date: " + (d.get(Calendar.MONTH)+ 1) + "/" + d.get(Calendar.DATE) + "/" + d.get(Calendar.YEAR) );          	
				
				// +1 day :-D
	        	d.add(Calendar.DATE, 1);
				
			}
			
			//Done! now continue on the UI thread
	        runOnUiThread(returnRes);
	        
		}
	};	
	
    
	//Since we cant update our UI from a thread this Runnable takes care of that! 
    private Runnable returnRes = new Runnable() {
        @Override
        public void run() {
        	
			//Loop thru the new items and add them to the adapter
			if(myListItems != null && myListItems.size() > 0){
                for(int i=0;i<myListItems.size();i++)
                	adapter.add(myListItems.get(i));
            }
        	
			//Update the Application title
        	setTitle("Neverending List with " + String.valueOf(adapter.getCount()) + " items");
			
			//Tell to the adapter that changes have been made, this will cause the list to refresh
            adapter.notifyDataSetChanged();
			
			//Done loading more.
            loadingMore = false;
        }
    };
}