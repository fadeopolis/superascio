/**
 * DEV - Stage
 * TODO : Finish it, cleanup
 */

package prealpha;

import prealpha.ascio.Ascio;
import prealpha.ascio.BoxAscio;
import prealpha.input.PAHandler;
import prealpha.util.Util;
import sun.applet.Main;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.DirectionalLight;
import com.jme.light.PointLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.*;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.*;
import com.jmex.game.state.*;
import com.jmex.physics.*;
import com.jmex.physics.util.states.*;

public class foobar {
	protected static Camera camera;
	protected static PAHandler input;
	protected PhysicsSpace space;
	protected static Vector3f vbuff = new Vector3f();
	
	public static void main(String[] args) throws InterruptedException {
		StandardGame app = new StandardGame("app");
			
//		if (GameSettingsPanel.prompt(app.getSettings(), "PreAlpha for Super Ascio")) {
		if (true) {
			app.start();
			Util.create(null);
		
			//foobar game = new foobar("foobar");
			//GameStateManager.getInstance().attachChild(game);
		
			DebugGameState debug = new DebugGameState(true);
			//DebugGameState debug = new DebugGameState(false);
			GameStateManager.getInstance().attachChild(debug);		
			
			PhysicsGameState phys = new PhysicsGameState("phys");
			GameStateManager.getInstance().attachChild(phys);

			debug.getRootNode().attachChild(phys.getRootNode());
			debug.getRootNode().updateRenderState();
			
			Node floorN = new Node();
			debug.getRootNode().attachChild(floorN);
			{
				StaticPhysicsNode floor = phys.getPhysicsSpace().createStaticNode();
				debug.getRootNode().attachChild(floor);
				//TerrainBuilder t = new TerrainBuilder(phys.getPhysicsSpace(), floorN);
				//t.start();				
				
				DynamicPhysicsNode player = phys.getPhysicsSpace().createDynamicNode();
				debug.getRootNode().attachChild(player);
				
				
				GameStateManager.getInstance().activateAllChildren();
				
			//	while (t.isAlive()) Thread.sleep(10);
				
				//player.setAffectedByGravity(true);
				Ascio ascio = new BoxAscio(player);
				player.getLocalTranslation().set(0, 50, 0);
			//	player.setAffectedByGravity(false);
				player.clearDynamics();
				
//				input = new PAHandler(ascio, camera);
				
				debug.getRootNode().updateRenderState();
			}
		}
	}
	private class Callback implements PhysicsUpdateCallback {
		@Override
		public void beforeStep(PhysicsSpace space, float time) {
			// TODO Auto-generated method stub
			input.update(time);
		}
		@Override
		public void afterStep(PhysicsSpace space, float time) {
			// TODO Auto-generated method stub
			
		}
	}
}