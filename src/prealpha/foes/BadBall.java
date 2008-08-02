package prealpha.foes;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.material.Material;

import prealpha.ascio.Ascio;
import prealpha.interfaces.Destructible;
import prealpha.interfaces.Foe;
import prealpha.util.Util;

public class BadBall extends Foe  {
	private int health = 100;
	private DynamicPhysicsNode node;	
	private Ascio target;

	public BadBall(DynamicPhysicsNode node) {
		this.node = node;
		
		Sphere ball = new Sphere("BadBall", 10, 10, 2);
		
		MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		state.setAmbient(ColorRGBA.red);
		state.setDiffuse(ColorRGBA.red);
	
		node.attachChild(ball);
		node.setRenderState(state);
		
		node.setModelBound(new BoundingBox());
		node.updateModelBound();
		node.generatePhysicsGeometry();
		node.setMaterial(Material.RUBBER);
		
		node.updateRenderState();
	}
	
	public DynamicPhysicsNode getNode() {
		return node;
	}
	
	@Override
	public void update(float time) {
		// TODO Auto-generated method stub
		if ( target != null) {
			System.out.println("UPDATE");
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
			node.addForce(destination.subtract(node.getWorldTranslation()));
		}
		return true;
	}

	public Ascio getTarget() {
		return target;
	}

	public void setTarget(Ascio target) {
		this.target = target;
	}
	
	public void setNode(DynamicPhysicsNode node) {
		this.node = node;
	}

}