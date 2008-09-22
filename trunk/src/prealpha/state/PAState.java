package prealpha.state;

import prealpha.ascio.*;
import prealpha.curve.CatmullRomCurve;
import prealpha.curve.Curve;
import prealpha.curve.CurveWrapper;
import prealpha.curve.RectifiedCurve;
import prealpha.foes.BadBall;
import prealpha.foes.Foe;
import prealpha.input.*;
import prealpha.enums.GameType;
import prealpha.terrain.LevelBlock;
import prealpha.terrain.TerrainSystem;
import prealpha.test.InputController;
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
import com.jme.input.controls.GameControl;
import com.jme.input.controls.GameControlManager;
import com.jme.light.*;
import com.jme.renderer.*;
import com.jme.scene.state.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.system.*;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.geom.BufferUtils;
import com.jme.util.geom.Debugger;
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
	float timer2 = 0;
	
	//system status booleans
	boolean physics_debug = false;
	boolean first_frame = true;
	
	//system stuff
	Camera cam;
	PAHandler input;
	Text[] text;
	Text newsTicker;
	StringBuilder news;
	float newsTime = 0;
	ThreadGroup builders;
	
	/** nodes and collections that contain the elments of the scene */
	public Ascio ascio;
	Node floorNode;
	Node doodadNode;
	Node foeNode;
	Node textNode;
	Curve curve;
	
	/** sounds */
	AudioTrack ascioTrack;
    AudioTrack laserSound;
	AudioTrack targetSound;
	
	/** buffer for calculation to prevent object creation */
	Quaternion qbuff = new Quaternion();
	Vector3f vbuff = new Vector3f();
	Vector3f vbuff2 = new Vector3f();
	Vector3f vbuff3 = new Vector3f();
	int ibuff = 0;
	float fbuff = 0;
	
	float progress;
	float limit;
	boolean cycle;
	Node tracer;
	
	public PAState( String name ) throws InterruptedException, IOException {
		super(name);
		//space = (PhysicsSpace) Util.util().getProp(PropType.PhysicsSpace);
		//space = Util.util().space;
		//space = PhysicsSpace.create();
		Util.util().putProp(rootNode);
		
		build();
	}
	/**
	 * Convenience method for building the state
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public void build() throws InterruptedException, IOException {
		System.out.println("LET'S GET IT ON!");	

		progress = 0;
		cycle = true;
		
		setupCamera();
		//setupSound();
		setupLight();
		setupText();
		setupTerrain();
		//setupDoodads();
		//setupFoes();
		//foes = new ArrayList<Foe>();
		setupPlayer();
		
		// wait for the building threads to finish before continuing
		/*
		lightT.join();
		textT.join();
		doodadT.join();
		terrainT.join();
		soundT.join();
		*/
		
		rootNode.addController(new InputController(ascio));
			
		setupInput();
		setupPhysics();
		finalTouch();
		
		System.out.println("HUZZAH! IT'S RUNNING!");
	}
	
	/** 
	 * methods that build seperate parts of the scene, these do not require each other
	 * and can be commented out for debugging in the build method without problems */
	protected void setupCamera() {
		System.out.println("SETTING UP CAMERA");
		
		cam = DisplaySystem.getDisplaySystem().getRenderer().getCamera();		
		
		System.out.println("CAMERA SET UP");
	}
	protected void setupSound() {   
		/** Set the 'ears' for the sound API */
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
	protected void setupLight() {		
		System.out.println("SETTING UP LIGHT");
		// TODO Auto-generated method stub
		
		LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
		lightState.setEnabled(true);
		
		PointLight light = new PointLight();
        light.setDiffuse( new ColorRGBA( 0.75f, 0.75f, 0.75f, 0.75f ) );
        light.setAmbient( new ColorRGBA( 0.95f, 0.75f, 0.95f, .75f ) );
        light.setLocation( new Vector3f( 100, 100, 100 ) );
        light.setEnabled( true );

        DirectionalLight sun = new DirectionalLight();	
		sun.setDiffuse( new ColorRGBA(.9f,.9f,.9f,1f) );
		sun.setAmbient( new ColorRGBA(.9f,.9f,.9f,1f));
		sun.setEnabled(true);
		lightState.attach(sun);
					
		rootNode.setRenderState(lightState);
		
		System.out.println("LIGHT SET UP");
	}
	protected void setupText() {
		textNode = new Node();
		news = new StringBuilder(" THE NEWS ");
		rootNode.attachChild(textNode);
		
		// setup the news ticker
		newsTicker = Text.createDefaultTextLabel("newsTicker");
		newsTicker.setLocalTranslation(new Vector3f(0,DisplaySystem.getDisplaySystem().getHeight()-15,0));
		newsTicker.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		textNode.attachChild(newsTicker);
		
		// setup a lot of text lines for printing all kinds of things/Strings
		text = new Text[20];
		for (int i=0; i<text.length; i++) {
			text[i] = Text.createDefaultTextLabel("text "+i);
			text[i].setLocalTranslation(new Vector3f(0,(i*12),0));
			text[i].setRenderQueueMode(Renderer.QUEUE_ORTHO);
			textNode.attachChild(text[i]);
		}
		for (int i=0; i<20; i++) {
			news.append("012345678910");
		}		
	}
	protected void setupTerrain() throws IOException {
		System.out.println("SETTING UP TERRAIN");
		// TODO Auto-generated method stub
		floorNode = new Node();
		floorNode.setName("Floor Node");
		rootNode.attachChild(floorNode);
		//the floor
		
		TerrainSystem.create(getPhysicsSpace());
		floorNode.attachChild(TerrainSystem.terrainRoot);
		//TerrainSystem.getSystem().get(0, 0, 0);

		
		for ( int i = 0; i < 10; i++) {
			TerrainSystem.getSystem().get(i, 0, 0);
		}	
		
		for ( int i = 1; i < 10; i++) {
			TerrainSystem.getSystem().get(i-1, 0, 0).link(TerrainSystem.getSystem().get(i, 0, 0));
		}
		
		curve = TerrainSystem.getSystem().get(0, 0, 0).getCurve(0);	
		    
		System.out.println("TERRAIN SET UP");
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
	protected void setupFoes() {
		// TODO Auto-generated method stub
		System.out.println("SETTING UP FOES");
		
		foeNode = new Node("FoeNode");
		rootNode.attachChild(foeNode);

		for (int i=0; i < 10; i++) {
			vbuff.set(Util.randomInt(200, 50, true), 2, Util.randomInt(200, 50, true));
			BadBall proto = new BadBall("BadBall "+i, vbuff, getPhysicsSpace());
			proto.getLocalTranslation().set(Util.randomInt(200, 50, true), 2, Util.randomInt(200, 50, true));
			Util.shout("I'm at " + proto.getLocalTranslation());
			foeNode.attachChild(proto);
		}
		
		System.out.println("FOES SET UP");
	}
	protected void setupPlayer() {
		System.out.println("SETTING UP PLAYER");

		ascio = new BoxAscio("ascio", this.getPhysicsSpace().createDynamicNode() );
		
		((Geometry)ascio.getPhysicsNode().getChild(0)).setRandomColors();
		
		//rootNode.attachChild(new Box("", new Vector3f(0, -3, 0),.1f, 0.1f, 0.1f));
		//setupTerrain();
		
//		ascio.setLocalTranslation(curve.getPoint(.01f));
		ascio.setLocalTranslation(-47, 5, 0);
		ascio.getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD,Vector3f.UNIT_Y);
		//ascio.clearDynamics();
		//ascio.setLocalTranslation(Vector3f.UNIT_Y.mult(2));
		//ascio.getPhysicsNode().setLocalTranslation(Vector3f.UNIT_Y.mult(2));
		
		rootNode.attachChild(ascio);	
		
		CameraNode camNode = new CameraNode("camNode", cam);
		camNode.getLocalTranslation().addLocal(0, 1, 15);
		camNode.lookAt( new Vector3f(-47,0,0), Vector3f.UNIT_Y);
		cam.setDirection(cam.getLocation().subtract(ascio.getWorldTranslation()));
		cam.update();
//		camNode.lookAt(ascio.getWorldTranslation(), Vector3f.UNIT_Y);
	//	cam.lookAt(ascio.getWorldTranslation(), Vector3f.UNIT_Y);
	//	camNode.getLocalRotation().fromAngleAxis(270*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
		
		ascio.attachChild(camNode);
		//ascio.getPhysicsNode().setAffectedByGravity(false);
		
		Util.util().putProp(ascio);
		
		System.out.println("PLAYER SET UP");
	}
	
	/** also convenience methods for building the scene,
	 * but they require the other methods to have finished
	 * turning off other build-methods may affect these */
	protected void setupInput() {
		System.out.println("SETTING UP INPUT");
		
		//input = new PAHandler(ascio, cam);
		//input.setGameType(PAHandler.GameType.sideScroller);
//		input.setGameType(prealpha.enums.GameType.thirdPerson);
		//input.setCurve(curve);
		
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
				// TODO Auto-generated method stud
				Node a = contact.getNode1();
				Node b = contact.getNode2();
				DynamicPhysicsNode pbuff;
				
				if ( a == ascio.getPhysicsNode() && b.getParent() instanceof Foe) {
					ascio.damage(5);					
					pbuff = (DynamicPhysicsNode) b;
					pbuff.addForce(Vector3f.UNIT_Y.mult(1000));
				} else if ( b == ascio.getPhysicsNode() && a.getParent() instanceof Foe) {
					ascio.damage(5);
					
					pbuff = (DynamicPhysicsNode) a;
					pbuff.addForce(Vector3f.UNIT_Y.mult(1000));
				}
				
				if ( a == ascio.getPhysicsNode() && b.hasAncestor(floorNode)) {
					contact.getContactPosition(vbuff);
					//System.out.print(vbuff+"\t");
					vbuff = ascio.getWorldTranslation();
					//System.out.print(vbuff+"\t");
					vbuff = ascio.getLocalTranslation();
					//System.out.println(vbuff+"\t");
					ascio.addForce(Vector3f.UNIT_Y.mult(ascio.getMass()*50));
				} else if ( b == ascio.getPhysicsNode() && a.hasAncestor(floorNode)) {
					contact.getContactPosition(vbuff);
					//System.out.print(vbuff+"\t");
					vbuff = ascio.getWorldTranslation();
					//System.out.print(vbuff+"\t");
					vbuff = ascio.getLocalTranslation();
					//System.out.println(vbuff+"\t");
				}
				
				//System.out.println(a.getName()+"\t"+b.getName());
				//System.out.println(a.getClass()+"\t"+b.getClass());
				
				return false;
			}			
		};
		
		FrictionCallback friction = new FrictionCallback();
		friction.add((DynamicPhysicsNode) ascio.getPhysicsNode(), .5f, .5f);
//		friction.add((DynamicPhysicsNode) doodadNode.getChild(0), .5f, .5f);
		this.getPhysicsSpace().addToUpdateCallbacks(friction);
		
		this.getPhysicsSpace().addToUpdateCallbacks(new Callback());
		this.getPhysicsSpace().getContactCallbacks().add(contact);
		
		this.getPhysicsSpace().setDirectionalGravity(vbuff.set(0, -9.81f, 0));
		//this.getPhysicsSpace().setDirectionalGravity(vbuff.set(0, 0, 0));
		
		System.out.println("PHYSICS SET UP");
	}
	protected void finalTouch() {
		System.out.println("GIVING IT THE FINAL TOUCH");
				
		// TODO Auto-generated method stub
		rootNode.updateRenderState();
		
		System.out.println("FINAL TOUCH GIVEN");
	}

	@Override
	public void update(float time) {
		super.update(time);

		InputManager.get().update(time);
		
		timer2 += time;
		
//		input.update(time);
		
//		System.out.println(curve.getLength());
		
		updateKeys();		
		//updateText();
		updateFoes(time);
		
        if ( first_frame )
        {
            // drawing and calculating the first frame usually takes longer than the rest
            // to avoid a rushing simulation we reset the timer
            timer.reset();
            first_frame = false;
        }              
//        progress2 = curve.checkProgress( ascio.getPhysicsNode().getLocalTranslation());
        
        //if ( ascio.hasCollision(floorNode, false)) ascio.mp.setAllValues(true);		
		//else ascio.mp.setAllValues(false);
        
        if ( !ascio.hasCollision(floorNode, false)) timer2 += time; else timer2 = 0;
        
        if ( timer2 >= 10f ) {

        		ascio.setLocalTranslation(0, 3, 0);
        		ascio.getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
        		ascio.clearDynamics();
     
        	timer2 = 0;
        }
	}

	private void updateKeys() {
		// TODO Auto-generated method stub
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("exit", false)) {
			System.exit(0);
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("reset", false)) {
			// funky effects happening here, when you let go of F12, ascio snaps back to his old positions
			ascio.clearDynamics();
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("turnx+", true)) {
			//ascio.getPhysicsNode().addTorque(Vector3f.UNIT_X.mult(90));
			if ( curve.hasCollision(ascio.getPhysicsNode())) {
	//			progress += .25f;
	//			ascio.addForce( curve.getPointByLength(progress2+1).subtract(ascio.getLocalTranslation()).mult(150), Vector3f.UNIT_Z.divide(3));
				//((DynamicPhysicsNode) ascio.getPhysicsNode()).addForce(new Vector3f(0, 5000, 0));
				//ascio.clearForce();
				//System.out.println(ascio.getClass());
				//System.out.println(ascio.getPhysicsNode().getClass());
				//ascio.clearTorque();
        	}	
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("turnx-", true)) {
			//ascio.getPhysicsNode().addTorque(Vector3f.UNIT_X.mult(-90));
			if ( curve.hasCollision(ascio.getPhysicsNode())) {
		//		progress -= .25f;
		//		//ascio.setLocalTranslation(curve.getPointByLength(progress));
		//		ascio.addForce( curve.getPointByLength(progress2-1).subtract(ascio.getLocalTranslation()).mult(150), Vector3f.UNIT_Z.divide(3));
				//ascio.clearForce(); 
				//ascio.clearTorque();
			}	
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("turny+", false)) {
			//ascio.getPhysicsNode().addTorque(Vector3f.UNIT_Y.mult(90));
			ascio.attack();
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("turny-", false)) {
			//ascio.getPhysicsNode().addTorque(Vector3f.UNIT_Y.mult(-90));
			DynamicPhysicsNode dpn = this.getPhysicsSpace().createDynamicNode();
			rootNode.attachChild(dpn);
			dpn.setLocalTranslation(0, 3, -10);
			
			dpn.setIsCollidable(false);
			
			dpn.attachChild(new Box("", new Vector3f(), 1,1,5));
			dpn.generatePhysicsGeometry();
			dpn.setMass(10000000);
			dpn.setAffectedByGravity(false);
			dpn.clearDynamics();
			vbuff.set(0,0, 100000000000f);
			
			dpn.addForce(vbuff);
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("turnz+")) {
			vbuff.set(0, 150, 0);
			ascio.addTorque(vbuff);
			System.out.println("PLUS");
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("turnz-")) {
			vbuff.set(0, -150, 0);
			ascio.addTorque(vbuff);
			System.out.println("MINUS");
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("funky")) {
			vbuff.set(1, -999999);
			ascio.setLinearVelocity(vbuff);
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("physics_debug", false)) {
			physics_debug = !physics_debug;
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("music", false)) {
			try {
//				if (ascioTrack.isPlaying()) ascioTrack.stop(); else ascioTrack.play();
			} finally {			
			}
		}
	}
 	private void updateText() {		
 		if ( physics_debug ) {
 			text[8].print("DEBUG MODE");
 			ascio.getAngularVelocity(vbuff);
 			Util.round( vbuff );
 			text[7].print("Angular Velocity    : X:"+ vbuff.x +" Y:"+ vbuff.y +" Z:"+ vbuff.z );
 			ascio.getLinearVelocity(vbuff);
 			Util.round( vbuff );
 			text[6].print("Linear Velocity     : X:"+ vbuff.x +" Y:"+ vbuff.y +" Z:"+ vbuff.z );
 			ascio.getForce(vbuff);
 			Util.round( vbuff );
 			text[5].print("Force               : X:"+ vbuff.x +" Y:"+ vbuff.y +" Z:"+ vbuff.z );
 			ascio.getPhysicsNode().getLocalTranslation();
 			Util.round( vbuff );
 			text[4].print("Physics Location    : X:"+ vbuff.x +" Y:"+ vbuff.y +" Z:"+ vbuff.z );
 			ascio.getLocalTranslation();
 			Util.round( vbuff );
 			text[3].print("Node Location       : X:"+ vbuff.x +" Y:"+ vbuff.y +" Z:"+ vbuff.z );
 		}
 		/*
		text[11].print("Rotation Quaternion : X:"+XAR+" Y:"+YAR+" Z:"+ZAR+" W:"+WAR );
		text[10].print("Rotation Column 0   : X:"+XAc0+" Y:"+YAc0+" Z:"+ZAc0 );
		text[9].print("Rotation Column 1   : X:"+XAc1+" Y:"+YAc1+" Z:"+ZAc1 );
		text[8].print("Rotation Column 2   : X:"+XAc2+" Y:"+YAc2+" Z:"+ZAc2 );
		text[7].print("Camera");
		text[6].print("Location            : X:"+XCL+" Y:"+YCL+" Z:"+ZCL );
		text[5].print("Rotation Quaternion : X:"+XCR+" Y:"+YCR+" Z:"+ZCR+" W:"+WCR );
		text[4].print("Rotation Left       : X:"+XCc0+" Y:"+YCc0+" Z:"+ZCc0 );
		text[3].print("Rotation Up         : X:"+XCc1+" Y:"+YCc1+" Z:"+ZCc1 );
		text[2].print("Rotation Direction  : X:"+XCc2+" Y:"+YCc2+" Z:"+ZCc2 );
 		*/
		text[1].print("HEALTH: " + ascio.getHealth());
		text[0].print("FPS: " + timer.getFrameRate());
	
		// take care of the newsTicker
		if ( (news.length() != 0 && timer.getTimeInSeconds() - newsTime > .75f) ) {
			news.deleteCharAt(0);
			newsTime = timer.getTimeInSeconds();
		}
		if ( news.length() > 100 ) {
			newsTime -= .2f;
		}
		
	//	if ( Util.randomInt(1000) > 990 ) news.append(" Random bit of news ");
		
		newsTicker.print(news.toString());
	}
 	private void updateFoes(float time) {
 	}
	
 	@Override
	public void render(float time) {
		super.render(time);
		
		if (physics_debug) {
			PhysicsDebugger.drawPhysics(getPhysicsSpace(), DisplaySystem.getDisplaySystem().getRenderer());
			Debugger.drawBounds(rootNode, DisplaySystem.getDisplaySystem().getRenderer(), true);
		}
	}
 	
 	private class Callback implements PhysicsUpdateCallback {
		@Override
		public void beforeStep(PhysicsSpace space, float time) {
			// TODO Auto-generated method stub
			//ascio.clearTorque();
		}		
		@Override
		public void afterStep(PhysicsSpace space, float time) {
		//	ascio.limitForces();
//			space.collide(ascio, ascio);
			if ( ascio.hasCollision(floorNode, false)) {
				ascio.mp.setAllValues(true);		
			}
			else ascio.mp.setAllValues(false);
			vbuff = ascio.getLocalTranslation();
 			Util.round( vbuff );
 			text[4].print("Location    : X:"+ vbuff.x +" Y:"+ vbuff.y +" Z:"+ vbuff.z );
 			vbuff = cam.getLocation();
 			Util.round( vbuff );
 			text[5].print("Cam             "+ vbuff);
 			ascio.getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
 			
 			
		}
	}
}