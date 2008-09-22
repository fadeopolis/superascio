package prealpha.test;

import java.util.EnumSet;
import java.util.concurrent.Callable;

import prealpha.ascio.Ascio;

import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.scene.Controller;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.Timer;
import com.jmex.game.StandardGame;
/*
public class TestInput {
	public static void main(String[] args) throws InterruptedException {
		System.setProperty("jme.stats", "set");
		
		StandardGame app = new StandardGame("app");
		
		if (true) {
//		if (GameSettingsPanel.prompt(app.getSettings())) {
			app.start();

			//first of all build util
			HashMap<PropType, Object> props = new HashMap<PropType, Object>();
			props.put(PropType.PhysicsSpace, PhysicsSpace.create());
			props.put(PropType.DisplaySystem, DisplaySystem.getDisplaySystem());
			props.put(PropType.Renderer, DisplaySystem.getDisplaySystem().getRenderer());
			props.put(PropType.Camera, DisplaySystem.getDisplaySystem().getRenderer().getCamera());
		
			Util.create(props);

		//	final DebugGameState debug;

			GameTaskQueueManager.getManager().update(new Callable<Void>(){
				public Void call() throws Exception {
					//DebugGameState debug = new DebugGameState(true);	// Create our game state
					//GameStateManager.getInstance().attachChild(debug);	// Attach it to the GameStateManager
					//debug.setActive(true);	// Activate it

					//PAState physics = new PAState("physics");
					//GameStateManager.getInstance().attachChild(physics);
					//GameStateManager.getInstance().activateAllChildren();	
					
					//debug.getRootNode().attachChild(physics.getRootNode());
					InputController in = new InputController(null);//physics.ascio);
					//physics.ascio.addController(in);
					
					return null;
				}
		    });
		
//			Util.check();
		}
	}	
}
*/
public class InputController extends Controller implements KeyInputListener, MouseInputListener {

	Ascio mover;
	
	Timer timer = Timer.getTimer();
	float time1 = 0;
	float time2 = 0;
	
	int buttonsPressed = 0;
	
	public InputController( Ascio mover ) {
		this.mover = mover;
		//this.oldStatus = false;
		KeyInput.get().addListener(this);
		MouseInput.get().addListener(this);
	//	InputManager.get().addListener(this, Integer.MAX_VALUE, UpdateType.ALWAYS);
	}
	
	@Override
	public void update(float time) {
	}

	@Override
	public void onKey(char character, int keyCode, boolean pressed) {
		// TODO Auto-generated method stub
		if ( pressed ) {
			buttonsPressed++;
			if ( buttonsPressed == 1 ) {
				time1 = timer.getTimeInSeconds();
			}
			System.out.println("You pressed " + KeyInput.get().getKeyName(keyCode));
		} else {
			buttonsPressed--;
			time2 = timer.getTimeInSeconds();
			System.out.println("You released " + KeyInput.get().getKeyName(keyCode));
		}
	}

	@Override
	public void onButton(int button, boolean pressed, int x, int y) {
		// TODO Auto-generated method stub
	//	System.out.println("");
	}

	@Override
	public void onMove(int delta, int delta2, int newX, int newY) {
		// TODO Auto-generated method stub
		System.out.println(delta + "\t" + delta2 + "\t" + newX + "\t" + newY );
	} 

	@Override
	public void onWheel(int wheelDelta, int x, int y) {
		// TODO Auto-generated method stub
		
	}

}