package prealpha.util;

import prealpha.ascio.Ascio;
import prealpha.ascio.BoxAscio;

import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.system.*;
import com.jmex.physics.*;

public class CharacterFactory {
	private static DisplaySystem display;
	private static Camera cam;
	
	private Node root;
	private PhysicsSpace space;
	
	public CharacterFactory(PhysicsSpace physics, Node root) {
		this.space = physics;
		display = DisplaySystem.getDisplaySystem();
		cam = display.getRenderer().getCamera();
		this.root = root;
	}
	
	public void delete() {
		space = null;
		display = null;
	}
	
	public BoxAscio createPlayer() {
		BoxAscio ascio = new BoxAscio(space);
		root.attachChild(ascio.getPhysicsNode());
		return ascio;
	}
}