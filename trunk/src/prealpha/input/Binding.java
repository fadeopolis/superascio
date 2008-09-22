package prealpha.input;

import static com.jme.input.KeyInput.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import prealpha.util.ArrayUtil;

public class Binding {
	
	public static final BindingManager Manager = new BindingManager();
	
	ActionType action;
	
	int[] keyCodes;
	int[] mouseCodes;
	
	public Binding() {
		Manager.registerBinding(this);
	}
	
	public static void actionTypeForKeyCode( int keyCode ) {
	//	return BindingManager.get(keyCode);		
	}
	
	public static void actionTypeForMouseButtonCode( int mouseCode ) {
//		return action;
	}
	
	public int[] getBoundKeys() {
		return keyCodes;
	}
	
	public int[] getBoundMouseButtons() {
		return mouseCodes;
	}
		
	public void addKeyCodes( int ... keyCodes ) {
		if ( this.keyCodes == null) this.keyCodes = keyCodes;
		else {
			int x = this.keyCodes.length;
			int[] array = Arrays.copyOf(this.keyCodes, x + keyCodes.length);
			for ( int i = 0; i < keyCodes.length; i++) {
				array[i+this.keyCodes.length] = keyCodes[i];
			}
			this.keyCodes = array;
		}		
		this.keyCodes = ArrayUtil.removeAllOccurences(this.keyCodes, 0);
	}
	
	public void addMouseCodes( int ... mouseCodes) {
		if ( this.mouseCodes == null) this.mouseCodes = mouseCodes;
		else {
			int x = this.mouseCodes.length;
			int[] array = Arrays.copyOf(this.mouseCodes, x + mouseCodes.length);
			for ( int i = 0; i < mouseCodes.length; i++) {
				array[i+this.mouseCodes.length] = mouseCodes[i];
			}
			this.mouseCodes = array;
		}		
		this.mouseCodes = ArrayUtil.removeAllOccurences(this.mouseCodes, 0);
	}
	
	public void removeKeyCodes( int ... keyCodes ) {
		if ( this.keyCodes == null || keyCodes == null ) return;
		else {
			if ( this.keyCodes.length - keyCodes.length <= 0 ) return;
			int x = 0;
			for ( int i : keyCodes ) {
				Arrays.sort(this.keyCodes);
				x = Arrays.binarySearch(this.keyCodes, i);
				if ( x >= 0 ) this.keyCodes[x] = 0;
			}
		}	
		this.keyCodes = ArrayUtil.removeAllOccurences(this.keyCodes, 0);
	}
	
	public void removeMouseCodes( int ... mouseCodes) {
		if ( this.mouseCodes == null || mouseCodes == null ) return;
		else {
			int x = 0;
			for ( int i : mouseCodes ) {
				Arrays.sort(this.mouseCodes);
				x = Arrays.binarySearch(this.mouseCodes, i);
				if ( x >= 0 ) this.mouseCodes[x] = 0;
			}
		}	
		this.mouseCodes = ArrayUtil.removeAllOccurences(this.mouseCodes, 0);
	}
	
	public void removeAllKeyCodes() {
		keyCodes = null;
		this.keyCodes = ArrayUtil.removeAllOccurences(this.keyCodes, 0);
	}
	
	public void removeAllMouseCodes() {
		mouseCodes = null;
		this.mouseCodes = ArrayUtil.removeAllOccurences(this.keyCodes, 0);
	}
		
	public void removeAllCodes() {
		this.removeAllKeyCodes();
		this.removeAllMouseCodes();
	}
	
	public ActionType getActionType() {
		return action;
	}
		
	public void setActionType( ActionType type ) {
		this.action = type;
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder("{ ActionType: " + (action == null ? null : action.toString()));
		s.append(" KeyCodes: ");
		if ( keyCodes == null) {
			s.append("null");
		} else {
			s.append(Arrays.toString(keyCodes));
		}
		s.append(" MouseCodes: ");
		if ( mouseCodes == null) {
			s.append("null");
		} else {
			s.append(Arrays.toString(mouseCodes));
		}
		s.append(" }");
		return s.toString();
	}
	
	public static void main(String[] args) {
//		Binding b = new Binding();
////
////		int x = Util.randomInt(1000);
////		for (int i = 0;i < x; i++) {
////			b.addKeyCodes(Util.randomInt(1000));
////		}
////		for (int i = 0;i < x; i++) {
////			b.addMouseCodes(Util.randomInt(1000));
////		}
////		for (int i = 0;i < Util.randomInt(x-5); i++) {
////			b.removeMouseCodes(Util.randomInt(1000));
////		}
////		for (int i = 0;i < Util.randomInt(x-5); i++) {
////			b.removeMouseCodes(Util.randomInt(1000));
////		}
//////		
//		int x = 5;
//		for (int i = 0; i < x; i++) {
//			b.addKeyCodes(i);
//			b.addMouseCodes(i);
//		}
//		for (int i = 0; i < 3; i++) {
//			b.removeKeyCodes(i);
//			b.removeMouseCodes(i);
//		}
////
//		System.out.println(b);		
		
		BindingManager m = new BindingManager();
		
		System.out.println(224*3*2*8);
	}
}

class BindingManager {
	
	int[][] keyCodes;
	int[][] mouseCodes;
	
	public BindingManager() {
		keyCodes = new int[224][3];
		mouseCodes = new int[224][3];
		
		for ( int i = 0; i < keyCodes.length; i++ ) {
			for ( int j = 0; i < keyCodes[i].length; j++ ) {
				keyCodes[i][j] = -1;
			}
		}
		for ( int i = 0; i < mouseCodes.length; i++ ) {
			for ( int j = 0; i < mouseCodes[i].length; j++ ) {
				mouseCodes[i][j] = -1;
			}
		}
	}
	
	public Binding createBinding() {
		return null;
		
	}
	
	public void registerBinding(Binding b) {
		//register keyCodes
		for ( int i : b.getBoundKeys() ) {
			addKeyAction(i, b.getActionType().ordinal());
		}
		
	}
	
	private void addKeyAction( int keyCode, int actionType) {
		int[] i = keyCodes[keyCode];
		boolean status = false;
		for ( int j = 0; j < i.length; j++) {
			if ( i[j] == -1 )  {
				keyCodes[keyCode][j] = actionType;
				status = true;
				break;
			}
		}
		
		if ( !status) {
			keyCodes[keyCode] = Arrays.copyOf(keyCodes[keyCode], keyCodes[keyCode].length+2);
			addKeyAction( keyCode, actionType);
		}
	}
	
	private void addMouseAction( int mouseCode, int actionType) {
		
	}
 	
	public ActionType[] getAction( int keyCode ) {
		ActionType[] output = new ActionType[keyCodes[keyCode].length];
		for ( int i : keyCodes[keyCode] ) {
			output[i] = ActionType.valueOf(i);
		}
		
		return output;
	}

	public static void main(String[] args) {
		Binding b = new Binding();
		b.setActionType(ActionType.FIRE);
		b.addKeyCodes(2,3,4,5);
		
		
	}
}