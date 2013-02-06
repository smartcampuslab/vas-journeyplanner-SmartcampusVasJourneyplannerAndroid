/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.jp;

import it.sayservice.platform.smartplanner.data.message.Position;
import it.sayservice.platform.smartplanner.data.message.journey.RecurrentJourney;
import it.sayservice.platform.smartplanner.data.message.journey.RecurrentJourneyParameters;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.jp.PlanRecurJourneyFragment.MonitorMyRecItineraryProcessor;
import eu.trentorise.smartcampus.jp.custom.MyRecurItinerariesListAdapter;
import eu.trentorise.smartcampus.jp.custom.MyRouteItinerariesListAdapter;
import eu.trentorise.smartcampus.jp.custom.data.BasicItinerary;
import eu.trentorise.smartcampus.jp.custom.data.BasicRecurrentJourney;
import eu.trentorise.smartcampus.jp.custom.data.BasicRecurrentJourneyParameters;
import eu.trentorise.smartcampus.jp.custom.data.RecurrentTemp;
import eu.trentorise.smartcampus.jp.custom.data.BasicRoute;
import eu.trentorise.smartcampus.jp.custom.data.RecurrentTemp;
import eu.trentorise.smartcampus.jp.helper.processor.DeleteMyRecurItineraryProcessor;
import eu.trentorise.smartcampus.jp.helper.processor.GetMyRecurItinerariesProcessor;
import eu.trentorise.smartcampus.jp.helper.processor.GetMyRecurItineraryProcessor;

public class MyRecurItineraryFragment extends SherlockFragment {

	public static final String PARAMS = "parameters";
	private List<BasicRecurrentJourney> myItineraries = new ArrayList<BasicRecurrentJourney>();
	private BasicRecurrentJourneyParameters params = null;
	private TextView myRecTime = null;
	private TextView myRecDate = null;
	private TextView myRecFrom = null;
	private TextView myRecTo = null;	
	protected Position fromPosition;
	protected Position toPosition;
	
	private MyRouteItinerariesListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(PARAMS)) {
			this.params = (BasicRecurrentJourneyParameters) savedInstanceState.getSerializable(PARAMS);
		} else if (getArguments() != null && getArguments().containsKey(PARAMS)) {
			this.params = (BasicRecurrentJourneyParameters) getArguments().getSerializable(PARAMS);
		}
		if (params != null) {
			if (params.getData().getFrom() != null)
				fromPosition = params.getData().getFrom();
			if (params.getData().getTo() != null)
				toPosition = params.getData().getTo();
		} else {
			//init
			
		}
		setHasOptionsMenu(true);

	}
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.gripmenu, menu);
		SubMenu submenu = menu.getItem(0).getSubMenu();
		submenu.clear();

		submenu.add(Menu.CATEGORY_SYSTEM, R.id.menu_item_edit, Menu.NONE,
				R.string.menu_item_edit);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_edit:
			//toggle the monitor
			FragmentTransaction fragmentTransaction = getSherlockActivity().getSupportFragmentManager()
			.beginTransaction();
			Fragment fragment = new PlanRecurJourneyFragment();
			Bundle b = new Bundle();
			b.putSerializable(PlanRecurJourneyFragment.PARAMS, params);
			fragment.setArguments(b);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			fragmentTransaction.replace(Config.mainlayout, fragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getSherlockActivity().getSupportActionBar().getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
			getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}

		return inflater.inflate(R.layout.myrecitinerary, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		 myRecDate =  (TextView) getView().findViewById(R.id.myrecitinerary_date);
		 myRecFrom =  (TextView) getView().findViewById(R.id.myrecitinerary_from);
		 myRecTo =  (TextView) getView().findViewById(R.id.myrecitinerary_to);
		 myRecTime =  (TextView) getView().findViewById(R.id.myrecitinerary_time);
		 
		 //riempi i dati delle label
		// myRecDate.setText(params.getData().get)
		 
		 
		ListView myJourneysList = (ListView) getView().findViewById(R.id.myrecitinerary_legs);
		adapter = new MyRouteItinerariesListAdapter(getSherlockActivity(),
				R.layout.leg_choices_row, myItineraries);
		myJourneysList.setAdapter(adapter);

		SCAsyncTask<BasicRecurrentJourneyParameters, Void, List<BasicRecurrentJourney>> task = new SCAsyncTask<BasicRecurrentJourneyParameters,Void, List<BasicRecurrentJourney>>(
				getSherlockActivity(), new GetMyRecurItineraryProcessor(getSherlockActivity(), adapter));
		task.execute(params);

		myJourneysList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				FragmentTransaction fragmentTransaction = getSherlockActivity().getSupportFragmentManager()
//						.beginTransaction();
//				Fragment fragment = new PlanRecurJourneyFragment();
//				Bundle b = new Bundle();
//				b.putSerializable(PlanRecurJourneyFragment.PARAMS, adapter.getItem(position));
//				fragment.setArguments(b);
//				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//				fragmentTransaction.replace(Config.mainlayout, fragment);
//				fragmentTransaction.addToBackStack(null);
//				fragmentTransaction.commit();
				
				
				Toast.makeText(getSherlockActivity(), "click on adapter", Toast.LENGTH_LONG).show();
			}
		});
	}

}
