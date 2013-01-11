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

import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import eu.trentorise.smartcampus.jp.R;
import eu.trentorise.smartcampus.jp.custom.AbstractAsyncTaskProcessor;
import eu.trentorise.smartcampus.jp.custom.data.BasicRecurrentJourneyParameters;
import eu.trentorise.smartcampus.jp.helper.JPHelper;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class SaveRecurJourneyProcessor extends AbstractAsyncTaskProcessor<BasicRecurrentJourneyParameters, Boolean> {

	public SaveRecurJourneyProcessor(SherlockFragmentActivity activity) {
		super(activity);
	}

	@Override
	public Boolean performAction(BasicRecurrentJourneyParameters... array) throws SecurityException, Exception {
		return JPHelper.saveRecurrentJourney(array[0]);
	}

	@Override
	public void handleResult(Boolean result) {
		Toast toast = Toast.makeText(activity, R.string.recur_journey_saved_alert, Toast.LENGTH_SHORT);
		toast.show();
		activity.getSupportActionBar().selectTab(activity.getSupportActionBar().getTabAt(1));
	}

}
