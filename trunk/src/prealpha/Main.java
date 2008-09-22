package prealpha;

import java.util.concurrent.Callable;

import prealpha.app.InGameState;
import prealpha.app.MenuState;
import prealpha.app.PreAlpha;

import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.util.GameTaskQueueManager;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;

public class Main {
	public static void main(String[] args) {
		PreAlpha app = new PreAlpha();
		app.start();
		
		GameTaskQueueManager.getManager().update(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				// TODO Auto-generated method stub
				GameState ingame = new InGameState(false);
				GameStateManager.getInstance().attachChild(ingame);
				
				GameState menu = new MenuState();
				GameStateManager.getInstance().attachChild(menu);
				
				BasicGameState debug = new DebugGameState(true);
				GameStateManager.getInstance().attachChild(debug);
				
				debug.getRootNode().attachChild( new Box("box", Vector3f.ZERO, 1,1,1) );
				ingame.setActive(true);
				debug.setActive(true);
				
				return null;
			}
			
		});

	}	
}
