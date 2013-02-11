package eu.trentorise.smartcampus.jp.custom.data;

import it.sayservice.platform.smartplanner.data.message.TType;

public class RecurrentItinerary {
private String name;
private TType transportType;
private String from;
private String to;
private boolean monitor;


public RecurrentItinerary(String name, TType transportType, String from,
		String to, boolean monitor) {
	super();
	this.name = name;
	this.transportType = transportType;
	this.from = from;
	this.to = to;
	this.monitor = monitor;
}
public String getFrom() {
	return from;
}
public void setFrom(String from) {
	this.from = from;
}
public String getTo() {
	return to;
}
public void setTo(String to) {
	this.to = to;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public TType getTransportType() {
	return transportType;
}
public void setTransportType(TType transportType) {
	this.transportType = transportType;
}
public boolean isMonitor() {
	return monitor;
}
public void setMonitor(boolean monitor) {
	this.monitor = monitor;
}
}
