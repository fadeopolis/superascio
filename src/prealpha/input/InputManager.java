package prealpha.input;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import bak.pcj.map.IntKeyBooleanMap;
import bak.pcj.map.IntKeyBooleanOpenHashMap;
import bak.pcj.map.IntKeyMap;
import bak.pcj.map.IntKeyOpenHashMap;

import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.Timer;
import com.jmex.game.StandardGame;
/**
 * This singleton gets the input from the InputSystem, interprets it 
 * ( how long has each button been pressed,...) and feeds this to the
 * listeners ( the inputhandlers )
 *
 * @author fader
 *
 */
public class InputManager implements KeyInputListener, MouseInputListener {

	private static InputManager instance;
			
	protected static IntKeyMap<ActionType> keyCodes;;
	protected static IntKeyMap<ActionType> mouseCodes;
	
	protected static final boolean[] oldKeyStats = new boolean[256];
	
	
	protected static EnumMap<ActionType, Set<InputListener>> listeners;
//	protected static EnumMap<ActionType, Set<InputListener>> listeners;
		
	//updatequeues
//	protected EnumMap<UpdateType, List<InputListener>> updateAlways;
	
	protected InputManager() {		
		KeyInput.get().addListener(this);
		MouseInput.get().addListener(this);
		
		keyCodes = new IntKeyOpenHashMap<ActionType>();
		mouseCodes = new IntKeyOpenHashMap<ActionType>();
		
//		oldKeyStats = new IntKeyBooleanOpenHashMap();
		
		listeners = new EnumMap<ActionType, Set<InputListener>>(ActionType.class);

		//TODO : make a savable bindingmap and load it at startup
		keyCodes.put(KeyInput.KEY_W, ActionType.MOVE_FORWARD);
		keyCodes.put(KeyInput.KEY_S, ActionType.MOVE_BACKWARD);
		keyCodes.put(KeyInput.KEY_LSHIFT, ActionType.FIRE);
	}
	
	public static InputManager get() {
		if ( instance == null ) instance = new InputManager();
		
		return instance;
	}
	
	public void update( float time ) {
		for ( ActionType action : ActionType.values()) {
			Set<InputListener> s = listeners.get(action);
			if ( s == null ) continue;
			for ( InputListener l : s ) {
				if ( action.isPressed() ) l.fire(action);
			}
		}
	}
	
	public void addListener( InputListener listener, ActionType action ) {
		Set<InputListener> s = listeners.get(action);
		if ( s == null ) {
			s = new HashSet<InputListener>();
			s.add(listener);
			listeners.put(action, s);
		} else s.add(listener);
	}
	
	public void removeListener( InputListener listener, ActionType action ) {
		Set<InputListener> s = listeners.get(action);
		if ( s != null ) s.remove(listener);
	}
	
	public void removeListener( InputListener listener ) {
		for ( ActionType action : ActionType.values() ) {
			Set<InputListener> s = listeners.get(action);
			if ( s != null ) s.remove(listener);
		}
	}
		
//	@Override
	public void onKey(char character, int keyCode, boolean pressed) {
		// TODO Auto-generated method stub
		ActionType action = keyCodes.get(keyCode);
		if ( action == null ) return;
		action.setPressed( pressed );
		
//		for ( InputListener l : listeners ) {
//			l.fire(action);
//		}
		// update the old states;
//		oldKeyStats.put(keyCode, pressed);
		oldKeyStats[keyCode] = pressed;
		
//		System.out.println("KEYBOARD\t" + KeyInput.get().getKeyName(keyCode) + "\t" + keyCode + "\t" + pressed + "\t");
	}

//	@Override
	public void onButton(int button, boolean pressed, int x, int y) {
		// TODO Auto-generated method stub
//		System.out.println("MOUSEBUTTON\t" + MouseInput.get().getButtonName(button) + "\t" + button + "\t" + pressed  + "\t" + x + "\t" + y + "\tx");
		ActionType action = mouseCodes.get(button);
		action.setPressed(pressed);
		
//		for ( InputListener l : listeners ) {
//			l.fire(action);
//		}
	}

//	@Override
	public void onMove(int delta, int delta2, int newX, int newY) {
		// TODO Auto-generated method stub
		//System.out.println("MOUSEMOVE\t" + delta + "\t" + delta2  + "\t" + newX + "\t" + newY + "\tx");
//		MouseEvent e = new MouseEvent();
//		ActionType action = mouseCodes.get(newX);
//		for ( InputListener il : listeners ) {
//			il.fire(action);
//		}
	}

//	@Override
	public void onWheel(int wheelDelta, int x, int y) {
		// TODO Auto-generated method stub
		//System.out.println("MOUSEWHEEL\t" + wheelDelta + "\tx" + x + "\t" + y + "\t");
	}

//	public static void main(String[] args) {
//		StandardGame app = new StandardGame("");
//		
//		app.start();
//		GameTaskQueueManager.getManager().update(new Callable<Void>(){
//			public Void call() throws Exception {
//				InputManager in = new InputManager();
//				
//				boolean b = ActionType.FIRE.isPressed();
//				System.out.println(b);
//				ActionType.FIRE.setPressed(true);
//				
//				in.addListener(new InputListener() {
//					@Override
//					public void fire(ActionType action) {
//						// TODO Auto-generated method stub
//						if ( action.isPressed() ) {
//							System.out.println(action.toString() + " PRESSED");
//						} else {
//							System.out.println(action.toString() + " RELEASED");
//						}
//					}
//				}, ActionType.FIRE);
//				//in.change();
//				return null;
//			}
//		});
//		
//	}
}

abstract class InputInterpreter implements KeyInputListener, MouseInputListener {}