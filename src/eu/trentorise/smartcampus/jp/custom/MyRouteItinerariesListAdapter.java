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
package eu.trentorise.smartcampus.jp.custom;

import it.sayservice.platform.smartplanner.data.message.SimpleLeg;
import it.sayservice.platform.smartplanner.data.message.journey.RecurrentJourney;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import eu.trentorise.smartcampus.jp.R;
import eu.trentorise.smartcampus.jp.custom.data.BasicItinerary;
import eu.trentorise.smartcampus.jp.custom.data.BasicRecurrentJourney;
import eu.trentorise.smartcampus.jp.custom.data.BasicRoute;
import eu.trentorise.smartcampus.jp.custom.data.RecurrentItinerary;
import eu.trentorise.smartcampus.jp.custom.data.RecurrentTemp;
import eu.trentorise.smartcampus.jp.helper.JPHelper;
import eu.trentorise.smartcampus.jp.helper.Utils;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class MyRouteItinerariesListAdapter extends ArrayAdapter<RecurrentItinerary> {

	Context context;
	int layoutResourceId;
	List<RecurrentItinerary> myItineraries;
	LinearLayout saveLayout;
	Map<String, List<SimpleLeg>> allLegs;
	Map<String, Boolean> mylegsmonitor;

	
	public MyRouteItinerariesListAdapter(Context context, int layoutResourceId, List<RecurrentItinerary> myItineraries,  Map<String, List<SimpleLeg>> alllegs, LinearLayout saveLayout, Map<String, Boolean> mylegsmonitor) {
		super(context, layoutResourceId, myItineraries);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.myItineraries = myItineraries;
		this.saveLayout = saveLayout;
		this.allLegs = alllegs;
		this.mylegsmonitor = mylegsmonitor;
		
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RowHolder holder = null;
			if ((myItineraries!=null) && (myItineraries.get(position)!=null))
			{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new RowHolder();
			holder.name = (TextView) row.findViewById(R.id.itname);
			holder.locationFrom = (TextView) row.findViewById(R.id.itlocation_from);
			holder.locationTo = (TextView) row.findViewById(R.id.itlocation_to);
			holder.transportTypes = (LinearLayout) row.findViewById(R.id.ittransporttypes);
			holder.monitor = (CheckBox) row.findViewById(R.id.its_monitor);
			holder.monitor.setOnClickListener(new CheckMonitorListener(position));
			row.setTag(holder);
			holder.monitor.setChecked(myItineraries.get(position).isMonitor());
			RecurrentItinerary myItinerary = myItineraries.get(position);
			holder.name.setText(myItinerary.getName());
			holder.locationFrom.setText(myItinerary.getFrom());
			holder.locationTo.setText(myItinerary.getTo());

			ImageView imgv = Utils.getImageByTType(getContext(), myItinerary.getTransport().getType());
			if (imgv.getDrawable() != null) {
				holder.transportTypes.removeAllViews();
				holder.transportTypes.addView(imgv);
			}
			}
		return row;
	}

	static class RowHolder {
		TextView name;
		TextView locationFrom;
		TextView locationTo;
		LinearLayout transportTypes;
		CheckBox monitor;

	}
	 private class CheckMonitorListener implements OnClickListener{
		 int position;
		 public CheckMonitorListener(int position) {
			 this.position=position;
			 }
		@Override
		public void onClick(View v) {
			RecurrentItinerary myItinerary = myItineraries.get(position);
			
			if (((CheckBox)v).isChecked())
			{
				mylegsmonitor.put(myItinerary.getTransport().getAgencyId()+"_"+myItinerary.getTransport().getRouteId(),true);
				myItineraries.get(position).setMonitor(true);
			}
			else
				{
				mylegsmonitor.put(myItinerary.getTransport().getAgencyId()+"_"+myItinerary.getTransport().getRouteId(),false);
				myItineraries.get(position).setMonitor(false);
		
				}
			saveLayout.setVisibility(View.VISIBLE);					
		}
	}	
}
