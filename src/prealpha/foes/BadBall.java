package prealpha.foes;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.material.Material;

import prealpha.ascio.Ascio;
import prealpha.interfaces.Destructible;
import prealpha.util.Util;

public class BadBall extends Foe  {
	private static final long serialVersionUID = -1842868006119490533L;
	
	public BadBall(DynamicPhysicsNode node) {
		super(node);
		
		Sphere ball = new Sphere("BadBall", 10, 10, 1.5f);
		node.attachChild(ball);
		node.getLocalTranslation().set(1, 5);
		
		MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		state.setAmbient(ColorRGBA.red);
		state.setDiffuse(ColorRGBA.red);
		node.setRenderState(state);
		
		node.setModelBound(new BoundingBox());
		node.updateModelBound();
		node.generatePhysicsGeometry();
		node.setMaterial(Material.ICE);
		node.setMass(5);
		
		node.updateRenderState();
	}
	
	@Override
	public void update(float time) {
		// TODO Auto-generated method stub
		if ( target != null && update) {
			goTo(target.getNode().getWorldTranslation());
		}
	}
	
	@Override
	public void damage(int damage) {
		// TODO Auto-generated method stub
		health -= damage;
		if (health == 0) this.destroy();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		node.removeFromParent();
		node.delete();
	}

	@Override
	public boolean attack() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean goTo(Vector3f destination) {
		// TODO find a cleverer way to do this
				
		if ( !Util.nearEqual(destination, node.getLocalTranslation())) {
			buff = destination.subtract(node.getWorldTranslation());
			//buff.set(1, 0);
			buff.mult(buff.lengthSquared());
			node.addForce(buff);
		}
		return true;
	}
}