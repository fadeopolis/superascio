package prealpha.ascio;

import prealpha.input.PAHandler;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.material.Material;

public class BoxAscio extends Ascio  {
	protected ColorRGBA healthColor;
	protected Box leftPart;
	protected Box centerPart;
	protected Box rightPart;
	
	public BoxAscio(DynamicPhysicsNode target) {
		super(target);
		
		leftPart = new Box("left",new Vector3f(.45f, 0, 0), 0.25f,1.5f,1f);
		centerPart = new Box("center",new Vector3f(), 0.2f,1.35f,.7f);
		rightPart = new Box("right",new Vector3f(-.45f, 0, 0), 0.25f,1.5f,1f);
		
		MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		MaterialState stateCenter = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		state.setDiffuse(new ColorRGBA(.5f,.5f,.5f,0));
		
		material = new Material("left");
		node.setMaterial(material);
		
		healthColor = new ColorRGBA(0,1,0,.5f);
		stateCenter.setDiffuse(healthColor);
		node.setRenderState(state);
		centerPart.setRenderState(stateCenter);
		
		weapon = new Sword();
		
		if (leftPart == null) System.out.println("FOFOOOOOOOO");
		node.attachChild(leftPart);
		node.attachChild(centerPart);
		node.attachChild(rightPart);
		node.attachChild(weapon.getNode());
		node.setModelBound(new BoundingBox());
		node.updateModelBound();
		//node.setAffectedByGravity(false);
		node.generatePhysicsGeometry();	
		node.computeMass();
		node.setCenterOfMass(new Vector3f(0,-1.5f,0));
		
		node.updateRenderState();
		}	

	public void damage(int damage) {
		super.damage(damage);
		
		if (health < 66) {
			healthColor = ColorRGBA.yellow;
		} else if (health < 33) {
			healthColor = ColorRGBA.red;	
		}
			
	}
}