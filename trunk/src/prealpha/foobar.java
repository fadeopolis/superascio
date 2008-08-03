/**
 * DEV - Stage
 * TODO : Finish it, cleanup
 */

package prealpha;

import java.util.ArrayList;

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
import com.jmex.physics.contact.ContactCallback;
import com.jmex.physics.contact.PendingContact;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.util.states.*;

public class foobar {
	protected static Vector3f vbuff = new Vector3f();
	
	static PhysicsGameState phys;
	static DebugGameState debug;
	
	public static void main(String[] args) throws InterruptedException {
		StandardGame app = new StandardGame("app");
		
//		if (GameSettingsPanel.prompt(app.getSettings(), "PreAlpha for Super Ascio")) {
		if (true) {
			app.start();
			Util.create(null);
			
			foobar game = new foobar();
			//GameStateManager.getInstance().attachChild(game);
		
			debug = new DebugGameState(true);
			//debug = new DebugGameState(false);
			GameStateManager.getInstance().attachChild(debug);		
			
			phys = new PhysicsGameState("phys");
			GameStateManager.getInstance().attachChild(phys);

			Node node = new Node();
			
			DynamicPhysicsNode base = phys.getPhysicsSpace().createDynamicNode();
			//base.setAffectedByGravity(false);
			Sphere baseVis = new Sphere("base", Vector3f.ZERO, 20, 20, 1);
			base.attachChild(baseVis);
			MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
			state.setAmbient(ColorRGBA.red);
			base.setRenderState(state);
			PhysicsBox basePhys = base.createBox("phys");
			basePhys.setLocalScale(new Vector3f(2f, 2f, 2f));
			base.setMass(2);
			debug.getRootNode().attachChild(base);
			
			DynamicPhysicsNode body = phys.getPhysicsSpace().createDynamicNode();
			body.setAffectedByGravity(false);
			Box bodyVis = new Box("", Vector3f.UNIT_Y.mult(3.f), 1f, 2f, .75f);
			body.attachChild(bodyVis);
			state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
			state.setAmbient(ColorRGBA.blue);
			body.setRenderState(state);
			PhysicsBox bodyPhys = body.createBox("phys");
			bodyPhys.setLocalScale(new Vector3f(1.5f,3f,2));
			body.setMass(1);
			debug.getRootNode().attachChild(body);
			
	        final Joint joint = phys.getPhysicsSpace().createJoint();
	        joint.attach( base );
	        JointAxis axis = joint.createRotationalAxis();
	        axis.setDirection( new Vector3f( 1, 0, 0 ) );

	        Joint joint2 = phys.getPhysicsSpace().createJoint();
	        joint2.attach( base,  body );
	        JointAxis axis2 = joint2.createTranslationalAxis();
	        axis2.setDirection( new Vector3f( 1, 1, 1 ) );
	 //       joint2.setCollisionEnabled( true );	
			
			StaticPhysicsNode floor = phys.getPhysicsSpace().createStaticNode();
			floor.attachChild(new Box("", Vector3f.UNIT_Y.mult(-2.5f), 10, 1, 10 ));
			//floor.attachChild(new Box("", Vector3f.UNIT_Y.mult(2.5f), 10, 1, 10 ));
			floor.generatePhysicsGeometry();
			state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
			state.setAmbient(new ColorRGBA(.5f, .5f, .5f, .5f));
			debug.getRootNode().attachChild(floor);
			
		//	body.addTorque(new Vector3f(0, 1000, 0));
		//	body.addForce(new Vector3f(500,0,000));
			
			debug.getRootNode().updateRenderState();
			
			GameStateManager.getInstance().activateAllChildren();
		}
	}
	
	class MyContactCallback implements ContactCallback {
		@Override
		public boolean adjustContact(PendingContact contact) {
			// TODO Auto-generated method stub
			return true;
		} 
		
	}
	
	class MyUpdateCallback implements PhysicsUpdateCallback {
		@Override
		public void beforeStep(PhysicsSpace space, float time) {
			// TODO Auto-generated method stub
		}
		@Override
		public void afterStep(PhysicsSpace space, float time) {
			// TODO Auto-generated method stub
			
		}
	}
}