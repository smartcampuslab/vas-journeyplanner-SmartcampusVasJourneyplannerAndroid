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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;

import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.jp.custom.MyItinerariesListAdapter;
import eu.trentorise.smartcampus.jp.custom.data.BasicItinerary;
import eu.trentorise.smartcampus.jp.helper.processor.GetMyItinerariesProcessor;

public class MyItinerariesFragment extends SherlockFragment {

	private List<BasicItinerary> myItineraries = new ArrayList<BasicItinerary>();
	private MyItinerariesListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getSherlockActivity().getSupportActionBar().getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
			getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}

		return inflater.inflate(R.layout.myitineraries, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		ListView myJourneysList = (ListView) getView().findViewById(R.id.myitineraries_list);
		adapter = new MyItinerariesListAdapter(getSherlockActivity(),
				R.layout.itinerarychoices_row, myItineraries);
		myJourneysList.setAdapter(adapter);

		SCAsyncTask<Void, Void, List<BasicItinerary>> task = new SCAsyncTask<Void, Void, List<BasicItinerary>>(
				getSherlockActivity(), new GetMyItinerariesProcessor(getSherlockActivity(), adapter));
		task.execute();

		myJourneysList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FragmentTransaction fragmentTransaction = getSherlockActivity().getSupportFragmentManager()
						.beginTransaction();
				Fragment fragment = MyItineraryFragment.newInstance(adapter.getItem(position));
				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				fragmentTransaction.replace(Config.mainlayout, fragment);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		});
		//hide keyboard if it is still open
		InputMethodManager imm = (InputMethodManager)getSherlockActivity().getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getSherlockActivity().getWindow().getDecorView().findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

}
