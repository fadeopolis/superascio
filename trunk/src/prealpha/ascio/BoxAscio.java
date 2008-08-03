package prealpha.ascio;

import java.io.IOException;

import prealpha.input.PAHandler;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.material.Material;

public class BoxAscio extends Ascio  {
	protected ColorRGBA healthColor;
	protected Box leftPart;
	protected Box centerPart;
	protected Box rightPart;
	
	public BoxAscio(DynamicPhysicsNode target) {
		super(target);
		
		/* create visuals for ascio */
		//TODO: use a proper model for ascio, not just boxes
		//File file = new File("data/model/ascio.jme");
		//if (file.exists() && file.canRead()) System.out.println("HUZZAH!"); else System.exit(1);
		/*
		try {
			BinaryImporter importer = new BinaryImporter();
			model = (Node)importer.load(file.toURI().toURL().openStream());
			//model.setLocalScale(10f);
			node.attachChild(model);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		*/
		leftPart = new Box("left",new Vector3f(.45f, 0, 0), 0.25f,1.5f,1f);
		centerPart = new Box("center",new Vector3f(), 0.2f,1.35f,.7f);
		rightPart = new Box("right",new Vector3f(-.45f, 0, 0), 0.25f,1.5f,1f);
		
		MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		state.setAmbient(ColorRGBA.lightGray);
		state.setDiffuse(ColorRGBA.white);
		state.setShininess(5f);
		node.setRenderState(state);
		
		MaterialState stateCenter = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		centerPart.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		stateCenter.setAmbient(healthColor= new ColorRGBA(0,1,0,.1f));
		stateCenter.setDiffuse(healthColor);		
		centerPart.setRenderState(stateCenter);
		
		node.attachChild(leftPart);
		node.attachChild(centerPart);
		node.attachChild(rightPart);
		node.setModelBound(new BoundingBox());
		node.updateModelBound();
		
		/* setup the physics for ascio */
		//node.setAffectedByGravity(false);
		node.generatePhysicsGeometry();	/*
		PhysicsBox box = node.createBox("Ascio Physics");
		box.getLocalTranslation().set(1, -.2f);
		box.setLocalScale(new Vector3f(1.3f, 3.2f, 2));*/
		Material m = new Material();
		m.setDensity(5);
		node.setMaterial(Material.PLASTIC);
		node.computeMass();
		node.setCenterOfMass(new Vector3f(0,-1.5f,0));

		node.updateRenderState();
		
		/* setup the rest */
		weapon = new Sword();
		node.attachChild(weapon.getNode());
	}	

	public void damage(int damage) {
		super.damage(damage);
		
		if (health < 66) {
			healthColor = ColorRGBA.yellow;
			node.updateRenderState();
		} else if (health < 33) {
			healthColor = ColorRGBA.red;	
			node.updateRenderState();
		}
			
	}

	@Override
	public void attack() {
		// TODO Auto-generated method stub
		weapon.fire();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(float time) {
		// TODO Auto-generated method stub
		
	}
}