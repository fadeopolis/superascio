package prealpha.character.pc;

import java.io.File;
import java.io.IOException;

import prealpha.util.Util;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.xml.XMLExporter;
import com.jmex.game.StandardGame;
import com.jmex.physics.PhysicsSpace;

public class BoxAscioModel extends Node {

	private static final long serialVersionUID = 539657214181652586L;

	public BoxAscioModel() {
		this.setName("BoxAscio Model");
		// left part
		Node left = new Node("BoxAscio Model, left part");
		left.getLocalTranslation().addLocal(0, 0, -3.5f);
		Box[] leftBoxes = new Box[8];
		leftBoxes[0] = new Box("leftBox 1", new Vector3f(0,0,0), 5,2,2);
		leftBoxes[1] = new Box("leftBox 2", new Vector3f(0,10,0), 5,2,2);
		leftBoxes[2] = new Box("leftBox 3", new Vector3f(5.5f,5,0), 2,5,2);
		leftBoxes[3] = new Box("leftBox 4", new Vector3f(-5.5f,5,0), 2,5,2);
		leftBoxes[4] = new Box("leftBox 5", new Vector3f(-8,0f,0), 2f,2,2);
		leftBoxes[5] = new Box("leftBox 6", new Vector3f(-4.5f,15f,0), 2,5,2);
		leftBoxes[6] = new Box("leftBox 7", new Vector3f(0,20f,0), 4.5f,2,2);
		leftBoxes[7] = new Box("leftBox 8", new Vector3f(5,18.5f,0), 2f,2,2);
		
		MaterialState leftMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		leftMaterial.setAmbient(ColorRGBA.gray);
		leftMaterial.setDiffuse(ColorRGBA.lightGray);
		left.setRenderState(leftMaterial);
		
		for ( Box b : leftBoxes ) {
			left.attachChild(b);
		}
		
		// right part
		Node right = new Node("BoxAscio Model, right part");
		right.getLocalTranslation().addLocal(0, 0, 3.5f);
		Box[] rightBoxes = new Box[8];
		rightBoxes[0] = new Box("rightBox 1", new Vector3f(0,0,0), 5,2,2);
		rightBoxes[1] = new Box("rightBox 2", new Vector3f(0,10,0), 5,2,2);
		rightBoxes[2] = new Box("rightBox 3", new Vector3f(5.5f,5,0), 2,5,2);
		rightBoxes[3] = new Box("rightBox 4", new Vector3f(-5.5f,5,0), 2,5,2);
		rightBoxes[4] = new Box("rightBox 5", new Vector3f(-8,0f,0), 2f,2,2);
		rightBoxes[5] = new Box("rightBox 6", new Vector3f(-4.5f,15f,0), 2,5,2);
		rightBoxes[6] = new Box("rightBox 7", new Vector3f(0,20f,0), 4.5f,2,2);
		rightBoxes[7] = new Box("rightBox 8", new Vector3f(5,18.5f,0), 2f,2,2);

		MaterialState rightMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		rightMaterial.setAmbient(ColorRGBA.gray);
		rightMaterial.setDiffuse(ColorRGBA.lightGray);
		right.setRenderState(rightMaterial);
		
		for ( Box b : rightBoxes ) {
			right.attachChild(b);
		}
		// center part
		Node center = new Node("BoxAscio Model, center part");
		center.getLocalScale().multLocal(.9f);
		Box[] centerBoxes = new Box[8];
		centerBoxes[0] = new Box("centerBox 1", new Vector3f(0,0,0), 5,2,2);
		centerBoxes[1] = new Box("centerBox 2", new Vector3f(0,10,0), 5,2,2);
		centerBoxes[2] = new Box("centerBox 3", new Vector3f(5.5f,5,0), 2,5,2);
		centerBoxes[3] = new Box("centerBox 4", new Vector3f(-5.5f,5,0), 2,5,2);
		centerBoxes[4] = new Box("centerBox 5", new Vector3f(-8,0f,0), 2f,2,2);
		centerBoxes[5] = new Box("centerBox 6", new Vector3f(-4.5f,15f,0), 2,5,2);
		centerBoxes[6] = new Box("centerBox 7", new Vector3f(0,20f,0), 4.5f,2,2);
		centerBoxes[7] = new Box("centerBox 8", new Vector3f(5,18.5f,0), 2f,2,2);
		
		MaterialState centerMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		centerMaterial.setAmbient(ColorRGBA.green);
		centerMaterial.setAmbient(ColorRGBA.green);
		center.setRenderState(centerMaterial);
		BlendState centerBlend = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
		centerBlend.setBlendEnabled(true);
		centerBlend.setDestinationFunction(BlendState.DestinationFunction.OneMinusConstantAlpha);
		centerBlend.setSourceFunction(BlendState.SourceFunction.DestinationAlpha);
		center.setRenderState(centerBlend);
		left.setRenderState(centerBlend);
		right.setRenderState(centerBlend);
		
		for ( Box b : centerBoxes ) {
			center.attachChild(b);
		}
		
		this.attachChild(left);
		this.attachChild(center);
		this.attachChild(right);
		
		this.setModelBound(new BoundingBox());
		this.updateModelBound();
		
		this.getLocalScale().multLocal( new Vector3f(.1f, .1f, .1f));
		
	}
	
	public static void main(String... args) {
		StandardGame app = new StandardGame("");
		app.start();
		
		BoxAscioModel ascio = new BoxAscioModel();
		
		try {
			BinaryExporter.getInstance().save(ascio, new File("/home/fader/workspace/ascio/data/model/BoxAscio.jme"));
			XMLExporter.getInstance().save(ascio, new File("/home/fader/workspace/ascio/data/model/BoxAscio.xml"));
		} catch (IOException e) {
			Util.shoutln("nothing is well");
			e.printStackTrace();
		}
		
		Util.shoutln("all is well");
		System.exit(0);
	}
}
