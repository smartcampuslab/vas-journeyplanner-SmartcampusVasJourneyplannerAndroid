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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;

import eu.trentorise.smartcampus.jp.notifications.BroadcastNotificationsActivity;

public class HomeActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
	}

	@Override
	protected void onStart() {
		super.onStart();

		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		if (getSupportActionBar().getNavigationMode() != ActionBar.NAVIGATION_MODE_STANDARD) {
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}
	}

	public void goToFunctionality(View view) {
		Intent intent;
		int viewId = view.getId();

		switch (viewId) {
		case R.id.btn_planjourney:
			intent = new Intent(this, PlanJourneyActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			return;
			// case R.id.btn_monitorrecurrentjourney:
		case R.id.btn_broadcast:
			intent = new Intent(this, BroadcastNotificationsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			return;
		case R.id.btn_myprofile:
			intent = new Intent(this, ProfileActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			return;
		case R.id.btn_monitorrecurrentjourney:
			intent = new Intent(this, PlanRecurJourneyActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			return;
		default:
			Toast toast = Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
	}

	public int getMainlayout() {
		return Config.mainlayout;
	}
}
