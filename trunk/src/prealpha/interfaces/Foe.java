package prealpha.interfaces;

import com.jme.math.Vector3f;
import com.jme.scene.Node;

public abstract class Foe extends Node implements Destructible, Updateable {
	// well a very basic and necessary ability of enemys i guess
	// returns true if possible, else false
	abstract public boolean goTo(Vector3f destination);
	
	// well, i don't know if i really want to realize attacks this way but, well, let's try it for now
	// returns true if possible/succesful
	abstract public boolean attack();
}
