package prealpha.tut;

import com.jme.bounding.BoundingBox;
import com.jme.input.ChaseCamera;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.thirdperson.ThirdPersonMouseLook;
import com.jme.input.util.SyntheticButton;
import com.jme.light.DirectionalLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.states.PhysicsGameState;
import java.util.HashMap;


public class SimpleThirdPersonGame
{
    
    public StandardGame game;
    public SimpleThirdPersonState stps;
    public InputHandler input;
    
    public SimpleThirdPersonGame() throws Exception
    {
        game = new StandardGame("Third Person Tutorial");
        //Show settings screen
        if(GameSettingsPanel.prompt(game.getSettings()))
        {
            //Start StandardGame, it will block until it has initialized successfully, then return
            game.start();
            stps = new SimpleThirdPersonState("main game state");
            GameStateManager.getInstance().attachChild(stps);
            stps.setActive(true);
        }
        
    }
    
    public static void main(String args[])
    {
        try
        {
            new SimpleThirdPersonGame();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    public void initGame()
    {
        
    }
    
    public class SimpleThirdPersonState extends PhysicsGameState
    {
        
        protected ChaseCamera chaser;
        protected Spatial player;
        protected Spatial terrain;
        protected DynamicPhysicsNode playerNode;
        protected PhysicsNode terrainNode;
        
        boolean playerOnFloor = true;
        
        private Timer timer;
        
        public SimpleThirdPersonState(String name)
        {
            super(name);
            initGame();
            makeKeyBindings();
        }
        public void update(float f)
        {
            super.update(f);
            chaser.update(f);
            input.update(f);
            
            if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit",
                    false))
            {
                System.exit(0);
            }
        }
        private void initGame()
        {
            
            input = new InputHandler();
            KeyBindingManager.getKeyBindingManager().set("exit", KeyInput.KEY_ESCAPE);
            
            //Get a high resolution timer for FPS updates. 
            timer = Timer.getTimer();
            
            // Set up a basic, default light. 
            DirectionalLight light = new DirectionalLight();
            light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
            light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
            light.setDirection(new Vector3f(1,-1,0));
            light.setEnabled(true);
            
            // Attach the light to a lightState and the lightState to rootNode. 
            LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
            lightState.setEnabled(true);
            lightState.attach(light);
            getRootNode().setRenderState(lightState);
            
            //create terrain
            terrain = new Box("The terrain", new Vector3f(0, -5, 0), 20f, 0.5f, 20f);
            terrain.setModelBound( new BoundingBox());
            terrain.updateModelBound();
            
            //create player
            player = new Box("The player", new Vector3f(0,0,0), 1f, 2f, 1f);
            player.setModelBound(new BoundingBox());
            player.updateModelBound();
            
            //make supernodes
            playerNode = getPhysicsSpace().createDynamicNode();
            terrainNode = getPhysicsSpace().createStaticNode();
            
            //attach
            playerNode.attachChild(player);
            terrainNode.attachChild(terrain);
            
            //physics stuff
            terrainNode.generatePhysicsGeometry(true);
            playerNode.generatePhysicsGeometry();
            
            //move the center of mass down to let the box land on its 'feet'
            playerNode.setCenterOfMass( new Vector3f( 0f, -2f, 0f ) );
            //this box keeps the default material
            
            //to move the player around we create a special material for it
            //and apply surface motion on it
            final Material playerMaterial = new Material(" player material ");
            playerNode.setMaterial( playerMaterial );
            //the actual motion is applied in the MoveAction (see below)
            
            //chase camera
            buildChaseCamera();
            
            //attach the player & terrain
            getRootNode().attachChild(playerNode);
            getRootNode().attachChild(terrainNode);
            
            getRootNode().updateGeometricState(0.0F, true);
            getRootNode().updateRenderState();
            
        }
        
        /**
          set the basic parameters of the chase camera. This includes the offset. We want
          to be behind the player and a little above it. So we will the offset as 0 for
          x and z, but be 1.5 times higher than the node.
         
          We then set the roll out parameters (2 units is the closest the camera can get, and
          5 is the furthest).
        */ 
         
        private void buildChaseCamera()
        {
        	Vector3f targetOffset = new Vector3f();
            targetOffset.y = ((BoundingBox) player.getWorldBound()).yExtent * 1.5f;
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put(ThirdPersonMouseLook.PROP_MAXROLLOUT, "20");
            props.put(ThirdPersonMouseLook.PROP_MINROLLOUT, "10");
            props.put(ThirdPersonMouseLook.PROP_MAXASCENT, ""+45 * FastMath.DEG_TO_RAD);
            props.put(ThirdPersonMouseLook.PROP_MOUSEBUTTON_FOR_LOOKING, 2);
            props.put(ChaseCamera.PROP_INITIALSPHERECOORDS, new Vector3f(5, 0, 30 * FastMath.DEG_TO_RAD));
            props.put(ChaseCamera.PROP_TARGETOFFSET, targetOffset);
            chaser = new ChaseCamera(DisplaySystem.getDisplaySystem().getRenderer().getCamera(), player, props);
            chaser.setMaxDistance(20);
            chaser.setMinDistance(8);
        }
        private void makeKeyBindings()
        {
            
            //we map the MoveAction to the keys DELETE and PAGE DOWN for forward and backward
            input.addAction( new MoveAction( new Vector3f( 0f, 0f, 12.5f ) ),
                    InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_UP, InputHandler.AXIS_NONE, false );
            input.addAction( new MoveAction( new Vector3f( 0f, 0f, -12.5f ) ),
                    InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_DOWN, InputHandler.AXIS_NONE, false );
            
            input.addAction( new RotateAction( new Vector3f( 0, 30,0 ) ),
                    InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_LEFT, InputHandler.AXIS_NONE, true );
            input.addAction( new RotateAction( new Vector3f( 0, -30, 0 ) ),
                    InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_RIGHT, InputHandler.AXIS_NONE, true);
            
            input.addAction( new MoveAction( new Vector3f( 12.5f, 0f, 0f ) ),
                    InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_S, InputHandler.AXIS_NONE, false );
            input.addAction( new MoveAction( new Vector3f( -12.5f, 0f, 0f ) ),
                    InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_D, InputHandler.AXIS_NONE, false );
            
           /** 
             now the player should be able to jump
             we do this by applying a single force vector when the HOME key is pressed
             this should happen only while the player is touching the floor (we determine that below)
            */
            input.addAction( new InputAction()
            {
                public void performAction( InputActionEvent evt )
                {
                    if ( playerOnFloor && evt.getTriggerPressed() )
                    {
                        playerNode.addForce( new Vector3f( 0, 500, 0 ) );
                    }
                }
            }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false );
            /**
             ok finally detect when the player is touching the ground
             a simple way to do this is making a boolean variable (playerOnFloor) which is
             set to true on collision with the floor and to false before each physics computation
             collision events analoguous to Lesson8
            */
            SyntheticButton playerCollisionEventHandler = playerNode.getCollisionEventHandler();
            input.addAction( new InputAction()
            {
                public void performAction( InputActionEvent evt )
                {
                    ContactInfo contactInfo = (ContactInfo) evt.getTriggerData();
                    if ( contactInfo.getNode1() == terrainNode || contactInfo.getNode2() == terrainNode )
                    {
                        playerOnFloor = true;
                    }
                }
            }, playerCollisionEventHandler.getDeviceName(), playerCollisionEventHandler.getIndex(),
                    InputHandler.AXIS_NONE, false );// TODO this should be true when physics supports release events
            
            //and a very simple callback to set the variable to false before each step
            getPhysicsSpace().addToUpdateCallbacks( new PhysicsUpdateCallback()
            {
                public void beforeStep( PhysicsSpace space, float time )
                {
                    playerOnFloor = false;
                }
                
                public void afterStep( PhysicsSpace space, float time )
                {
                    
                }
            } );
        }
        
        
        //Action called on key input for applying movement of the player.
         
        private class MoveAction extends InputAction
        {
            
            //  store direction this action instance will move.
             
            private Vector3f direction;
            
            /**
              @param direction direction this action instance will move
            */ 
            public MoveAction( Vector3f direction )
            {
                this.direction = direction;
            }
            
            public void performAction( InputActionEvent evt )
            {
                if ( evt.getTriggerPressed() )
                {
                    //key goes down - apply motion
                    playerNode.getMaterial().setSurfaceMotion( direction );
                }
                else
                {
                    //key goes up - stand still
                    playerNode.getMaterial().setSurfaceMotion( Vector3f.ZERO );
                    //note for a game we usually won't want zero motion on key release but be able to combine
                    //       keys in some way
                }
            }
        }
        
        private class RotateAction extends InputAction
        {
            public Vector3f direction;
            
            public RotateAction(Vector3f direction)
            {
                this.direction = direction;
            }
            
            public void performAction( InputActionEvent evt )
            {
                // key goes down - apply motion
                playerNode.addTorque(direction);
            }
        }
    }
}
