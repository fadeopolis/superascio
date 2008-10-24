package prealpha.input.action;

import java.io.File;
import java.net.MalformedURLException;

import prealpha.character.Character;
import prealpha.util.Util;
import prealpha.util.Util.Prop;
import prealpha.weapon.LifeTimeController;
import prealpha.weapon.Weapon;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.RangedAudioTracker;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

public class FireAction implements InputActionInterface {
	
	AudioTrack track;
	Weapon weapon;
	
	public FireAction(Weapon weapon) {
		this.weapon = weapon;
		
		File f = new File("data/sound/laser.ogg");
		try {
			track = AudioSystem.getSystem().createAudioTrack(f.toURI().toURL(), false);
			track.setLooping(false);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        track1.setToTrack(ascio.getSpatial());
//        track1.setTrackIn3D(true);
//        track1.setMaxVolume(1.35f);  // set volume on the tracker as it will control fade in, etc.
	}

	@Override
	public void performAction(InputActionEvent evt) {
//		for ( int i = 0; i < 5; i++) {
//			DynamicPhysicsNode bullet = ((PhysicsSpace) Util.get().getProp(Prop.PhysicsSpace)).createDynamicNode();
//			
//			float size = .5f;
//			bullet.attachChild(new Sphere("bullet", 10, 10 , size));
//			bullet.generatePhysicsGeometry();
//			bullet.computeMass();
//			bullet.getLocalTranslation().set(weapon.getSpatial().getLocalTranslation()).addLocal(size+1.5f, Util.randomFloat(5f, true), Util.randomFloat(5f, true));
//			bullet.addForce(weapon.getDirection(null).multLocal(10000));
//			bullet.addController(new LifeTimeController(bullet));
//
//			((Node) Util.get().getProp(Prop.RootNode)).attachChild(bullet);
//		}
//        AudioSystem.getSystem().getMusicQueue().addTrack(track);
//        track.play();
		weapon.attack();
	}
}
