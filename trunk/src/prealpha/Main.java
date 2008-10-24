package prealpha;

import java.io.IOException;
import java.util.concurrent.Callable;

import prealpha.app.InGameState;
import prealpha.app.MenuState;
import prealpha.app.PreAlpha;

import com.jme.util.GameTaskQueueManager;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;

public class Main {
	public static void main(String[] args) throws InterruptedException, IOException {
		PreAlpha app = new PreAlpha();
		app.start();
		System.setProperty("jme.stats", "set");
	
//		BoxAscio ascio = new BoxAscio();
//		BinaryExporter.getInstance().save(ascio, new File("/home/fader/workspace/ascio/data/model/BoxAscio.jme"));
//		XMLExporter.getInstance().save(ascio, new File("/home/fader/workspace/ascio/data/model/BoxAscio.xml"));
		
//		if ( GameSettingsPanel.prompt(app.getSettings())) {
		if (true) {
			GameTaskQueueManager.getManager().update(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					// TODO Auto-generated method stub
					GameState ingame = new InGameState(true);
					GameStateManager.getInstance().attachChild(ingame);
					
					GameState menu = new MenuState();
					GameStateManager.getInstance().attachChild(menu);
					
					BasicGameState debug = new DebugGameState(false);
					GameStateManager.getInstance().attachChild(debug);
					
//					debug.getRootNode().attachChild( new Box("box", Vector3f.ZERO, 1,1,1) );
					ingame.setActive(true);
					debug.setActive(true);
					
					return null;
				}		
			});
		}
	}	
}