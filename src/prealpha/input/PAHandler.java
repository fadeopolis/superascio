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
	float turnSpeed = 1f;
	float moveSpeed = 50;
	
	Vector3f vbuff = new Vector3f();
	
	public PAHandler( Ascio target, Camera cam) {
		super();
		this.target = target;
		this.cam = cam;
		
		camOffset.set(0, 3, 9);
		
		setupKeys();
		setupCamera();
	}
	@Override
	public void update(float time) {
		super.update(time);
		
		updateCamera(time);
		updateKeys(time);
		
		limitForces();	
	}

	/**
	 * puts the camera in the right position according to set gametype
	 */
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
			vbuff = target.getNode().getLocalRotation().getRotationColumn(1).mult(-25).add(1,0,0);
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
				MoveAction.type = GameType.sideScroller;
				//resetKeys();
				break;
			case isometric :
				type = GameType.thirdPerson;
				MoveAction.type = GameType.sideScroller;
				//resetKeys();
				break;
			}
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("swing", false)) {
			Quaternion buff1 = new Quaternion();
			Quaternion buff2 = new Quaternion();
			
			SpatialTransformer swing = new SpatialTransformer(1);
			System.out.println("HOHAAH");
			swing.setObject(target.getWeapon().getNode(), 0, -1);
			
			Vector3f v1 = new Vector3f(-1, 2, 1.5f);
			Quaternion q1 = new Quaternion();
			buff1.fromAngleAxis(-135*FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
			buff2.fromAngleAxis(-45*FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
			q1 = buff1;
			q1.multLocal(buff2);
			//q1.fromAngleAxis(angle, axis)
			swing.setPosition(0, 0, v1);
			swing.setRotation(0, 0, q1);
			
			Vector3f v2 = new Vector3f(.5f, .0f, 1.5f);
			Quaternion q2 = new Quaternion();
			buff1.fromAngleAxis(30*FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
			buff2.fromAngleAxis(-75*FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
			q2 = buff1;
			q2.multLocal(buff2);
			swing.setPosition(0, .5f, v2);
			swing.setRotation(0, .5f, q2);
			
			swing.interpolateMissing();
			
			target.getWeapon().getNode().addController(swing);
		}
	}
	
	/**
	 * prevents ascio from spinning or moving to fast
	 */
	private void limitForces() {
		// limit turning speed
		vbuff = target.getNode().getAngularVelocity(vbuff);
		if (vbuff.x > turnSpeed) vbuff.x = turnSpeed; else if (vbuff.x < -turnSpeed) vbuff.x = -turnSpeed;
		if (vbuff.y > turnSpeed) vbuff.y = turnSpeed; else if (vbuff.y < -turnSpeed) vbuff.y = -turnSpeed;
		if (vbuff.z > turnSpeed) vbuff.z = turnSpeed; else if (vbuff.z < -turnSpeed) vbuff.z = -turnSpeed;
		target.getNode().setAngularVelocity(vbuff);
		// limit movement speed
		vbuff = target.getNode().getLinearVelocity(vbuff);
		if (vbuff.x > moveSpeed) vbuff.x = moveSpeed; else if (vbuff.x < -moveSpeed) vbuff.x = -moveSpeed;
		if (vbuff.y > moveSpeed) vbuff.y = moveSpeed; else if (vbuff.y < -moveSpeed) vbuff.y = -moveSpeed;
		if (vbuff.z > moveSpeed) vbuff.z = moveSpeed; else if (vbuff.z < -moveSpeed) vbuff.z = -moveSpeed;
		target.getNode().setLinearVelocity(vbuff);
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
	public void setGameType( GameType type ) {
		this.type = type;
	}
	
}
