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

import it.sayservice.platform.smartplanner.data.message.journey.RecurrentJourney;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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
	
	public MyRouteItinerariesListAdapter(Context context, int layoutResourceId, List<RecurrentItinerary> myItineraries, LinearLayout saveLayout) {
		super(context, layoutResourceId, myItineraries);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.myItineraries = myItineraries;
		this.saveLayout = saveLayout;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RowHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RowHolder();
			holder.name = (TextView) row.findViewById(R.id.itname);
			holder.locationFrom = (TextView) row.findViewById(R.id.itlocation_from);
			holder.locationTo = (TextView) row.findViewById(R.id.itlocation_to);
			holder.transportTypes = (LinearLayout) row.findViewById(R.id.ittransporttypes);
			holder.monitor = (CheckBox) row.findViewById(R.id.its_monitor);
			

			row.setTag(holder);
		} else {
			holder = (RowHolder) row.getTag();
		}
		RecurrentItinerary myItinerary = myItineraries.get(position);
		holder.name.setText(myItinerary.getName());
		holder.locationFrom.setText(myItinerary.getFrom());
		holder.locationTo.setText(myItinerary.getTo());

		ImageView imgv = Utils.getImageByTType(getContext(), myItinerary.getTransportType());
		if (imgv.getDrawable() != null) {
			holder.transportTypes.removeAllViews();
			holder.transportTypes.addView(imgv);
		}
		
		holder.monitor.setChecked(myItinerary.isMonitor());
		holder.monitor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				saveLayout.setVisibility(View.VISIBLE);
				
			}
		});

		return row;
	}

	static class RowHolder {
		TextView name;
		TextView locationFrom;
		TextView locationTo;
		LinearLayout transportTypes;
		CheckBox monitor;

	}
	
	public class MonitorMyItineraryProcessor extends AbstractAsyncTaskProcessor<Object, Boolean> {

		Integer position;
		List<BasicItinerary> myItineraries;
		String id;
		
		public MonitorMyItineraryProcessor(SherlockFragmentActivity activity) {
			super(activity);
		}

		@Override
		public Boolean performAction(Object... params) throws SecurityException, Exception {
			// 0: monitor
			// 1: id
			boolean monitor = Boolean.parseBoolean((String) params[0]);
			 position = (Integer) params[2];
			 myItineraries=(List<BasicItinerary>) params[1];
			 id =  myItineraries.get(position).getClientId();
			return JPHelper.monitorMyItinerary(monitor, id);
		}

		@Override
		public void handleResult(Boolean result) {
			//cambia background in funzione a quello che ho
			myItineraries.get(position).setMonitor(result);
			notifyDataSetChanged();

		}
	}
}
