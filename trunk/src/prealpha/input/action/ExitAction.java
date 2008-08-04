package prealpha.input.action;

import prealpha.ascio.Ascio;

import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.math.*;
import com.jmex.physics.*;

public class ExitAction extends KeyInputAction {
	
	public ExitAction( ) {
	}
	
	@Override
	public void performAction(InputActionEvent evt) {
		// TODO Auto-generated method stub
		System.exit(0);
	}
}
