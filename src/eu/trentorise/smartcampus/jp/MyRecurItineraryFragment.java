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
import it.sayservice.platform.smartplanner.data.message.SimpleLeg;
import it.sayservice.platform.smartplanner.data.message.journey.RecurrentJourney;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.jp.custom.AbstractAsyncTaskProcessor;
import eu.trentorise.smartcampus.jp.custom.DialogHandler;
import eu.trentorise.smartcampus.jp.custom.MyRouteItinerariesListAdapter;
import eu.trentorise.smartcampus.jp.custom.data.BasicRecurrentJourney;
import eu.trentorise.smartcampus.jp.custom.data.BasicRecurrentJourneyParameters;
import eu.trentorise.smartcampus.jp.custom.data.RecurrentItinerary;
import eu.trentorise.smartcampus.jp.helper.JPHelper;
import eu.trentorise.smartcampus.jp.helper.processor.DeleteMyRecurItineraryProcessor;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class MyRecurItineraryFragment extends SherlockFragment {

	public static final String PARAMS = "parameters";
	//adapter ad-hoc solo nome, tipo trasporto e se true e false
	private List<RecurrentItinerary> myItineraries = new ArrayList<RecurrentItinerary>();
	//hashmap che mappa <route/agencyid><transport,from,to>
	RecurrentJourney myjourney = new RecurrentJourney();
	//private List<SimpleLeg> mylegs;
	private Map<String, RecurrentItinerary> itineraryInformation = new HashMap<String, RecurrentItinerary>();
	private Map<String,List<SimpleLeg>> mylegs = new HashMap<String, List<SimpleLeg>>();
	private Map<String,List<SimpleLeg>> alllegs = new HashMap<String, List<SimpleLeg>>();
	private Map<String,Boolean> mylegsmonitor = new HashMap<String, Boolean>();
	private BasicRecurrentJourney params = null;
	private TextView myRecName = null;
	private TextView myRecTime = null;
	private TextView myRecDate = null;
	private TextView myRecFrom = null;
	private TextView myRecTo = null;
	private LinearLayout saveLayout=null;
	private Button saveButton =null;
	protected Position fromPosition;
	protected Position toPosition;
	
	private MyRouteItinerariesListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(PARAMS)) {
			this.params = (BasicRecurrentJourney) savedInstanceState.getSerializable(PARAMS);
		} else if (getArguments() != null && getArguments().containsKey(PARAMS)) {
			this.params = (BasicRecurrentJourney) getArguments().getSerializable(PARAMS);
		}
		if (params != null) {
			if (params.getData().getParameters().getFrom() != null)
				fromPosition = params.getData().getParameters().getFrom();
			if (params.getData().getParameters().getTo() != null)
				toPosition = params.getData().getParameters().getTo();
			/*setta l'array dei miei dati (single itinerary)*/
			if (params.getClientId()!=null)
				myItineraries=createItineraryFromLegs(params.getData());
			else {
				/*trova le corse*/
            	SCAsyncTask<BasicRecurrentJourneyParameters,Void, RecurrentJourney> task = new SCAsyncTask<BasicRecurrentJourneyParameters,Void, RecurrentJourney>(getSherlockActivity(),
					new PlanRecurJourneyProcessor(getSherlockActivity()));
				BasicRecurrentJourneyParameters parameters = new BasicRecurrentJourneyParameters();
				/*fill the params*/
				parameters.setClientId(params.getClientId());
				parameters.setData(params.getData().getParameters());
				parameters.setMonitor(true);
				parameters.setName(params.getName());
				task.execute(parameters);
				}
		} else {
			//init
			
		}
		setHasOptionsMenu(true);

	}
	private List<RecurrentItinerary> createItineraryFromLegs(
			RecurrentJourney recurrentJourney) {
		for (SimpleLeg leg: recurrentJourney.getLegs())
		{
		 if (!itineraryInformation.containsKey(leg.getTransport().getAgencyId()+"_"+leg.getTransport().getRouteId()))
		 {
			 if (recurrentJourney.getMonitorLegs().containsKey(leg.getTransport().getAgencyId()+"_"+leg.getTransport().getRouteId()))
				 itineraryInformation.put(leg.getTransport().getAgencyId()+"_"+leg.getTransport().getRouteId(), new RecurrentItinerary(leg.getTransport().getRouteId(), leg.getTransport(), leg.getFrom(), leg.getTo(), recurrentJourney.getMonitorLegs().get(leg.getTransport().getAgencyId()+"_"+leg.getTransport().getRouteId())));
		 }
		}
		/*costruisci mylegs coppia agencyid e routeid e lista di leg*/
		List<SimpleLeg> alllegslist =null;
		 mylegsmonitor = null;
		if (params.getClientId()!=null)
		{
			 alllegslist = params.getData().getLegs();
			 mylegsmonitor = params.getData().getMonitorLegs();

		} else{
			alllegslist = recurrentJourney.getLegs();
			 mylegsmonitor = recurrentJourney.getMonitorLegs();
		}
		for (SimpleLeg leg: alllegslist)
		{
			if (alllegs.get(leg.getTransport().getAgencyId()+"_"+leg.getTransport().getRouteId())!=null)
				alllegs.get(leg.getTransport().getAgencyId()+"_"+leg.getTransport().getRouteId()).add(leg);
			else {
				alllegs.put(leg.getTransport().getAgencyId()+"_"+leg.getTransport().getRouteId(), new ArrayList<SimpleLeg>());
				alllegs.get(leg.getTransport().getAgencyId()+"_"+leg.getTransport().getRouteId()).add(leg);
			}

		}
		/*per tutte le chiavi, se sono a true inserisco la lista in mylegs*/
		for(Entry<String, Boolean> entry : mylegsmonitor.entrySet()) {
		    String key = entry.getKey();
		    Boolean value = entry.getValue();
		    if (value)
		    	/*inserisci list*/
		    {
		    	mylegs.put(key, alllegs.get(key));
		    }
		}
		return new ArrayList(itineraryInformation.values());
	}
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.gripmenu, menu);
		SubMenu submenu = menu.getItem(0).getSubMenu();
		submenu.clear();

		submenu.add(Menu.CATEGORY_SYSTEM, R.id.menu_item_edit, Menu.NONE,
				R.string.menu_item_edit);
		submenu.add(Menu.CATEGORY_SYSTEM, R.id.menu_item_delete, Menu.NONE,
				R.string.menu_item_delete);
		if (params.isMonitor())
			submenu.add(Menu.CATEGORY_SYSTEM, R.id.menu_item_monitor, Menu.NONE,R.string.menu_item_monitor_off);
		else submenu.add(Menu.CATEGORY_SYSTEM, R.id.menu_item_monitor, Menu.NONE,R.string.menu_item_monitor_on);
		
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
			return true;

		case R.id.menu_item_delete:
			//delete monitor
			AlertDialog.Builder deleteAlertDialog = new AlertDialog.Builder(getSherlockActivity());
			deleteAlertDialog.setTitle("Delete " + params.getName());
			deleteAlertDialog.setMessage("Are you sure?");
			deleteAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					SCAsyncTask<String, Void, Void> task = new SCAsyncTask<String, Void, Void>(getSherlockActivity(),
							new DeleteMyRecurItineraryProcessor(getSherlockActivity()));
					task.execute(params.getName(), params.getClientId());
					dialog.dismiss();
					getSherlockActivity().getSupportFragmentManager().popBackStackImmediate();
				}
			});
			deleteAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			deleteAlertDialog.show();
			return true;
		case R.id.menu_item_monitor:
			//toggle the monitor
					SCAsyncTask<String, Void, Boolean> task = new SCAsyncTask<String, Void, Boolean>(getSherlockActivity(),
							new MonitorMyRecItineraryProcessor(getSherlockActivity()));
					task.execute(Boolean.toString(!params.isMonitor()), params.getClientId());
				
			
			return true;
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
		 myRecName = (TextView) getView().findViewById(R.id.myrecitinerary_name);
		 myRecDate =  (TextView) getView().findViewById(R.id.myrecitinerary_date);
		 myRecFrom =  (TextView) getView().findViewById(R.id.myrecitinerary_from);
		 myRecTo =  (TextView) getView().findViewById(R.id.myrecitinerary_to);
		 myRecTime =  (TextView) getView().findViewById(R.id.myrecitinerary_time);
		 saveButton = (Button) getView().findViewById(R.id.myrecitinerary_save);
		 saveLayout = (LinearLayout) getView().findViewById(R.id.save_layout);

		 //riempi i dati delle label
		 myRecName.setText(params.getName());
		 myRecDate.setText(Config.FORMAT_DATE_UI.format(new Date(params.getData().getParameters().getFromDate())));
		 myRecTime.setText(params.getData().getParameters().getTime());
		 myRecFrom.setText((Html.fromHtml("<i>"+getString(R.string.label_from)+" </i>"+params.getData().getParameters().getFrom().getName())));
		 myRecTo.setText((Html.fromHtml("<i>"+getString(R.string.label_to)+" </i>"+params.getData().getParameters().getTo().getName())));
		 
		 saveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String suggestedName = params.getName();

					if (params.getClientId()==null){
					Dialog dialog = new ItineraryNameDialog(getActivity(), new DialogHandler<String>() {
						@Override
						public void handleSuccess(String name) {
							SCAsyncTask<BasicRecurrentJourney,Void, Boolean> task = new SCAsyncTask<BasicRecurrentJourney,Void, Boolean>(getSherlockActivity(),
									new SaveRecurJourneyProcessor(getSherlockActivity()));
								BasicRecurrentJourney parameters = new BasicRecurrentJourney();
								/*fill the params*/
								parameters.setClientId(params.getClientId());
								RecurrentJourney data = new RecurrentJourney();

								List<SimpleLeg> paramsLegs=new ArrayList<SimpleLeg>();
								Map<String,Boolean> paramMap = new HashMap<String, Boolean>();

								
								//fill the monitorLegs
										for(Entry<String, List<SimpleLeg>> entry : alllegs.entrySet()) {
										    String key = entry.getKey();
										    List<SimpleLeg> value = entry.getValue();
										    if (alllegs.containsKey(key))
										    	{
										    	paramsLegs.addAll(value);
										    	//paramMap.put(key,true);
										    	}
										    else
										    {
										    	paramsLegs.addAll(value);
										    	//paramMap.put(key,false);
										    }
										}
								parameters.setData(params.getData());
								parameters.getData().setLegs(paramsLegs);
								parameters.getData().setMonitorLegs(mylegsmonitor);

								parameters.setMonitor(params.isMonitor());
								parameters.setName(name);
								task.execute(parameters);
						}
					}, suggestedName);
					dialog.show();
					}
					else{
						SCAsyncTask<BasicRecurrentJourney,Void, Boolean> task = new SCAsyncTask<BasicRecurrentJourney,Void, Boolean>(getSherlockActivity(),
								new SaveRecurJourneyProcessor(getSherlockActivity()));
							BasicRecurrentJourney parameters = new BasicRecurrentJourney();
							/*fill the params*/
							parameters.setClientId(params.getClientId());
							RecurrentJourney data = new RecurrentJourney();

							List<SimpleLeg> paramsLegs=new ArrayList<SimpleLeg>();
							Map<String,Boolean> paramMap = new HashMap<String, Boolean>();

							
							//fill the monitorLegs
									for(Entry<String, List<SimpleLeg>> entry : alllegs.entrySet()) {
									    String key = entry.getKey();
									    List<SimpleLeg> value = entry.getValue();
									    if (alllegs.containsKey(key))
								    	{
								    	paramsLegs.addAll(value);
								    	//paramMap.put(key,true);
								    	}
								    else
								    {
								    	paramsLegs.addAll(value);
								    	//paramMap.put(key,false);
								    }								    
									}
							parameters.setName(params.getName());
							parameters.setData(params.getData());
							parameters.getData().setLegs(paramsLegs);
							parameters.getData().setMonitorLegs(mylegsmonitor);

							parameters.setMonitor(params.isMonitor());
							parameters.setName(params.getName());
							task.execute(parameters);
					}
				}
			});
		 
	 
		ListView myJourneysList = (ListView) getView().findViewById(R.id.myrecitinerary_legs);
		adapter = new MyRouteItinerariesListAdapter(getSherlockActivity(),
				R.layout.leg_choices_row, myItineraries,alllegs,saveLayout,mylegsmonitor);
		myJourneysList.setAdapter(adapter);

		

	}

	private class PlanRecurJourneyProcessor extends AbstractAsyncTaskProcessor<BasicRecurrentJourneyParameters, RecurrentJourney> {


		public PlanRecurJourneyProcessor(SherlockFragmentActivity activity) {
			super(activity);
		}

		@Override
		public RecurrentJourney performAction(BasicRecurrentJourneyParameters... array) throws SecurityException, Exception {
			return JPHelper.planRecurItinerary(array[0]);
		}

		@Override
		public void handleResult(RecurrentJourney result) {
			/**/
			 if ((result.getLegs()!=null)&&(!result.getLegs().isEmpty())) {
				 myjourney = result;
				 myItineraries=createItineraryFromLegs(result);
				 adapter.clear();
			for (RecurrentItinerary myt : myItineraries) {
				adapter.add(myt);
			}
			ListView myJourneysList = (ListView) getView().findViewById(R.id.myrecitinerary_legs);
			adapter = new MyRouteItinerariesListAdapter(getSherlockActivity(),
					R.layout.leg_choices_row, myItineraries,alllegs,saveLayout,mylegsmonitor);
			myJourneysList.setAdapter(adapter);
			//adapter.notifyDataSetChanged();
			saveLayout.setVisibility(View.VISIBLE);
			 }
		}
	}
	
	private class SaveRecurJourneyProcessor extends AbstractAsyncTaskProcessor<BasicRecurrentJourney, Boolean> {


		public SaveRecurJourneyProcessor(SherlockFragmentActivity activity) {
			super(activity);
		}

		@Override
		public Boolean performAction(BasicRecurrentJourney... array) throws SecurityException, Exception {
			return JPHelper.saveMyRecurrentJourney(array[0]);
		}

		@Override
		public void handleResult(Boolean result) {
			Intent intent = new Intent(getSherlockActivity(), HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			Toast.makeText(getSherlockActivity(), getString(R.string.saved_journey), Toast.LENGTH_LONG).show();
//			FragmentTransaction fragmentTransaction = getSherlockActivity().getSupportFragmentManager()
//					.beginTransaction();
//			Fragment fragment = new MyRecurItinerariesFragment();
//			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//			fragmentTransaction.replace(Config.mainlayout, fragment);
//			fragmentTransaction.addToBackStack(null);
//			fragmentTransaction.commit();
		}
	}
	
	private class MonitorMyRecItineraryProcessor extends AbstractAsyncTaskProcessor<String, Boolean> {
		//ToggleButton monitorToggleBtn;
		//TextView monitorLabel;
		public MonitorMyRecItineraryProcessor(SherlockFragmentActivity activity) {
			super(activity);
			//monitorToggleBtn= (ToggleButton) activity.findViewById(R.id.myitinerary_toggle);
			//monitorLabel= (TextView) activity.findViewById(R.id.myitinerary_monitor_label);
		}

		@Override
		public Boolean performAction(String... strings) throws SecurityException, Exception {
			// 0: monitor
			// 1: id
			boolean monitor = Boolean.parseBoolean(strings[0]);
			String id = strings[1];
			return JPHelper.monitorMyRecItinerary(monitor, id);
		}

		@Override
		public void handleResult(Boolean result) {
			params.setMonitor(result);
//			if (result)
//				{
//				monitorToggleBtn.setBackgroundResource(R.drawable.ic_monitor_on);
//				monitorLabel.setText(getString(R.string.monitor_on));
//				monitorLabel.setTextAppearance(getSherlockActivity(), R.style.label_jp);
//
//				}
//			else 
//				{
//				monitorToggleBtn.setBackgroundResource(R.drawable.ic_monitor_off);
//				monitorLabel.setText(getString(R.string.monitor_off));
//				monitorLabel.setTextAppearance(getSherlockActivity(), R.style.label_black_jp);
//				}
			getSherlockActivity().invalidateOptionsMenu();

		}

	}

}
