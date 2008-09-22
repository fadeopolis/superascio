package prealpha.ascio;

import java.io.IOException;

import prealpha.ascio.weapon.Jumper;
import prealpha.ascio.weapon.Sword;
import prealpha.input.ActionType;
import prealpha.input.InputManager;
import prealpha.input.PAHandler;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.material.Material;

public class BoxAscio<E extends DynamicPhysicsNode> extends Ascio implements PhysicsSpatial  {
	protected ColorRGBA healthColor;
	protected Box leftPart;
	protected Box centerPart;
	protected Box rightPart;
	
	public BoxAscio( E e ) {
		this(null, e);
	}
	
	public BoxAscio( String name, E e ) {
		this( name, e, null);
	}
		
	public BoxAscio( String name, E e, Vector3f location ) {
		super(name , e );
	
		InputManager.get().addListener(this, ActionType.FIRE);
		InputManager.get().addListener(this, ActionType.MOVE_FORWARD);
		InputManager.get().addListener(this, ActionType.MOVE_BACKWARD);
		
		
		/* create visuals for ascio */
		leftPart = new Box("left",new Vector3f(.45f, 0, 0), 0.25f,1.5f,1f);
		centerPart = new Box("center",new Vector3f(), 0.2f,1.35f,.7f);
		rightPart = new Box("right",new Vector3f(-.45f, 0, 0), 0.25f,1.5f,1f);
		
		MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		state.setAmbient(ColorRGBA.lightGray);
		state.setDiffuse(ColorRGBA.white);
		state.setShininess(5f);
		setRenderState(state);
		
		MaterialState stateCenter = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		centerPart.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		healthColor= new ColorRGBA(0,1,0,.1f);
		stateCenter.setAmbient(healthColor);
		stateCenter.setDiffuse(healthColor);		
		centerPart.setRenderState(stateCenter);
		
		BlendState blendState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        blendState.setEnabled( true );
        blendState.setBlendEnabled( true );
        blendState.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
        blendState.setDestinationFunction( BlendState.DestinationFunction.OneMinusSourceAlpha );
        centerPart.setRenderState( blendState );
        centerPart.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
		
		attachChild(leftPart);
		attachChild(centerPart);
		attachChild(rightPart);
		
		//Box b = new Box("left",new Vector3f(.45f, -.2f, 0), 0.7f,1.5f,1f);
		//attachChild(b);
		
		setModelBound(new BoundingBox(new Vector3f(0, -.2f, 0), 0.7f,1.7f,1f));
		//updateModelBound();
		//updateWorldBound();
		//BoundingVolume bb =  getWorldBound();
		//bb.setCenter(this.localTranslation.add(0,-2.5f,0));
		
		/* setup the physics for ascio */
		//node.setAffectedByGravity(false);
		generatePhysicsGeometry( true );	/*
		PhysicsBox box = node.createBox("Ascio Physics");
		box.getLocalTranslation().set(1, -.2f);
		box.setLocalScale(new Vector3f(1.3f, 3.2f, 2));*/
		Material m = new Material();
		m.setDensity(5);
		m.setDebugColor( new ColorRGBA( 0.8f, 0.7f, 0.0f, 1 ) );
       // m.setDensity( 0.1f );
        m.setSpringPenetrationDepth( .5f );

        //m.setMu( 6 );
        //m.setDampingCoefficient( 20 );
        //m.setSpringConstant( 2 );
        //m.setBounce( 0.1f );
       // m.putContactHandlingDetails( null, info );
		setMaterial(Material.SPONGE);
		computeMass();
		//System.out.println(this.getChildren());
		setCenterOfMass(new Vector3f(0,-1.5f,.2f));

		//detachChild(b);
		
		updateRenderState();
		
		/* setup the weapon */
		weapon = new Jumper(e);
		this.attachChild(weapon);
		
		//setupControlls
		
	}	

	public void damage(int damage) {
		super.damage(damage);
		
		if (health < 66) {
			healthColor = ColorRGBA.yellow;
			updateRenderState();
		} else if (health < 33) {
			healthColor = ColorRGBA.red;	
			updateRenderState();
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

	@Override
	public void fire(ActionType action) {
		// TODO Auto-generated method stub
//		System.out.println( action + "\t" + action.isPressed());
		switch( action ) {
		case FIRE :
//			if ( action.isPressed() ) 
			attack();
			break;
		case MOVE_FORWARD :
//			if ( action.isPressed() ) 
			addForce( new Vector3f(150,0,0));
			break;
		case MOVE_BACKWARD :
//			if ( action.isPressed() ) 
			addForce( new Vector3f(-150,0,0));
			break;
		}
	}
}