package prealpha.state;
import com.jme.bounding.*;
import com.jme.light.DirectionalLight;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jmex.game.state.*;

public class PhysThread extends BasicGameState implements Runnable {
	Thread thread;
	
	public PhysThread( String name ) {
		super(name);
		
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {		
		// TODO Auto-generated method stub
		Box box = new Box("", Vector3f.ZERO, 1,1,1);
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		rootNode.attachChild(box);
		/*
		MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		state.setAmbient(ColorRGBA.red);
		state.setDiffuse(ColorRGBA.red);
		box.setRenderState(state);
		box.updateRenderState();
		*/
		rootNode.updateRenderState();
		
		System.out.println("STARTED");
	}
}