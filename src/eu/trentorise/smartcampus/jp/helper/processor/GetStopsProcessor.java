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
package eu.trentorise.smartcampus.jp.helper.processor;

import it.sayservice.platform.smartplanner.data.message.otpbeans.Stop;

import java.util.List;

import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import eu.trentorise.smartcampus.jp.custom.AbstractAsyncTaskProcessor;
import eu.trentorise.smartcampus.jp.helper.JPHelper;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class GetStopsProcessor extends AbstractAsyncTaskProcessor<String, List<Stop>> {

	private ArrayAdapter<Stop> adapter;

	public GetStopsProcessor(SherlockFragmentActivity activity, ArrayAdapter<Stop> adapter) {
		super(activity);
		this.adapter = adapter;
	}

	@Override
	public List<Stop> performAction(String... params) throws SecurityException, Exception {
		// 0: agencyId
		// 1: routeId
		return JPHelper.getStopsByAgencyIdRouteId(params[0], params[1]);
	}

	@Override
	public void handleResult(List<Stop> result) {
		adapter.clear();
		for (Stop r : result) {
			adapter.add(r);
		}
	}

}
