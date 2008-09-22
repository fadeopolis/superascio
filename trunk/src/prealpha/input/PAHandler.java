package prealpha.input;

import jmetest.input.TestHardwareMouse;
import prealpha.ascio.*;
import prealpha.curve.Curve;
import prealpha.input.*;
import prealpha.input.action.*;
import prealpha.util.*;
import prealpha.state.*;

import com.jme.animation.SpatialTransformer;
import com.jme.image.Texture;
import com.jme.input.*;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.physics.*;

public class PAHandler extends InputHandler {
	
	public enum GameType {
		thirdPerson, sideScroller, isometric;
	}
	/**
	 * The Ascio to be manipulated
	 */
	Ascio target;
	
	public Curve curve;
	public float progress;
	
	/**
	 * The CAMERA! 
	 */
	Camera cam;
	ChaseCamera chaser;
	/**
	 * Node used for controlling the cam's movement
	 */
	CameraNode camNode;
	
	//you should be able to easily change the way the player is viewed and controlled
	GameType type = GameType.thirdPerson;
	//Camera offset from the target;
	Vector3f camOffset = new Vector3f();

	Vector3f location;
	Vector3f left;
	Vector3f up;
	Vector3f direction;
	
	Vector3f displace = new Vector3f();
		
	float turnSpeed = 2f;
	float moveSpeed = 25;
	
	int offset = 25;
	
	boolean foobar = true;
	Vector3f oldPos = new Vector3f();
	
	AbsoluteMouse mouse;
	
	Vector3f vbuff = new Vector3f();
	
	public PAHandler( Ascio target, Camera cam) {
		super();
		this.target = target;
		this.cam = cam;
		
		progress = 0;
		
		mouse = new AbsoluteMouse("Mouse Input", 800, 600);
		mouse.registerWithInputHandler(this);
		TextureState cursor = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		cursor.setTexture(TextureManager.loadTexture(
				PAHandler.class.getClassLoader().getResource("jmetest/data/cursor/cursor1.png"),
				Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear));
		mouse.setRenderState(cursor);
		BlendState as1 = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
		as1.setBlendEnabled(true);
		as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		as1.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		as1.setTestEnabled(true);
		as1.setTestFunction( BlendState.TestFunction.GreaterThan);
		mouse.setRenderState(as1);
		
		Node node = (Node) Util.util().getProp(Util.PropType.RootNode);
		node.attachChild(mouse);
		
		camOffset.set(0, 3, 9);
		
		setupKeys();
		setupCamera();
	}
	
	private void setupCamera() {
		/*
				camNode = new CameraNode("Camera", cam);
				target.getNode().attachChild(camNode);
				camNode.getLocalTranslation().set(camOffset);
		*/
		chaser = new ChaseCamera(cam, target);
		chaser.setMaxDistance(50);
		chaser.setMinDistance(10);
			}
	private void setupKeys( ) {
				// TODO Auto-generated method stub
				this.addAction(new ForwardAction(this,target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_W, InputHandler.AXIS_NONE, true);
				this.addAction(new BackwardAction(this,target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_S, InputHandler.AXIS_NONE, true);
		//		this.addAction(new StrafeLeftAction(this,target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_A, InputHandler.AXIS_NONE, true);
		//		this.addAction(new StrafeRightAction(this,target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_D, InputHandler.AXIS_NONE, true);
		//		this.addAction(new TurnLeftAction(this,target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_Q, InputHandler.AXIS_NONE, true);
		//		this.addAction(new TurnRightAction(this,target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_E, InputHandler.AXIS_NONE, true);
				this.addAction(new JumpAction(this,target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false);
				this.addAction(new ExitAction(), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_ESCAPE, InputHandler.AXIS_NONE, false);
				
				KeyBindingManager.getKeyBindingManager().add("changeMode", KeyInput.KEY_F10);
				KeyBindingManager.getKeyBindingManager().add("swing", KeyInput.KEY_LSHIFT);
				
				KeyBindingManager.getKeyBindingManager().add("zoomin", KeyInput.KEY_5);
				KeyBindingManager.getKeyBindingManager().add("zoomout", KeyInput.KEY_6);
			}

	@Override
	public void update(float time) {
		super.update(time);
		
		if ( foobar ) oldPos.set(target.getWorldTranslation());
		/*
		if ( progress > curve.getLength()-5 ) {
    		//progress -= curve.getLength();
  //  		System.out.print(curve.hashCode()+ "\t");
			curve = curve.getSuccessor();
    		progress -= 10.1f;
  //  		else System.out.println("FUCK");
  //  		System.out.println(curve.hashCode()+ "\t" + progress + "\t" + curve.getLength());
    	} else if ( progress < 0 ) {
   // 		System.out.print(curve.hashCode()+ "\t");
    		if ( curve.getPredecessor() != null ) curve = curve.getPredecessor();
   // 		else System.out.println("FUCK");
   // 		System.out.println(curve.hashCode()+ "\t" + progress + "\t" + curve.getLength());
   // 		progress -= curve.getLength();
    	}
    	
    	float temp = curve.checkProgress(target.getLocalTranslation());
		
    	try {
//			System.out.println(curve.hashCode()+ "\t" + curve.getSuccessor() + "\t" + progress + "\t" + temp + "\t" + curve.getLength());
    	//	System.out.printlnsssssss(curve.getSuccessor());
    	} catch ( Exception e) {

		}
		
		progress = FastMath.clamp(progress , 0, temp);
		*/
		updateCamera(time);
		updateKeys(time);
		
		limitForces();	
	}

	/** puts the camera in the right position according to set gametype */
	private void updateCamera(float time) {
		// TODO Auto-generated method stub
		switch (type) {
		case thirdPerson :
			//chaser.update(time);
			//Util.shout(time);
			
			
			vbuff = target.getPhysicsNode().getLocalRotation().getRotationColumn(2).mult(7).add(0,-2,0);
			location = target.getPhysicsNode().getLocalTranslation().subtract(vbuff);
			//left = target.getPhysicsNode().getLocalRotation().getRotationColumn(0);
			//TODO : don't let the camera sink to far even, if ascio lies on his back
			//up = target.getPhysicsNode().getLocalRotation().getRotationColumn(1);
			//direction = target.getPhysicsNode().getLocalTranslation().subtract(location);
			cam.setLocation(location.add(displace));
			cam.lookAt( target.getPhysicsNode().getLocalTranslation(), Vector3f.UNIT_Y);
			
			break;
		case sideScroller :
			vbuff = target.getPhysicsNode().getLocalRotation().getRotationColumn(0).mult(offset);
			location = target.getPhysicsNode().getLocalTranslation().subtract(vbuff);

			//left = target.getPhysicsNode().getLocalRotation().getRotationColumn(0);
			//TODO : don't let the camera sink to far even, if ascio lies on his back
			//up = target.getPhysicsNode().getLocalRotation().getRotationColumn(1);
			//direction = target.getPhysicsNode().getLocalTranslation().subtract(location);
			cam.setLocation(location);
			cam.lookAt( target.getPhysicsNode().getLocalTranslation(), Vector3f.UNIT_Y);
			break;
		case isometric :
			vbuff = new Vector3f().add(20, -50, 0);
			location = target.getPhysicsNode().getLocalTranslation().subtract(vbuff);
			//left = target.getPhysicsNode().getLocalRotation().getRotationColumn(0);
			//TODO : don't let the camera sink to far even, if ascio lies on his back
			//up = target.getPhysicsNode().getLocalRotation().getRotationColumn(1);
			//direction = target.getPhysicsNode().getLocalTranslation().subtract(location);
			cam.setLocation(location);
			cam.lookAt( target.getPhysicsNode().getLocalTranslation(), Vector3f.UNIT_Y);
			break;
		default :
			break;
		}
	
		cam.update();
	}
	private void updateKeys(float time) {
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("changeMode", false)) {
			switch (type) {
			case thirdPerson :
				type = GameType.sideScroller;
				MoveAction.type = GameType.sideScroller;
				//resetKeys();
				break;
			case sideScroller :
				type = GameType.isometric;
				MoveAction.type = GameType.isometric;
				//resetKeys();
				break;
			case isometric :
				type = GameType.thirdPerson;
				MoveAction.type = GameType.thirdPerson;
				//resetKeys();
				break;
			}
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("swing", false)) {
			target.attack();
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("zoomin", true)) {
			if ( offset > 10 ) offset--;
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("zoomout", true)) {
			offset++;
		}
	}
	
	/** prevents ascio from spinning or moving to fast */
	private void limitForces() {
		// limit movement speed
		target.getLinearVelocity(vbuff);
		vbuff.set(0, vbuff.x > moveSpeed ? moveSpeed : vbuff.x < -moveSpeed ? -moveSpeed : vbuff.x);
		//vbuff.set(1, vbuff.y > moveSpeed ? moveSpeed : vbuff.y < -moveSpeed ? -moveSpeed : vbuff.y);
		vbuff.set(2, vbuff.z > moveSpeed ? moveSpeed : vbuff.z < -moveSpeed ? -moveSpeed : vbuff.z);
		target.setLinearVelocity(vbuff);
		
		// limit turning speed
		target.getAngularVelocity(vbuff);
		vbuff.set(0, vbuff.x > turnSpeed ? turnSpeed : vbuff.x < -turnSpeed ? -turnSpeed : vbuff.x);
		vbuff.set(1, vbuff.y > turnSpeed ? turnSpeed : vbuff.y < -turnSpeed ? -turnSpeed : vbuff.y);
		vbuff.set(2, vbuff.z > turnSpeed ? turnSpeed : vbuff.z < -turnSpeed ? -turnSpeed : vbuff.z);
		target.setAngularVelocity(vbuff);
	}

    public Vector3f advance( float amount ) {
    	progress += amount;
    	/*if ( progress > curve.getLength() ) {
    		progress -= curve.getLength();
    		System.out.print(curve.hashCode()+ "\t");
    		if ( curve.getSuccessor() != null ) curve = curve.getSuccessor();	
    		System.out.println(curve.hashCode()+ "\t" + progress);
    	} else if ( progress < 0 ) {
    		System.out.print(curve.hashCode()+ "\t");
    		if ( curve.getPredecessor() != null ) curve = curve.getPredecessor();
    		System.out.println(curve.hashCode()+ "\t" + progress);
    		progress -= curve.getLength();
    	} */
    	return curve.getPointByLength(progress);   	
    }
	
	public Curve getCurve() {
		return curve;
	}

	public void setCurve(Curve curve) {
		this.curve = curve;
	}

	public void setGameType( GameType type ) {
		this.type = type;
	}
	
}
