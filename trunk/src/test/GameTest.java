package test;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import jmetest.input.TestAbsoluteMouse;

import prealpha.curve.CatmullRomCurve;
import prealpha.curve.CurveWrapper;
import prealpha.util.Util;

import com.jme.curve.Curve;
import com.jme.curve.CurveController;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.InputHandler;
import com.jme.input.Mouse;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.input.RelativeMouse;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.util.states.PhysicsGameState;

public class GameTest extends DebugGameState implements MouseInputListener {

	Sphere s2;
	Mouse mouse;
	
	public GameTest() {
		super(true);
		
		MouseInput.get().addListener(this);
		
		Node wielder = new Node();
		getRootNode().attachChild(wielder);
		
		Sphere s1 = new Sphere("wielder", 20,20, 1);
		wielder.attachChild(s1);
		
		s2 = new Sphere("wielder", 20,20, .5f);
		wielder.attachChild(s2);
				
//		s2.lookAt( s2.getWorldTranslation().add(5, 5, 0), Vector3f.UNIT_Y);
		
		input = new InputHandler();
		
		mouse = new AbsoluteMouse("Mouse Input", DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
        TextureState cursor = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        cursor.setEnabled(true);
        cursor.setTexture(TextureManager.loadTexture(TestAbsoluteMouse.class
                .getClassLoader().getResource("jmetest/data/cursor/test.PNG"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear));
        mouse.setRenderState(cursor);
        BlendState as1 = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        as1.setBlendEnabled(true);
        as1.setSourceFunction(BlendState.SourceFunction.One);
        as1.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceColor);
        as1.setTestEnabled(true);
        as1.setTestFunction(BlendState.TestFunction.GreaterThan);
        mouse.setRenderState(as1);
        mouse.registerWithInputHandler(input);
		getRootNode().attachChild(mouse);
		mouse.setLocalScale(1);
		
		getRootNode().updateRenderState();
	}
	
	final Vector3f v = new Vector3f();
	
//	@Override
//	public void update( float time ) {
//		super.update(time);
//		
//		v.set( mouse.getHotSpotPosition() );
//		v.x /= DisplaySystem.getDisplaySystem().getWidth();
//		v.y /= DisplaySystem.getDisplaySystem().getHeight();
//		if ( !v.equals(Vector3f.ZERO) ) Util.shoutln(v);
//		s2.getLocalTranslation().set( v );
//		s2.getLocalTranslation().set( 2 , 0);
//		
//		s2.getLocalTranslation().normalizeLocal();
//		s2.getLocalTranslation().multLocal(2);
//	}
	
	static StandardGame app;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Throwable {
		app = new StandardGame("app");
		app.start();
		
		Future<DebugGameState> f = GameTaskQueueManager.getManager().update(new Callable<DebugGameState>() {

			@Override
			public DebugGameState call() throws Exception {
				return new GameTest();
			}
			
		});
		DebugGameState debug = f.get();
		GameStateManager.getInstance().attachChild(debug);
		
		PhysicsGameState phys = new PhysicsGameState("phys");
		GameStateManager.getInstance().attachChild(phys);
//
//		StaticPhysicsNode floor = phys.getPhysicsSpace().createStaticNode();
//		floor.attachChild(new Box("floor", new Vector3f(0,-2,0), 100, .5f, 100));
//		floor.generatePhysicsGeometry();
//		debug.getRootNode().attachChild(floor);
//		
//		Future<MaterialState> g = GameTaskQueueManager.getManager().update(new Callable<MaterialState>() {
//
//			@Override
//			public MaterialState call() throws Exception {
//				return DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
//			}
//			
//		});
//		
//		DynamicPhysicsNode node = phys.getPhysicsSpace().createDynamicNode();
//		node.attachChild(new Sphere("node", 25, 25,1.5f));
//		node.generatePhysicsGeometry();
//		debug.getRootNode().attachChild(node);
//		MaterialState s = g.get();
//		s.setAmbient(ColorRGBA.brown);
//		s.setDiffuse(ColorRGBA.gray);
////		node.setRenderState(s);
//		floor.setRenderState(s);
//	
//		
//		
		

		
		GameStateManager.getInstance().activateAllChildren();
	}

	@Override
	public void onButton(int button, boolean pressed, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	float x = 0;
	float y = 0;
	
	@Override
	public void onMove(int delta, int delta2, int newX, int newY) {
		x = newX - DisplaySystem.getDisplaySystem().getWidth()/2;
		y = newY - DisplaySystem.getDisplaySystem().getHeight()/2;
		
		s2.getLocalTranslation().set(x, y, 0);
		s2.getLocalTranslation().normalizeLocal();
		s2.getLocalTranslation().multLocal(2);
		Util.shoutln(x,y, newX, newY);
	}

	@Override
	public void onWheel(int wheelDelta, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	
}