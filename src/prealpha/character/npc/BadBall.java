package prealpha.character.npc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import prealpha.character.Character;
import prealpha.character.Faction;
import prealpha.core.ActionPermitter;
import prealpha.core.Damageable;
import prealpha.core.Updater;
import prealpha.util.Util;
import prealpha.util.Util.Prop;

import com.jme.input.InputHandler;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.contact.ContactInfo;

public class BadBall extends NonPlayerCharacter {
	
	Vector3f oldpos;
	
	public BadBall( final Updater updater, final PhysicsSpace space, final Node scene ) {
		super(updater, space, scene, Faction.None );
		
		File f = new File("data/model/BadBall.jme");
		
		try {
			Spatial model = (Spatial) BinaryImporter.getInstance().load(f);
			getPhysicsNode().attachChild(model);
			getPhysicsNode().generatePhysicsGeometry();
			getPhysicsNode().computeMass();
			
			MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
			state.setAmbient(ColorRGBA.red);
			state.setDiffuse(ColorRGBA.red);
			state.setSpecular(ColorRGBA.red);
			
			this.setRenderState(state);
			
			this.oldpos = new Vector3f();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		movementSpeed = 7.5f*getPhysicsNode().getMass();
		direction.multLocal(movementSpeed);
	}

	private final Vector3f direction = new Vector3f(1,0,0);
	private float movementSpeed;
	private float downTime = 0;
	
	@Override
	public void update( float time ) {		
		if ( isAlive() ) {
			super.update(time);
			
			float posDelta = getPhysicsNode().getLocalTranslation().distanceSquared(oldpos);
			oldpos.set(getPhysicsNode().getLocalTranslation());
			
			if ( posDelta < 0.0001f ) {
				direction.multLocal(-1);
				getPhysicsNode().addForce(direction.multLocal(50));
				direction.divideLocal(50f);
			}
		
			getPhysicsNode().addForce(direction);
		
			this.getPhysicsNode().getLocalTranslation().set(2, 0);
		} else {
			downTime += time;
			
			getPhysicsNode().addTorque(Vector3f.UNIT_Y.mult(1000));
			
			if ( downTime > 7.5f ) delete();
		}
	}

	@Override
	protected void onCollision( ContactInfo c ) {		
		if ( c.getNode1() != getPhysicsNode() && ( c.getNode1().getParent() instanceof Character ) ) {			
			Character c1 = ((Character) c.getNode1().getParent());
			if (getFaction().isEnemy(c1.getFaction()) ) c1.damage(.01f);
		} else 	if ( c.getNode2() != getPhysicsNode() && ( c.getNode2().getParent() instanceof Character ) ) {			
			Character c1 = ((Character) c.getNode2().getParent());
			if (getFaction().isEnemy(c1.getFaction()) ) c1.damage(.01f);
		}
	}
	
}
