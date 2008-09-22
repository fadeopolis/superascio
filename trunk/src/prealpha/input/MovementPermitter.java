package prealpha.input;

import java.util.EnumMap;
import java.util.EnumSet;


public class MovementPermitter {

	public enum MovementType {
		Forward, Backward, Turnleft, Turnright, Strafeleft, Straferight, Up, Down, Jump
	}
	
	protected EnumMap<MovementType, Boolean> possibleMovements;
	
	public MovementPermitter() {
		this.possibleMovements = new EnumMap<MovementType, Boolean>(MovementType.class);
	}
	
	public MovementPermitter( EnumMap<MovementType, Boolean> possibleMovements ) {
		this.possibleMovements = possibleMovements;
	}
	
	public boolean requestPermit(MovementType type) {
		Boolean out = possibleMovements.get(type); 
		return out == null ? false : out;			
	}

	public void setValue(MovementType type, boolean value) {
		this.possibleMovements.put(type, value);
	}
	
	public void setAllValues( boolean value ) {
		for ( MovementType type : this.possibleMovements.keySet()) {
			this.possibleMovements.put(type, value);
		}
	}
 	
	public void addMovementType(MovementType type) {
		possibleMovements.put(type, true);
	}
	
	public void removeMovementType(MovementType type) {
		possibleMovements.remove(type);
	}
	
	/*
	public static void main(String[] args) {
		MovementPermitter mp = new MovementPermitter();
		mp.addMovementType(MovementType.Forward);
		mp.setAllValues(false);
		
		System.out.println(mp.possibleMovements);
	}
	*/
	
	public String toString() {
		return this.possibleMovements.toString();
	}
}
