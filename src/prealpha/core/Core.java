package prealpha.core;

import com.jme.renderer.Renderer;
import com.jme.system.DisplaySystem;
import com.jmex.physics.PhysicsSpace;

public class Core {
	
	private static Core instance;
	
	private final DisplaySystem display;
	
	private final Renderer renderer;
	
	private final PhysicsSpace physics;
	
	private Core( DisplaySystem display, Renderer renderer, PhysicsSpace physics ) {
		this.display = display;
		this.renderer = renderer;
		this.physics = physics;
	}
	
	public static void create( DisplaySystem display, Renderer renderer, PhysicsSpace physics ) {
		instance = new Core( display, renderer, physics );
	}

	public static Core get() {
		return instance;
	}

	public DisplaySystem getDisplay() {
		return display;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public PhysicsSpace getPhysics() {
		return physics;
	}	
	
}
