package prealpha.character.npc;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import prealpha.app.InGameState;
import prealpha.app.MenuState;
import prealpha.util.Util;

import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Capsule;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.xml.XMLExporter;
import com.jmex.game.StandardGame;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.util.states.PhysicsGameState;

public class BadBallModel extends Node {

	private static final long serialVersionUID = 3778846589336092405L;

	public BadBallModel() {
		this.setName("BadBall Model");
		
		float radius = 1.25f;
		float spikeradius = radius/5;
		float spikes = 2f * radius + .02f;

		attachChild( new Sphere("A Bad Ball", 10, 10, radius));
		Capsule[] c = new Capsule[14];
		
		c[0] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius , spikes );
		c[0].getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(0,0,1));
		c[1] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[1].getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(0,1,0));
		c[2] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[2].getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(0,1,1));
		c[3] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[3].getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(1,0,0));
		c[4] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[4].getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(1,0,1));
		c[5] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[5].getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(1,1,0));
		c[6] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[6].getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(1,1,1));

		c[7] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[7].getLocalRotation().fromAngleAxis(180*FastMath.DEG_TO_RAD, new Vector3f(0,0,1));
		c[8] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[8].getLocalRotation().fromAngleAxis(180*FastMath.DEG_TO_RAD, new Vector3f(0,1,0));
		c[9] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[9].getLocalRotation().fromAngleAxis(180*FastMath.DEG_TO_RAD, new Vector3f(0,1,1));
		c[10] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[10].getLocalRotation().fromAngleAxis(180*FastMath.DEG_TO_RAD, new Vector3f(1,0,0));
		c[11] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[11].getLocalRotation().fromAngleAxis(180*FastMath.DEG_TO_RAD, new Vector3f(1,0,1));
		c[12] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[12].getLocalRotation().fromAngleAxis(180*FastMath.DEG_TO_RAD, new Vector3f(1,1,0));
		c[13] = new Capsule("BadBall Spike", 10, 10, 10, spikeradius, spikes );
		c[13].getLocalRotation().fromAngleAxis(180*FastMath.DEG_TO_RAD, new Vector3f(1,1,1));

//		c[14] = new Capsule("BadBall Spike", 10, 10, 10, .3f, 3f );
//		c[14].getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, new Vector3f(1,1,1));

		for ( Capsule cap : c ) attachChild(cap);
		
		MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		state.setAmbient(ColorRGBA.red);
		state.setDiffuse(ColorRGBA.red);
		state.setSpecular(ColorRGBA.red);

		MaterialState spike = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		spike.setAmbient(ColorRGBA.black);
		spike.setDiffuse(ColorRGBA.brown);
		spike.setSpecular(ColorRGBA.gray);
		
		for ( Capsule cap : c ) cap.setRenderState(spike);
		
		this.setRenderState(state);
		
		this.setModelBound(new BoundingBox());
		this.updateModelBound();
		
//		this.getLocalRotation().fromAngleNormalAxis(FastMath.PI, Vector3f.UNIT_Y);
		
//		this.getLocalScale().multLocal( new Vector3f(.15f, .175f, .3f));
	}
	
	public static void main(String... args) {
		StandardGame app = new StandardGame("");
		app.start();
		
		GameTaskQueueManager.getManager().update(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				BasicGameState debug = new DebugGameState(true);
				GameStateManager.getInstance().attachChild(debug);
				debug.setActive(true);
				
				PhysicsGameState phys = new PhysicsGameState("phys");
				GameStateManager.getInstance().attachChild(phys);
				phys.setActive(true);				
				
				DynamicPhysicsNode node = phys.getPhysicsSpace().createDynamicNode();
				node.setAffectedByGravity(false);
				node.addTorque(Vector3f.UNIT_XYZ.mult(35));
				
				Node model = new BadBallModel();

				node.attachChild(model);

				debug.getRootNode().attachChild(node);
				debug.getRootNode().updateRenderState();
				
				try {
					BinaryExporter.getInstance().save(model, new File("/home/fader/workspace/ascio/data/model/BadBall.jme"));
					XMLExporter.getInstance().save(model, new File("/home/fader/workspace/ascio/data/model/BadBall.xml"));
				} catch (IOException e) {
					Util.shoutln("nothing is well");
					e.printStackTrace();
				}
				
				return null;
			}		
		});
	}
}
