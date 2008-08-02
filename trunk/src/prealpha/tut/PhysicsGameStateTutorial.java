//Here's an adaptation of the Lesson2 tutorial (included in the jME Physics source code) to use the StandardGame? multithreaded approach.
//First, the code in its entirety. After the code, I will describe the interesting parts individually. It's not as complicated as it looks at first glance.

/*
 * Copyright (c) 2005-2006 jME Physics 2
 * Copyright (c) 2007 Sam Couter
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

import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
//import com.jme.util.LoggingSystem;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.util.states.PhysicsGameState;

/**
 * <p>
 * This class uses PhysicsGameState to show the most simple physics with graphical representation: A
 * dynamic box falling onto a static floor (also a box).
 * </p>
 * 
 * <p>
 * This demonstration has been mostly copied from Lesson2.java and modified to use PhysicsGameState
 * instead of extending SimplePhysicsGame.
 * </p>
 * 
 * @author Irrisor
 * @author Sam Couter
 */
public class PhysicsGameStateTutorial
{
	/**
	 * The main method to allow starting this class as application.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String [] args)
	{
		//LoggingSystem.getLogger().setLevel(Level.WARNING); // to see the important stuff

		// Create and start the OpenGL thread.
		final StandardGame game = new StandardGame("Physics tutorial");
		game.start();

		// Create the debug game state. This gives us some basic jME functions useful for
		// demonstrations, such as a root node and some keyboard controls.
		// It also draws the scene, which is somewhat necessary for seeing what's going on.
		final DebugGameState debugGameState = new DebugGameState();

		// Create the PhysicsGameState object. This will track all state related to the physics
		// interactions.
		final PhysicsGameState physicsGameState = new PhysicsGameState("Physics tutorial");

		// Now let jME know about the game states we've created.
		GameStateManager.getInstance().attachChild(debugGameState);
		debugGameState.setActive(true);

		GameStateManager.getInstance().attachChild(physicsGameState);
		physicsGameState.setActive(true);

		// first we will create the floor
		// as the floor can't move we create a _static_ physics node
		StaticPhysicsNode staticNode = physicsGameState.getPhysicsSpace().createStaticNode();

		// attach the node to the root node to have it updated each frameit
		debugGameState.getRootNode().attachChild(staticNode);

		// now we do not create a collision geometry but a visual box
		final Box visualFloorBox = new Box("floor", new Vector3f(), 5, 0.25f, 5);
		// note: we have used the constructor (name, center, xExtent, yExtent, zExtent)
		// thus our box is centered at (0,0,0) and has size (10, 0.5f, 10)

		// we have to attach it to our node
		staticNode.attachChild(visualFloorBox);

		// now we let jME Physics 2 generate the collision geometry for our box
		staticNode.generatePhysicsGeometry();

		// second we create a box that should fall down on the floor
		// as the new box should move we create a _dynamic_ physics node
		DynamicPhysicsNode dynamicNode = physicsGameState.getPhysicsSpace().createDynamicNode();
		debugGameState.getRootNode().attachChild(dynamicNode);

		// again we create a visual box
		final Box visualFallingBox = new Box("falling box", new Vector3f(), 0.5f, 0.5f, 0.5f);
		// note: again we have used the constructor (name, center, xExtent, yExtent, zExtent)
		// thus our box is centered at (0,0,0) and has size (1, 1, 1)
		// the center is really important here because we want the center of the box to lie in the
		// center
		// of the dynamic physics node - which is the center of gravity!

		// attach it to the dynamic node
		dynamicNode.attachChild(visualFallingBox);

		// and generate collision geometries again
		dynamicNode.generatePhysicsGeometry();

		// we have to move our dynamic node upwards such that is does not start in but above the
		// floor
		dynamicNode.getLocalTranslation().set(0, 5, 0);
		// note: we do not move the collision geometry but the physics node!

		// now we have visuals for the physics and don't necessarily need to activate the physics
		// debugger
		// though you can do it (V key) to see physics in the app

		// Once all this is set up, we need to update the render state on the root node.
		debugGameState.getRootNode().updateRenderState();
	}
}
/**
Now for the explanation.
The first step is to create the StandardGame? object which will create and start the OpenGL thread. This is the thread that will call update() and render() on all the registered GameState? objects.

		final StandardGame game = new StandardGame("Physics tutorial");
		game.start();


Next we create the GameState? objects, register them with the GameStateManager? and activate them. Registering them causes the update() and render() methods to be called. Setting them active makes them do their jobs.

		final DebugGameState debugGameState = new DebugGameState();
		final PhysicsGameState physicsGameState = new PhysicsGameState("Physics tutorial");

		GameStateManager.getInstance().attachChild(debugGameState);
		debugGameState.setActive(true);
		GameStateManager.getInstance().attachChild(physicsGameState);
		physicsGameState.setActive(true);


The rest of the code looks almost identical to Lesson2. The major difference is that since we no longer inherit from SimplePhysicsGame (and therefore from BaseSimpleGame) the rootNode and physicsState member variables do not exist. Instead rootNode is a property of DebugGameState?, and the physics space is a property of the PhysicsGameState. See the following for an example:

		final DynamicPhysicsNode dynamicNode = physicsGameState.getPhysicsSpace().createDynamicNode();
		debugGameState.getRootNode().attachChild(dynamicNode);


And that's all there is to it.

*/