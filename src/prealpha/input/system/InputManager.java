package prealpha.input.system;

import java.util.Set;

import prealpha.input.ActionType;
import prealpha.input.InputListener;

import bak.pcj.map.IntKeyMap;
import bak.pcj.map.IntKeyOpenHashMap;

import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.input.joystick.JoystickInput;

public class InputManager implements KeyInputListener, MouseInputListener {

    public static final String INPUT_SYSTEM_LWJGL = "LWJGL";
    public static final String INPUT_SYSTEM_AWT = "AWT";
    public static final String INPUT_SYSTEM_DUMMY = "DUMMY";
    
	Set<ActionListener> listeners;
	Set<MouseMoveListener> mouseMoveListeners;
	
	
	IntKeyMap<ActionType> keyBindings;
	IntKeyMap<ActionType> mouseBindings;
	
	public InputManager() {
		keyBindings = new IntKeyOpenHashMap<ActionType>();
	}
	
    /**
     * Update the core input system - mouse, keyboard and joystick.
     * Thus all events are handled within this method call.<br>
     * To disable joystick support call {@link JoystickInput#setProvider(String)} with {@link #INPUT_SYSTEM_DUMMY} as
     * parameter proir to creating the display.
     * @see KeyInput#update()
     * @see MouseInput#update()
     * @see JoystickInput#update()
     */
    public void update()
    {
        MouseInput.get().update();
        KeyInput.get().update();
        JoystickInput.get().update();
    }

	@Override
	public void onKey(char character, int keyCode, boolean pressed) {
		// TODO Auto-generated method stub
//		keyBindings.get(keyCode).setPressed(pressed);
	}

	@Override
	public void onButton(int button, boolean pressed, int x, int y) {
		// TODO Auto-generated method stub
//		mouseBindings.get(button).setPressed(pressed);
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
