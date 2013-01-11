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

import it.sayservice.platform.smartplanner.data.message.RType;
import it.sayservice.platform.smartplanner.data.message.TType;
import it.sayservice.platform.smartplanner.data.message.otpbeans.Route;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import android.content.Context;
import android.widget.ImageView;
import eu.trentorise.smartcampus.jp.Config;
import eu.trentorise.smartcampus.jp.R;

public class Utils {

	public static ImageView getImageByTType(Context ctx, TType tType) {
		ImageView imgv = new ImageView(ctx);

		switch (tType) {
		case BICYCLE:
			imgv.setImageResource(R.drawable.ic_mt_bicyle);
			break;
		case CAR:
			imgv.setImageResource(R.drawable.ic_mt_car);
			break;
		case BUS:
			imgv.setImageResource(R.drawable.ic_mt_bus);
			break;
		case WALK:
			imgv.setImageResource(R.drawable.ic_mt_foot);
			break;
		case TRAIN:
			imgv.setImageResource(R.drawable.ic_mt_train);
			break;
		case TRANSIT:
			imgv.setImageResource(R.drawable.ic_mt_bus);
			break;
		default:
		}

		return imgv;
	}

	public static String getTTypeUIString(TType tType) {
		String uis = null;

		switch (tType) {
		case TRANSIT:
			uis = "Transit";
			break;
		case CAR:
			uis = "Car";
			break;
		case BICYCLE:
			uis = "Bicycle";
			break;
		case SHAREDBIKE:
			uis = "Shared bike";
			break;
		case SHAREDBIKE_WITHOUT_STATION:
			uis = "Shared bike without station";
			break;
		case CARWITHPARKING:
			uis = "Car with parking";
			break;
		case SHAREDCAR:
			uis = "Shared car";
			break;
		case SHAREDCAR_WITHOUT_STATION:
			uis = "Shared car without station";
			break;
		case BUS:
			uis = "Bus";
			break;
		case TRAIN:
			uis = "Train";
			break;
		case WALK:
			uis = "Walk";
			break;
		case GONDOLA:
			uis = "Gondola";
			break;
		case SHUTTLE:
			uis = "Shuttle";
			break;
		default:
			break;
		}

		return uis;
	}

	public static String getRTypeUIString(RType rType) {
		String uis = null;

		switch (rType) {
		case fastest:
			uis = "Fastest";
			break;
		case greenest:
			uis = "Greenest";
			break;
		case healthy:
			uis = "Healthy";
			break;
		case leastChanges:
			uis = "Least changes";
			break;
		case leastWalking:
			uis = "Least walking";
			break;
		case safest:
			uis = "Safest";
			break;
		}

		return uis;
	}

	public static Date sjDateString2date(String s) {
		SimpleDateFormat df = Config.FORMAT_DATE_SMARTPLANNER;
		try {
			return df.parse(s);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date sjTimeString2date(String s) {
		SimpleDateFormat df = Config.FORMAT_TIME_SMARTPLANNER;
		try {
			return df.parse(s);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String millis2time(long millis) {
		SimpleDateFormat df = Config.FORMAT_TIME_UI;
		Date date = new Date(millis);
		return df.format(date);
	}

	public static String uiTime2ServerTime(CharSequence timeStr) {
		try {
			Date time = timeStr != null ? Config.FORMAT_TIME_UI.parse(timeStr.toString()) : new Date();
			return Config.FORMAT_TIME_SMARTPLANNER.format(time);
		} catch (ParseException e) {
			return Config.FORMAT_TIME_SMARTPLANNER.format(new Date());
		}
	}

	public static String serverTime2UITime(CharSequence timeStr) {
		try {
			Date time = timeStr != null ? Config.FORMAT_TIME_SMARTPLANNER.parse(timeStr.toString()) : new Date();
			return Config.FORMAT_TIME_UI.format(time);
		} catch (ParseException e) {
			return Config.FORMAT_TIME_UI.format(new Date());
		}
	}

	public static String uiDate2ServerDate(CharSequence dateStr) {
		try {
			Date date = dateStr != null ? Config.FORMAT_DATE_UI.parse(dateStr.toString()) : new Date();
			return Config.FORMAT_DATE_SMARTPLANNER.format(date);
		} catch (ParseException e) {
			return Config.FORMAT_DATE_SMARTPLANNER.format(new Date());
		}
	}

	public static String serverDate2UIDate(CharSequence dateStr) {
		try {
			Date date = dateStr != null ? Config.FORMAT_DATE_SMARTPLANNER.parse(dateStr.toString()) : new Date();
			return Config.FORMAT_DATE_UI.format(date);
		} catch (ParseException e) {
			return Config.FORMAT_DATE_UI.format(new Date());
		}
	}

	public static boolean validFromDateTime(Date fromDate, Date fromTime) {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		// minutes in the past span
		now.add(Calendar.MINUTE, Config.PAST_MINUTES_SPAN);

		Calendar time = Calendar.getInstance();
		time.setTime(fromTime);
		Calendar from = Calendar.getInstance();
		from.setTime(fromDate);
		from.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		from.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);

		return from.compareTo(now) < 0 ? false : true;
	}

	public static boolean validFromDateTimeToDateTime(Date fromDate, Date fromTime, Date toDate, Date toTime) {
		Calendar fromTimeCal = Calendar.getInstance();
		fromTimeCal.setTime(fromTime);
		Calendar fromCal = Calendar.getInstance();
		fromCal.setTime(fromDate);
		fromCal.set(Calendar.HOUR_OF_DAY, fromTimeCal.get(Calendar.HOUR_OF_DAY));
		fromCal.set(Calendar.MINUTE, fromTimeCal.get(Calendar.MINUTE));
		fromCal.set(Calendar.SECOND, 0);
		fromCal.set(Calendar.MILLISECOND, 0);

		Calendar toTimeCal = Calendar.getInstance();
		toTimeCal.setTime(toTime);
		Calendar toCal = Calendar.getInstance();
		toCal.setTime(toDate);
		toCal.set(Calendar.HOUR_OF_DAY, toTimeCal.get(Calendar.HOUR_OF_DAY));
		toCal.set(Calendar.MINUTE, toTimeCal.get(Calendar.MINUTE));
		toCal.set(Calendar.SECOND, 0);
		toCal.set(Calendar.MILLISECOND, 0);

		return fromCal.before(toCal);
	}

	public static Comparator<Route> getRouteComparator() {
		Comparator<Route> comparator = new Comparator<Route>() {
			public int compare(Route o1, Route o2) {
				String s1 = o1.getRouteShortName();
				String s2 = o2.getRouteShortName();

				int thisMarker = 0;
				int thatMarker = 0;
				int s1Length = s1.length();
				int s2Length = s2.length();

				while (thisMarker < s1Length && thatMarker < s2Length) {
					String thisChunk = getChunk(s1, s1Length, thisMarker);
					thisMarker += thisChunk.length();

					String thatChunk = getChunk(s2, s2Length, thatMarker);
					thatMarker += thatChunk.length();

					// If both chunks contain numeric characters, sort them
					// numerically
					int result = 0;
					if (Character.isDigit(thisChunk.charAt(0)) && Character.isDigit(thatChunk.charAt(0))) {
						// Simple chunk comparison by length.
						int thisChunkLength = thisChunk.length();
						result = thisChunkLength - thatChunk.length();
						// If equal, the first different number counts
						if (result == 0) {
							for (int i = 0; i < thisChunkLength; i++) {
								result = thisChunk.charAt(i) - thatChunk.charAt(i);
								if (result != 0) {
									return result;
								}
							}
						}
					} else {
						result = thisChunk.compareTo(thatChunk);
					}

					if (result != 0)
						return result;
				}

				return s1Length - s2Length;
			}
		};

		return comparator;
	}

	private static String getChunk(String s, int slength, int marker) {
		StringBuilder chunk = new StringBuilder();
		char c = s.charAt(marker);
		chunk.append(c);
		marker++;
		if (Character.isDigit(c)) {
			while (marker < slength) {
				c = s.charAt(marker);
				if (!Character.isDigit(c))
					break;
				chunk.append(c);
				marker++;
			}
		} else {
			while (marker < slength) {
				c = s.charAt(marker);
				if (Character.isDigit(c))
					break;
				chunk.append(c);
				marker++;
			}
		}
		return chunk.toString();
	}
}
