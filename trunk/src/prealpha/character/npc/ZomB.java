package prealpha.character.npc;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import prealpha.character.Faction;
import prealpha.core.Damageable;
import prealpha.core.Updater;
import prealpha.util.Util;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.contact.ContactInfo;

public class ZomB extends NonPlayerCharacter {

	private static final long serialVersionUID = -8040900793350618549L;

	public ZomB( final Updater updater, final PhysicsSpace space, final Node scene ) {
		super(updater, space, scene, Faction.ZomBies );
		
			File f = new File("data/model/ZomB.jme");
			
			try {
				Spatial model = (Spatial) BinaryImporter.getInstance().load(f);
				getPhysicsNode().attachChild(model);
				getPhysicsNode().generatePhysicsGeometry();
				getPhysicsNode().computeMass();
				this.getPhysicsNode().getCenterOfMass(calcV);
				calcV.addLocal(.2f, -1.5f, 0);
				this.getPhysicsNode().setCenterOfMass(calcV);
				
				MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				state.setAmbient(ColorRGBA.red);
				state.setDiffuse(ColorRGBA.red);
				state.setSpecular(ColorRGBA.red);
				
				this.setRenderState(state);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}
	
	@Override
	public void onCollision( ContactInfo c ) {
		super.onCollision(c);
		
		if ( c.getNode1() != getPhysicsNode() && c.getNode1().getParent() instanceof Damageable  ) {
			((Damageable) c.getNode1().getParent()).damage( .1f );
		} else if ( c.getNode2() != getPhysicsNode() && c.getNode2().getParent() instanceof Damageable  ) {
			((Damageable) c.getNode2().getParent()).damage( .1f );			
		}
	}

	@Override
	public void update( float time ) {
//		Util.shout(getHealth());
		super.update(time);
		
		getPhysicsNode().addForce(Vector3f.UNIT_X.mult(-5*getPhysicsNode().getMass()));
		
		getPhysicsNode().getLocalTranslation().set(2, 0);
	}
	
	@Override
	public void kill() {
		super.kill();
		delete();
	}

}
