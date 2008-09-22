package prealpha.input;

import java.util.Set;

import bak.pcj.map.IntKeyMap;
import bak.pcj.map.IntKeyOpenHashMap;

import com.jme.input.KeyInputListener;
import com.jme.input.MouseInputListener;

public class InputManager implements KeyInputListener, MouseInputListener {

	Set<InputListener> listeners;
	
	IntKeyMap<ActionType> keyBindings;
	IntKeyMap<ActionType> mouseBindings;
	
	public InputManager() {
		keyBindings = new IntKeyOpenHashMap<ActionType>();
	}
	
    public void update() {
    }

	@Override
	public void onKey(char character, int keyCode, boolean pressed) {
		// TODO Auto-generated method stub
		keyBindings.get(keyCode).setPressed(pressed);
	}

	@Override
	public void onButton(int button, boolean pressed, int x, int y) {
		// TODO Auto-generated method stub
		mouseBindings.get(button).setPressed(pressed);
	}

	@Override
	public void onMove(int delta, int delta2, int newX, int newY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWheel(int wheelDelta, int x, int y) {
		// TODO Auto-generated method stub
		
	}
}
