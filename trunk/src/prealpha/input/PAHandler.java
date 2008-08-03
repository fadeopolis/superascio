package prealpha.input;

import prealpha.ascio.*;
import prealpha.enums.GameType;
import prealpha.input.*;
import prealpha.input.action.*;
import prealpha.util.*;
import prealpha.state.*;

import com.jme.animation.SpatialTransformer;
import com.jme.input.*;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.system.DisplaySystem;
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
	
	Vector3f vbuff = new Vector3f();
	
	public PAHandler( Ascio target, Camera cam) {
		super();
		this.target = target;
		this.cam = cam;
		
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
				KeyBindingManager.getKeyBindingManager().add("swing", KeyInput.KEY_LCONTROL);
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
			vbuff = target.getNode().getLocalRotation().getRotationColumn(2).mult(12).add(0,-4,0);
			location = target.getNode().getLocalTranslation().subtract(vbuff);
			left = target.getNode().getLocalRotation().getRotationColumn(0);
			//TODO : don't let the camera sink to far even, if ascio lies on his back
			up = target.getNode().getLocalRotation().getRotationColumn(1);
			direction = target.getNode().getLocalTranslation().subtract(location);
			cam.setLocation(location);
			cam.lookAt( target.getNode().getLocalTranslation(), Vector3f.UNIT_Y);
			break;
		case sideScroller :
			vbuff = target.getNode().getLocalRotation().getRotationColumn(0).mult(12);
			location = target.getNode().getLocalTranslation().subtract(vbuff);
			left = target.getNode().getLocalRotation().getRotationColumn(0);
			//TODO : don't let the camera sink to far even, if ascio lies on his back
			up = target.getNode().getLocalRotation().getRotationColumn(1);
			direction = target.getNode().getLocalTranslation().subtract(location);
			cam.setLocation(location);
			cam.lookAt( target.getNode().getLocalTranslation(), Vector3f.UNIT_Y);
			break;
		case isometric :
			vbuff = Vector3f.UNIT_Y.mult(-100).add(10,0,0);
			location = target.getNode().getLocalTranslation().subtract(vbuff);
			left = target.getNode().getLocalRotation().getRotationColumn(0);
			//TODO : don't let the camera sink to far even, if ascio lies on his back
			up = target.getNode().getLocalRotation().getRotationColumn(1);
			direction = target.getNode().getLocalTranslation().subtract(location);
			cam.setLocation(location);
			cam.lookAt( target.getNode().getLocalTranslation(), Vector3f.UNIT_Y);
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
		target.getNode().getLinearVelocity(vbuff);
		vbuff.set(0, vbuff.x > moveSpeed ? moveSpeed : vbuff.x < -moveSpeed ? -moveSpeed : vbuff.x);
		//vbuff.set(1, vbuff.y > moveSpeed ? moveSpeed : vbuff.y < -moveSpeed ? -moveSpeed : vbuff.y);
		vbuff.set(2, vbuff.z > moveSpeed ? moveSpeed : vbuff.z < -moveSpeed ? -moveSpeed : vbuff.z);
		target.getNode().setLinearVelocity(vbuff);
		
		// limit turning speed
		target.getNode().getAngularVelocity(vbuff);
		vbuff.set(0, vbuff.x > turnSpeed ? turnSpeed : vbuff.x < -turnSpeed ? -turnSpeed : vbuff.x);
		vbuff.set(1, vbuff.y > turnSpeed ? turnSpeed : vbuff.y < -turnSpeed ? -turnSpeed : vbuff.y);
		vbuff.set(2, vbuff.z > turnSpeed ? turnSpeed : vbuff.z < -turnSpeed ? -turnSpeed : vbuff.z);
		target.getNode().setAngularVelocity(vbuff);
	}

	public void setGameType( GameType type ) {
		this.type = type;
	}
	
}
