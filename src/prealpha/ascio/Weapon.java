package prealpha.ascio;

import com.jme.scene.Node;

public abstract class Weapon {
	protected Node node;
	
	public Weapon() {
		node = new Node("WeaponNode");
		node.getLocalTranslation().set(0, 0, 2.25f);
	}
	
	public void update(float time) {
		
	}
	
	public Node getNode() {
		return node;
	}
}
