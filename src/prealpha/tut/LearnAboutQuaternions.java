                     package prealpha.tut;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.*;

import com.jme.bounding.*;
import com.jme.image.Texture;
import com.jme.light.*;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.*;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.font3d.*;
import com.jmex.game.*;
import com.jmex.game.state.*;
import java.util.logging.Logger;
import com.jme.input.ChaseCamera;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.light.DirectionalLight;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.TexCoords;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.geom.BufferUtils;
import com.jme.util.geom.Debugger;

public class LearnAboutQuaternions extends BasicGameState{
    private static final Logger logger = Logger.getLogger(DebugGameState.class.getName());
    
    static Quaternion addX = new Quaternion(.01f,0,0,0); 
    static Quaternion addY = new Quaternion(0,.01f,0,0);
    static Quaternion addZ = new Quaternion(0,0,.01f,0);
    static Quaternion addW = new Quaternion(0,0,0,.01f);
    static Quaternion subX = new Quaternion(-.01f,0,0,0); 
    static Quaternion subY = new Quaternion(0,-.01f,0,0);
    static Quaternion subZ = new Quaternion(0,0,-.01f,0);
    static Quaternion subW = new Quaternion(0,0,0,-.01f);
    static Quaternion buff = new Quaternion();
    
    static float[] angles = new float[3];
    
    protected InputHandler input;
    protected WireframeState wireState;
    protected LightState lightState;
    protected LightState discoA;
    protected LightState discoB;
    protected LightState discoC;
    
    protected PointLight lightsA[];
    protected PointLight lightsB[];
    int switcherCount = FastMath.rand.nextInt(75);
    boolean switcher = false;
    
    protected byte rotateMode = 0;
    protected boolean lockW = true;
	protected byte letsDance = 3;
	protected byte letsDisco = 3;
	protected byte funkyColor = 3;
    
    protected static float speed = 1;
	
	Text[] info = new Text[15];
    
	Node xAxis;
	Node yAxis;
	Node zAxis;
	
	protected Node subject;
	protected Node specials[];
	protected Node model;
	protected Node discoBall;
	
    public LearnAboutQuaternions() {
    	this(true);
    }  
    public LearnAboutQuaternions(boolean handleInput) {
    	super("LearnAboutQuaternions");
    	rootNode = new Node("RootNode");
    	
    	// Create a wirestate to toggle on and off. Starts disabled with default
        // width of 1 pixel.
    	setupText();
    	setupWireState();
    	setupZBuffer();
    	setupAxes();
    	setupSubject();
    	setupLight();
    	setupInput(handleInput);
    	
        // Finish up
        rootNode.updateRenderState();
        rootNode.updateWorldBound();
        rootNode.updateGeometricState(0.0f, true);
    }

    private void setupText() {
    	info[0] = Text.createDefaultTextLabel("infoTextLine "+0);
    	rootNode.attachChild(info[0]);
    	info[0].setLocalTranslation(0, info.length*12, 0);
    	
    	for ( int i=1; i < info.length;i++) {
    		info[i] = Text.createDefaultTextLabel("infoTextLine "+i);
    		rootNode.attachChild(info[i]);
    		info[i].setLocalTranslation(0, (info.length-1-i)*12, 0);
    	}	
    }
	private void setupWireState() {
		// TODO Auto-generated method stub
    	wireState = DisplaySystem.getDisplaySystem().getRenderer().createWireframeState();
    	wireState.setEnabled(false);
    	rootNode.setRenderState(wireState);
	}
    private void setupZBuffer() {
		// TODO Auto-generated method stub
        // Create ZBuffer for depth
        ZBufferState zbs = DisplaySystem.getDisplaySystem().getRenderer()
                .createZBufferState();
        zbs.setEnabled(true);
        zbs.setFunction(ZBufferState.TestFunction.EqualTo);
        rootNode.setRenderState(zbs);
	}
    private void setupAxes() {   	
    	Box xBox = new Box("x-Axis", Vector3f.ZERO, 10000, .1f, .1f);
    	Arrow xArrow = new Arrow("xArrow"); 
    	xArrow.setLocalTranslation(30, 0, 0);
    	xArrow.setLocalScale(.3f);
    	
    	MaterialState xState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
    	xState.setAmbient(new ColorRGBA(1,0,0,1));
    	xAxis = new Node();
    	xAxis.attachChild(xBox);
    	xAxis.attachChild(xArrow);
    	xAxis.setRenderState(xState);
    	xAxis.setModelBound(new BoundingBox());
    	xAxis.updateModelBound();

    	Box yBox = new Box("y-Axis", Vector3f.ZERO, .1f, 10000f, .1f);
    	Arrow yArrow = new Arrow("xArrow"); 
    	yArrow.getLocalRotation().fromAngleAxis(90*FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
    	yArrow.setLocalTranslation(0, 30, 0);
    	yArrow.setLocalScale(.3f);
    	 	
    	MaterialState yState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
    	yState.setAmbient(new ColorRGBA(0,1,0,1));
    	yAxis = new Node();
    	yAxis.attachChild(yBox);
    	yAxis.attachChild(yArrow);
    	yAxis.setRenderState(yState);
    	yAxis.setModelBound(new BoundingBox());
    	yAxis.updateModelBound();
    	
    	
    	Box zBox = new Box("z-Axis", Vector3f.ZERO, .1f, .1f, 10000f);	
    	Arrow zArrow = new Arrow("xArrow"); 
    	zArrow.getLocalRotation().fromAngleAxis(-90*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
    	zArrow.setLocalTranslation(0, 0, 30);
    	zArrow.setLocalScale(.3f);
    	   	
    	MaterialState zState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
    	zState.setAmbient(new ColorRGBA(0,0,1,1));
    	zAxis = new Node();
    	zAxis.attachChild(zBox);
    	zAxis.attachChild(zArrow);
    	zAxis.setRenderState(zState);
    	zAxis.setModelBound(new BoundingBox());
    	zAxis.updateModelBound();
    	 	
    	rootNode.attachChild(xAxis);
    	rootNode.attachChild(yAxis);
    	rootNode.attachChild(zAxis);
    }
   	private void setupSubject() {
		// TODO Auto-generated method stub
		subject = new Node();
		rootNode.attachChild(subject);
		
	/*	try {
			File file = new File("data/model/ascio.jme");
			BinaryImporter importer = new BinaryImporter();
			model = (Node)importer.load(file.toURI().toURL().openStream());
			model.setModelBound(new BoundingBox());
			model.updateModelBound();
			model.setLocalScale(100f);
			//model.setLocalTranslation(-1.5f, -1, .3f);
			subject.attachChild(model);
		} catch (IOException e) {*/
			Arrow arrow = new Arrow("rotate me");
			arrow.setLocalScale(1);
			
			model = new Node();
			model.attachChild(arrow);
			model.setModelBound(new BoundingBox());
			model.updateModelBound();
		//}	
		subject.attachChild(model);
		
		specials = new Node[50];
		for ( int i = 0; i < specials.length; i++ ) {
			specials[i] = new Node(i+"");
			specials[i].attachChild(new Box(1+"Box", new Vector3f(rand()*FastMath.rand.nextInt(500),rand()*FastMath.rand.nextInt(500),rand()*FastMath.rand.nextInt(500)),5,20,5));
		}	
	}
    private void setupLight() {
		// TODO Auto-generated method stub
    	// Lighting
        /** Set up a basic, default light. */
        PointLight light = new PointLight();
        light.setDiffuse( new ColorRGBA( 0.75f, 0.75f, 0.75f, 0.75f ) );
        light.setAmbient( new ColorRGBA( 0.5f, 0.5f, 0.5f, 1.0f ) );
        light.setLocation( new Vector3f( 100, 100, 100 ) );
        light.setEnabled( true );
        
    	DirectionalLight sun = new DirectionalLight();
    	sun.setAmbient(new ColorRGBA(1,1,1,1));
    	sun.setDiffuse(new ColorRGBA(1,1,1,1));
    	sun.setEnabled(true);
    	   	
        /** Attach the light to a lightState and the lightState to rootNode. */
        lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled( true );
        lightState.attach( light );
        lightState.attach(sun);
        rootNode.setRenderState( lightState );
        
        lightsA = new PointLight[8];
		for ( int i=0; i < lightsA.length; i++) {
			lightsA[i] = new PointLight();
			lightsA[i].setAmbient(ColorRGBA.randomColor());
			lightsA[i].setDiffuse(ColorRGBA.randomColor());
			lightsA[i].setEnabled(true);
		}
		lightsA[0].setLocation(new Vector3f(50,50,50));
		lightsA[1].setLocation(new Vector3f(50,50,-50));
		lightsA[2].setLocation(new Vector3f(50,-50,50));
		lightsA[3].setLocation(new Vector3f(50,-50,-50));
		lightsA[4].setLocation(new Vector3f(-50,50,50));
		lightsA[5].setLocation(new Vector3f(-50,50,-50));
		lightsA[6].setLocation(new Vector3f(-50,-50,50));
		lightsA[7].setLocation(new Vector3f(-50,-50,-50));
        
		discoA = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
		discoA.setEnabled(true);
		for (int i=0;i<lightsA.length;i++) discoA.attach(lightsA[0]);
		
        lightsB = new PointLight[8];
		for ( int i=0; i < lightsA.length; i++) {
			lightsB[i] = new PointLight();
			lightsB[i].setAmbient(ColorRGBA.randomColor());
			lightsB[i].setDiffuse(ColorRGBA.randomColor());
			lightsB[i].setEnabled(true);
		}
		lightsB[0].setLocation(new Vector3f(50,50,50));
		lightsB[1].setLocation(new Vector3f(50,50,-50));
		lightsB[2].setLocation(new Vector3f(50,-50,50));
		lightsB[3].setLocation(new Vector3f(50,-50,-50));
		lightsB[4].setLocation(new Vector3f(-50,50,50));
		lightsB[5].setLocation(new Vector3f(-50,50,-50));
		lightsB[6].setLocation(new Vector3f(-50,-50,50));
		lightsB[7].setLocation(new Vector3f(-50,-50,-50));
        
		discoB = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
		discoB.setEnabled(true);
		for (int i=0;i<lightsB.length;i++) discoB.attach(lightsB[0]);
		
		PointLight discoLight = new PointLight();
		discoLight.setAmbient(ColorRGBA.lightGray);
		discoLight.setDiffuse(ColorRGBA.white);
		discoLight.setEnabled(true);
		MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		state.setAmbient(new ColorRGBA(1,1,1,0));
		state.setDiffuse(new ColorRGBA(1,1,1,0));
		state.setShininess(1);
		discoBall = new Node();
		discoBall.setRenderState(state);
		discoBall.attachChild(new Sphere("discoBall", new Vector3f(0,0,0), 40, 40, 15));
		discoC = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
		discoC.setEnabled(true);
		discoC.attach(discoLight);
		discoBall.setRenderState(discoC);
	}
    private void setupInput(boolean handleInput) {
		// TODO Auto-generated method stub
    	// Initial InputHandler
        if (handleInput) {
	        input = new ChaseCamera(DisplaySystem.getDisplaySystem().getRenderer().getCamera(), rootNode);
	        input.setActionSpeed(0.3f);
	        setupKeyBindings();
        }
	}
    private void setupKeyBindings() {
        /** Assign key C to action "camera_out". */
        KeyBindingManager.getKeyBindingManager().set("camera_info", KeyInput.KEY_C);
        KeyBindingManager.getKeyBindingManager().set("exit", KeyInput.KEY_ESCAPE);
        KeyBindingManager.getKeyBindingManager().set("change_sign", KeyInput.KEY_F2);
        KeyBindingManager.getKeyBindingManager().set("setW_to0", KeyInput.KEY_F3);
        KeyBindingManager.getKeyBindingManager().set("setW_to.5f", KeyInput.KEY_F4);
        KeyBindingManager.getKeyBindingManager().set("setW_to1", KeyInput.KEY_F5);
        KeyBindingManager.getKeyBindingManager().set("toggle_lockW", KeyInput.KEY_F6);
        KeyBindingManager.getKeyBindingManager().set("randomRotation", KeyInput.KEY_F7);
        KeyBindingManager.getKeyBindingManager().set("printRotation", KeyInput.KEY_F8);
        KeyBindingManager.getKeyBindingManager().set("8", KeyInput.KEY_F8);
        KeyBindingManager.getKeyBindingManager().set("9", KeyInput.KEY_F10);
        KeyBindingManager.getKeyBindingManager().set("toggle_rotate_mode", KeyInput.KEY_F11);
        KeyBindingManager.getKeyBindingManager().set("reset_subject", KeyInput.KEY_F12);
        //change movementspeed
        KeyBindingManager.getKeyBindingManager().set("add_speed", KeyInput.KEY_PGUP);
        KeyBindingManager.getKeyBindingManager().set("red_speed", KeyInput.KEY_PGDN);
        //KeyBindings for manipulating the subject
        //change position
        KeyBindingManager.getKeyBindingManager().set("add_pos_x", KeyInput.KEY_W);
        KeyBindingManager.getKeyBindingManager().set("add_pos_y", KeyInput.KEY_Q);
        KeyBindingManager.getKeyBindingManager().set("add_pos_z", KeyInput.KEY_A);
        KeyBindingManager.getKeyBindingManager().set("reduce_pos_x", KeyInput.KEY_S);
        KeyBindingManager.getKeyBindingManager().set("reduce_pos_y", KeyInput.KEY_E);
        KeyBindingManager.getKeyBindingManager().set("reduce_pos_z", KeyInput.KEY_D);
        //change rotation
        KeyBindingManager.getKeyBindingManager().set("add_quat_x", KeyInput.KEY_R);
        KeyBindingManager.getKeyBindingManager().set("add_quat_y", KeyInput.KEY_T);
        KeyBindingManager.getKeyBindingManager().set("add_quat_z", KeyInput.KEY_Z);
        KeyBindingManager.getKeyBindingManager().set("add_quat_w", KeyInput.KEY_U);
        KeyBindingManager.getKeyBindingManager().set("reduce_quat_x", KeyInput.KEY_F);
        KeyBindingManager.getKeyBindingManager().set("reduce_quat_y", KeyInput.KEY_G);
        KeyBindingManager.getKeyBindingManager().set("reduce_quat_z", KeyInput.KEY_H);
        KeyBindingManager.getKeyBindingManager().set("reduce_quat_w", KeyInput.KEY_J);
        //funny things
        KeyBindingManager.getKeyBindingManager().set("dance", KeyInput.KEY_1);
        KeyBindingManager.getKeyBindingManager().set("disco_light", KeyInput.KEY_2);
        KeyBindingManager.getKeyBindingManager().set("funky_colors", KeyInput.KEY_3);
    }

    public void update(float tpf) {
    	super.update(tpf);
    	
        // Update the InputHandler    
    	checkSystemInput(tpf);
    	checkSubjectInput();
    	updateText();      
        
    	//subject.getLocalRotation().fromAngles(angles);
    	angles[0] %= 2*Math.PI;
    	angles[1] %= 2*Math.PI;
    	angles[2] %= 2*Math.PI;
    	
    	// W is locked to [-1,1]
    	if (lockW) {
    		if (rotateMode == 0) {
    			if (subject.getLocalRotation().w < -1) subject.getLocalRotation().w = -1;
        		if (subject.getLocalRotation().w > 1) subject.getLocalRotation().w = 1;
    		} else if (rotateMode == 1) {
    			if (subject.getLocalRotation().w < -1.05) subject.getLocalRotation().w = -1;
        		if (subject.getLocalRotation().w > 1.05) subject.getLocalRotation().w = 1;
    		} else if (rotateMode == 2) {
    			if (subject.getLocalRotation().w < -1) subject.getLocalRotation().w = -1;
        		if (subject.getLocalRotation().w > 1) subject.getLocalRotation().w = 1;
    		}
    	}
    	dance();
    	disco();
    	funkOut();
    	
        // Update the geometric state of the rootNode
        rootNode.updateGeometricState(tpf, true);
        rootNode.updateWorldVectors();
        rootNode.updateWorldData(tpf);
    }

	private void checkSystemInput(float tpf) {
		// TODO Auto-generated method stub
    	input.update(tpf);

    	/** If camera_out is a valid command (via key C), show camera location. */
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("camera_info", false)) {
    		logger.info("Camera at: " + DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation() );
    	}
    	// get outta here
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit", false)) System.exit(0);
    	// toggle help display status
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("change_sign", false)) {
    		subject.getLocalRotation().multLocal(-1);
    	}
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("setW_to0", false)) {
    		subject.getLocalRotation().w = 0;
    	}
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("setW_to.5f", false)) {
    		subject.getLocalRotation().w = .5f;
    	}
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("setW_to1", false)) {
    		subject.getLocalRotation().w = 1;
    	}
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("toggle_lockW", false)) lockW = !lockW;
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("randomRotation", false)) {
    		subject.setLocalRotation(new Quaternion(rand(),rand(),rand(),rand()));
    		subject.getLocalRotation().toAngles(angles);
    	}
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("printRotation", false)) {
    		Quaternion temp1 = subject.getLocalRotation();
    		float[] temp2 = new float[3];
    		subject.getLocalRotation().toAngles(temp2);
    		System.out.println("Rotation Quaternion");
    		System.out.println("X: "+temp1.x+"   Y: "+temp1.y+"   Z: "+temp1.z+"   W: "+temp1.w);
    		System.out.println("Rotation Angles");
    		System.out.println("X-Axis : "+temp2[0]+"   Y-Axis : "+temp2[1]+"   Z-Axis : "+temp2[2]);
    	}
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("8", false)) {}
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("9", false)) {}
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("10", false)) {}    	
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("toggle_rotate_mode", false)) {
    		rotateMode++;
    		rotateMode %= 3;
    	}
    	if (KeyBindingManager.getKeyBindingManager().isValidCommand("reset_subject", false)) {
    		subject.setLocalRotation(new Quaternion());
    		model.setLocalTranslation(Vector3f.ZERO);
    		angles = new float[3];
    		speed = 100;
    		letsDance = 2;
    		letsDisco = 2;
    		funkyColor = 2;
    	}     
	}
    private void checkSubjectInput() {
    		//check manipulation of subject
    		//change movementspeed
    		if (KeyBindingManager.getKeyBindingManager().isValidCommand("add_speed", true)) speed+=.01f;
    		if (KeyBindingManager.getKeyBindingManager().isValidCommand("red_speed", true)) {
    			if ( speed <= 0 ) speed = 0; else speed-=.01f;
    		}
 	        //alter pos
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("add_pos_x", true)) {
 	            model.setLocalTranslation(model.getLocalTranslation().add(.1f*speed, 0, 0));
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("add_pos_y", true)) {
 	        	model.setLocalTranslation(model.getLocalTranslation().add(0, .1f*speed, 0));
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("add_pos_z", true)) {
 	        	model.setLocalTranslation(model.getLocalTranslation().add(0, 0, .1f*speed));
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("add_pos_z", true)) {
 	        	model.setLocalTranslation(model.getLocalTranslation().add(0, 0, .1f*speed));
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("reduce_pos_x", true)) {
 	        	model.setLocalTranslation(model.getLocalTranslation().add(-.1f*speed, 0, 0));
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("reduce_pos_y", true)) {
 	        	model.setLocalTranslation(model.getLocalTranslation().add(0, 0-.1f*speed, 0));
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("reduce_pos_z", true)) {
 	        	model.setLocalTranslation(model.getLocalTranslation().add(0, 0, -.1f*speed));
 	        }
 	        //change rotation 
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("add_quat_x", true)) {
 	        	if (rotateMode == 0) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(addX.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	} else if (rotateMode == 1) {
 	        		angles[0]+=.5f*FastMath.DEG_TO_RAD*speed;
 	        		buff.fromAngles(angles);
 	        		subject.setLocalRotation(buff);
 	        	} else if (rotateMode == 2) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(addX.mult(speed)));
 	        		subject.setLocalRotation(subject.getWorldRotation().add(subW.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	}
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("add_quat_y", true)) {
 	        	if (rotateMode == 0) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(addY.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	} else if (rotateMode == 1) {
 	        		angles[1]+=.5f*FastMath.DEG_TO_RAD*speed;
 	        		buff.fromAngles(angles);
 	        		subject.setLocalRotation(buff);
 	        	} else if (rotateMode == 2) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(addY.mult(speed)));
 	        		subject.setLocalRotation(subject.getWorldRotation().add(subW.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	}
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("add_quat_z", true)) {
 	        	if (rotateMode == 0) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(addZ.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	} else if (rotateMode == 1) {
 	        		angles[2]+=.5f*FastMath.DEG_TO_RAD*speed;
 	        		buff.fromAngles(angles);
 	        		subject.setLocalRotation(buff);
 	        	} else if (rotateMode == 2) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(addZ.mult(speed)));
 	        		subject.setLocalRotation(subject.getWorldRotation().add(subW.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	}
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("add_quat_w", true)) {
 	        	if (rotateMode == 0) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(addX.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	}
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("reduce_quat_x", true)) {
 	        	if (rotateMode == 0) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(subX.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	} else if (rotateMode == 1) {
 	        		angles[0]-=.5f*FastMath.DEG_TO_RAD*speed;
 	        		buff.fromAngles(angles);
 	        		subject.setLocalRotation(buff);
 	        	} else if (rotateMode == 2) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(subX.mult(speed)));
 	        		subject.setLocalRotation(subject.getWorldRotation().add(addW.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	}
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("reduce_quat_y", true)) {
 	        	if (rotateMode == 0) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(subY.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	} else if (rotateMode == 1) {
 	        		angles[1]-=.5f*FastMath.DEG_TO_RAD*speed;
 	        		buff.fromAngles(angles);
 	        		subject.setLocalRotation(buff);
 	        	} else if (rotateMode == 2) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(subY.mult(speed)));
 	        		subject.setLocalRotation(subject.getWorldRotation().add(addW.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	}
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("reduce_quat_z", true)) {
 	        	if (rotateMode == 0) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(subZ.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	} else if (rotateMode == 1) {
 	        		angles[2]-=.5f*FastMath.DEG_TO_RAD*speed;
 	        		buff.fromAngles(angles);
 	        		subject.setLocalRotation(buff);
 	        	} else if (rotateMode == 2) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(subZ.mult(speed)));
 	        		subject.setLocalRotation(subject.getWorldRotation().add(addW.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	}
 	        }
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("reduce_quat_w", true)) {
 	        	if (rotateMode == 0) {
 	        		subject.setLocalRotation(subject.getWorldRotation().add(subW.mult(speed)));
 	        		subject.getLocalRotation().toAngles(angles);
 	        	}
 	        }
 	        //funky
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("dance", false)) letsDance = (byte) (letsDance == 1 ? 2 : 0);
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("disco_light", false)) letsDisco = (byte) (letsDisco == 1 ? 2 : 0);
 	        if (KeyBindingManager.getKeyBindingManager().isValidCommand("funky_colors", false)) funkyColor = (byte) (funkyColor == 1 ? 2 : 0);
    }
	private void updateText() {
		String[] lines = new String[8];

		lines[0] =	"Press F1 for help";
		lines[1] =  "";
		lines[2] = 	subject.getLocalRotation().w <= -1 || 1 <= subject.getLocalRotation().w ? "Notice that as long as W element of [-1,1] nothing happens!" : "";
		if (rotateMode == 0)  {
			lines[3] = 	"rotate Quaternion  " + (lockW ? "W locked " : "");
		} else if (rotateMode == 1) {
			lines[3] = 	"rotate Angles  " + (lockW ? "W locked " : "");
		} else if (rotateMode == 2) {
			lines[3] = 	"rotate Experimentally  " + (lockW ? "W locked " : "");
		}
		
		lines[4] = 	"Turn/Movement-Speed "+ (int) (speed*100) + "%";
		lines[5] = "Quaternion " +
		" X" + round(subject.getLocalRotation().x)+" "+
		" Y" + round(subject.getLocalRotation().y)+" "+
		" Z" + round(subject.getLocalRotation().z)+" "+
		" W" + round(subject.getLocalRotation().w)+" ";
		lines[6] = 	"Angles     " +
		" X " + round((angles[0]*FastMath.RAD_TO_DEG)) +
		" Y " + round((angles[1]*FastMath.RAD_TO_DEG)) +
		" Z " + round((angles[2]*FastMath.RAD_TO_DEG));
		lines[7] = "Position   " +
		" X" + round(model.getLocalTranslation().x) +
		" Y" + round(model.getLocalTranslation().y) +
		" Z" + round(model.getLocalTranslation().z);

		for (int i=0; i<8; i++) info[i].print(lines[i]);
	}
   
	private void dance() {
		// TODO Auto-generated method stub
		switch (letsDance) {
		case 0 : 
			for ( int i=0; i < specials.length; i++) {
				rootNode.attachChild(specials[i]);
			}
			letsDance++;
	    break;	
		case 1 :
			speed = .01f;
			
			subject.getLocalRotation().x += rand()*(float)speed;
			subject.getLocalRotation().y += rand()*(float)speed;
			subject.getLocalRotation().z += rand()*(float)speed;
			subject.getLocalRotation().w = FastMath.rand.nextFloat()*(speed/100);
			subject.getLocalRotation().toAngles(angles);

			for ( int i = 0; i < specials.length; i++ ) {
				specials[i].getLocalRotation().x += rand()*(float)speed;
				specials[i].getLocalRotation().y += rand()*(float)speed;
				specials[i].getLocalRotation().z += rand()*(float)speed;
				specials[i].getLocalRotation().w = FastMath.rand.nextFloat()*(float)speed;
			}
		break;
		case 2 :
			for ( Node n : specials ) rootNode.detachChild(n);
			
			speed = 1;
	    	
	    	letsDance++;
		break;	
		default :
		break;	
		}
	}
	private void disco() {
		switch (letsDisco) {
		case 0 : 
			rootNode.attachChild(discoBall);
			rootNode.detachChild(subject);
			rootNode.updateRenderState();
			
			letsDisco++;
		break;	
		case 1 :
			if (switcherCount == 0) {
				switcherCount = FastMath.rand.nextInt(75);
				if (switcher) {
					rootNode.setRenderState(discoA);
					MaterialState state = (MaterialState) discoBall.getRenderState(RenderState.RS_MATERIAL);
					state.setAmbient(new ColorRGBA(Math.abs(rand()),Math.abs(rand()),Math.abs(rand()),Math.abs(rand())));
					switcher = !switcher;
				} else {
					rootNode.setRenderState(discoB);
					switcher = !switcher;
				}
			}
			switcherCount--;
			
			rootNode.updateRenderState();
		break;
		case 2 :
			rootNode.detachChild(discoBall);
			rootNode.attachChild(subject);
	        rootNode.setRenderState( lightState );
	        
			rootNode.updateRenderState();
	    	
	    	letsDisco++;
			break;	
		default :
		break;	
		}
	}
    private void funkOut() {
		switch (funkyColor) {
		case 0 : 
			for ( int i=0; i < specials.length; i++) {
				MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				state.setAmbient(ColorRGBA.randomColor());
				state.setDiffuse(ColorRGBA.randomColor());
				specials[i].setRenderState(state);
			}
			rootNode.updateRenderState();
			
			funkyColor++;
		break;	
		case 1 :
		break;
		case 2 :
	    	
	    	funkyColor++;
			break;	
		default :
		break;	
		}
	}
    
    public LightState getLightState() {
    	return lightState;
    }
    
    protected void cameraPerspective() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        Camera cam = display.getRenderer().getCamera();
        cam.setFrustumPerspective(45.0f, (float) display.getWidth()
                / (float) display.getHeight(), 1, 1000);
        cam.setParallelProjection(false);
        cam.update();
    }

    protected void cameraParallel() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        Camera cam = display.getRenderer().getCamera();
        cam.setParallelProjection(true);
        float aspect = (float) display.getWidth() / display.getHeight();
        cam.setFrustum(-100.0f, 1000.0f, -50.0f * aspect, 50.0f * aspect,
                -50.0f, 50.0f);
        cam.update();
    }

    public void render(float tpf) {
    	super.render(tpf);
    	
        // Render the rootNode
        DisplaySystem.getDisplaySystem().getRenderer().draw(rootNode);
    }

    public void cleanup() {
    }
 
    // returns random float [-1,1]
    private float rand() {
    	return FastMath.rand.nextBoolean() ? FastMath.rand.nextFloat() : FastMath.rand.nextFloat()*-1;
    }

    private float round(float f) {
    	double d = f*10000;
    	Math.round(d);
    	long l = (long) d;
    	return (float) l/10000;
    }
    
    public static void main(String[] args) throws Exception {
		StandardGame app = new StandardGame("app");
		if (GameSettingsPanel.prompt(app.getSettings())) {
			app.start();
	
			final LearnAboutQuaternions state = new LearnAboutQuaternions();
			GameStateManager.getInstance().attachChild(state);
	
			state.rootNode.updateGeometricState(0, true);
			state.rootNode.updateRenderState();
			GameStateManager.getInstance().activateAllChildren();
		}
	}
}

class Arrow extends TriMesh {
	private static final long serialVersionUID = 9147208810053682009L;
	
	public Arrow(String name) {
		super(name);
        // Vertex positions for the mesh
        Vector3f[] vertexes= {
        		//leftarrow
            new Vector3f(-15  ,-1,-1),
            new Vector3f(  5,-1,-1),
            new Vector3f(  5, 1,-1),
            new Vector3f(-15  , 1,-1),
            new Vector3f(  5,-5,-1),
            new Vector3f( 15  , 0,-1),
            new Vector3f(  5, 5,-1),
            	//rightarrow
            new Vector3f(-15  ,-1,1),
            new Vector3f(  5,-1,1),
            new Vector3f( 5, 1,1),
            new Vector3f(-15  , 1,1),
            new Vector3f(  5,-5,1),
            new Vector3f( 15  , 0,1),
            new Vector3f(  5, 5,1)
        };

        // Normal directions for each vertex position
        Vector3f[] normals={
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1)
        };

        // Color for each vertex position
        ColorRGBA[] colors={
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(0,1,0,1),
            new ColorRGBA(0,1,0,1),
            new ColorRGBA(0,0,1,1),
            new ColorRGBA(0,0,1,1),
            new ColorRGBA(1,1,1,1),
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(0,1,0,1),
            new ColorRGBA(0,1,0,1),
            new ColorRGBA(0,0,1,1),
            new ColorRGBA(0,0,1,1),
            new ColorRGBA(1,1,1,1)
        };

        // Texture Coordinates for each position
        Vector2f[] texCoords={
            new Vector2f(0,0),
            new Vector2f(1,0),
            new Vector2f(0,1),
            new Vector2f(1,1),
            new Vector2f(0,1),
            new Vector2f(1,0),
            new Vector2f(1,1),
            new Vector2f(0,0),
            new Vector2f(1,0),
            new Vector2f(0,1),
            new Vector2f(1,1),
            new Vector2f(0,1),
            new Vector2f(1,0),
            new Vector2f(1,1)
        };

        // The indexes of Vertex/Normal/Color/TexCoord sets.  Every 3 makes a triangle.
        int[] indexes={
        		//left arrow
            0,1,2,0,2,3,4,5,6,
            	//right arrow
            7,8,9,7,9,10,11,12,13,
            	//top/bottom
            0,1,8,0,7,8,3,2,9,3,9,10,
            	//back end
            0,3,7,3,7,10,
            	// arrowhead back
            2,6,9,6,9,13,1,4,8,4,8,11,
            	//arrowhead front
            4,5,12,4,11,12,5,6,13,5,12,13
        };

        // Feed the information to the TriMesh
      this.reconstruct(BufferUtils.createFloatBuffer(vertexes), BufferUtils.createFloatBuffer(normals),
                BufferUtils.createFloatBuffer(colors), TexCoords.makeNew(texCoords), BufferUtils.createIntBuffer(indexes));
}
}