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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import eu.trentorise.smartcampus.jp.Config;
import eu.trentorise.smartcampus.jp.MyRecurItinerariesFragment;
import eu.trentorise.smartcampus.jp.R;
import eu.trentorise.smartcampus.jp.custom.AbstractAsyncTaskProcessor;
import eu.trentorise.smartcampus.jp.helper.JPHelper;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class DeleteMyRecurItineraryProcessor extends AbstractAsyncTaskProcessor<String, Void> {

	private String name;

	public DeleteMyRecurItineraryProcessor(SherlockFragmentActivity activity) {
		super(activity);
	}

	@Override
	public Void performAction(String... strings) throws SecurityException, Exception {
		name = strings[0];
		String id = strings[1];
		JPHelper.deleteMyRecurItinerary(id);
		return null;
	}

	@Override
	public void handleResult(Void result) {
		Toast toast = Toast.makeText(activity, R.string.deleted_journey, Toast.LENGTH_SHORT);
		toast.show();
		//activity.getSupportFragmentManager().popBackStackImmediate();
		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager()
				.beginTransaction();
		Fragment fragment = new MyRecurItinerariesFragment();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.replace(Config.mainlayout, fragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

	}
}
