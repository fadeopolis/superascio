//SimpleGame to StandardGame - An effort to gain mindshare by darkfrog
//In the beginning there was SimpleGame

package prealpha.tut;
 
 
import com.jme.bounding.*;
import com.jme.math.*;
import com.jme.scene.shape.Box;
import com.jmex.editors.swing.settings.*;
import com.jmex.game.*;
import com.jmex.game.state.*;
 
/**
 * TestStandardGame is meant to be an example replacement of
 * jmetest.base.TestSimpleGame using the StandardGame implementation
 * instead of SimpleGame.
 * 
 * @author Matthew D. Hicks
 */
public class TestStandardGame {
	public static void main(String[] args) throws Exception {
		// Instantiate StandardGame
		StandardGame game = new StandardGame("A Simple Test");
		// Show settings screen
		GameSettingsPanel.prompt(game.getSettings());
		// Start StandardGame, it will block until it has initialized successfully, then return
		game.start();
		
		// Create a DebugGameState - has all the built-in features that SimpleGame provides
		// NOTE: for a distributable game implementation you'll want to use something like
		// BasicGameState instead and provide control features yourself.
		DebugGameState state = new DebugGameState();
		// Put our box in it
		Box box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2);
		box.setModelBound(new BoundingSphere());
		box.updateModelBound();
		// We had to add the following line because the render thread is already running
		// Anytime we add content we need to updateRenderState or we get funky effects
		box.updateRenderState();
		state.getRootNode().attachChild(box);
		state.getRootNode().updateRenderState();
		// Add it to the manager
		GameStateManager.getInstance().attachChild(state);
		// Activate the game state
		state.setActive(true);
	}
}
/*
Okay, so in the end we have like three more lines of actual code in it, but weve gained so much that you dont necessarily see here. First of all weve got multithreadedness for free. When we used game.start() it created a thread that the StandardGame runs in and invokes all OpenGL calls from within that. Weve also got GameState support so we can divide out our game into logical states that can be enabled/disabled, added/removed, or customized as necessary. The example above is probably not exactly the way youd want to implement the code if you were writing a full game as a single main method that creates all your objects and applies them to the scene would get really cluttered really fast. Expanding out to create custom GameStates is the recommended method of handling that and then you can manage custom objects within that as you would in any jME project.
Creating GameStates

StandardGame is all about GameStates. Youll never add a Spatial directly to StandardGame, youll always add it to your GameState. You can have as many GameStates active at one time as youd like and enable/disable them at will. For example, you might have a GameState for your main game, but if the user hits Escape you want to display the main menu overlayed on top of the game. With GameStates you can easily do this by having a GameState that represents your menu and simply enable or disable it as needed.
OpenGL Thread and You

Okay, so thats a really cheesy title, but there are lots of circumstances where youll need to execute something in the OpenGL thread. Im sure youre asking yourself, Well, this StandardGame is cool, but its locked me out of the OpenGL thread and if I want to do anything with it I have to extend StandardGame and implement something myself. Well, be quiet for a minute and let me explain....dang, youre so negative!

There are actually many options for inserting into the OpenGL thread:
GameTasks and GameTaskQueue

This is a really neat feature that can be utilized independently of StandardGame, but StandardGame does have built-in support for it. A simple example is a circumstance where I want to lockBounds on a Spatial in my code I can simply create a Callable implementation and toss it into the queue to be parsed:

GameTaskQueueManager.getManager().update(new Callable<Object>() {
    public Object call() throws Exception {
        stars.lockBounds();
        return null;
    }
});

Its as easy as that. The first big advantage to this method is it doesnt stop the OpenGL thread from executing and it doesnt block the current thread from continuing to work. However, that can be a potential problem since you may want to wait until the task has completed before continuing in your current thread. Fortunately, this invocation actually returns a Future object that can then be told to block until it is completed. For example:

Future<Object> future = GameTaskQueueManager.getManager().update(new Callable<Object>() {
    public Object call() throws Exception {
        stars.lockBounds();
        return null;
    }
});
future.get();    // This will block the current thread until call() has successfully returned in the OpenGL thread

Now, Im sure by now youve noticed that return null; on there. This is another really cool feature of GameTasks is that you can return an object when you are done and retrieve back out via the future.get() method.
lock() and unlock()

Though a relatively new feature, it is an extremely cool one if I do say so myself. The above example of locking the bounds on a Spatial can be done much easier via this route:

game.lock();   // Requests a lock on StandardGame that will pause the OpenGL thread when it starts the next update
stars.lockBounds();    // We can now call this in our current thread because thread-safety is no longer a concern, the OpenGL thread is paused at a safe point
game.unlock();    // Don't forget to do this as otherwise your game will be paused permanently and causes for a pretty sad gameplay experience

Though extremely easy and cool be careful when you use this as doing anything that blocks for very long will create graphical pauses in your game until it completes. You are blocking two threads when you lock() StandardGame (StandardGames thread and your current thread) so in most cases GameTasks are probably a more efficient way to go unless you need both threads to block and you arent going to be doing anything that will take long to accomplish.

It is important to note that when you call lock() it will block the current thread until StandardGame hits its next update cycle. Further, if another thread has called lock() first it will give precedence to it first and wait for it to unlock() before giving control over to the other lock().
Creating and Managing Cameras

The previous example makes use of DebugGameState, which includes a single default camera. That might be fine for demo purposes, but what happens when youre actually writing a game thats a little more complex? You will probably want to create and manage cameras yourself, like this:

        DisplaySystem display = DisplaySystem.getDisplaySystem();
        int width = display.getWidth();
        int height = display.getHeight();
        Camera myCamera = display.getRenderer().createCamera(width, height);
        myCamera.setFrustumPerspective(45.0f, (float)width / (float)height, 1, 1000);

If you run that code outside of the GameTaskQueue, youll get this:

java.lang.NullPointerException
	at org.lwjgl.opengl.GL11.glMatrixMode(GL11.java:1958)
	at com.jme.scene.state.lwjgl.records.RendererRecord.switchMode(RendererRecord.java:25)
	at com.jme.renderer.lwjgl.LWJGLCamera.doFrustumChange(LWJGLCamera.java:172)
	at com.jme.renderer.lwjgl.LWJGLCamera.apply(LWJGLCamera.java:134)
	at com.jme.renderer.lwjgl.LWJGLCamera.<init>(LWJGLCamera.java:75)
	at com.jme.renderer.lwjgl.LWJGLRenderer.createCamera(LWJGLRenderer.java:240)

As we saw before with lock and unlock, camera creation has to happen in the OpenGL thread. Exceptions coming out of the lwjgl.jar are a very good indication that whatever youre doing has to be done in the OpenGL thread.

To fix it, just do like before (Note the use of Generics to prevent casting and increase readability):

    Future<Camera> future = GameTaskQueueManager.getManager().update(new Callable<Camera>() {
      public Camera call() throws Exception {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        int width = display.getWidth();
        int height = display.getHeight();
        Camera camera = display.getRenderer().createCamera(width, height);
        camera.setFrustumPerspective(45.0f, (float)width / (float)height, 1, 1000);
        return camera;
      }
    });
    Camera myCamera = future.get();    

This example waits for the camera to be created, but you dont have to. Just follow the non-Future example above if you dont need to wait. Finally, dont forget to call

DisplaySystem.getDisplaySystem().getRenderer().setCamera(myCamera);

to see things with your newly created camera.

*/
