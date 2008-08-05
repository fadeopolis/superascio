package prealpha.input;

import jmetest.input.TestHardwareMouse;
import prealpha.ascio.*;
import prealpha.enums.GameType;
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
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.physics.*;

public class PAHandler extends InputHandler {
	/**
	 * The Ascio to be manipulated
	 */
	Ascio target;
	
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
	float turnSpeed = 2f;
	float moveSpeed = 25;
	
	AbsoluteMouse mouse;
	
	Vector3f vbuff = new Vector3f();
	
	public PAHandler( Ascio target, Camera cam) {
		super();
		this.target = target;
		this.cam = cam;
		
		mouse = new AbsoluteMouse("Mouse Input", 800, 600);
		mouse.registerWithInputHandler(this);
		TextureState cursor = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		cursor.setTexture(TextureManager.loadTexture(
				TestHardwareMouse.class.getClassLoader().getResource("jmetest/data/cursor/cursor1.png"),
				Texture.MM_LINEAR, Texture.FM_LINEAR));
		mouse.setRenderState(cursor);
		AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		as1.setBlendEnabled(true);
		as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		as1.setTestEnabled(true);
		as1.setTestFunction(AlphaState.TF_GREATER);
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
				this.addAction(new ForwardAction(target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_W, InputHandler.AXIS_NONE, true);
				this.addAction(new BackwardAction(target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_S, InputHandler.AXIS_NONE, true);
				this.addAction(new StrafeLeftAction(target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_A, InputHandler.AXIS_NONE, true);
				this.addAction(new StrafeRightAction(target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_D, InputHandler.AXIS_NONE, true);
				this.addAction(new TurnLeftAction(target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_Q, InputHandler.AXIS_NONE, true);
				this.addAction(new TurnRightAction(target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_E, InputHandler.AXIS_NONE, true);
				this.addAction(new JumpAction(target), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false);
				this.addAction(new ExitAction(), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_ESCAPE, InputHandler.AXIS_NONE, false);
				
				KeyBindingManager.getKeyBindingManager().add("changeMode", KeyInput.KEY_F10);
				KeyBindingManager.getKeyBindingManager().add("swing", KeyInput.KEY_LSHIFT);
			}

	@Override
	public void update(float time) {
		super.update(time);
		
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
			
			vbuff = target.getPhysicsNode().getLocalRotation().getRotationColumn(2).mult(12).add(0,-4,0);
			location = target.getPhysicsNode().getLocalTranslation().subtract(vbuff);
			left = target.getPhysicsNode().getLocalRotation().getRotationColumn(0);
			//TODO : don't let the camera sink to far even, if ascio lies on his back
			up = target.getPhysicsNode().getLocalRotation().getRotationColumn(1);
			direction = target.getPhysicsNode().getLocalTranslation().subtract(location);
			cam.setLocation(location);
			cam.lookAt( target.getPhysicsNode().getLocalTranslation(), Vector3f.UNIT_Y);
			
			break;
		case sideScroller :
			vbuff = target.getPhysicsNode().getLocalRotation().getRotationColumn(0).mult(12);
			location = target.getPhysicsNode().getLocalTranslation().subtract(vbuff);
			left = target.getPhysicsNode().getLocalRotation().getRotationColumn(0);
			//TODO : don't let the camera sink to far even, if ascio lies on his back
			up = target.getPhysicsNode().getLocalRotation().getRotationColumn(1);
			direction = target.getPhysicsNode().getLocalTranslation().subtract(location);
			cam.setLocation(location);
			cam.lookAt( target.getPhysicsNode().getLocalTranslation(), Vector3f.UNIT_Y);
			break;
		case isometric :
			vbuff = Vector3f.UNIT_Y.mult(-50).add(target.getPhysicsNode().getLocalRotation().getRotationColumn(0).mult(20));
			location = target.getPhysicsNode().getLocalTranslation().subtract(vbuff);
			left = target.getPhysicsNode().getLocalRotation().getRotationColumn(0);
			//TODO : don't let the camera sink to far even, if ascio lies on his back
			up = target.getPhysicsNode().getLocalRotation().getRotationColumn(1);
			direction = target.getPhysicsNode().getLocalTranslation().subtract(location);
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
	}
	
	/** prevents ascio from spinning or moving to fast */
	private void limitForces() {
		// limit movement speed
		target.getPhysicsNode().getLinearVelocity(vbuff);
		vbuff.set(0, vbuff.x > moveSpeed ? moveSpeed : vbuff.x < -moveSpeed ? -moveSpeed : vbuff.x);
		//vbuff.set(1, vbuff.y > moveSpeed ? moveSpeed : vbuff.y < -moveSpeed ? -moveSpeed : vbuff.y);
		vbuff.set(2, vbuff.z > moveSpeed ? moveSpeed : vbuff.z < -moveSpeed ? -moveSpeed : vbuff.z);
		target.getPhysicsNode().setLinearVelocity(vbuff);
		
		// limit turning speed
		target.getPhysicsNode().getAngularVelocity(vbuff);
		vbuff.set(0, vbuff.x > turnSpeed ? turnSpeed : vbuff.x < -turnSpeed ? -turnSpeed : vbuff.x);
		vbuff.set(1, vbuff.y > turnSpeed ? turnSpeed : vbuff.y < -turnSpeed ? -turnSpeed : vbuff.y);
		vbuff.set(2, vbuff.z > turnSpeed ? turnSpeed : vbuff.z < -turnSpeed ? -turnSpeed : vbuff.z);
		target.getPhysicsNode().setAngularVelocity(vbuff);
	}

	public void setGameType( GameType type ) {
		this.type = type;
	}
	
}
