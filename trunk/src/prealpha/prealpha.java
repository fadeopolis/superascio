package prealpha;

import prealpha.ascio.*;
import prealpha.input.*;
import prealpha.util.*;
import prealpha.util.Util.PropType;
import prealpha.state.*;
//java
import java.io.*;
import java.net.*;
import java.util.*;
//jME
import com.jme.app.AbstractGame;
import com.jme.bounding.*;
import com.jme.light.*;
import com.jme.renderer.*;
import com.jme.scene.state.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.system.DisplaySystem;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.math.*;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.*;
import com.jmex.game.state.*;
//physics
import com.jmex.physics.*;
import com.jmex.physics.geometry.*;
import com.jmex.physics.callback.*;

public class prealpha {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		StandardGame app = new StandardGame("app");
		if (true) {
//p		if (GameSettingsPanel.prompt(app.getSettings())) {
			app.start();
		
			//first of all build util
			HashMap<PropType, Object> props = new HashMap<PropType, Object>();
			props.put(PropType.PhysicsSpace, PhysicsSpace.create());
			props.put(PropType.DisplaySystem, DisplaySystem.getDisplaySystem());
			props.put(PropType.Renderer, DisplaySystem.getDisplaySystem().getRenderer());
			props.put(PropType.Camera, DisplaySystem.getDisplaySystem().getRenderer().getCamera());
		
			Util.create(props);
			Util.check();
		
			//	DebugGameState debug = new DebugGameState();
			//	GameStateManager.getInstance().attachChild(debug);
		
			PAState physics = new PAState("physics");
			GameStateManager.getInstance().attachChild(physics);

//			CharacterFactory.create((PhysicsSpace) Util.util().getProp(PropType.PhysicsSpace), physics.getRootNode());	
		
			physics.build();
				
			GameStateManager.getInstance().activateAllChildren();	
		}
	}	
}