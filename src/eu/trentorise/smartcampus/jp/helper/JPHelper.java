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
package eu.trentorise.smartcampus.jp.helper;

import it.sayservice.platform.smartplanner.data.message.Itinerary;
import it.sayservice.platform.smartplanner.data.message.journey.SingleJourney;
import it.sayservice.platform.smartplanner.data.message.otpbeans.Route;
import it.sayservice.platform.smartplanner.data.message.otpbeans.Stop;
import it.sayservice.platform.smartplanner.data.message.otpbeans.StopTime;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;
import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.ac.authenticator.AMSCAccessProvider;
import eu.trentorise.smartcampus.android.common.GlobalConfig;
import eu.trentorise.smartcampus.android.common.LocationHelper;
import eu.trentorise.smartcampus.jp.Config;
import eu.trentorise.smartcampus.jp.custom.data.BasicAlert;
import eu.trentorise.smartcampus.jp.custom.data.BasicItinerary;
import eu.trentorise.smartcampus.jp.custom.data.BasicRecurrentJourneyParameters;
import eu.trentorise.smartcampus.protocolcarrier.ProtocolCarrier;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.Method;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageRequest;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageResponse;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.DataException;

public class JPHelper {

	private static JPHelper instance = null;

	private static SCAccessProvider accessProvider = new AMSCAccessProvider();

	private Context mContext;

	private ProtocolCarrier protocolCarrier = null;

	private static LocationHelper mLocationHelper;

	protected JPHelper(Context mContext) {
		super();
		this.mContext = mContext;
		setProtocolCarrier(new ProtocolCarrier(mContext, Config.APP_TOKEN));
	
		// LocationManager locationManager = (LocationManager)
		// mContext.getSystemService(Context.LOCATION_SERVICE);
		// locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
		// 0, 0, new JPLocationListener());
		// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// 0, 0, new JPLocationListener());
		// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 0, 0, new JPLocationListener());
		setLocationHelper(new LocationHelper(mContext));
	}

	public static void init(Context mContext) {
		instance = new JPHelper(mContext);
	}

	public static boolean isInitialized() {
		return instance != null;
	}

	public static List<Itinerary> planSingleJourney(SingleJourney sj) throws JsonParseException, JsonMappingException,
			IOException, ConnectionException, ProtocolException, SecurityException, ParseException {
		List<Itinerary> list = new ArrayList<Itinerary>();

		if (sj != null) {
			String json = JSONUtils.convertToJSON(sj);
			MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS
					+ Config.CALL_PLANSINGLEJOURNEY);
			req.setMethod(Method.POST);
			req.setBody(json);

			MessageResponse res = JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN,
					getAuthToken());

			List<?> its = JSONUtils.getFullMapper().readValue(res.getBody(), List.class);
			for (Object it : its) {
				Itinerary itinerary = JSONUtils.getFullMapper().convertValue(it, Itinerary.class);
				list.add(itinerary);
			}

		}

		return list;
	}

	public static void saveItinerary(BasicItinerary bi) throws ConnectionException, ProtocolException,
			SecurityException {
		if (bi != null) {
			String json = JSONUtils.convertToJSON(bi);
			MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS
					+ Config.CALL_ITINERARY);
			req.setMethod(Method.POST);
			req.setBody(json);

			JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());
		}
	}

	public static List<BasicItinerary> getMyItineraries() throws ConnectionException, ProtocolException,
			SecurityException, JSONException, JsonParseException, JsonMappingException, IOException {
		MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS + Config.CALL_ITINERARY);
		req.setMethod(Method.GET);

		MessageResponse res = JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());
		return eu.trentorise.smartcampus.android.common.Utils.convertJSONToObjects(res.getBody(), BasicItinerary.class);

		// JSONArray dobjs = new JSONArray(res.getBody());
		// for (int i = 0; i < dobjs.length(); i++) {
		// JSONObject dobj = dobjs.getJSONObject(i);
		// MyItinerary it =
		// JSONUtils.getFullMapper().readValue(dobj.getString("content"),
		// MyItinerary.class);
		// it.setId(dobj.getString("id"));
		// list.add(it);
		// }
		// return list;
	}

	public static void deleteMyItinerary(String id) throws ConnectionException, ProtocolException, SecurityException {
		if (id != null && id.length() > 0) {
			MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS
					+ Config.CALL_ITINERARY + "/" + id);
			req.setMethod(Method.DELETE);

			JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());
		}
	}


	public static boolean monitorMyItinerary(boolean monitor, String id) throws ConnectionException, ProtocolException,
			SecurityException {
		if (id != null && id.length() > 0) {
			MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS + Config.CALL_MONITOR
					+ "/" + id + "/" + Boolean.toString(monitor));
			req.setMethod(Method.POST);
			req.setBody("");

			MessageResponse res = JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());
			
		}
		//se cambiato restituisce il valore del monitor
		return monitor;

	}
	
	
	public static boolean monitorMyRecItinerary(boolean monitor, String id) throws ConnectionException, ProtocolException,
	SecurityException {
//		if (id != null && id.length() > 0) {
//	MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS + Config.CALL_MONITOR
//			+ "/" + id + "/" + Boolean.toString(monitor));
//	req.setMethod(Method.POST);
//	req.setBody("");
//
//	MessageResponse res = JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());
//	
//}
//se cambiato restituisce il valore del monitor
return monitor;

}

	/*
	 * BUS
	 */
	public static List<Route> getRoutesByAgencyId(String agencyId) throws ConnectionException, ProtocolException,
			SecurityException, JsonParseException, JsonMappingException, IOException {
		List<Route> list = new ArrayList<Route>();

		MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS + Config.CALL_BUS_ROUTES
				+ "/" + agencyId);
		req.setMethod(Method.GET);

		MessageResponse res = JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());

		List<?> routes = JSONUtils.getFullMapper().readValue(res.getBody(), List.class);
		for (Object r : routes) {
			Route route = JSONUtils.getFullMapper().convertValue(r, Route.class);
			list.add(route);
		}

		Collections.sort(list, Utils.getRouteComparator());

		return list;
	}

	public static List<Stop> getStopsByAgencyIdRouteId(String agencyId, String routeId) throws ConnectionException,
			ProtocolException, SecurityException, JsonParseException, JsonMappingException, IOException {
		List<Stop> list = new ArrayList<Stop>();

		MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS + Config.CALL_BUS_STOPS
				+ "/" + agencyId + "/" + routeId);
		req.setMethod(Method.GET);

		MessageResponse res = JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());

		List<?> stops = JSONUtils.getFullMapper().readValue(res.getBody(), List.class);
		for (Object r : stops) {
			Stop stop = JSONUtils.getFullMapper().convertValue(r, Stop.class);
			list.add(stop);
		}

		Collections.sort(list, new Comparator<Stop>() {
			public int compare(Stop o1, Stop o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		return list;
	}

	public static List<StopTime> getStoptimesByAgencyIdRouteIdStopId(String agencyId, String routeId, String stopId)
			throws ConnectionException, ProtocolException, SecurityException, JsonParseException, JsonMappingException,
			IOException {
		List<StopTime> list = new ArrayList<StopTime>();

		MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS
				+ Config.CALL_BUS_STOPTIMES + "/" + agencyId + "/" + routeId + "/" + stopId);
		req.setMethod(Method.GET);

		MessageResponse res = JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());

		List<?> stoptimes = JSONUtils.getFullMapper().readValue(res.getBody(), List.class);
		for (Object r : stoptimes) {
			StopTime stoptime = JSONUtils.getFullMapper().convertValue(r, StopTime.class);
			list.add(stoptime);
		}

		long now = Calendar.getInstance().getTimeInMillis();
		List<StopTime> newlist = new ArrayList<StopTime>();
		for (StopTime st : list) {
			long newTime = st.getTime() * 1000;
			if (newTime < now) {
				st.setTime(newTime);
				newlist.add(st);
			}
		}
		list = newlist;

		Collections.sort(list, new Comparator<StopTime>() {
			public int compare(StopTime o1, StopTime o2) {
				if (o1.getTime() == o2.getTime())
					return 0;
				return o1.getTime() < o2.getTime() ? -1 : 1;
			}
		});

		return list;
	}

	/*
	 * Alerts
	 */
	public static void submitAlert(BasicAlert ba) throws ConnectionException, ProtocolException, SecurityException {
		if (ba != null) {
			String json = JSONUtils.convertToJSON(ba);
			System.err.println(json);
			MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS
					+ Config.CALL_ALERT_SUBMIT);
			req.setMethod(Method.POST);
			req.setBody(json);

			JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());
		}
	}

	public static String getAuthToken() {
		return getAccessProvider().readToken(instance.mContext, null);
	}

	public static JPHelper getInstance() throws DataException {
		if (instance == null)
			throw new DataException("JPHelper is not initialized");
		return instance;
	}

	public static SCAccessProvider getAccessProvider() {
		return accessProvider;
	}

	public static void endAppFailure(Activity activity, int id) {
		Toast.makeText(activity, activity.getResources().getString(id), Toast.LENGTH_LONG).show();
		activity.finish();
	}

	public static void showFailure(Activity activity, int id) {
		Toast.makeText(activity, activity.getResources().getString(id), Toast.LENGTH_LONG).show();
	}

	public ProtocolCarrier getProtocolCarrier() {
		return protocolCarrier;
	}

	public void setProtocolCarrier(ProtocolCarrier protocolCarrier) {
		this.protocolCarrier = protocolCarrier;
	}

	public static LocationHelper getLocationHelper() {
		return mLocationHelper;
	}

	public static void setLocationHelper(LocationHelper mLocationHelper) {
		JPHelper.mLocationHelper = mLocationHelper;
	}

	public static Boolean saveRecurrentJourney(BasicRecurrentJourneyParameters brj) throws ConnectionException,
			ProtocolException, SecurityException {
		if (brj != null) {
			String json = JSONUtils.convertToJSON(brj);
			MessageRequest req = null;
			if (brj.getClientId() != null) {
				req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS + Config.CALL_RECUR + "/"
						+ brj.getClientId());
				req.setMethod(Method.PUT);
			} else {
				req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS + Config.CALL_RECUR);
				req.setMethod(Method.POST);
			}
			req.setBody(json);

			JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());
			return true;
		}
		return false;
	}

	public static List<BasicRecurrentJourneyParameters> getMyRecurItineraries() throws ConnectionException,
			ProtocolException, SecurityException, JSONException, JsonParseException, JsonMappingException, IOException {
		MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS + Config.CALL_RECUR);
		req.setMethod(Method.GET);

		MessageResponse res = JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());
		return eu.trentorise.smartcampus.android.common.Utils.convertJSONToObjects(res.getBody(),
				BasicRecurrentJourneyParameters.class);
		//
		// JSONArray dobjs = new JSONArray(res.getBody());
		// for (int i = 0; i < dobjs.length(); i++) {
		// JSONObject dobj = dobjs.getJSONObject(i);
		// BasicRecurrentJourneyParameters it =
		// JSONUtils.getFullMapper().readValue(dobj.getString("content"),
		// BasicRecurrentJourneyParameters.class);
		// list.add(it);
		// }
		// return list;
	}

	public static void deleteMyRecurItinerary(String id) throws ConnectionException, ProtocolException,
			SecurityException {
		if (id != null && id.length() > 0) {
			MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS + Config.CALL_RECUR
					+ "/" + id);
			req.setMethod(Method.DELETE);

			JPHelper.instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());
		}
	}

	public static Object getItineraryObject(String objectId) throws ConnectionException, ProtocolException,
			SecurityException {
		MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(instance.mContext), Config.TARGET_ADDRESS + Config.CALL_ITINERARY
				+ "/" + objectId);
		req.setMethod(Method.GET);

		MessageResponse res = instance.getProtocolCarrier().invokeSync(req, Config.APP_TOKEN, getAuthToken());
		if (res.getBody() != null && res.getBody().length() != 0) {
			return eu.trentorise.smartcampus.android.common.Utils.convertJSONToObject(res.getBody(),
					BasicItinerary.class);
		} else {
			req.setTargetAddress(Config.TARGET_ADDRESS + Config.CALL_RECUR + "/" + objectId);
			return eu.trentorise.smartcampus.android.common.Utils.convertJSONToObject(res.getBody(),
					BasicRecurrentJourneyParameters.class);
		}
	}

	public class JPLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}


}
