package eu.trentorise.smartcampus.jp.helper.processor;

import it.sayservice.platform.smartplanner.data.message.journey.RecurrentJourney;

import java.util.List;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import eu.trentorise.smartcampus.android.common.SCAsyncTask.SCAsyncTaskProcessor;
import eu.trentorise.smartcampus.jp.custom.AbstractAsyncTaskProcessor;
import eu.trentorise.smartcampus.jp.custom.MyRecurItinerariesListAdapter;
import eu.trentorise.smartcampus.jp.custom.MyRouteItinerariesListAdapter;
import eu.trentorise.smartcampus.jp.custom.data.BasicItinerary;
import eu.trentorise.smartcampus.jp.custom.data.BasicRecurrentJourney;
import eu.trentorise.smartcampus.jp.custom.data.BasicRecurrentJourneyParameters;
import eu.trentorise.smartcampus.jp.custom.data.BasicRoute;
import eu.trentorise.smartcampus.jp.custom.data.RecurrentTemp;
import eu.trentorise.smartcampus.jp.helper.JPHelper;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class GetMyRecurItineraryProcessor extends AbstractAsyncTaskProcessor<BasicRecurrentJourneyParameters, List<BasicRecurrentJourney>> {

	private MyRouteItinerariesListAdapter adapter;

	public GetMyRecurItineraryProcessor(SherlockFragmentActivity activity, MyRouteItinerariesListAdapter adapter) {
		super(activity);
		this.adapter = adapter;
	}

	@Override
	public List<BasicRecurrentJourney> performAction(BasicRecurrentJourneyParameters... params) throws SecurityException, Exception {
		return JPHelper.getRecurItinerary(params[0]);
	}

	@Override
	public void handleResult(List<BasicRecurrentJourney> result) {
		
		//smanaccia con l'array di risultato e aggiungi
		adapter.clear();
		for (BasicRecurrentJourney myt : result) {
			adapter.add(myt);
		}
		adapter.notifyDataSetChanged();
	}
}
