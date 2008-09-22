package prealpha.util;

import java.io.File;
import java.io.IOException;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.xml.XMLExporter;

public class CrappyModelCreator {

	public static void main(String[] args) throws IOException {
		for ( int x = 1; x < 30; x++) {
			for ( int y = -1; y < 2; y++) {
				for ( int z = -1; z < 2; z++) {
					Box b1 = new Box("", new Vector3f(0, 0, 0),50.f, 1.f, 2.5f);
					Box b2 = new Box("", new Vector3f(-40,  7 + ((x-1)*35), 0),10.f, 1.f, 2.5f);
					Box b3 = new Box("", new Vector3f(-20, 14 + ((x-1)*35), 0),10.f, 1.f, 2.5f);
					Box b4 = new Box("", new Vector3f(0,   21 + ((x-1)*35), 0),10.f, 1.f, 2.5f);
					Box b5 = new Box("", new Vector3f(20,  28 + ((x-1)*35), 0),10.f, 1.f, 2.5f);
					Box b6 = new Box("", new Vector3f(40,  35 + ((x-1)*35), 0),10.f, 1.f, 2.5f);
					
					Node node = new Node();
					node.attachChild(b1);
					node.attachChild(b2);
					node.attachChild(b3);
					node.attachChild(b4);
					node.attachChild(b5);
					node.attachChild(b6);
					
					node.setModelBound(new BoundingBox());
					node.updateModelBound();
					
					File file = new File("/home/fader/workspace/ascio/data/level/1/"+x+" "+y+" "+z+".jme");
					XMLExporter.getInstance().save(node, file);
				}
			}
		}		
	}
}
