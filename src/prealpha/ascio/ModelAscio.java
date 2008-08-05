package prealpha.ascio;

import java.io.File;
import java.io.IOException;

import prealpha.input.PAHandler;
import prealpha.util.Util;

import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.material.Material;

public class ModelAscio extends Ascio  {
	private static final long serialVersionUID = 1L;

	protected ColorRGBA healthColor;
	protected Box leftPart;
	protected Box centerPart;
	protected Box rightPart;
	
	public ModelAscio( PhysicsSpace space ) {
		this(null, space);
	}
	
	public ModelAscio( String name, PhysicsSpace space ) {
		this( name, null, space );
	}
	
	public ModelAscio( String name, Vector3f location, PhysicsSpace space ) {
		super(name , location, space);
			
		/* create visuals for ascio */
		File file = new File("data/model/ascio.jme");
		Node model = new Node();
				
		try {
			BinaryImporter importer = new BinaryImporter();
			Node foo = (Node)importer.load(file.toURI().toURL().openStream());
			foo.setLocalScale(new Vector3f(12.5f, 15f, 7.5f));
			foo.getLocalRotation().fromAngleNormalAxis(90*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
			foo.setLocalTranslation(0.2f, -1.5f, 1.25f);
			model.attachChild(foo);
			physicsNode.attachChild(model);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		state.setAmbient(ColorRGBA.red);
		
		try {
			Node foo = (Node) model.getChild(0);
			Util.shout(foo.getChildren().size());			
			foo = (Node) foo.getChild(0);
			Util.shout(foo.getChildren().size());
			Spatial bar = foo.getChild(0);
			Spatial bar2 = foo.getChild(1);
			Util.shout("ColorIT");
			bar2.setRenderState(state);
		} catch (Exception e) {
			Util.shout("NO MORE");
		}
		/*
		foo.setRenderState(state);
		state.setAmbient(ColorRGBA.blue);
		bar.setRenderState(state);
		*/
		model.setRenderState(state);
		
		physicsNode.setModelBound(new BoundingBox());
		physicsNode.updateModelBound();
		
		/* setup the physics for ascio */
		//node.setAffectedByGravity(false);
//		node.generatePhysicsGeometry();	
		PhysicsBox box = physicsNode.createBox("Ascio Physics");
		box.getLocalTranslation().set(1, -.2f);
		box.setLocalScale(new Vector3f(2f, 3.2f, 2.5f));
		Material m = new Material();
		m.setDensity(15);
		physicsNode.setMaterial(Material.PLASTIC);
		physicsNode.computeMass();
		physicsNode.setCenterOfMass(new Vector3f(0,-1.5f,0));

		physicsNode.updateRenderState();
		
		/* setup the weapon */
		weapon = new Sword(space);
		physicsNode.attachChild(weapon.getNode());
		
		//Joint j = space.createJoint();
		//j.attach(node, weapon.getNode());
	}	

	public void damage(int damage) {
		super.damage(damage);
		
		if (health < 66) {
			healthColor = ColorRGBA.yellow;
			physicsNode.updateRenderState();
		} else if (health < 33) {
			healthColor = ColorRGBA.red;	
			physicsNode.updateRenderState();
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