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

import it.sayservice.platform.smartplanner.data.message.TType;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.trentorise.smartcampus.jp.Config;
import eu.trentorise.smartcampus.jp.PlanRecurJourneyFragment;
import eu.trentorise.smartcampus.jp.R;
import eu.trentorise.smartcampus.jp.custom.data.BasicRecurrentJourneyParameters;
import eu.trentorise.smartcampus.jp.helper.Utils;

public class MyRecurItinerariesListAdapter extends ArrayAdapter<BasicRecurrentJourneyParameters> {

	Context context;
	int layoutResourceId;

	public MyRecurItinerariesListAdapter(Context context, int layoutResourceId, List<BasicRecurrentJourneyParameters> myItineraries) {
		super(context, layoutResourceId, myItineraries);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RowHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RowHolder();
			holder.name = (TextView) row.findViewById(R.id.it_name);
			holder.timeFrom = (TextView) row.findViewById(R.id.it_time_from);
			holder.timeTo = (TextView) row.findViewById(R.id.it_time_to);
			holder.from = (TextView) row.findViewById(R.id.itlocation_from);
			holder.to = (TextView) row.findViewById(R.id.itlocation_to);
			holder.recurrence = (TextView) row.findViewById(R.id.recurrence);
			holder.transportTypes = (LinearLayout) row.findViewById(R.id.it_transporttypes);
			row.setTag(holder);
		} else {
			holder = (RowHolder) row.getTag();
		}

		BasicRecurrentJourneyParameters myItinerary = getItem(position);
		if (myItinerary.getName() != null && myItinerary.getName().length() > 0) {
			holder.name.setText(myItinerary.getName());
		} else {
			holder.name.setText(null);
		}

		try {
			Date time = Config.FORMAT_TIME_SMARTPLANNER.parse(myItinerary.getData().getTime());
			// time from
			holder.timeFrom.setText(Config.FORMAT_TIME_UI.format(time));
			// time to
			time.setTime(time.getTime()+myItinerary.getData().getInterval());
			holder.timeTo.setText(Config.FORMAT_TIME_UI.format(time));
		} catch (ParseException e) {
		}
		// position from
		holder.from.setText(myItinerary.getData().getFrom().getName());
		// position to
		holder.to.setText(myItinerary.getData().getTo().getName());
		
		// recurrence 
		holder.recurrence.setText(PlanRecurJourneyFragment.getRecurrenceString(myItinerary.getData().getRecurrence()));
		
		// transport types
		holder.transportTypes.removeAllViews();
		for (TType t : myItinerary.getData().getTransportTypes()) {
			ImageView imgv = Utils.getImageByTType(getContext(), t);
			if (imgv.getDrawable() != null) {
				holder.transportTypes.addView(imgv);
			}
		}

		return row;
	}

	static class RowHolder {
		TextView name;
		TextView timeFrom;
		TextView timeTo;
		TextView from;
		TextView to;
		TextView recurrence;
		LinearLayout transportTypes;
	}
}
