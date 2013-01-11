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

import it.sayservice.platform.smartplanner.data.message.Itinerary;
import it.sayservice.platform.smartplanner.data.message.Leg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;

import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.jp.custom.LegsListAdapter;
import eu.trentorise.smartcampus.jp.custom.data.BasicItinerary;
import eu.trentorise.smartcampus.jp.helper.processor.DeleteMyItineraryProcessor;
import eu.trentorise.smartcampus.jp.helper.processor.MonitorMyItineraryProcessor;

public class MyItineraryFragment extends SherlockFragment {

	private BasicItinerary myItinerary;
	private Itinerary itinerary;
	private List<Leg> legs;

	public static MyItineraryFragment newInstance(BasicItinerary myItinerary) {
		MyItineraryFragment f = new MyItineraryFragment();
		f.myItinerary = myItinerary;
		f.itinerary = myItinerary.getData();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.myitinerary, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		legs = itinerary.getLeg();

		TextView nameTextView = (TextView) getView().findViewById(R.id.myitinerary_name);
		nameTextView.setText(myItinerary.getName());

		TextView dateTextView = (TextView) getView().findViewById(R.id.myitinerary_date);
		dateTextView.setText(Config.FORMAT_DATE_UI.format(new Date(itinerary.getStartime())));

		TextView timeTextView = (TextView) getView().findViewById(R.id.myitinerary_time);
		timeTextView.setText(Config.FORMAT_TIME_UI.format(new Date(itinerary.getStartime())));

		ListView legsListView = (ListView) getView().findViewById(R.id.myitinerary_legs);

		// HEADER (before setAdapter or it won't work!)
		if (legsListView.getHeaderViewsCount() == 0) {
			ViewGroup startLayout = (ViewGroup) getSherlockActivity().getLayoutInflater().inflate(R.layout.itinerary_leg, null);
			TextView startLegTimeTextView = (TextView) startLayout.findViewById(R.id.leg_time);
			startLegTimeTextView.setText(Config.FORMAT_TIME_UI.format(new Date(itinerary.getStartime())));
			TextView startLegDescTextView = (TextView) startLayout.findViewById(R.id.leg_description);
			startLegDescTextView.setText(myItinerary.getOriginalFrom().getName());
			legsListView.addHeaderView(startLayout);
		}

		// FOOTER (before setAdapter or it won't work!)
		if (legsListView.getFooterViewsCount() == 0) {
			ViewGroup endLayout = (ViewGroup) getSherlockActivity().getLayoutInflater().inflate(R.layout.itinerary_leg, null);
			TextView endLegTimeTextView = (TextView) endLayout.findViewById(R.id.leg_time);
			endLegTimeTextView.setText(Config.FORMAT_TIME_UI.format(new Date(itinerary.getEndtime())));
			TextView endLegDescTextView = (TextView) endLayout.findViewById(R.id.leg_description);
			endLegDescTextView.setText(myItinerary.getOriginalTo().getName());
			legsListView.addFooterView(endLayout);
		}

		legsListView.setAdapter(new LegsListAdapter(getSherlockActivity(), R.layout.itinerary_leg, myItinerary.getOriginalFrom(),
				myItinerary.getOriginalTo(), legs));

		legsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(getActivity(), LegMapActivity.class);
				if (legs != null) {
					i.putExtra(LegMapActivity.LEGS, new ArrayList<Leg>(legs));
				}
				i.putExtra(LegMapActivity.ACTIVE_POS, position - 1);
				getActivity().startActivity(i);
			}
		});

		Button deleteMyItineraryBtn = (Button) getView().findViewById(R.id.myitinerary_delete);
		deleteMyItineraryBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder deleteAlertDialog = new AlertDialog.Builder(getSherlockActivity());
				deleteAlertDialog.setTitle("Delete " + myItinerary.getName());
				deleteAlertDialog.setMessage("Are you sure?");
				deleteAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						SCAsyncTask<String, Void, Void> task = new SCAsyncTask<String, Void, Void>(getSherlockActivity(),
								new DeleteMyItineraryProcessor(getSherlockActivity()));
						task.execute(myItinerary.getName(), myItinerary.getClientId());
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
			}
		});

		final ToggleButton monitorToggleBtn = (ToggleButton) getView().findViewById(R.id.myitinerary_toggle);
		monitorToggleBtn.setChecked(myItinerary.isMonitor());
		monitorToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SCAsyncTask<String, Void, Void> task = new SCAsyncTask<String, Void, Void>(getSherlockActivity(),
						new MonitorMyItineraryProcessor(getSherlockActivity()));
				task.execute(Boolean.toString(isChecked), myItinerary.getClientId());
			}
		});
	}

}
