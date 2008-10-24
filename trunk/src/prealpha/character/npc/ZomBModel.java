package prealpha.character.npc;

import java.io.File;
import java.io.IOException;

import prealpha.util.Util;

import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.xml.XMLExporter;
import com.jmex.game.StandardGame;

public class ZomBModel extends Node {

	private static final long serialVersionUID = 4222627136593757865L;

	public ZomBModel() {
		this.setName("ZomB Model");
//		this.getLocalTranslation().addLocal(0, 0, -3.5f);
		Box[] boxes = new Box[6];
		boxes[0] = new Box("leftBox 1", new Vector3f(0,0,0), 5,2,2);
		boxes[1] = new Box("leftBox 2", new Vector3f(0,10,0), 5,2,2);
		boxes[2] = new Box("leftBox 3", new Vector3f(5.5f,5,0), 2,5,2);
		boxes[3] = new Box("leftBox 4", new Vector3f(-5.5f,5,0), 2,5,2);
		boxes[4] = new Box("leftBox 5", new Vector3f(-8,0f,0), 2f,2,2);
		boxes[5] = new Box("leftBox 6", new Vector3f(-5.5f,15f,0), 2,5,2);
//		boxes[6] = new Box("leftBox 7", new Vector3f(0,20f,0), 4.5f,2,2);
//		boxes[7] = new Box("leftBox 8", new Vector3f(5,18.5f,0), 2f,2,2);
		
		MaterialState leftMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		leftMaterial.setAmbient(ColorRGBA.brown);
		leftMaterial.setDiffuse(ColorRGBA.lightGray);
		this.setRenderState(leftMaterial);
		
		for ( Box b : boxes ) {
			this.attachChild(b);
		}
		
		this.setModelBound(new BoundingBox());
		this.updateModelBound();
		
//		this.getLocalRotation().fromAngleNormalAxis(FastMath.PI, Vector3f.UNIT_Y);
		
		this.getLocalScale().multLocal( new Vector3f(.15f, .175f, .3f));
	}
	
	public static void main(String... args) {
		StandardGame app = new StandardGame("");
		app.start();
		
		Node model = new ZomBModel();
		
		try {
			BinaryExporter.getInstance().save(model, new File("/home/fader/workspace/ascio/data/model/ZomB.jme"));
			XMLExporter.getInstance().save(model, new File("/home/fader/workspace/ascio/data/model/ZomB.xml"));
		} catch (IOException e) {
			Util.shoutln("nothing is well");
			e.printStackTrace();
		}
		
		Util.shoutln("all is well");
		System.exit(0);
	}
}
