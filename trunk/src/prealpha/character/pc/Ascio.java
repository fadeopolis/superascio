package prealpha.character.pc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Map;

import jmetest.input.TestAbsoluteMouse;

import prealpha.character.Character;
import prealpha.core.ActionPermitter;
import prealpha.core.Updater;
import prealpha.input.action.FireAction;
import prealpha.input.action.JumpAction;
import prealpha.input.action.MoveBackwardAction;
import prealpha.input.action.MoveForwardAction;
import prealpha.input.action.MovementAction;
import prealpha.util.Util;
import prealpha.weapon.SimpleGun;

import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.KeyInput;
import com.jme.input.Mouse;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.input.RelativeMouse;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.contact.MutableContactInfo;
import com.jmex.physics.material.Material;

public class Ascio extends PlayerCharacter implements MouseInputListener {

	private final Vector3f buff = new Vector3f();
	
	Mouse mouse;
	
	public Ascio( final Updater updater, final PhysicsSpace space, final Node scene ) {
		super(updater, space, scene);
		
		setupModelAndPhysics();
		setupWeapon();
		setupInput();
	}
	
	private void setupModelAndPhysics() {
		// we define a custom material
		final Material customMaterial = new Material( "supra-stopper" );
		// we make it really light
		customMaterial.setDensity( 0.05f );
		// a material should define contact detail pairs for each other material it could collide with in the scene
		// do that just for the floor material - the DEFAULT material
		MutableContactInfo contactDetails = new MutableContactInfo();
		// our material should not bounce on DEFAULT
		contactDetails.setBounce( 0 );
		// and should never slide on DEFAULT
		contactDetails.setMu( 1 ); // todo: Float.POSITIVE_INFINITY seems to cause issues on Linux (only o_O)
		// now set that behaviour
		customMaterial.putContactHandlingDetails( Material.DEFAULT, contactDetails );
	        
		this.getPhysicsNode().setMaterial(customMaterial);
		
		Spatial model = null;
		try {
			model = (Spatial) BinaryImporter.getInstance().load(new File("data/model/BoxAscio.jme"));
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		this.getPhysicsNode().attachChild(model);
		this.getPhysicsNode().generatePhysicsGeometry();
		this.getPhysicsNode().computeMass();
		this.getPhysicsNode().getCenterOfMass(calcV);
		calcV.addLocal(.25f, -1.25f, 0);
		this.getPhysicsNode().setCenterOfMass(calcV);
	}
	private void setupWeapon() {
		this.weapon = new SimpleGun( getUpdater(), this );
	}
	private void setupInput() {
		MouseInput.get().addListener(this);
		
		// basic controls
		getInputHandler().addAction(new MoveForwardAction(this), "moveforward", true);
		getInputHandler().addAction(new MoveBackwardAction(this), "movebackward", true);
		getInputHandler().addAction(new JumpAction(this), "jump", false);
		getInputHandler().addAction(new FireAction(this.weapon), "fire", true);

		// play THE song
		getInputHandler().addAction(new InputAction() {
			
			private AudioTrack theSong = null;

			@Override
			public void performAction(InputActionEvent evt) {	
				// load THE song
				if ( theSong == null ) {
					File f = new File("data/sound/fabb-Ascc2.ogg");
					try {
						theSong = AudioSystem.getSystem().createAudioTrack(f.toURI().toURL(), true);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				
				if ( theSong.isPlaying() ) theSong.pause();
				else theSong.play();
			}
			
			
		}, "music", false);
		
		mouse = new AbsoluteMouse("Mouse Input", 
				DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
        TextureState cursor = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        cursor.setEnabled(true);
        cursor.setTexture(TextureManager.loadTexture(TestAbsoluteMouse.class
                .getClassLoader().getResource("jmetest/data/cursor/cursor1.png"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear));
        mouse.setRenderState(cursor);
        BlendState as1 = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        as1.setBlendEnabled(true);
        as1.setSourceFunction(BlendState.SourceFunction.One);
        as1.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceColor);
        as1.setTestEnabled(true);
        as1.setTestFunction(BlendState.TestFunction.GreaterThan);
        mouse.setRenderState(as1);
        mouse.registerWithInputHandler(getInputHandler());
		attachChild(mouse);
	}
	
	Quaternion q = new Quaternion();
	Quaternion noTurn = new Quaternion(0,0,0,1);
	Quaternion turn = new Quaternion(0,1,0,0);
	Vector3f v = new Vector3f();
	boolean fire;
	float x = 0;
	float y = 0;
	
	@Override
	public void update( float time ) {
		if ( getActionPermitter().hasTurned() ) {
			q.multLocal(turn);
			Util.shoutln(getActionPermitter().hasTurned(),q);
		}
		
		super.update(time);
		
		getPhysicsNode().getLocalRotation().set(q);
		
		float f = ( x > 0 ) ? FastMath.atan(y/x) : FastMath.atan(y/x)*-1;
		
		weapon.getLocalRotation().fromAngleAxis( f, Vector3f.UNIT_Z);
	
		Util.shoutln( x, y, f*FastMath.RAD_TO_DEG, weapon.getLocalTranslation() );
		
		if ( fire ) weapon.attack();
		
//		Util.shoutln(x,y,Util.round(FastMath.RAD_TO_DEG*f,0),Util.round(FastMath.RAD_TO_DEG*FastMath.atan(y/x),0));
	}
 	
	@Override
	public void onButton(int button, boolean pressed, int x, int y) {
		fire = pressed;
	}
	
	@Override
	public void onMove(int delta, int delta2, int newX, int newY) {
		x = newX - DisplaySystem.getDisplaySystem().getWidth()/2;
		y = newY - DisplaySystem.getDisplaySystem().getHeight()/2;	
	
		v.set( Math.abs(x), y*1.5f,0);
		v.normalizeLocal();
		v.multLocal(3);
		v.addLocal(0, .8f, 0);
		weapon.getLocalTranslation().set(v);
		
		this.getActionPermitter().setFacingForward( x > 0 );
	}

	@Override
	public void onWheel(int wheelDelta, int x, int y) {
	}
	
	@Override
	public void kill() {
		
	}
	
	@Override
	public boolean attack() {
		// TODO Auto-generated method stub
		return false;
	}
	
}