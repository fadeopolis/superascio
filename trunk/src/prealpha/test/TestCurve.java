package prealpha.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import prealpha.curve.*;
import prealpha.state.PAState;
import prealpha.util.Util;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.curve.BezierCurve;
import com.jme.curve.Curve;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.MaterialState.ColorMaterial;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.util.states.PhysicsGameState;

/**
 * <code>TestBezierCurve</code>
 * @author Mark Powell
 * @version $Id: TestBezierCurve.java,v 1.19 2006/05/11 19:39:48 nca Exp $
 */
public class TestCurve extends DebugGameState {
	
  private Vector3f up = new Vector3f(0, 1, 0);

  PhysicsSpace space;
  
  Curve curve1;
  Curve curve2;
  
  prealpha.curve.Curve foo;
  prealpha.curve.Curve bar;
  
  TriMesh mover1;
  TriMesh mover2;
  
  CurveController cc;
  CurveController cc2;
  
  boolean displayCurve1 = true;
  boolean displayCurve2 = false;
  
  List<Vector3f> v;
  
  float timer = 0;
  
  public static void main(String[] args) throws InterruptedException {
	StandardGame app = new StandardGame("app");
	
	//if ( true ) {
	if (GameSettingsPanel.prompt(app.getSettings())) {	
		app.start();
		
		GameTaskQueueManager.getManager().update(new Callable<Void>(){
			public Void call() throws Exception {
				PhysicsGameState physics = new PhysicsGameState("physics");
				GameStateManager.getInstance().attachChild(physics);
				
				DebugGameState debug = new TestCurve( physics.getPhysicsSpace() );	// Create our game state
				GameStateManager.getInstance().attachChild(debug);	// Attach it to the GameStateManager
							
				GameStateManager.getInstance().activateAllChildren();	
				
				return null;
			}
	    });
	}
  }

  public TestCurve( PhysicsSpace space ) {
	  this(space, true);
  }
  /* (non-Javadoc)
   * @see com.jme.app.SimpleGame#initGame()
   */
  public TestCurve( PhysicsSpace space, boolean handleInput ) {
	super(handleInput);
	  
	v = new LinkedList<Vector3f>();
	
	this.space = space;
	
    DisplaySystem.getDisplaySystem().setTitle("Curve Test");

    ZBufferState buf = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
    buf.setEnabled(true);
    buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
    //rootNode.setRenderState(buf);
    
    int numberOfPoints = 11;
    float tension = .5f;
    float boxSize = .25f;
    float controllerSpeed = 1.f;
    int pointRange = 15;
    int precision = 5;
    
    //create control Points
    Vector3f[] points = new Vector3f[numberOfPoints];
    /*
    points[0] = new Vector3f(0,0,0);
    points[1] = new Vector3f(1,0,0);
    points[2] = new Vector3f(2,0,0);
    points[3] = new Vector3f(3,0,0);
    points[4] = new Vector3f(4,0,0);
    points[5] = new Vector3f(5,0,0);
    points[6] = new Vector3f(6,0,0);
    points[7] = new Vector3f(7,0,0);
    points[8] = new Vector3f(8,0,0);
    points[9] = new Vector3f(9,0,0);
    points[10] = new Vector3f(10,0,0);
    */
    for ( int i = 0; i < numberOfPoints; i++) {
    	points[i] = new Vector3f(  Util.randomInt(pointRange, true),  Util.randomInt(pointRange, true),  Util.randomInt(pointRange, true));
    }
    /*
    points[0] = points[1].add(points[1].subtract(points[2]));
    int j = points.length -1;
	points[j] = points[j-1].add(points[j-1].subtract(points[j-2]));
    */
    ColorRGBA[] colors = new ColorRGBA[numberOfPoints];
    for ( int i = 0; i < numberOfPoints; i++) {
    	colors[i] = ColorRGBA.randomColor();
    }
       
    Sphere[] controlPoints = new Sphere[numberOfPoints];
   // Box[] controlPoints = new Box[numberOfPoints];
    for ( int i = 0; i < numberOfPoints; i++) {
    	//controlPoints[i] = new Box(" ControlBox " + i, new Vector3f(points[i]), boxSize*((i/2)+1), boxSize*((i/2)+1), boxSize*((i/2)+1));
    	controlPoints[i] = new Sphere(" ControlBox " + i, new Vector3f(points[i]), 15, 15, (float) boxSize*((((float)i)/7)+1));
    	controlPoints[i].setModelBound(new BoundingSphere());
    	controlPoints[i].updateModelBound();
    	//controlPoints[i].setLocalTranslation(new Vector3f(points[i]));
        rootNode.attachChild(controlPoints[i]);
    }
    foo =  new RectifiedCurve(new CatmullRomCurve("Curve", points, tension), precision);
    curve1 = new CurveWrapper( foo );
   // System.out.println( ((CurveWrapper) curve1).getNumberOfSamplingPoints() + "\t" + ((CurveWrapper) curve1).getLength() );
    curve1.setSteps(500);
    curve1.setColorBuffer(BufferUtils.createFloatBuffer(colors));
    rootNode.attachChild(curve1);
    
    Vector3f min = new Vector3f( -0.1f, -0.1f, -0.1f);
    Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);
    
    mover1 = new Box("Controlled Box", min.mult(5), max.mult(5));
    mover1.setModelBound(new BoundingSphere());
    mover1.updateModelBound();
    mover1.setLocalTranslation(new Vector3f(points[0]));
    rootNode.attachChild(mover1);
    
    cc = new CurveController(foo, mover1);
    mover1.addController(cc);
    cc.setRepeatType(Controller.RT_CYCLE);
    cc.setUpVector(up);
    cc.setSpeed(controllerSpeed);
    
    for ( int i = 0; i < numberOfPoints; i++) {
 //   	points[i] = new Vector3f(  Util.randomInt(pointRange, true),  Util.randomInt(pointRange, true),  Util.randomInt(pointRange, true));
    }
    
    bar =  new CatmullRomCurve("Curve", points);
    //curve2 = new CurveWrapper( new RectifiedCurve("Curve", points));
    curve2 = new CurveWrapper(bar);
    curve2.setSteps(500);
    curve2.setColorBuffer(BufferUtils.createFloatBuffer(colors));
    rootNode.attachChild(curve2);
    
    foo.setSuccessor(bar);
    foo.setPredecessor(bar);
    bar.setSuccessor(foo);
    bar.setPredecessor(foo);
    
    mover2 = new Box("Controlled Box", min.mult(5), max.mult(5));
    mover2.setModelBound(new BoundingSphere());
    mover2.updateModelBound();
    mover2.setLocalTranslation(new Vector3f(points[0]));
   // rootNode.attachChild(mover2);
    
    cc2 = new CurveController(bar, mover2);
    mover2.addController(cc2);
    cc2.setRepeatType(Controller.RT_CYCLE);
    cc2.setUpVector(up);
    cc2.setSpeed(controllerSpeed);
 
    //texture the moving boxes
    TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture(
        TextureManager.loadTexture(
        TestCurve.class.getClassLoader().getResource(
        "jmetest/data/images/Monkey.jpg"),
        Texture.MinificationFilter.BilinearNearestMipMap,
        Texture.MagnificationFilter.Bilinear));
    mover1.setRenderState(ts);
    mover2.setRenderState(ts);
    
    // color the moving boxes
    MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
    ms.setAmbient(new ColorRGBA(1f, 0, .05f, 1f));
    ms.setDiffuse(new ColorRGBA(0.5f, 0, .05f, .1f));
    ms.setColorMaterial( ColorMaterial.AmbientAndDiffuse );
    ms.setEnabled(true);
    mover1.setRenderState(ms);
    mover2.setRenderState(ms);
    
    // make the boxes transparent
    BlendState bs = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
    bs.setBlendEnabled(false);
    bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
    bs.setDestinationFunction(BlendState.DestinationFunction.One);
    bs.setTestEnabled(true);
    bs.setTestFunction(BlendState.TestFunction.GreaterThan);
    bs.setEnabled(true);
    mover1.setRenderState(bs);
    mover2.setRenderState(bs);
    
    //allows switching the visibility of curves on and of
    KeyBindingManager.getKeyBindingManager().add("toggle_curve1", KeyInput.KEY_1);
    KeyBindingManager.getKeyBindingManager().add("toggle_curve2", KeyInput.KEY_2);
       
    rootNode.updateRenderState();
    
    Vector3f buff1 = new Vector3f();
    Vector3f buff2 = new Vector3f();
    
    for ( int i = 0; i < foo.getLength(); i++ ) {
    	System.out.println(foo.getPointByLength(i+1,buff1).subtractLocal(foo.getPointByLength(i, buff2)));
    }
  }
  
  @Override
  public void update(float time) {
	  super.update(time);
	  
	  if(KeyBindingManager.getKeyBindingManager().isValidCommand("toggle_curve1", false) && curve1 != null) {
		  if ( this.displayCurve1 ) {
			  curve1.getParent().detachChild(curve1);
			  mover1.getParent().detachChild(mover1);
		  }
		  else {
			  rootNode.attachChild(curve1);
			  rootNode.attachChild(mover1);
		  }
		  this.displayCurve1 = !this.displayCurve1;
		}
	  if(KeyBindingManager.getKeyBindingManager().isValidCommand("toggle_curve2", false) && curve2 != null) {
		  if ( this.displayCurve2 ) {
			  curve2.getParent().detachChild(curve2);
			  mover2.getParent().detachChild(mover2);
		  }
		  else {
			  rootNode.attachChild(curve2);
			  rootNode.attachChild(mover2);
		  }
		  this.displayCurve2 = !this.displayCurve2;
		}
	  
	  v.add(new Vector3f(mover2.getLocalTranslation()));
	  
	  timer += time;
	  if (timer >= 2.1f ) {
		  timer = Integer.MIN_VALUE;
		  Vector3f[] sr = new Vector3f[v.size()];
		  for ( int i = 0; i < v.size(); i++) {
			 sr[i] = v.get(0);
		  }
		  bar = new CatmullRomCurve("", sr);
		  curve2 = new CurveWrapper(bar);
		  
		  cc.setSpeed(.1f);
		  cc2.setSpeed(.1f);
	  }
  }
}

class CurveController extends Controller {
    private static final long serialVersionUID = 1L;
	private Spatial mover;
    private prealpha.curve.Curve curve;
    private Vector3f up;
    private float orientationPrecision = 0.1f;
    private float currentTime = 0.0f;
    private float deltaTime = 0.0f;

    private boolean cycleForward = true;
    private boolean autoRotation = false;

    /**
     * Constructor instantiates a new <code>CurveController</code> object.
     * The curve object that the controller operates on and the spatial object
     * that is moved is specified during construction.
     * @param curve the curve to operate on.
     * @param mover the spatial to move.
     */
    public CurveController(prealpha.curve.Curve curve, Spatial mover) {
        this.curve = curve;
        this.mover = mover;
        setUpVector(new Vector3f(0,1,0));
        setMinTime(0);
        setMaxTime(Float.MAX_VALUE);
        setRepeatType(Controller.RT_CLAMP);
        setSpeed(1.0f);
    }

    /**
     * Constructor instantiates a new <code>CurveController</code> object.
     * The curve object that the controller operates on and the spatial object
     * that is moved is specified during construction. The game time to
     * start and the game time to finish is also supplied.
     * @param curve the curve to operate on.
     * @param mover the spatial to move.
     * @param minTime the time to start the controller.
     * @param maxTime the time to end the controller.
     */
    public CurveController(
    	prealpha.curve.Curve curve,
        Spatial mover,
        float minTime,
        float maxTime) {
        this.curve = curve;
        this.mover = mover;
        setMinTime(minTime);
        setMaxTime(maxTime);
        setRepeatType(Controller.RT_CLAMP);
    }

    /**
     *
     * <code>setUpVector</code> sets the locking vector for the spatials up
     * vector. This prevents rolling along the curve and allows for a better
     * tracking.
     * @param up the vector to lock as the spatials up vector.
     */
    public void setUpVector(Vector3f up) {
        this.up = up;
    }

    /**
     *
     * <code>setOrientationPrecision</code> sets a precision value for the
     * spatials orientation. The smaller the number the higher the precision.
     * By default 0.1 is used, and typically does not require changing.
     * @param value the precision value of the spatial's orientation.
     */
    public void setOrientationPrecision(float value) {
        orientationPrecision = value;
    }

    /**
     *
     * <code>setAutoRotation</code> determines if the object assigned to
     * the controller will rotate with the curve or just following the
     * curve.
     * @param value true if the object is to rotate with the curve, false
     *      otherwise.
     */
    public void setAutoRotation(boolean value) {
        autoRotation = value;
    }

    /**
     *
     * <code>isAutoRotating</code> returns true if the object is rotating with
     * the curve and false if it is not.
     * @return true if the object is following the curve, false otherwise.
     */
    public boolean isAutoRotating() {
        return autoRotation;
    }

    /**
     * <code>update</code> moves a spatial along the given curve for along a
     * time period.
     * @see com.jme.scene.Controller#update(float)
     */
    public void update(float time) {
        if(mover == null || curve == null || up == null) {
            return;
        }
        currentTime += time * getSpeed();

        if (currentTime >= getMinTime() && currentTime <= getMaxTime()) {

            if (getRepeatType() == RT_CLAMP) {
                deltaTime = currentTime - getMinTime();
                mover.setLocalTranslation(curve.getPoint(deltaTime,mover.getLocalTranslation()));
                if(autoRotation) {
                    mover.setLocalRotation(
                        curve.getOrientation(
                            deltaTime,
                            orientationPrecision,
                            up));
                }
            } else if (getRepeatType() == RT_WRAP) {
                deltaTime = (currentTime - getMinTime()) % 1.0f;
                if (deltaTime > 1) {
                    currentTime = 0;
                    deltaTime = 0;
                }
                mover.setLocalTranslation(curve.getPoint(deltaTime,mover.getLocalTranslation()));
                if(autoRotation) {
                    mover.setLocalRotation(
                        curve.getOrientation(
                            deltaTime,
                            orientationPrecision,
                            up));
                }
            } else if (getRepeatType() == RT_CYCLE) {
            	float prevTime = deltaTime;
                deltaTime = (currentTime - getMinTime()) % 1.0f;
            	if (prevTime > deltaTime) {
                    cycleForward = !cycleForward;
                    curve = curve.getSuccessor();
                }
                if (cycleForward) {
                    mover.setLocalTranslation(curve.getPointByLength(curve.getLength()*deltaTime,mover.getLocalTranslation()));
                    if(autoRotation) {
                        mover.setLocalRotation(
                            curve.getOrientation(
                                deltaTime,
                                orientationPrecision,
                                up));
                    }
                } else {
                    mover.setLocalTranslation(
                    	curve.getPointByLength(curve.getLength()*(1.0f - deltaTime),mover.getLocalTranslation()));
                    if(autoRotation) {
                        mover.setLocalRotation(
                            curve.getOrientation(
                                1.0f - deltaTime,
                                orientationPrecision,
                                up));
                    }
                }
            	/*
                float prevTime = deltaTime;
                deltaTime = (currentTime - getMinTime()) % 1.0f;
                if (prevTime > deltaTime) {
                    cycleForward = !cycleForward;
                }
                if (cycleForward) {

                    mover.setLocalTranslation(curve.getPoint(deltaTime,mover.getLocalTranslation()));
                    if(autoRotation) {
                        mover.setLocalRotation(
                            curve.getOrientation(
                                deltaTime,
                                orientationPrecision,
                                up));
                    }
                } else {
                    mover.setLocalTranslation(
                        curve.getPoint(1.0f - deltaTime,mover.getLocalTranslation()));
                    if(autoRotation) {
                        mover.setLocalRotation(
                            curve.getOrientation(
                                1.0f - deltaTime,
                                orientationPrecision,
                                up));
                    }
                }
                */
            } else {
                return;
            }
        }
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(mover, "mover", null);
        capsule.write(curve, "Curve", null);
        capsule.write(up, "up", null);
        capsule.write(orientationPrecision, "orientationPrecision", 0.1f);
        capsule.write(cycleForward, "cycleForward", true);
        capsule.write(autoRotation, "autoRotation", false);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        
        mover = (Spatial)capsule.readSavable("mover", null);
        curve = (prealpha.curve.Curve)capsule.readSavable("curve", null);
        up = (Vector3f)capsule.readSavable("up", null);
        orientationPrecision = capsule.readFloat("orientationPrecision", 0.1f);
        cycleForward = capsule.readBoolean("cycleForward", true);
        autoRotation = capsule.readBoolean("autoRotation", false);
    }
}







