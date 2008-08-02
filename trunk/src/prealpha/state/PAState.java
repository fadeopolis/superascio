package prealpha.state;

import prealpha.ascio.*;
import prealpha.foes.BadBall;
import prealpha.input.*;
import prealpha.interfaces.Foe;
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
	
	//the nodes for manipulating everything
	Node playa = new Node();
	Ascio ascio;
	StaticPhysicsNode floorNode;
	Node doodadNode;
	Node foeNode;
	
	Map<Node, Foe> foes;
	
	Node textNode;
	
	// sounds
	AudioTrack ascioTrack;
    AudioTrack laserSound;
	AudioTrack targetSound;
	
	//buffer for calculation prevent object creation
	Quaternion qbuff = new Quaternion();
	Vector3f vbuff = new Vector3f();
	int ibuff = 0;
	int fbuff = 0;
	
	CharacterFactory factory;
	
	public PAState( String name ) {
		super(name);
		//space = (PhysicsSpace) Util.util().getProp(PropType.PhysicsSpace);
		//space = Util.util().space;
		//space = PhysicsSpace.create();
		factory = new CharacterFactory(this.getPhysicsSpace(), rootNode);
		//build();
	}

	public void build() {
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
		setupInput();
		setupPhysics();
		finalTouch();
			
		//ascio.getNode().addForce(new Vector3f(0,9999999,0));
		
		System.out.println("HUZZAH! IT'S RUNNING!");
	}
	
	protected void setupCamera() {
		System.out.println("SETTING UP CAMERA");
		
		cam = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
		
		System.out.println("CAMERA SET UP");
	}
	protected void setupSound() {   
		/** Set the 'ears' for the sound API */
		new Thread(builders, new Runnable() {
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
					ascioTrack.track(playa);
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
		}).start();		
	}
	protected void setupLight() {		
		new Thread(builders, new Runnable() {

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
		}).start();
	}
	protected void setupText() {
		new Thread(builders, new Runnable() {
			public void run() {
				text = new Text[20];
				for (int i=0; i<text.length; i++) {
					text[i] = Text.createDefaultTextLabel("text "+i);
					text[i].setLocalTranslation(new Vector3f(0,(i*12),0));
					text[i].setRenderQueueMode(Renderer.QUEUE_ORTHO);
					rootNode.attachChild(text[i]);
				}
			}
		}).start();		
	}
	protected void setupTerrain() {
		new Thread(builders, new Runnable() {
			@Override
			public void run() {
				System.out.println("SETTING UP TERRAIN");
				// TODO Auto-generated method stub

				//visuals for the floor
				Box floorVis = new Box("floorVis", new Vector3f(0,-2,0),1000,.5f,1000);
				floorVis.setModelBound(new BoundingBox());
				floorVis.updateModelBound();
				
				//pillars in the 4 directions, for orientation
				Box northernPillar = new Box("northernPillar", new Vector3f(1000,0,0),10,1000,10);
				northernPillar.setModelBound(new BoundingBox());
				northernPillar.updateModelBound();
				MaterialState northernState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				northernState.setAmbient(ColorRGBA.red);
				northernState.setDiffuse(ColorRGBA.randomColor());
				northernPillar.setRenderState(northernState);
				
				Box southernPillar = new Box("southernPillar", new Vector3f(-1000,0,0),10,1000,10);
				southernPillar.setModelBound(new BoundingBox());
				southernPillar.updateModelBound();
				MaterialState southernState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				southernState.setAmbient(ColorRGBA.green);
				southernState.setDiffuse(ColorRGBA.randomColor());
				southernPillar.setRenderState(southernState);
				
				Box easternPillar = new Box("easternPillar", new Vector3f(0,0,1000),10,1000,10);
				easternPillar.setModelBound(new BoundingBox());
				easternPillar.updateModelBound();
				MaterialState easternState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				easternState.setAmbient(ColorRGBA.blue);
				easternState.setDiffuse(ColorRGBA.randomColor());
				easternPillar.setRenderState(easternState);
				
				Box westernPillar = new Box("westernPillar", new Vector3f(0,0,-1000),10,1000,10);
				westernPillar.setModelBound(new BoundingBox());
				westernPillar.updateModelBound();
				MaterialState westernState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				westernState.setAmbient(ColorRGBA.orange);
				westernState.setDiffuse(ColorRGBA.randomColor());
				westernPillar.setRenderState(westernState);
				
				// boxes, just lying around for fun
				Box[] boxes = new Box[200];
				for (int i=0; i < boxes.length; i++) {
					vbuff.set(Util.randomInt(750, true), Util.randomInt(3, false), Util.randomInt(750, true));
					
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
		System.out.println("SETTING UP DOODADS");
		// TODO Auto-generated method stub
		
		doodadNode = new Node("Doodads");

		//a sphere for ascio to play with
		Sphere sphereVis = new Sphere("sphere", 30,30,3);
		Cylinder sphereAxis = new Cylinder("sphereAxis", 30, 30, 1, 5.8f,true);
		
		MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		state.setDiffuse(ColorRGBA.blue);
		state.setAmbient(ColorRGBA.green);
		
		DynamicPhysicsNode sphere = this.getPhysicsSpace().createDynamicNode();
		sphere.setName("Doodad : Sphere");
		sphere.setRenderState(state);
		sphere.attachChild(sphereVis);
		sphere.attachChild(sphereAxis);
		sphere.setModelBound(new BoundingSphere());
		sphere.updateModelBound();
		sphere.generatePhysicsGeometry();
		sphere.setMass(5);
		sphere.setLocalTranslation(5,10,20);
		sphere.getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
		
		doodadNode.attachChild(sphere);
		rootNode.attachChild(doodadNode);
		
		System.out.println("DOODADS SET UP");
	}
	protected void setupFoes() {
		System.out.println("SETTING UP FOES");
		// TODO Auto-generated method stub
		foeNode = new Node();
		rootNode.attachChild(foeNode);
		foeNode.setLocalTranslation(10, 0, 10);
		
		foes = new HashMap<Node, Foe>();
		
		BadBall proto = new BadBall(this.getPhysicsSpace().createDynamicNode());
		proto.setTarget(ascio);
		foeNode.attachChild(proto.getNode());
		foes.put(proto.getNode(), proto);
		BadBall bi = new BadBall(this.getPhysicsSpace().createDynamicNode());
		bi.setTarget(ascio);
		foeNode.attachChild(bi.getNode());
		foes.put(bi.getNode(), bi);
		BadBall tri = new BadBall(this.getPhysicsSpace().createDynamicNode());
		tri.setTarget(ascio);
		foeNode.attachChild(tri.getNode());
		foes.put(tri.getNode(), tri);
		BadBall quad = new BadBall(this.getPhysicsSpace().createDynamicNode());
		quad.setTarget(ascio);
		foeNode.attachChild(quad.getNode());
		foes.put(quad.getNode(), quad);
		
		System.out.println("FOES SET UP");
	}
	protected void setupPlayer() {
		System.out.println("SETTING UP PLAYER");

		//visuals
		//todo: use com.jme.model.util.resoucelocator
		//File file = new File("data/model/ascio.jme");
		//if (file.exists() && file.canRead()) System.out.println("HUZZAH!"); else System.exit(1);
		/*
		try {
			BinaryImporter importer = new BinaryImporter();
			model = (Node)importer.load(file.toURI().toURL().openStream());
			model.setModelBound(new BoundingBox());
			model.updateModelBound();
			model.setLocalScale(10f);
		} catch (IOException e) {*/
		ascio = factory.createPlayer();
		playa.attachChild(ascio.getNode());
		rootNode.attachChild(playa);
//		rootNode.attachChild(ascio.getNode());
		//}		
		//physics
		//todo : put a physbox/or other geom around the player, don't just generatephysgeom
		ascio.getNode().setLocalTranslation(0,5,0);
		//ascio.getNode().setAffectedByGravity(false);
		
		Util.util().putProp(ascio);
		
		System.out.println("PLAYER SET UP");
	}
	protected void setupInput() {
		System.out.println("SETTING UP INPUT");
		
		input = new PAHandler(ascio, cam);
		input.setGameType(prealpha.enums.GameType.thirdPerson);
		
		KeyBindingManager.getKeyBindingManager().add("exit", KeyInput.KEY_ESCAPE);
		KeyBindingManager.getKeyBindingManager().add("physics_debug", KeyInput.KEY_P);
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
						f.destroy();
						System.out.println("HIT");
					}
					
					return false;
				} else return false;
			}
			
		};
		
		FrictionCallback friction = new FrictionCallback();
		friction.add(ascio.getNode(), .5f, .5f);
		friction.add((DynamicPhysicsNode) doodadNode.getChild(0), .5f, .5f);
		this.getPhysicsSpace().addToUpdateCallbacks(friction);
		this.getPhysicsSpace().addToUpdateCallbacks(new Callback());
		this.getPhysicsSpace().getContactCallbacks().add(contact);
		
		System.out.println("PHYSICS SET UP");
	}

	protected void finalTouch() {
		System.out.println("GIVING IT THE FINAL TOUCH");
		// TODO Auto-generated method stub
		rootNode.updateGeometricState(0, true);
		rootNode.updateRenderState();
		
		System.out.println("FINAL TOUCH GIVEN");
	}

	@Override
	public void update(float time) {
		super.update(time);
				
		input.update(time);
		
		if (foes.values() == null) System.out.println("NULLNULL");
		
		for ( Foe f : foes.values()) {
			f.update(time);
		}
		
        if ( first_frame )
        {
            // drawing and calculating the first frame usually takes longer than the rest
            // to avoid a rushing simulation we reset the timer
            timer.reset();
            first_frame = false;
        }
	}

	@Override
	public void render(float time) {
		super.render(time);
		
		if (physics_debug) PhysicsDebugger.drawPhysics(getPhysicsSpace(), DisplaySystem.getDisplaySystem().getRenderer());
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
		
		long foo = 1024*1024;
		long freeMemory = Runtime.getRuntime().freeMemory();
		long maxMemory = Runtime.getRuntime().maxMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		text[17].print("FreeMemory : "+freeMemory+" | MaxMemory : "+maxMemory+" | TotalMemory : "+totalMemory);
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

	
 	private class Callback implements PhysicsUpdateCallback {
		@Override
		public void beforeStep(PhysicsSpace space, float time) {
			// TODO Auto-generated method stub
			//
		}		
		@Override
		public void afterStep(PhysicsSpace space, float time) {
			// TODO Auto-generated method stub
			updateKeys();
			
			//if (ibuff == 0) {
				updateText();
			//	ibuff = 30;
			//}
			//ibuff--;
			
			//resets player when he falls through the floor, don't know what causes that (yet).
			if (ascio.getNode().getLocalTranslation().y< -100) {
				ascio.getNode().getLocalTranslation().set(0,5,0);
				ascio.getNode().clearDynamics();
			}
		}
	}


}
