/*
 * Copyright (c) 2005-2007 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package prealpha.tut;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.shape.Box;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.MaterialState;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.MutableContactInfo;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.PhysicsPicker;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * Based on Lesson8 this shows a simple "tank" moving and rotating with surface motion on it's "chains".
 *
 * @author Irrisor
 */
public class Lesson8b extends SimplePhysicsGame {

    protected void simpleInitGame() {
        // no magic here - just create a floor in that method
        createFloor();

        // second we create a box - as we create multiple boxes this time the code moved into a separate method
        player = createPlayer();
        player.setName( "player" );
        color( player, new ColorRGBA( 0, 0, 1, 1 ) );
        // the first box gets in the center above the floor
        player.getLocalTranslation().set( 8, 1, 0 );
        // move the center of mass down to let the box land on its 'feet'
        player.computeMass();
        player.setCenterOfMass( new Vector3f( 0, -0.5f, 0 ) );
        // this box keeps the default material

        // to move the player around we create a special material for it
        // and apply surface motion on it
        playerLeftPart.setMaterial( new Material( "player material left" ) );
        playerRightPart.setMaterial( new Material( "player material right" ) );
        MutableContactInfo info = new MutableContactInfo();
        {
            info.setMu( 1 );
            info.setBounce( 0.4f );
            info.setMinimumBounceVelocity( 1f );
            info.setMuOrthogonal( 0.01f );
        }
        playerLeftPart.getMaterial().putContactHandlingDetails( null, info );
        playerRightPart.getMaterial().putContactHandlingDetails( null, info );
        // the actual motion is applied in the MoveAction (see below)

        // we map the MoveAction to the keys DELETE and PAGE DOWN for forward and backward
        input.addAction( new MoveAction( new Vector3f( -2, 0, 0 ), playerLeftPart ),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_DELETE, InputHandler.AXIS_NONE, false );
        input.addAction( new MoveAction( new Vector3f( -2, 0, 0 ), playerRightPart ),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_DELETE, InputHandler.AXIS_NONE, false );
        input.addAction( new MoveAction( new Vector3f( 2, 0, 0 ), playerLeftPart ),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_PGDN, InputHandler.AXIS_NONE, false );
        input.addAction( new MoveAction( new Vector3f( 2, 0, 0 ), playerRightPart ),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_PGDN, InputHandler.AXIS_NONE, false );

        // PGUP and INSERT should turn the node - move chains in opposite directions
        input.addAction( new MoveAction( new Vector3f( -2, 0, 0 ), playerLeftPart ), InputHandler.DEVICE_KEYBOARD,
                KeyInput.KEY_PGUP, InputHandler.AXIS_NONE, false );
        input.addAction( new MoveAction( new Vector3f( 2, 0, 0 ), playerRightPart ), InputHandler.DEVICE_KEYBOARD,
                KeyInput.KEY_PGUP, InputHandler.AXIS_NONE, false );
        input.addAction( new MoveAction( new Vector3f( 2, 0, 0 ), playerLeftPart ), InputHandler.DEVICE_KEYBOARD,
                KeyInput.KEY_INSERT, InputHandler.AXIS_NONE, false );
        input.addAction( new MoveAction( new Vector3f( -2, 0, 0 ), playerRightPart ), InputHandler.DEVICE_KEYBOARD,
                KeyInput.KEY_INSERT, InputHandler.AXIS_NONE, false );
        // Note that applying tourque needs to be done singular (like above: allowRepeats == false)
        // or it must be done _per step_, which would mean we need to move the action into an InputHandler which
        // is updated each physics step not each frame

        // finally print a key-binding message
        Text infoText = Text.createDefaultTextLabel( "key info", "[del] and [page down] to move, [insert] and [page up] to rotate" );
        infoText.getLocalTranslation().set( 0, 20, 0 );
        fpsNode.attachChild( infoText );

        new PhysicsPicker( input, rootNode, getPhysicsSpace(), true ).setPickModeVisual( false );
        MouseInput.get().setCursorVisible( true );

        cam.setLocation( new Vector3f( 8, 10, 4 ) );
        cam.lookAt( player.getLocalTranslation(), Vector3f.UNIT_Y );

        showPhysics = true;
    }

    private void createFloor() {
        // first we will create the floor like in Lesson3, but put into into a field
        floor = getPhysicsSpace().createStaticNode();
        rootNode.attachChild( floor );
        final Box visualFloorBox = new Box( "floor", new Vector3f(), 5, 0.25f, 5 );
        floor.attachChild( visualFloorBox );
        // and not that steep
        visualFloorBox.getLocalRotation().fromAngleNormalAxis( 0.1f, new Vector3f( 0, 0, -1 ) );
        final Box visualFloorBox2 = new Box( "floor", new Vector3f(), 5, 0.25f, 5 );
        floor.attachChild( visualFloorBox2 );
        visualFloorBox2.getLocalTranslation().set( 9.7f, -0.5f, 0 );
        // and another one a bit on the left
        final Box visualFloorBox3 = new Box( "floor", new Vector3f(), 5, 0.25f, 5 );
        floor.attachChild( visualFloorBox3 );
        visualFloorBox3.getLocalTranslation().set( -11, 0, 0 );
        floor.generatePhysicsGeometry();
    }

    /**
     * Action called on key input for applying movement of the player.
     */
    private class MoveAction extends InputAction {
        /**
         * store direction this action instance will move.
         */
        private Vector3f direction;
        private PhysicsCollisionGeometry geom;

        /**
         * @param direction direction this action instance will move
         * @param geometry  where to apply surface motion
         */
        public MoveAction( Vector3f direction, PhysicsCollisionGeometry geometry ) {
            this.direction = direction;
            geom = geometry;
        }

        public void performAction( InputActionEvent evt ) {
            if ( evt.getTriggerPressed() ) {
                // key goes down - apply motion
                geom.getMaterial().setSurfaceMotion( direction );
    			System.out.print("Forward! ");
            } else {
                // key goes up - stand still
                geom.getMaterial().setSurfaceMotion( ZERO );
                System.out.print("Stop! ");
                // note: for a game we usually won't want zero motion on key release but be able to combine
                //       keys in some way
            }
        }
    }

    /**
     * helper vector for zeroing motion.
     */
    private static final Vector3f ZERO = new Vector3f( 0, 0, 0 );

    /**
     * floor node - is a field to easily access it in the action and callback.
     */
    private StaticPhysicsNode floor;
    /**
     * player node - also a field to easily access it in the actions.
     */
    private DynamicPhysicsNode player;
    private PhysicsCollisionGeometry playerLeftPart;
    private PhysicsCollisionGeometry playerRightPart;

    /**
     * Little helper method to color a spatial.
     *
     * @param spatial the spatial to be colored
     * @param color   desired color
     */
    private void color( Spatial spatial, ColorRGBA color ) {
        final MaterialState materialState = display.getRenderer().createMaterialState();
        materialState.setDiffuse( color );
        if ( color.a < 1 ) {
            final AlphaState alphaState = display.getRenderer().createAlphaState();
            alphaState.setEnabled( true );
            alphaState.setBlendEnabled( true );
            alphaState.setSrcFunction( AlphaState.SB_SRC_ALPHA );
            alphaState.setDstFunction( AlphaState.DB_ONE_MINUS_SRC_ALPHA );
            spatial.setRenderState( alphaState );
            spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
        }
        spatial.setRenderState( materialState );
    }

    /**
     * Create a box like known from Lesson2.
     *
     * @return a physics node containing a box
     */
    private DynamicPhysicsNode createPlayer() {
        DynamicPhysicsNode dynamicNode = getPhysicsSpace().createDynamicNode();
        rootNode.attachChild( dynamicNode );
        dynamicNode.attachChild( new Box( "player", new Vector3f(), 0.5f, 0.5f, 0.5f ) );
        playerLeftPart = dynamicNode.createBox( "left" );
        playerLeftPart.getLocalTranslation().set( 0, 0, 0.4f );
        playerLeftPart.getLocalScale().set( 1, 1, 0.2f );
        playerRightPart = dynamicNode.createBox( "right" );
        playerRightPart.getLocalTranslation().set( 0, 0, -0.4f );
        playerRightPart.getLocalScale().set( 1, 1, 0.2f );
        return dynamicNode;
    }

    @Override
    protected void simpleUpdate() {
        // move the cam where the player is
        cam.getLocation().x = player.getLocalTranslation().x;
        cam.update();
        cameraInputHandler.setEnabled( MouseInput.get().isButtonDown( 1 ) );
    }

    /**
     * The main method to allow starting this class as application.
     *
     * @param args command line arguments
     */
    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new Lesson8b().start();
    }
}

/*
 * $log$
 */

