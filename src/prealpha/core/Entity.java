package prealpha.core;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import prealpha.util.Util.Prop;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsSpatial;

/**
 * A core game element
 * @author fader
 *
 * @param <S>
 */
public abstract class Entity extends Node implements Savable, Updateable {

	private ID id;

	private Updater updater;
	
	/**
	 * 
	 * @param spatial Representation of this entity in the scenegraph, can be null
	 * @param data A map for game data ( data must implements Savable ), can be null
	 */
	public Entity( final Updater updater ) {
		this.id = ID.requestID(getClass().getSimpleName());
		setName(id.getName());
		
		this.updater = updater;
		if ( updater != null ) updater.add(this);
	}
	
	/**
	 * 
	 * @return the ID of this entity
	 */
	public ID getID() {
		return id;
	}
	
	public Updater getUpdater() {
		return updater;
	}
	
	@Override
	public void update(float time) {
		throw new UnsupportedOperationException("Update not implemented in base class entity");
	}
	
	protected final Vector3f calcV = new Vector3f();

	public Vector3f getDirection( Vector3f store ) {
		if ( store == null ) {
			store = new Vector3f();
		}
		return getWorldRotation().getRotationColumn(0, store);
	}

	public Vector3f getLeft( Vector3f store ) {
		if ( store == null ) {
			store = calcV;
		}
		return getWorldRotation().getRotationColumn(1, store);
	}
	
	public Vector3f getUp( Vector3f store ) {
		if ( store == null ) {
			store = calcV;
		}
		return getWorldRotation().getRotationColumn(2, store);
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		super.write(ex);
		
		OutputCapsule cap = ex.getCapsule(this);
//		cap.write(spatial, "spatial", null);
//		cap.writeStringSavableMap(data, "data", null);
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		super.read(im);
		
	}

	@Override
	public void removeFromUpdater() {
		this.updater.remove(this);
	}
	
}