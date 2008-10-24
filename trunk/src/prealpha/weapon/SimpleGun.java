package prealpha.weapon;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.NoSuchElementException;

import prealpha.character.Character;
import prealpha.core.ActionPermitter;
import prealpha.core.Damageable;
import prealpha.core.Updateable;
import prealpha.core.Updater;
import prealpha.util.Util;
import prealpha.util.Util.Prop;

import com.jme.bounding.BoundingSphere;
import com.jme.input.InputHandler;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Capsule;
import com.jme.scene.shape.Sphere;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.contact.ContactInfo;

public class SimpleGun extends Weapon {

	AudioTrack track;
	private InputHandler bulletCollisionHandler;
	PhysicsSpace space;
	Node bulletNode;
	Character wielder;
	
	public SimpleGun( final Updater updater, Character wielder ) {
		super(updater);
		
		this.wielder = wielder;
		
		this.space = wielder.getPhysicsNode().getSpace();
			
		Capsule c = new Capsule("Simple gun, simple model", 15,15,15, .4f, .75f);
		c.getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
		this.attachChild( c );
		wielder.getPhysicsNode().attachChild(this);
		this.getLocalTranslation().addLocal(0, 0, 0);
			
		bulletNode = new Node();
		((Node) Util.get().getProp(Prop.RootNode)).attachChild(bulletNode);
			
		File f = new File("data/sound/laser.ogg");
		try {
			track = AudioSystem.getSystem().createAudioTrack(f.toURI().toURL(), false);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		track.setLooping(false);
			
		this.bulletCollisionHandler = new InputHandler();
			
		setFireRate( 5 );
	}
	
	@Override
	public boolean attack() {
		if ( mayFire() ) {
			// create Bullet
			Bullet bullet = new SimpleBullet( (PhysicsSpace) Util.get().getProp(Prop.PhysicsSpace), bulletCollisionHandler );
				
			bulletNode.attachChild(bullet);
			
			bullet.fire(getWorldTranslation(), getDirection(null));
			
			track.play();
		}
 		
		
		return false;
	}

	@Override
	public void update(float time) {
		super.update(time);
		
		this.bulletCollisionHandler.update(time);
		
//		bulletNode.getLocalTranslation().set(getSpatial().getLocalTranslation());
//		bulletNode.getWorldTranslation().set(getSpatial().getWorldTranslation());
	}

	class SimpleBullet extends Bullet {

		public SimpleBullet( final PhysicsSpace physics, final InputHandler handler ) {
			super( physics, handler );
			
			this.speed = 5f;
			this.mass = .001f;
			this.damage = 10f;
			
			Sphere c = new Sphere("SimpleBullet", 10, 10, .2f );
			c.setModelBound(new BoundingSphere());
			c.updateModelBound();
//			c.getLocalRotation().fromAngleNormalAxis(90*FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
			this.getPhysicsNode().attachChild(c);
			
			this.getPhysicsNode().setAffectedByGravity(false);
			this.getPhysicsNode().generatePhysicsGeometry();
			this.getPhysicsNode().computeMass();
			this.getPhysicsNode().setMass(mass);
			
			this.updateRenderState();
		}
		
		boolean expired;
		
		@Override
		protected void onCollision(ContactInfo c) {
			if ( (!expired) && fired ) {				
				if ( c.getNode1().getParent() instanceof Damageable ) {
					((Damageable) c.getNode1().getParent()).damage( this.damage );
				} else if ( c.getNode2().getParent() instanceof Damageable ) {
					((Damageable) c.getNode2().getParent()).damage( this.damage );				
				}
			}
			
			expired = true;
			getPhysicsNode().delete();
		}
	
		@Override
		public void fire( Vector3f origin, Vector3f direction ) {					
			getPhysicsNode().getLocalTranslation().set(origin);
			
			getPhysicsNode().addForce(direction.multLocal(speed));
			
			addController(new LifeTimeController(getPhysicsNode()));	

			fired = true;
		}
		
	}

}
