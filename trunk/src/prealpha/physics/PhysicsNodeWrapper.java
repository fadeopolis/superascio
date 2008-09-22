package prealpha.physics;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingCapsule;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.input.util.SyntheticButton;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.LineSegment;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Capsule;
import com.jme.scene.shape.GeoSphere;
import com.jme.scene.shape.Sphere;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jmex.physics.CollisionGroup;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsDebugger;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.geometry.PhysicsCapsule;
import com.jmex.physics.geometry.PhysicsCylinder;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.geometry.PhysicsRay;
import com.jmex.physics.geometry.PhysicsSphere;
import com.jmex.physics.material.Material;
import com.jmex.terrain.TerrainBlock;

/**
 * A Node that maintains at least one PhysicsNode for physics representation
 * The getters/setters for Translation,Rotation,... mostly pass data from/to the PhysicsNode
 * 
 * @author fader
 *
 */
public class PhysicsNodeWrapper<E  extends PhysicsNode> extends Node {
	private static final Logger logger = Logger.getLogger(Node.class.getName());
	
	protected Vector3f vbuff = new Vector3f();
	protected float fbuff = 0;
	
	private E e;
	
	public PhysicsNodeWrapper(E e) {
		this(null, e);
	}
	
	public PhysicsNodeWrapper(String name, E e) {
		super(name + " Wrapper");

		this.e = e;
	}

	/**
	 *  all public Methods inherited from Node are overridden and passed to the physicsNode
	 *  if you want to add children to the actual Wrapper use super
	 */		
	
	/**
	 * 
	 * <code>getQuantity</code> returns the number of children this node
	 * maintains.
	 * 
	 * @return the number of children this node maintains.
	 */
	@Override
	public int getQuantity() {
		return e.getQuantity();      
	}
	    
	/**
	 * <code>getTriangleCount</code> returns the number of triangles contained
	 * in all sub-branches of this node that contain geometry.
	 * @return the triangle count of this branch.
	 */
	@Override
	public int getTriangleCount() {
		return e.getTriangleCount();
	}
	    
	    /**
	     * <code>getVertexCount</code> returns the number of vertices contained
	     * in all sub-branches of this node that contain geometry.
	     * @return the vertex count of this branch.
	     */
	@Override
	public int getVertexCount() {
			    	return e.getVertexCount();
	}

	    /**
	     * 
	     * <code>attachChild</code> attaches a child to this node. This node
	     * becomes the child's parent. The current number of children maintained is
	     * returned.
	     * <br>
	     * If the child already had a parent it is detached from that former parent.
	     * 
	     * @param child
	     *            the child to attach to this node.
	     * @return the number of children maintained by this node.
	     */
	@Override
    public int attachChild(Spatial child) {
	    	return e.attachChild(child);
	}
	    
	    /**
	     * 
	     * <code>attachChildAt</code> attaches a child to this node at an index. This node
	     * becomes the child's parent. The current number of children maintained is
	     * returned.
	     * <br>
	     * If the child already had a parent it is detached from that former parent.
	     * 
	     * @param child
	     *            the child to attach to this node.
	     * @return the number of children maintained by this node.
	     */
	@Override
	public int attachChildAt(Spatial child, int index) {
		return e.attachChildAt(child, index);
	}

	    /**
	     * <code>detachChild</code> removes a given child from the node's list.
	     * This child will no longe be maintained.
	     * 
	     * @param child
	     *            the child to remove.
	     * @return the index the child was at. -1 if the child was not in the list.
	     */
	@Override
	public int detachChild(Spatial child) {
		return e.detachChild(child);     
	}

	    /**
	     * <code>detachChild</code> removes a given child from the node's list.
	     * This child will no longe be maintained. Only the first child with a
	     * matching name is removed.
	     * 
	     * @param childName
	     *            the child to remove.
	     * @return the index the child was at. -1 if the child was not in the list.
	     */
	@Override
	public int detachChildNamed(String childName) {
	       return e.detachChildNamed(childName);
	    }

	    /**
	     * 
	     * <code>detachChildAt</code> removes a child at a given index. That child
	     * is returned for saving purposes.
	     * 
	     * @param index
	     *            the index of the child to be removed.
	     * @return the child at the supplied index.
	     */
	@Override
	public Spatial detachChildAt(int index) {
	    	return e.detachChildAt(index);
	    }

	/**
	 * 
	 * <code>detachAllChildren</code> removes all children attached to this
	 * node.
	 */
	@Override
	public void detachAllChildren() {
		e.detachAllChildren();
	}
	
	@Override
	public int getChildIndex(Spatial sp) {
	        return e.getChildIndex(sp);
	    }
	
	@Override
	public void swapChildren(int index1, int index2) {
	    	e.swapChildren(index1, index2);
	    }
	
	@Override
	public Spatial getChild(int i) {
	        return e.getChild(i);
	    }
	
	    /**
	     * <code>getChild</code> returns the first child found with exactly the
	     * given name (case sensitive.)
	     * 
	     * @param name
	     *            the name of the child to retrieve. If null, we'll return null.
	     * @return the child if found, or null.
	     */
	@Override
	public Spatial getChild(String name) {
	    	return e.getChild(name);
	    }
	    
	    /**
	     * determines if the provided Spatial is contained in the children list of
	     * this node.
	     * 
	     * @param spat
	     *            the child object to look for.
	     * @return true if the object is contained, false otherwise.
	     */
	@Override
	public boolean hasChild(Spatial spat) {
	    	return e.hasChild(spat);
	    }

	    /**
	     * <code>updateWorldData</code> updates all the children maintained by
	     * this node.
	     * 
	     * @param time
	     *            the frame time.
	     */
	@Override
	public void updateWorldData(float time) {
		e.updateWorldData(time);
	}

	@Override
	public void updateWorldVectors(boolean recurse) {
		e.updateWorldVectors(recurse);	
	}
	
	@Override
	public void lockBounds() {
		e.lockBounds();
	}

	@Override
	public void lockShadows() {
		e.lockShadows();
	}
	    
	@Override
	public void lockTransforms() {
		e.lockTransforms();
	}

	@Override
	public void lockMeshes(Renderer r) {
		e.lockMeshes( r );
	}
    
    @Override
    public void unlockBounds() {
    	e.unlockBounds();
    }
    
    @Override
    public void unlockShadows() {
    	e.unlockShadows();
    }
    
    @Override
    public void unlockTransforms() {
    	e.unlockTransforms();
    }

    @Override
    public void unlockMeshes(Renderer r) {
    	e.unlockMeshes( r );
    }

    /**
     * <code>draw</code> calls the onDraw method for each child maintained by
     * this node.
     * 
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     * @param r
     *            the renderer to draw to.
     */
    @Override
    public void draw(Renderer r) {
        e.draw(r);
    }

    /**
     * Applies the stack of render states to each child by calling
     * updateRenderState(states) on each child.
     * 
     * @param states
     *            The Stack[] of render states to apply to each child.
     */
/*	    @Override
    protected void applyRenderState(Stack<? extends RenderState>[] states) {
        if(children == null) {
            return;
        }
        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            Spatial pkChild = getChild(i);
            if (pkChild != null)
                pkChild.updateRenderState(states);
        }
    }*/

    @Override
    public void sortLights() {
    	e.sortLights();
    }

    /**
     * <code>updateWorldBound</code> merges the bounds of all the children
     * maintained by this node. This will allow for faster culling operations.
     * 
     * @see com.jme.scene.Spatial#updateWorldBound()
     */
    @Override
    public void updateWorldBound() {
    	e.updateWorldBound();
    }

    public void updateModelBound() {
    	e.updateModelBound();
    }
    
    @Override
    public void findCollisions(Spatial scene, CollisionResults results) {
    	e.findCollisions(scene, results);
    }

    @Override
    public boolean hasCollision(Spatial scene, boolean checkTriangles) {
        return e.hasCollision(scene, checkTriangles);
    }

    @Override
    public void findPick(Ray toTest, PickResults results) {
    	e.findPick(toTest, results);
    }

	/**
	 * Returns all children to this node.
	 *
	 * @return a list containing all children to this node
	 */
	public List<Spatial> getChildren() {
        return e.getChildren();
    }
	
    public void childChange(Geometry geometry, int index1, int index2) {
    	e.childChange(geometry, index1, index2);
    }

    public void setModelBound(BoundingVolume modelBound) {
    	e.setModelBound(modelBound);
    }	

    /**
     * @return space this node belongs to, must not be null
     */
    public PhysicsSpace getSpace() {
    	return e.getSpace();
    }

    /**
     * @return true if this is a static (passive, immovable) node.
     */
    public boolean isStatic() {
    	return e.isStatic();
    }

    /**
     * This method generates physics geometry bounds for detecting collision from the graphical representation in this
     * PhysicsNode.
     *
     * @throws IllegalStateException if no graphical representation is present (no Geometries within this Node)
     * @see PhysicsCollisionGeometry
     */
    public void generatePhysicsGeometry() {
        generatePhysicsGeometry( false );
    }

    /**
     * This method generates physics geometry bounds for detecting collision from the graphical representation in this
     * PhysicsNode.
     *
     * @param useTriangleAccurateGeometries true to use triangle accuracy for collision detection with arbitrary
     * geometries - use with care! (makes it expensive to compute collisions)
     * @throws IllegalStateException if no graphical representation is present (no Geometries within this Node)
     * @see PhysicsCollisionGeometry
     */
    public void generatePhysicsGeometry( boolean useTriangleAccurateGeometries ) {
    	generatePhysicsGeometry( this, e, useTriangleAccurateGeometries );
    }

    /**
     * This method generates physics geometry bounds for detecting collision from the graphical representation in this
     * PhysicsNode.
     *
     * @param source The Node whose children are traversed to create corresponding collision geometries
     * @param target The PhysicsNode to which the collision geometries are added. Can be the same object as <b>source</b>.
     * @param useTriangleAccurateGeometries true to use triangle accuracy for collision detection with arbitrary
     * geometries - use with care! (makes it expensive to compute collisions)
     * @throws IllegalStateException if no graphical representation is present (no Geometries within this Node)
     * @see PhysicsCollisionGeometry
     */
    public static void generatePhysicsGeometry( Node source, PhysicsNode target, boolean useTriangleAccurateGeometries ) {
    	generatePhysicsGeometry( source, target, useTriangleAccurateGeometries, null );
    }

    /**
     * This method generates physics geometry bounds for detecting collision from the graphical representation in this
     * PhysicsNode.
     *
     * @param source The Node whose children are traversed to create corresponding collision geometries
     * @param target The PhysicsNode to which the collision geometries are added. Can be the same object as <b>source</b>.
     * @param useTriangleAccurateGeometries true to use triangle accuracy for collision detection with arbitrary
     * geometries - use with care! (makes it expensive to compute collisions)
     * @param collisionGeometryMap If a map is passed, each spatial for which a collision geometry is generated
     * will be put into it with the collision geometry as value. Also, Spatials already inside the map won't have
     * any collision geometry generated. Pass null to avoid this.
     * @throws IllegalStateException if no graphical representation is present (no Geometries within this Node)
     * @see PhysicsCollisionGeometry
     */
    public static void generatePhysicsGeometry( Node source, PhysicsNode target, boolean useTriangleAccurateGeometries, Map<Spatial, PhysicsCollisionGeometry> collisionGeometryMap ) {
    	
    }

    /**
     * overridden to check we don't get another PhysicsNode as parent.
     *
     * @param parent new Parent
     * @see com.jme.scene.Spatial#setParent(com.jme.scene.Node)
     */
    @Override
    protected void setParent( Node parent ) {
        if ( parent != null ) {
            Node ancestor = parent;
            while ( ancestor != null ) {
                if ( ancestor instanceof DynamicPhysicsNode ) {
                    throw new IllegalArgumentException( "DynamicPhysicsNodes cannot contain other PhysicsNodes!" );
                }
                ancestor = ancestor.getParent();
            }
        }
        super.setParent( parent );
    }


    /**
     * Create a physics sphere.
     *
     * @param name name of the Spatial
     * @return a new physics sphere
     * @see PhysicsCollisionGeometry
     * @see PhysicsSphere
     */
    public PhysicsSphere createSphere( String name ) {
        return e.createSphere(name);
    }

    /**
     * Create a physics box.
     *
     * @param name name of the Spatial
     * @return a new physics box
     * @see PhysicsCollisionGeometry
     * @see PhysicsBox
     */
    public PhysicsBox createBox( String name ) {
        return e.createBox( name );
    }

    /**
     * Create a physics cylinder.
     *
     * @param name name of the Spatial
     * @return a new physics cylinder
     * @see PhysicsCollisionGeometry
     * @see PhysicsCylinder
     */
    public PhysicsCylinder createCylinder( String name ) {
        return e.createCylinder( name );
    }

    /**
     * Create a physics capsule.
     *
     * @param name name of the Spatial
     * @return a new physics capsule
     * @see PhysicsCollisionGeometry
     * @see PhysicsCapsule
     */
    public PhysicsCapsule createCapsule( String name ) {
        return e.createCapsule( name );
    }

    /**
     * Create a physics mesh.
     *
     * @param name name of the Spatial
     * @return a new physics mesh
     * @see PhysicsCollisionGeometry
     * @see PhysicsMesh
     */
    public PhysicsMesh createMesh( String name ) {
        return e.createMesh( name );
    }

    /**
     * Create a physics ray.
     *
     * @param name name of the Spatial
     * @return a new physics ray
     * @see PhysicsCollisionGeometry
     * @see PhysicsRay
     */
    public PhysicsRay createRay( String name ) {
        return e.createRay( name );
    }

    private boolean active;

    /**
     * @return true if node is currently active
     */
    public final boolean isActive() {
        return active;
    }

    /**
     * Activate the node when added to a space. Deactivate when removed.
     *
     * @param value true when activated
     * @return true if node was (de)activated, false if state was already set to value
     */
    public boolean setActive( boolean value ) {
        return e.setActive(value);
    }

    /**
     * Query default material of this node. If no material was set for a geometry the material of this
     * physics node is returned.
     *
     * @return material used for inheriting geometries, not null
     * @see PhysicsCollisionGeometry#getMaterial
     */
    public Material getMaterial() {
        return e.getMaterial();
    }

    /**
     * store the value for field material
     */
    private Material material;

    /**
     * Change material of this node. All geometries that did not have a matrial set as well as those with the same
     * material like the node inherit this nodes material.
     *
     * @param value new material
     * @see PhysicsCollisionGeometry#setMaterial
     */
    public void setMaterial( final Material value ) {
        this.material = value;
    }

    /**
     * Creates a synthetic button that is triggered when this node collides with another node.
     * <p>
     * Note: if this event handler is obtained it <i>must</i> be used with an InputHandler which is updated regularly
     *
     * @return a synthetic button that is triggered on a collision event that involves this node
     * @see PhysicsSpace#getCollisionEventHandler()
     */
    public SyntheticButton getCollisionEventHandler() {
        return e.getCollisionEventHandler();
    }

    public void delete() {
    	e.delete();
    }

    /**
     * @return true if the physics engine believes that this nodes is resting like described in the
     * {@link DynamicPhysicsNode#rest} method. For static nodes this return true, always.
     * @see DynamicPhysicsNode#rest
     */
    public boolean isResting() {
    	return e.isResting();
    }

    public final PhysicsNode getPhysicsNode() {
        return e;
    }

	@SuppressWarnings("unchecked")
	@Override
    public Class getClassTag() {
    		return this.getClass();
    }

    /**
     * @return current value of the field collisionGroup
     */
    public CollisionGroup getCollisionGroup() {
        return e.getCollisionGroup();
    }

    /**
     * @see CollisionGroup
     * @param value new value for field collisionGroup
     * @return true if collisionGroup was changed
     */
    public void setCollisionGroup( CollisionGroup value ) {
        e.setCollisionGroup(value);
    }
    
	@Override
	public void read(JMEImporter im) throws IOException {
		super.read(im);

		e.read(im);
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		super.write(ex);

        e.write(ex);
	}

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
/*	    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        if (children != null) {
            // go through children and set parent to this node
            for (int x = 0, cSize = children.size(); x < cSize; x++) {
                Spatial child = children.get(x);
                child.parent = this;
            }
        }
    }
*/

/*    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        e.getCapsule(this).writ
        if (children == null)
            e.getCapsule(this).writeSavableArrayList(null, "children", null);
        else
            e.getCapsule(this).writeSavableArrayList(new ArrayList<Spatial>(children), "children", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        ArrayList<Spatial> cList = e.getCapsule(this).readSavableArrayList("children", null);
        if (cList == null)
            children = null;
        else
            children = Collections.synchronizedList(cList);

        // go through children and set parent to this node
        if (children != null) {
            for (int x = 0, cSize = children.size(); x < cSize; x++) {
                Spatial child = children.get(x);
                child.parent = this;
            }
        }
    }
*/
}    