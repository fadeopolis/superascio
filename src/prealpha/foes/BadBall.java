package prealpha.foes;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.material.Material;

import prealpha.ascio.Ascio;
import prealpha.interfaces.Destructible;
import prealpha.util.Util;

public class BadBall extends Foe  {
	private static final long serialVersionUID = -1842868006119490533L;
	
	static Timer timer = Timer.getTimer(); 
	protected float counter = -60;
	
	public BadBall( PhysicsSpace space ) {
		this(null, space);
	}
	
	public BadBall( String name, PhysicsSpace space ) {
		this( name, null, space );
	}
	
	public BadBall( String name, Vector3f location, PhysicsSpace space ) {
		super(name, location, space);
		
		Sphere ball = new Sphere("BadBall", 10, 10, 1.5f);
		attachChild(ball);
		getLocalTranslation().set(1, 5);
		
		MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		state.setAmbient(ColorRGBA.red);
		state.setDiffuse(ColorRGBA.red);
		setRenderState(state);
		
		setModelBound(new BoundingBox());
		updateModelBound();
		generatePhysicsGeometry();
		setMaterial(Material.ICE);
		setMass(5);
		
		updateRenderState();
	}
	
	/** for now all the game logic of this foe is in here,
	 * ! CHANGE THAT ! */
	@Override
	public void update(float time) {
		fbuff = getWorldTranslation().distance(target.getPhysicsNode().getWorldTranslation());
		
		// if the target is to far away, don't do anything
		if ( fbuff > 100 ) return;
		
		if (updateEnabled) {	
			goTo(target.getPhysicsNode().getWorldTranslation());
			
			if ( fbuff < 20 ) attack();
			
			if ( this.hasCollision(target, false) ) target.damage(5);
		}
	}
	
	@Override
	public void damage(int damage) {
		// TODO Auto-generated method stub
		health -= damage;
		if (health == 0) this.destroy();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		removeFromParent();
		delete();
	}

	@Override
	public boolean attack() {
		// if there is nothing to attack or you already attacked this second, do nothing
		if ( target == null || Math.abs(timer.getTimeInSeconds() - counter) < 3 ) return false;
	
		vbuff = target.getPhysicsNode().getWorldTranslation().subtract(getWorldTranslation()).mult(500);
		addForce(vbuff);
		
		//prevents the BadBall from attacking more than once a second
		counter = timer.getTimeInSeconds();
		
		return true;
	}

	@Override
	public boolean goTo(Vector3f destination) {
		// TODO find a cleverer way to do this
				
		if ( !Util.nearEqual(destination, getLocalTranslation())) {
			vbuff = destination.subtract(getWorldTranslation());
			//buff.set(1, 0);
			vbuff.mult(vbuff.lengthSquared());
			addForce(vbuff);
		}
		return true;
	}
}