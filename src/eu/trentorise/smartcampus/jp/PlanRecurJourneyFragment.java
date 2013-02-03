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

import it.sayservice.platform.smartplanner.data.message.TType;
import it.sayservice.platform.smartplanner.data.message.journey.JourneyRecurrence;
import it.sayservice.platform.smartplanner.data.message.journey.RecurrentJourneyParameters;

import java.text.ParseException;
import java.util.Date;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.jp.custom.data.BasicRecurrentJourneyParameters;
import eu.trentorise.smartcampus.jp.helper.PrefsHelper;
import eu.trentorise.smartcampus.jp.helper.processor.DeleteMyRecurItineraryProcessor;
import eu.trentorise.smartcampus.jp.helper.processor.SaveRecurJourneyProcessor;

public class PlanRecurJourneyFragment extends PlanNewJourneyFragment {

	private static final String[] RECURRENCE = new String[] { "Daily", "Weekdays", "Weekends" };

	public static final String PARAMS = "parameters";

	private BasicRecurrentJourneyParameters params = null;
	private EditText fromTime = null;
	private EditText fromDate = null;
	private EditText toTime = null;
	private EditText toDate = null;

	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		if (fromPosition != null)
			params.getData().setFrom(fromPosition);
		if (toPosition != null)
			params.getData().setTo(toPosition);

		params.getData().setTransportTypes((TType[]) userPrefsHolder.getTransportTypes());
		params.getData().setRouteType(userPrefsHolder.getRouteType());
		arg0.putSerializable(PARAMS, params);
	}

	private JourneyRecurrence mapRecurrence(int pos) {
		return JourneyRecurrence.values()[pos];
	}

	private int mapRecurrenceInv(JourneyRecurrence recurrence) {
		switch (recurrence) {
		case EVERYDAY:
			return 0;
		case WEEKDAYS:
			return 1;
		case WEEKENDS:
			return 2;
		default:
			break;
		}
		return 0;
	}

	public static String getRecurrenceString(JourneyRecurrence recurrence) {
		switch (recurrence) {
		case EVERYDAY:
			return RECURRENCE[0];
		case WEEKDAYS:
			return RECURRENCE[1];
		case WEEKENDS:
			return RECURRENCE[2];
		default:
			break;
		}
		return null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.containsKey(PARAMS)) {
			this.params = (BasicRecurrentJourneyParameters) savedInstanceState.getSerializable(PARAMS);
		} else if (getArguments() != null && getArguments().containsKey(PARAMS)) {
			this.params = (BasicRecurrentJourneyParameters) getArguments().getSerializable(PARAMS);
		}
		if (params != null) {
			if (params.getData().getFrom() != null)
				fromPosition = params.getData().getFrom();
			if (params.getData().getTo() != null)
				toPosition = params.getData().getTo();
		} else {
			params = new BasicRecurrentJourneyParameters();
			params.setMonitor(true);
			params.setData(new RecurrentJourneyParameters());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.planrecurjourney, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (params.getName() != null)
			((EditText) getView().findViewById(R.id.name)).setText(params.getName());
		((CheckBox) getView().findViewById(R.id.recur_monitor)).setChecked(params.isMonitor());
		Spinner spinner = (Spinner) getView().findViewById(R.id.recurrence);
		spinner.setAdapter(new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_spinner_item, RECURRENCE));
		if (params.getData().getRecurrence() != null) {
			spinner.setSelection(mapRecurrenceInv(params.getData().getRecurrence()));
		}
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				params.getData().setRecurrence(mapRecurrence(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		// hide keyboard if it is still open
		InputMethodManager imm = (InputMethodManager) getSherlockActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (fromDate != null) {
			imm.hideSoftInputFromWindow(fromDate.getWindowToken(), 0);
		}
	}

	@Override
	protected void setUpMainOperation() {
		if (params.getClientId() == null) {
			getView().findViewById(R.id.recurr_delete).setVisibility(View.GONE);
		} else {
			((Button) getView().findViewById(R.id.recurr_delete)).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					SCAsyncTask<String, Void, Void> task = new SCAsyncTask<String, Void, Void>(getSherlockActivity(),
							new DeleteMyRecurItineraryProcessor(getSherlockActivity()));
					task.execute(params.getName(), params.getClientId());
				}
			});
		}
		Button searchBtn = (Button) getView().findViewById(R.id.recurr_save);
		searchBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// user preferences
				ToggleButton useCustomPrefsToggleBtn = (ToggleButton) getView().findViewById(R.id.plannew_options_toggle);
				View userPrefsLayout = (View) getView().findViewById(R.id.plannew_userprefs);

				if (useCustomPrefsToggleBtn.isChecked()) {
					TableLayout tTypesTableLayout = (TableLayout) userPrefsLayout.findViewById(R.id.transporttypes_table);
					RadioGroup rTypesRadioGroup = (RadioGroup) userPrefsLayout.findViewById(R.id.routetypes_radioGroup);
					userPrefsHolder = PrefsHelper.userPrefsViews2Holder(tTypesTableLayout, rTypesRadioGroup, userPrefs);
				} else {
					userPrefsHolder = PrefsHelper.sharedPreferences2Holder(userPrefs);
				}

				EditText name = (EditText) getView().findViewById(R.id.name);
				if (name.getText() == null || name.getText().toString().length() == 0) {
					Toast.makeText(getActivity(), R.string.name_field_empty, Toast.LENGTH_SHORT).show();
					return;
				}
				params.setName(name.getText().toString().trim());
				params.setMonitor(((CheckBox) getView().findViewById(R.id.recur_monitor)).isChecked());

				RecurrentJourneyParameters rj = params.getData();
				if (fromPosition == null) {
					Toast.makeText(getActivity(), R.string.from_field_empty, Toast.LENGTH_SHORT).show();
					return;
				}
				if (toPosition == null) {
					Toast.makeText(getActivity(), R.string.to_field_empty, Toast.LENGTH_SHORT).show();
					return;
				}

				rj.setFrom(fromPosition);
				rj.setTo(toPosition);

				Date fromDateD;
				Date fromTimeD;
				Date toDateD;
				Date toTimeD;

				CharSequence timeString = fromDate.getText();
				if (timeString == null) {
					Toast.makeText(getActivity(), R.string.from_date_field_empty, Toast.LENGTH_SHORT).show();
					return;
				} else {
					try {
						fromDateD = Config.FORMAT_DATE_UI.parse(timeString.toString());
						rj.setFromDate(fromDateD.getTime());
					} catch (ParseException e) {
						Toast.makeText(getActivity(), R.string.from_date_field_empty, Toast.LENGTH_SHORT).show();
						return;
					}
				}

				timeString = fromTime.getText();
				if (timeString == null) {
					Toast.makeText(getActivity(), R.string.from_time_field_empty, Toast.LENGTH_SHORT).show();
					return;
				} else {
					try {
						fromTimeD = Config.FORMAT_TIME_UI.parse(timeString.toString());
						rj.setTime(Config.FORMAT_TIME_SMARTPLANNER.format(fromTimeD));
					} catch (ParseException e) {
						Toast.makeText(getActivity(), R.string.from_time_field_empty, Toast.LENGTH_SHORT).show();
						return;
					}
				}

				if (!eu.trentorise.smartcampus.jp.helper.Utils.validFromDateTime(fromDateD, fromTimeD)) {
					Toast.makeText(getActivity(), R.string.datetime_before_now, Toast.LENGTH_SHORT).show();
					return;
				}

				timeString = toDate.getText();
				if (timeString == null) {
					Toast.makeText(getActivity(), R.string.to_date_field_empty, Toast.LENGTH_SHORT).show();
					return;
				} else {
					try {
						toDateD = Config.FORMAT_DATE_UI.parse(timeString.toString());
						rj.setToDate(toDateD.getTime());
						if (rj.getToDate() < rj.getFromDate()) {
							Toast.makeText(getActivity(), R.string.to_date_before_from_date, Toast.LENGTH_SHORT).show();
							return;
						}
					} catch (ParseException e) {
						Toast.makeText(getActivity(), R.string.to_date_field_empty, Toast.LENGTH_SHORT).show();
						return;
					}
				}

				timeString = toTime.getText();
				if (timeString == null) {
					Toast.makeText(getActivity(), R.string.to_time_field_empty, Toast.LENGTH_SHORT).show();
					return;
				} else {
					try {
						toTimeD = Config.FORMAT_TIME_UI.parse(timeString.toString());
						rj.setInterval(toTimeD.getTime() - toDateD.getTime());
						if (rj.getInterval() < 0)
							rj.setInterval(rj.getInterval() + 24 * 60 * 60 * 1000);
						if (rj.getInterval() > Config.MAX_RECUR_INTERVAL) {
							Toast.makeText(getActivity(), R.string.interval_too_large, Toast.LENGTH_SHORT).show();
							return;
						}

					} catch (ParseException e) {
						Toast.makeText(getActivity(), R.string.to_time_field_empty, Toast.LENGTH_SHORT).show();
						return;
					}
				}

				if (!eu.trentorise.smartcampus.jp.helper.Utils.validFromDateTimeToDateTime(fromDateD, fromTimeD, toDateD,
						toTimeD)) {
					Toast.makeText(getActivity(), R.string.datetime_to_before_from, Toast.LENGTH_SHORT).show();
					return;
				}

				rj.setTransportTypes((TType[]) userPrefsHolder.getTransportTypes());
				rj.setRouteType(userPrefsHolder.getRouteType());

				rj.setRecurrence(mapRecurrence(((Spinner) getView().findViewById(R.id.recurrence)).getSelectedItemPosition()));

				SCAsyncTask<BasicRecurrentJourneyParameters, Void, Boolean> task = new SCAsyncTask<BasicRecurrentJourneyParameters, Void, Boolean>(
						getSherlockActivity(), new SaveRecurJourneyProcessor(getSherlockActivity()));
				task.execute(params);
			}
		});
	}

	@Override
	protected void setUpTimingControls() {
		fromTime = (EditText) getView().findViewById(R.id.recur_time_from);
		toTime = (EditText) getView().findViewById(R.id.recur_time_to);

		fromDate = (EditText) getView().findViewById(R.id.recur_date_from);
		toDate = (EditText) getView().findViewById(R.id.recur_date_to);

		Date newDate = new Date();

		if (params.getData().getTime() != null) {
			try {
				Date d = Config.FORMAT_TIME_SMARTPLANNER.parse(params.getData().getTime());
				fromTime.setText(Config.FORMAT_TIME_UI.format(d));
				d.setTime(d.getTime() + params.getData().getInterval());
				toTime.setText(Config.FORMAT_TIME_UI.format(d));
			} catch (ParseException e) {
			}
		} else {
			fromTime.setTag(newDate);
			fromTime.setText(Config.FORMAT_TIME_UI.format(newDate));
			toTime.setTag(newDate);
			toTime.setText(Config.FORMAT_TIME_UI.format(newDate));
		}

		Date d = params.getData().getFromDate() > 0 ? new Date(params.getData().getFromDate()) : newDate;
		fromDate.setText(Config.FORMAT_DATE_UI.format(d));

		d = params.getData().getToDate() > 0 ? new Date(params.getData().getToDate()) : newDate;
		toDate.setText(Config.FORMAT_DATE_UI.format(d));

		fromTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = TimePickerDialogFragment.newInstance((EditText) v);
				newFragment.setArguments(TimePickerDialogFragment.prepareData(fromTime.toString()));
				newFragment.show(getSherlockActivity().getSupportFragmentManager(), "timePicker");
			}
		});

		toTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = TimePickerDialogFragment.newInstance((EditText) v);
				newFragment.setArguments(TimePickerDialogFragment.prepareData(toTime.toString()));
				newFragment.show(getSherlockActivity().getSupportFragmentManager(), "timePicker");
			}
		});

		fromDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = DatePickerDialogFragment.newInstance((EditText) v);
				newFragment.setArguments(DatePickerDialogFragment.prepareData(fromDate.toString()));
				newFragment.show(getSherlockActivity().getSupportFragmentManager(), "datePicker");
			}
		});
		toDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = DatePickerDialogFragment.newInstance((EditText) v);
				newFragment.setArguments(DatePickerDialogFragment.prepareData(toDate.toString()));
				newFragment.show(getSherlockActivity().getSupportFragmentManager(), "datePicker");
			}
		});

	}

}
