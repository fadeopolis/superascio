package prealpha.state;

import prealpha.ascio.*;
import prealpha.foes.BadBall;
import prealpha.foes.Foe;
import prealpha.input.*;
import prealpha.enums.GameType;
import prealpha.util.*;
import prealpha.util.Util.PropType;
import prealpha.state.*;
//java
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
//jME
import com.jme.util.*;
import com.jme.util.Timer;
import com.jme.bounding.*;
import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.light.*;
import com.jme.renderer.*;
import com.jme.scene.state.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.system.*;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.math.*;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.game.*;
import com.jmex.game.state.*;
//physics
import com.jmex.physics.*;
import com.jmex.physics.geometry.*;
import com.jmex.physics.callback.*;
import com.jmex.physics.contact.ContactCallback;
import com.jmex.physics.contact.PendingContact;
import com.jmex.physics.util.states.*;

public class PAState extends PhysicsGameState {
	static Logger logger = Logger.getLogger("Ascio");
	Timer timer = Timer.getTimer();
	
	//system status booleans
	static boolean physics_debug = false;
	static boolean first_frame = true;
	
	//system stuff
	Camera cam;
	PAHandler input;
	Text[] text;
	ThreadGroup builders;
	
	/** nodes and collections that contain the elments of the scene */
	Ascio ascio;
	StaticPhysicsNode floorNode;
	Node doodadNode;
	Node foeNode;
	Map<Node, Foe> foes;	
	Node textNode;
	
	/** threads that build the scene */
	Thread soundT;
	Thread lightT;
	Thread textT;
	Thread terrainT;
	Thread doodadT;
	Thread foeT;
	
	/** sounds */
	AudioTrack ascioTrack;
    AudioTrack laserSound;
	AudioTrack targetSound;
	
	/** buffer for calculation to prevent object creation */
	Quaternion qbuff = new Quaternion();
	Vector3f vbuff = new Vector3f();
	int ibuff = 0;
	int fbuff = 0;
	
	/** not used at the moment */
	CharacterFactory factory;
	
	public PAState( String name ) {
		super(name);
		//space = (PhysicsSpace) Util.util().getProp(PropType.PhysicsSpace);
		//space = Util.util().space;
		//space = PhysicsSpace.create();
		factory = new CharacterFactory(this.getPhysicsSpace(), rootNode);
		//build();
	}
	/**
	 * Convenience method for building the state
	 * @throws InterruptedException
	 */
	public void build() throws InterruptedException {
		System.out.println("LET'S GET IT ON!");
	
		builders = new ThreadGroup("builders");
		

		setupCamera();
		setupSound();
		setupLight();
		setupText();
		setupTerrain();
		setupDoodads();
		setupFoes();
		setupPlayer();
		
		// wait for the building threads to finish before continuing
		lightT.join();
		textT.join();
		//doodadT.join();
		//soundT.join();
			
		setupInput();
		setupPhysics();
		finalTouch();
		
		System.out.println("HUZZAH! IT'S RUNNING!");
	}
	
	/** 
	 * methods that build seperate parts of the scene, these do not require each other
	 * and can be commented out for debugging in build without problems */
	protected void setupCamera() {
		System.out.println("SETTING UP CAMERA");
		
		cam = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
		
		System.out.println("CAMERA SET UP");
	}
	protected void setupSound() {   
		/** Set the 'ears' for the sound API */
		soundT = new Thread(builders, new Runnable() {
			public void run () {
				System.out.println("SETTING UP SOUND");
				
				// TODO Auto-generated method stub
		        AudioSystem audio = AudioSystem.getSystem();
		        //oh it took me some time to find out what caused the 
		        //funky sound when moving, but this fixes it
				audio.setDopplerFactor(.001f);

		        /** Create program sound */
		        try {
					ascioTrack = audio.createAudioTrack(new File("/home/fader/workspace/ascio/data/sound/fabb-Ascc2.ogg").toURI().toURL(), false);
					ascioTrack.setType(AudioTrack.TrackType.ENVIRONMENT);
					ascioTrack.setMaxAudibleDistance(1000);
			        ascioTrack.setVolume(1f);
		        } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		        targetSound = audio.createAudioTrack( getClass().getResource( "/jmetest/data/sound/explosion.ogg" ), false);
		        targetSound.setMaxAudibleDistance(1000);
		        targetSound.setVolume(1.0f);
				laserSound = audio.createAudioTrack( getClass().getResource( "/jmetest/data/sound/laser.ogg" ), false);
		        laserSound.setMaxAudibleDistance(1000);
		        laserSound.setVolume(1.0f);
		        
		        ascioTrack.setRelative(false);
		        System.out.println("SOUND SET UP");
			}
		});
		soundT.start();		
	}
	protected void setupLight() {		
		lightT =new Thread(builders, new Runnable() {

			@Override
			public void run() {
				System.out.println("SETTING UP LIGHT");
				// TODO Auto-generated method stub
				
				LightState lightState = (LightState) rootNode.getRenderState(RenderState.RS_LIGHT);
				if (lightState == null) {
					lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
					lightState.setEnabled(true);
					
			        PointLight light = new PointLight();
			        light.setDiffuse( ColorRGBA.lightGray );
			        light.setAmbient(ColorRGBA.white);
			        light.setLocation( new Vector3f( 100, 100, 100 ) );
			        light.setEnabled( true );
			        lightState.attach(light);
			        
				}
				DirectionalLight sun = new DirectionalLight();	
				sun.setDiffuse( ColorRGBA.lightGray );
				sun.setAmbient(ColorRGBA.white);
				sun.setEnabled(true);
				lightState.attach(sun);
						
				rootNode.setRenderState(lightState);
				
				System.out.println("LIGHT SET UP");
			}
		});
		lightT.start();
	}
	protected void setupText() {
		textT = new Thread(builders, new Runnable() {
			public void run() {
				text = new Text[20];
				for (int i=0; i<text.length; i++) {
					text[i] = Text.createDefaultTextLabel("text "+i);
					text[i].setLocalTranslation(new Vector3f(0,(i*12),0));
					text[i].setRenderQueueMode(Renderer.QUEUE_ORTHO);
					rootNode.attachChild(text[i]);
				}
			}
		});
		textT.start();		
	}
	protected void setupTerrain() {
		new Thread(builders, new Runnable() {
			@Override
			public void run() {
				System.out.println("SETTING UP TERRAIN");
				// TODO Auto-generated method stub

				//visuals for the floor
				Box floorVis = new Box("floorVis", new Vector3f(0,-2,0),500,.5f,500);
				floorVis.setModelBound(new BoundingBox());
				floorVis.updateModelBound();
				
				//pillars in the 4 directions, for orientation
				Box northernPillar = new Box("northernPillar", new Vector3f(500,0,0),.1f,500,500);
				northernPillar.setModelBound(new BoundingBox());
				northernPillar.updateModelBound();
				MaterialState northernState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				northernState.setAmbient(ColorRGBA.black);
				northernPillar.setRenderState(northernState);
				
				Box southernPillar = new Box("southernPillar", new Vector3f(-500,0,0),.1f,500,500);
				southernPillar.setModelBound(new BoundingBox());
				southernPillar.updateModelBound();
				MaterialState southernState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				southernState.setAmbient(ColorRGBA.black);
				southernPillar.setRenderState(southernState);
				
				Box easternPillar = new Box("easternPillar", new Vector3f(0,0,500),500,500,.1f);
				easternPillar.setModelBound(new BoundingBox());
				easternPillar.updateModelBound();
				MaterialState easternState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				easternState.setAmbient(ColorRGBA.black);
				easternPillar.setRenderState(easternState);
				
				Box westernPillar = new Box("westernPillar", new Vector3f(0,0,-500),500,1000,.1f);
				westernPillar.setModelBound(new BoundingBox());
				westernPillar.updateModelBound();
				MaterialState westernState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				westernState.setAmbient(ColorRGBA.black);
				westernPillar.setRenderState(westernState);
				
				// boxes, just lying around for fun
				Box[] boxes = new Box[100];
				for (int i=0; i < boxes.length; i++) {
					vbuff.set(Util.randomInt(450, true)+20, Util.randomInt(3, false), Util.randomInt(450, true)+20);
					if ( vbuff.x > -20 && vbuff.x < 20 ) vbuff.set(0, 40);
					if ( vbuff.z > -20 && vbuff.z < 20 ) vbuff.set(2, 40);
					
					boxes[i] = new Box("Box "+i, vbuff, Util.randomInt(10, false)+1, Util.randomInt(10, false)+1, Util.randomInt(10, false)+1);
					MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
					state.setDiffuse(ColorRGBA.randomColor());
					boxes[i].setRenderState(state);
				}
				
				//Physics
				floorNode = getPhysicsSpace().createStaticNode();
				
				floorNode.attachChild(floorVis);
				floorNode.attachChild(northernPillar);
				floorNode.attachChild(southernPillar);
				floorNode.attachChild(easternPillar);
				floorNode.attachChild(westernPillar);
				for (int i=0; i<boxes.length;i++) floorNode.attachChild(boxes[i]);
				
				floorNode.generatePhysicsGeometry();
				
				rootNode.attachChild(floorNode);
				rootNode.updateRenderState();
				
				System.out.println("TERRAIN SET UP");
			}
		}).start();

	}
	protected void setupDoodads() {
		doodadT = new Thread(builders, new Runnable() {
			@Override
			public void run() {
				System.out.println("SETTING UP DOODADS");
				// TODO Auto-generated method stub
				
				
				doodadNode = new Node("Doodads");

				//a sphere for ascio to play with
				Sphere sphereVis = new Sphere("sphere", 30,30,3);
				Cylinder sphereAxis = new Cylinder("sphereAxis", 30, 30, 1, 5.8f,true);
				
				MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				state.setDiffuse(ColorRGBA.blue);
				state.setAmbient(ColorRGBA.green);
				
				DynamicPhysicsNode sphere = getPhysicsSpace().createDynamicNode();
				sphere.setLocalTranslation(5,10,20);
				sphere.setName("Doodad : Sphere");
				sphere.setRenderState(state);
				sphere.attachChild(sphereVis);
				sphere.attachChild(sphereAxis);
				sphere.setModelBound(new BoundingSphere());
				sphere.updateModelBound();
				sphere.generatePhysicsGeometry();
				sphere.setMass(5);			
				sphere.getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
				
				doodadNode.attachChild(sphere);
				rootNode.attachChild(doodadNode);
				
				System.out.println("DOODADS SET UP");
			}
		});
		doodadT.start();
	}
	protected void setupFoes() {
		// TODO Auto-generated method stub
		System.out.println("SETTING UP FOES");
		
		foeNode = new Node();
		rootNode.attachChild(foeNode);
		foeNode.setLocalTranslation(10, 0, 10);
		
		foes = new HashMap<Node, Foe>();
		
		for (int i=0; i < 1; i++) {
			BadBall proto = new BadBall(getPhysicsSpace().createDynamicNode());
			proto.getNode().getLocalTranslation().set(0, 5*i);
			foeNode.attachChild(proto.getNode());
			foes.put(proto.getNode(), proto);
		}
		
		System.out.println("FOES SET UP");
	}
	protected void setupPlayer() {
		System.out.println("SETTING UP PLAYER");

		ascio = factory.createPlayer();
		rootNode.attachChild(ascio.getNode());
		ascio.getNode().getLocalTranslation().add(0, 30, 0);
		//ascio.getNode().setAffectedByGravity(false);
		
		Util.util().putProp(ascio);
		
		System.out.println("PLAYER SET UP");
	}
	
	/** also convenience methods for building the scene,
	 * but they require the other methods to have finished
	 * turning off other build-methods may affect these */
	protected void setupInput() {
		System.out.println("SETTING UP INPUT");
		
		input = new PAHandler(ascio, cam);
		input.setGameType(prealpha.enums.GameType.thirdPerson);
		
		KeyBindingManager.getKeyBindingManager().add("exit", KeyInput.KEY_ESCAPE);
		KeyBindingManager.getKeyBindingManager().add("physics_debug", KeyInput.KEY_P);
		KeyBindingManager.getKeyBindingManager().add("music", KeyInput.KEY_M);
		KeyBindingManager.getKeyBindingManager().add("reset", KeyInput.KEY_F12);
		
		KeyBindingManager.getKeyBindingManager().add("turnx+", KeyInput.KEY_1);
		KeyBindingManager.getKeyBindingManager().add("turnx-", KeyInput.KEY_2);
		KeyBindingManager.getKeyBindingManager().add("turny+", KeyInput.KEY_3);
		KeyBindingManager.getKeyBindingManager().add("turny-", KeyInput.KEY_4);
		KeyBindingManager.getKeyBindingManager().add("turnz+", KeyInput.KEY_5);
		KeyBindingManager.getKeyBindingManager().add("turnz-", KeyInput.KEY_6);
		
		KeyBindingManager.getKeyBindingManager().add("funky", KeyInput.KEY_0);
		
		System.out.println("INPUT SET UP");
	}
	protected void setupPhysics() {
		System.out.println("SETTING UP PHYSICS");
		// TODO Auto-generated method stub
		
		ContactCallback contact = new ContactCallback() {
			@Override
			public boolean adjustContact(PendingContact contact) {
				// TODO Auto-generated method stub
				//contact.getNode1();
				if ( contact.getNode1() == ascio.getNode() || contact.getNode1() == ascio.getNode() ) {
					Foe f = foes.get(contact.getNode1());
					if (f == null) f = foes.get(contact.getNode2());
					
					if (f != null) {
						ascio.damage(5);
						BadBall b = (BadBall) f;
						vbuff.set(0, 1000, 0);
						b.getNode().addForce(vbuff);
						System.out.println("YOU GOT HIT");
					}
					
					return false;
				} else if ( contact.getNode1() == ascio.getWeapon().getNode() || contact.getNode1() == ascio.getWeapon().getNode() ) {
					Foe f = foes.get(contact.getNode1());
					if (f == null) f = foes.get(contact.getNode2());
					
					if (f != null) {
						f.damage(15);
						System.out.println("HIT THE ENEMY");
					}
					
					return false;
				} 
				return false;
			}			
		};
		
		FrictionCallback friction = new FrictionCallback();
		friction.add((DynamicPhysicsNode) ascio.getNode(), .5f, .5f);
//		friction.add((DynamicPhysicsNode) doodadNode.getChild(0), .5f, .5f);
		this.getPhysicsSpace().addToUpdateCallbacks(friction);
		this.getPhysicsSpace().addToUpdateCallbacks(new Callback());
		this.getPhysicsSpace().getContactCallbacks().add(contact);
		
		System.out.println("PHYSICS SET UP");
	}
	protected void finalTouch() {
		System.out.println("GIVING IT THE FINAL TOUCH");
		
		// set ascio as target for the foes
		for ( Foe f : foes.values()) {
			f.setTarget(ascio);
		}
		
		// TODO Auto-generated method stub
		rootNode.updateGeometricState(0, true);
		rootNode.updateRenderState();
		
		System.out.println("FINAL TOUCH GIVEN");
	}

	@Override
	public void update(float time) {
		super.update(time);
				
		input.update(time);
		
		updateKeys();		
		updateText();
		updateFoes(time);
		
        if ( first_frame )
        {
            // drawing and calculating the first frame usually takes longer than the rest
            // to avoid a rushing simulation we reset the timer
            timer.reset();
            first_frame = false;
        }
	}

	private void updateKeys() {
		// TODO Auto-generated method stub
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("exit", false)) {
			System.exit(0);
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("reset", false)) {
			// funky effects happening here, when you let go of F12, ascio snaps back to his old position
			ascio.getNode().getLocalTranslation().set(0,5,0);
			ascio.getNode().clearDynamics();
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("turnx+", true)) {
			ascio.getNode().addTorque(Vector3f.UNIT_X.mult(90));
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("turnx-")) {
			ascio.getNode().addTorque(Vector3f.UNIT_X.mult(-90));
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("turny+")) {
			ascio.getNode().addTorque(Vector3f.UNIT_Y.mult(90));
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("turny-")) {
			ascio.getNode().addTorque(Vector3f.UNIT_Y.mult(-90));
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("turnz+")) {
			ascio.getNode().addTorque(Vector3f.UNIT_Z.mult(90));
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("turnz-")) {
			ascio.getNode().addTorque(Vector3f.UNIT_Z.mult(-90));
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("funky")) {
			vbuff.set(1, -999999);
			ascio.getNode().setLinearVelocity(vbuff);
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("physics_debug", false)) {
			physics_debug = !physics_debug;
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("music", false)) {
			try {
				if (ascioTrack.isPlaying()) ascioTrack.stop(); else ascioTrack.play();
			} finally {			
			}
		}
	}
 	private void updateText() {
		float XAc0,XAc1,XAc2,XAL,XAR, XCc0,XCc1,XCc2,XCL,XCR;
		float YAc0,YAc1,YAc2,YAL,YAR, YCc0,YCc1,YCc2,YCL,YCR;
		float ZAc0,ZAc1,ZAc2,ZAL,ZAR, ZCc0,ZCc1,ZCc2,ZCL,ZCR;
		float WAR, WCR;
		
		XAc0 = Util.round(ascio.getNode().getLocalRotation().getRotationColumn(0).x);
		XAc1 = Util.round(ascio.getNode().getLocalRotation().getRotationColumn(1).x);
		XAc2 = Util.round(ascio.getNode().getLocalRotation().getRotationColumn(2).x);
		XAL = Util.round(ascio.getNode().getLocalTranslation().x);
		XAR = Util.round(ascio.getNode().getLocalRotation().x);
		YAc0 = Util.round(ascio.getNode().getLocalRotation().getRotationColumn(0).y);
		YAc1 = Util.round(ascio.getNode().getLocalRotation().getRotationColumn(1).y);
		YAc2 = Util.round(ascio.getNode().getLocalRotation().getRotationColumn(2).y);
		YAL = Util.round(ascio.getNode().getLocalTranslation().y);
		YAR = Util.round(ascio.getNode().getLocalRotation().y); 
		ZAc0 = Util.round(ascio.getNode().getLocalRotation().getRotationColumn(0).z);
		ZAc1 = Util.round(ascio.getNode().getLocalRotation().getRotationColumn(1).z);
		ZAc2 = Util.round(ascio.getNode().getLocalRotation().getRotationColumn(2).z);
		ZAL = Util.round(ascio.getNode().getLocalTranslation().z);	
		ZAR = Util.round(ascio.getNode().getLocalRotation().z);
		WAR = Util.round(ascio.getNode().getLocalRotation().w);
		
		qbuff.fromAxes(cam.getLeft(),cam.getUp(),cam.getDirection());
		XCc0 = Util.round(cam.getLeft().x);
		XCc1 = Util.round(cam.getUp().x);
		XCc2 = Util.round(cam.getDirection().x);
		XCL = Util.round(cam.getLocation().x);
		XCR = Util.round(qbuff.x);
		YCc0 = Util.round(cam.getLeft().y);
		YCc1 = Util.round(cam.getUp().y);
		YCc2 = Util.round(cam.getDirection().y);
		YCL = Util.round(cam.getLocation().y);
		YCR = Util.round(qbuff.y); 
		ZCc0 = Util.round(cam.getLeft().z);
		ZCc1 = Util.round(cam.getUp().z);
		ZCc2 = Util.round(cam.getDirection().z);
		ZCL = Util.round(cam.getLocation().z);	
		ZCR = Util.round(qbuff.z);
		WCR = Util.round(qbuff.w);
		
		text[15].print("Ascio");
		vbuff = ascio.getNode().getAngularVelocity(vbuff);
		text[14].print("Angular Velocity    : X:"+Util.round(vbuff.x)+" Y:"+Util.round(vbuff.y)+" Z:"+Util.round(vbuff.z));
		vbuff = ascio.getNode().getLinearVelocity(vbuff);
		text[13].print("Linear Velocity     : X:"+Util.round(vbuff.x)+" Y:"+Util.round(vbuff.y)+" Z:"+Util.round(vbuff.z));
		vbuff = ascio.getNode().getForce(vbuff);
		text[12].print("Force               : X:"+Util.round(vbuff.x)+" Y:"+Util.round(vbuff.y)+" Z:"+Util.round(vbuff.z));
		text[11].print("Location            : X:"+XAL+" Y:"+YAL+" Z:"+ZAL );
		text[10].print("Rotation Quaternion : X:"+XAR+" Y:"+YAR+" Z:"+ZAR+" W:"+WAR );
		text[9].print("Rotation Column 0   : X:"+XAc0+" Y:"+YAc0+" Z:"+ZAc0 );
		text[8].print("Rotation Column 1   : X:"+XAc1+" Y:"+YAc1+" Z:"+ZAc1 );
		text[7].print("Rotation Column 2   : X:"+XAc2+" Y:"+YAc2+" Z:"+ZAc2 );
		/*
		text[6].print("Camera");
		text[5].print("Location            : X:"+XCL+" Y:"+YCL+" Z:"+ZCL );
		text[4].print("Rotation Quaternion : X:"+XCR+" Y:"+YCR+" Z:"+ZCR+" W:"+WCR );
		text[3].print("Rotation Left       : X:"+XCc0+" Y:"+YCc0+" Z:"+ZCc0 );
		text[2].print("Rotation Up         : X:"+XCc1+" Y:"+YCc1+" Z:"+ZCc1 );
		text[1].print("Rotation Direction  : X:"+XCc2+" Y:"+YCc2+" Z:"+ZCc2 );
 		*/
		text[0].print("FPS: "+ timer.getFrameRate());
 	}
 	private void updateFoes(float time) {
		for ( Foe f : foes.values()) {
			f.update(time);	
			if (true) {
			//if ( this.getPhysicsSpace().collide(floorNode, f.getNode())) {
			//if ( f.getWorldBound().intersects(floorNode.getWorldBound())) {
				f.update = true;
			} else f.update = false;
		}
 	}
	
 	@Override
	public void render(float time) {
		super.render(time);
		
		if (physics_debug) PhysicsDebugger.drawPhysics(getPhysicsSpace(), DisplaySystem.getDisplaySystem().getRenderer());
	}
 	
 	private class Callback implements PhysicsUpdateCallback {
		@Override
		public void beforeStep(PhysicsSpace space, float time) {
			// TODO Auto-generated method stub
			//
		}		
		@Override
		public void afterStep(PhysicsSpace space, float time) {
			// TODO Auto-generated method stub			
			//resets player when he falls through the floor, don't know what causes that (yet).
			if (ascio.getNode().getLocalTranslation().y< -100) {
				ascio.getNode().getLocalTranslation().set(0,5,0);
				ascio.getNode().clearDynamics();
			}
		}
	}


}
