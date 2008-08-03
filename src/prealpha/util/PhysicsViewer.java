package prealpha.util;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import prealpha.ascio.Ascio;
import prealpha.ascio.BoxAscio;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.util.SimplePhysicsGame;

public class PhysicsViewer extends SimplePhysicsGame {
	Node model;
	static File file = new File("/home/fader/workspace/ascio/data/ascio.jme");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (file.exists() && file.canRead()) System.out.println("HUZZAH!"); else System.exit(1);
		
		new PhysicsViewer().start();
	}

	@Override
	protected void simpleInitGame() {
		// TODO Auto-generated method stub
		StaticPhysicsNode node = this.getPhysicsSpace().createStaticNode();
		
		Node model = new Node();
		try {
			BinaryImporter importer = new BinaryImporter();
			model = (Node)importer.load(file.toURI().toURL().openStream());
			model.setModelBound(new BoundingBox());
			model.updateModelBound();
			model.setLocalScale(10f);
		} catch (Exception e) {
			Box box = new Box("box",new Vector3f(0,0,0),1,1,1);
			box.setModelBound(new BoundingBox());
			box.updateModelBound();
			model.attachChild(box);
		}		
		
		node.attachChild(model);
		
		
		Box box = new Box("box",new Vector3f(5,0,0),1,1,1);
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		node.attachChild(box);
		
		Ascio ascio = new BoxAscio(this.getPhysicsSpace());
		rootNode.attachChild(ascio.getNode());
		ascio.getNode().setLocalTranslation(-5, 0, 0);
		DynamicPhysicsNode foo = (DynamicPhysicsNode) ascio.getNode();
		foo.setAffectedByGravity(false);
		foo.setIsCollidable(false);
		this.setPhysicsSpeed(.2f);
		
		node.generatePhysicsGeometry();
		rootNode.attachChild(node);
		showPhysics = true;
	}
}
