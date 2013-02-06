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

import it.sayservice.platform.smartplanner.data.message.RType;
import it.sayservice.platform.smartplanner.data.message.TType;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Config {

	public static final String APP_TOKEN = "journeyplanner";

	public static int mainlayout = android.R.id.content;

	public static final String TARGET_ADDRESS = "/smartcampus-journeyplanner-web/rest";

	public static final String CALL_PLANSINGLEJOURNEY = "/plansinglejourney";
	public static final String CALL_ITINERARY = "/eu.trentorise.smartcampus.journeyplanner.sync.BasicItinerary";
	public static final String CALL_RECUR = "/eu.trentorise.smartcampus.journeyplanner.sync.BasicRecurrentJourneyParameters";

	public static final String CALL_PLAN_RECUR = "/plansinglejourney";
	public static final String CALL_SAVE_RECUR = "/eu.trentorise.smartcampus.journeyplanner.sync.BasicRecurrentJourney";

	public static final String CALL_MONITOR = "/monitoritinerary";
	public static final String CALL_BUS_ROUTES = "/getroutes"; // agencyId
	public static final String CALL_BUS_STOPS = "/getstops"; // agencyId,
																// routeId
	public static final String CALL_BUS_STOPTIMES = "/gettimetable"; // agencyId,
																		// routeId,
																		// stopId
	public static final String CALL_ALERT_SUBMIT = "/submitalert";

	// public static final TType[] TTYPES_ALLOWED = new TType[] { TType.BICYCLE,
	// TType.BUS, TType.CAR, TType.TRAIN,
	// TType.TRANSIT, TType.WALK };
	public static final TType[] TTYPES_ALLOWED = new TType[] { TType.CAR, TType.BICYCLE, TType.TRANSIT,
			TType.SHAREDBIKE, TType.SHAREDBIKE_WITHOUT_STATION, TType.CARWITHPARKING, TType.SHAREDCAR,
			TType.SHAREDCAR_WITHOUT_STATION, TType.WALK };// TType.values();
	public static final RType[] RTYPES_ALLOWED = new RType[] { RType.fastest, RType.leastChanges, RType.leastWalking };

	public static final TType[] TTYPES_DEFAULT = new TType[] { TType.TRANSIT };
	public static final RType RTYPE_DEFAULT = RType.fastest;

	public static final String HOME_FRAGMENT_TAG = "homefragment";
	public static final String PLAN_NEW_FRAGMENT_TAG = "plannewfragment";
	public static final String LEGMAP_FRAGMENT_TAG = "legmap";
	public static final String MY_JOURNEYS_FRAGMENT_TAG = "myjourneysfragment";
	public static final String PROFILE_FRAGMENT_TAG = "profile";
	public static final String FAVORITES_FRAGMENT_TAG = "favorites";

	public static final String USER_PREFS = "userprefs";
	// public static final String USER_PREFS_TTYPE_TRANSIT =
	// TType.TRANSIT.toString(); // default
	// public static final String USER_PREFS_TTYPE_CAR = TType.CAR.toString();
	// public static final String USER_PREFS_TTYPE_BICYCLE =
	// TType.BICYCLE.toString();
	// public static final String USER_PREFS_TTYPE_SHAREDBIKE =
	// TType.SHAREDBIKE.toString();
	// public static final String USER_PREFS_TTYPE_SHAREDBYKE_WITHOUT_STATION =
	// TType.SHAREDBIKE_WITHOUT_STATION
	// .toString();
	// public static final String USER_PREFS_TTYPE_CARWITHPARKING =
	// TType.CARWITHPARKING.toString();
	// public static final String USER_PREFS_TTYPE_SHAREDCAR =
	// TType.SHAREDCAR.toString();
	// public static final String USER_PREFS_TTYPE_SHAREDCAR_WITHOUT_STATION =
	// TType.SHAREDCAR_WITHOUT_STATION.toString();
	// public static final String USER_PREFS_TTYPE_BUS = TType.BUS.toString();
	// public static final String USER_PREFS_TTYPE_TRAIN =
	// TType.TRAIN.toString();
	// public static final String USER_PREFS_TTYPE_WALK = TType.WALK.toString();

	public static final String USER_PREFS_RTYPE = "routetype";
	// public static final String USER_PREFS_RTYPE_FASTEST =
	// RType.fastest.toString(); // default
	// public static final String USER_PREFS_RTYPE_GREENEST =
	// RType.greenest.toString();
	// public static final String USER_PREFS_RTYPE_SAFEST =
	// RType.safest.toString();

	public static final SimpleDateFormat FORMAT_DATE_SMARTPLANNER = new SimpleDateFormat("MM/dd/yyyy",Locale.ENGLISH);
	public static final SimpleDateFormat FORMAT_TIME_SMARTPLANNER = new SimpleDateFormat("hh:mmaa",Locale.ENGLISH);
	public static final SimpleDateFormat FORMAT_DATE_UI = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
	public static final SimpleDateFormat FORMAT_TIME_UI = new SimpleDateFormat("HH:mm",Locale.ENGLISH);
	public static final String USER_PREFS_FAVORITES = "favorites";

	protected static final long MAX_RECUR_INTERVAL = 2 * 60 * 60 * 1000;
	
	public static final int PAST_MINUTES_SPAN = -5; // has to be negative

	// parameters for the Province of Trento
	// public static final double LOWER_LEFT_LAT = 45.665544;
	// public static final double LOWER_LEFT_LONG = 10.374525;
	// public static final double UPPER_RIGHT_LAT = 46.552733;
	// public static final double UPPER_RIGHT_LONG = 11.975756;
	// rectangle reduced
	public static final String TN_REGION = "it";
	public static final String TN_COUNTRY = "IT";
	public static final String TN_ADM_AREA = "TN";

}
