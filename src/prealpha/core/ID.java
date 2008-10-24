package prealpha.core;

import com.jme.util.Timer;

public final class ID {
	
	private static final StringBuilder s = new StringBuilder();
	
	private String name;
	private float time;

	
	private ID( String name, float time ) {
		this.name = name;
		this.time = time;
	}
	
	public static ID requestID( String name ) {
		return new ID( name , Timer.getTimer().getTimeInSeconds() );
	}
	
	public String getName() {
		return name;
	}
	
	public float getCreationTime() {
		return time;
	}
	
	public boolean equals( Object o) {
		if ( !( o instanceof ID ) ) return false;
		ID id = (ID) o;
		if ( !id.name.equals(this.name)) return false;
		if ( !(id.time == this.time) ) return false;
		
		return true;
	}
	
	public String toString() {
		int hours = (int) (time/3600);
		int minutes = (int) ((time-hours*3600)/60);
		float seconds = time - hours*3600 - minutes*60;
		return name + " : " + hours + ":" + minutes + ":" + seconds;
	}	
}