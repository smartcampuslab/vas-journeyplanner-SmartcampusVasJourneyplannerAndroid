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

import it.sayservice.platform.smartplanner.data.message.otpbeans.StopTime;

import java.util.List;

import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import eu.trentorise.smartcampus.jp.custom.AbstractAsyncTaskProcessor;
import eu.trentorise.smartcampus.jp.helper.JPHelper;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class GetStoptimesProcessor extends AbstractAsyncTaskProcessor<String, List<StopTime>> {

	private ArrayAdapter<StopTime> adapter;


	public GetStoptimesProcessor(SherlockFragmentActivity activity, ArrayAdapter<StopTime> adapter) {
		super(activity);
		this.adapter = adapter;
	}

	@Override
	public List<StopTime> performAction(String... params) throws SecurityException, Exception {
		// 0: agencyId
		// 1: routeId
		// 2: stopId
		return JPHelper.getStoptimesByAgencyIdRouteIdStopId(params[0], params[1], params[2]);
	}

	@Override
	public void handleResult(List<StopTime> result) {
		adapter.clear();
		for (StopTime r : result) {
			adapter.add(r);
		}
	}
}
