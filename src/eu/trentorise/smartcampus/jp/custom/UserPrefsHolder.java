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
package eu.trentorise.smartcampus.jp.custom;

import it.sayservice.platform.smartplanner.data.message.Position;
import it.sayservice.platform.smartplanner.data.message.RType;
import it.sayservice.platform.smartplanner.data.message.TType;

import java.util.ArrayList;
import java.util.List;

public class UserPrefsHolder {

	private TType[] transportTypes;
	private RType routeType;
	private List<Position> favorites;

	public UserPrefsHolder(List<Position> addresses, RType routeType, TType... tTypes) {
		this.setRouteType(routeType);

		List<TType> list = new ArrayList<TType>();
		for (TType tType : tTypes) {
			list.add(tType);
		}
		this.transportTypes = list.toArray(new TType[] {});
		this.favorites = addresses;
	}

	public TType[] getTransportTypes() {
		return transportTypes;
	}

	public void setTransportTypes(TType[] transportTypes) {
		this.transportTypes = transportTypes;
	}

	public RType getRouteType() {
		return routeType;
	}

	public void setRouteType(RType routeType) {
		this.routeType = routeType;
	}

	public List<Position> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Position> favorites) {
		this.favorites = favorites;
	}

}
