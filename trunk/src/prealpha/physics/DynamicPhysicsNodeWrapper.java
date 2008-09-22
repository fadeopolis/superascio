package prealpha.physics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
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
import com.jme.math.Matrix3f;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.UserDataManager;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.Spatial.NormalsMode;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Capsule;
import com.jme.scene.shape.GeoSphere;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jmex.physics.CollisionGroup;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsDebugger;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.PhysicsUpdateCallback;
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
public class DynamicPhysicsNodeWrapper<E  extends DynamicPhysicsNode> extends Node implements PhysicsSpatial{
	private static final Logger logger = Logger.getLogger(Node.class.getName());
	
	protected Vector3f vbuff = new Vector3f();
	protected float fbuff = 0;
	
	E e;
	
	public DynamicPhysicsNodeWrapper(E e) {
		this(null, e);
	}
	
	public DynamicPhysicsNodeWrapper(String name, E e) {
		super(name + " Wrapper");

		this.e = e;
	}

/** methods from spatial
 * 	
 */
	
    public transient float queueDistance = Float.NEGATIVE_INFINITY;

    /**
     * Adds a Controller to this Spatial's list of controllers.
     * 
     * @param controller
     *            The Controller to add
     * @see com.jme.scene.Controller
     */
    public void addController(Controller controller) {
        e.addController(controller);
    }

    /**
     * Removes a Controller from this Spatial's list of controllers, if it
     * exist.
     * 
     * @param controller
     *            The Controller to remove
     * @return True if the Controller was in the list to remove.
     * @see com.jme.scene.Controller
     */
    public boolean removeController(Controller controller) {
        return e.removeController(controller);
    }

    /**
     * Removes all Controllers from this Spatial's list of controllers.
     * 
     * @see com.jme.scene.Controller
     */
    public void clearControllers() {
    	e.clearControllers();
    }

    /**
     * Returns the controller in this list of controllers at index i.
     * 
     * @param i
     *            The index to get a controller from.
     * @return The controller at index i.
     * @see com.jme.scene.Controller
     */
    public Controller getController(int i) {
    	return e.getController(i);
    }

    /**
     * Returns the ArrayList that contains this spatial's Controllers.
     * 
     * @return This spatial's geometricalControllers.
     */
    public ArrayList<Controller> getControllers() {
        if (geometricalControllers == null) {
            geometricalControllers = new ArrayList<Controller>(1);
        }
        return geometricalControllers;
    }

    /**
     * @return the number of controllers set on this Spatial.
     */
    public int getControllerCount() {
        if (geometricalControllers == null) {
            return 0;
        }
        return geometricalControllers.size();
    }

    /**
     * <code>onDraw</code> checks the spatial with the camera to see if it
     * should be culled, if not, the node's draw method is called.
     * <p>
     * This method is called by the renderer. Usually it should not be called
     * directly.
     * 
     * @param r
     *            the renderer used for display.
     */
    public void onDraw(Renderer r) {
        e.onDraw(r);
    }

    /**
     * <code>getWorldRotation</code> retrieves the absolute rotation of the
     * Spatial.
     * 
     * @return the Spatial's world rotation matrix.
     */
    public Quaternion getWorldRotation() {
        return worldRotation;
    }

    /**
     * <code>getWorldTranslation</code> retrieves the absolute translation of
     * the spatial.
     * 
     * @return the world's tranlsation vector.
     */
    public Vector3f getWorldTranslation() {
        return worldTranslation;
    }

    /**
     * <code>getWorldScale</code> retrieves the absolute scale factor of the
     * spatial.
     * 
     * @return the world's scale factor.
     */
    public Vector3f getWorldScale() {
        return worldScale;
    }

    /**
     * <code>rotateUpTo</code> is a util function that alters the
     * localrotation to point the Y axis in the direction given by newUp.
     * 
     * @param newUp
     *            the up vector to use - assumed to be a unit vector.
     */
    public void rotateUpTo(Vector3f newUp) {
       e.rotateUpTo(newUp);
    }

    /**
     * <code>lookAt</code> is a convienence method for auto-setting the local
     * rotation based on a position and an up vector. It computes the rotation
     * to transform the z-axis to point onto 'position' and the y-axis to 'up'.
     * Unlike {@link Quaternion#lookAt} this method takes a world position to
     * look at not a relative direction.
     * 
     * @param position
     *            where to look at in terms of world coordinates
     * @param upVector
     *            a vector indicating the (local) up direction. (typically {0,
     *            1, 0} in jME.)
     */
    public void lookAt(Vector3f position, Vector3f upVector) {
        e.lookAt(position, upVector);
    }

    /**
     * <code>updateGeometricState</code> updates all the geometry information
     * for the node.
     * 
     * @param time
     *            the frame time.
     * @param initiator
     *            true if this node started the update process.
     */
    public void updateGeometricState(float time, boolean initiator) {
        if ((lockedMode & Spatial.LOCKED_BRANCH) != 0)
            return;
        updateWorldData(time);
        if ((lockedMode & Spatial.LOCKED_BOUNDS) == 0) {
            updateWorldBound();
            if (initiator) {
                propagateBoundToRoot();
            }
        }
    }

    /**
     * If not locked, updates worldscale, worldrotation and worldtranslation
     */
    public void updateWorldVectors() {
        updateWorldVectors(false);
    }


    protected void updateWorldTranslation() {
        if (parent != null) {
            worldTranslation = parent.localToWorld(localTranslation,
                    worldTranslation);
        } else {
            worldTranslation.set(localTranslation);
        }
    }

    protected void updateWorldRotation() {
        if (parent != null) {
            parent.getWorldRotation().mult(localRotation, worldRotation);
        } else {
            worldRotation.set(localRotation);
        }
    }

    protected void updateWorldScale() {
        if (parent != null) {
            worldScale.set(parent.getWorldScale()).multLocal(localScale);
        } else {
            worldScale.set(localScale);
        }
    }

    /**
     * Convert a vector (in) from this spatials local coordinate space to world
     * coordinate space.
     * 
     * @param in
     *            vector to read from
     * @param store
     *            where to write the result (null to create a new vector, may be
     *            same as in)
     * @return the result (store)
     */
    public Vector3f localToWorld(final Vector3f in, Vector3f store) {
        if (store == null)
            store = new Vector3f();
        // multiply with scale first, then rotate, finally translate (cf.
        // Eberly)
        return getWorldRotation().mult(
                store.set(in).multLocal(getWorldScale()), store).addLocal(
                getWorldTranslation());
    }

    /**
     * Convert a vector (in) from world coordinate space to this spatials local
     * coordinate space.
     * 
     * @param in
     *            vector to read from
     * @param store
     *            where to write the result
     * @return the result (store)
     */
    public Vector3f worldToLocal(final Vector3f in, final Vector3f store) {
        in.subtract(getWorldTranslation(), store).divideLocal(getWorldScale());
        getWorldRotation().inverse().mult(store, store);
        return store;
    }

    /**
     * <code>getParent</code> retrieve's this node's parent. If the parent is
     * null this is the root node.
     * 
     * @return the parent of this node.
     */
    public Node getParent() {
        return parent;
    }


    /**
     * <code>removeFromParent</code> removes this Spatial from it's parent.
     * 
     * @return true if it has a parent and performed the remove.
     */
    public boolean removeFromParent() {
        if (parent != null) {
            parent.detachChild(this);
            return true;
        }
        return false;
    }

    /**
     * determines if the provided Node is the parent, or parent's parent, etc. of this Spatial.
     * 
     * @param ancestor
     *            the ancestor object to look for.
     * @return true if the ancestor is found, false otherwise.
     */
    public boolean hasAncestor(Node ancestor) {
        if (parent == null) {
            return false;
        } else if (parent.equals(ancestor)) {
            return true;
        } else {
            return parent.hasAncestor(ancestor);
        }
    }
    
    /**
     * <code>getLocalRotation</code> retrieves the local rotation of this
     * node.
     * 
     * @return the local rotation of this node.
     */
    public Quaternion getLocalRotation() {
        return e.getLocalRotation();
    }

    /**
     * <code>setLocalRotation</code> sets the local rotation of this node.
     * 
     * @param rotation
     *            the new local rotation.
     */
    public void setLocalRotation(Matrix3f rotation) {
       e.setLocalRotation(rotation);
    }

    /**
     * <code>setLocalRotation</code> sets the local rotation of this node,
     * using a quaterion to build the matrix.
     * 
     * @param quaternion
     *            the quaternion that defines the matrix.
     */
    public void setLocalRotation(Quaternion quaternion) {
        e.setLocalRotation(quaternion);
    }

    /**
     * <code>getLocalScale</code> retrieves the local scale of this node.
     * 
     * @return the local scale of this node.
     */
    public Vector3f getLocalScale() {
        return e.getLocalScale();
    }

    /**
     * <code>setLocalScale</code> sets the local scale of this node.
     * 
     * @param localScale
     *            the new local scale, applied to x, y and z
     */
    public void setLocalScale(float localScale) {
        e.setLocalScale(localScale);
    }

    /**
     * <code>setLocalScale</code> sets the local scale of this node.
     * 
     * @param localScale
     *            the new local scale.
     */
    public void setLocalScale(Vector3f localScale) {
    	e.setLocalScale(localScale);
    }

    /**
     * <code>getLocalTranslation</code> retrieves the local translation of
     * this node.
     * 
     * @return the local translation of this node.
     */
    public Vector3f getLocalTranslation() {
        return e.getLocalTranslation();
    }

    /**
     * <code>setLocalTranslation</code> sets the local translation of this
     * node.
     * 
     * @param localTranslation
     *            the local translation of this node.
     */
    public void setLocalTranslation(Vector3f localTranslation) {
        this.localTranslation = localTranslation;
        this.worldTranslation.set(this.localTranslation);
        e.setLocalTranslation(localTranslation);
    }

    public void setLocalTranslation(float x, float y, float z) {
        localTranslation.set(x, y, z);
        worldTranslation.set(localTranslation);
        e.setLocalTranslation(x,y,z);
    }

    /**
     * Sets the zOrder of this Spatial and, if setOnChildren is true, all
     * children as well. This value is used in conjunction with the RenderQueue
     * and QUEUE_ORTHO for determining draw order.
     * 
     * @param zOrder
     *            the new zOrder.
     * @param setOnChildren
     *            if true, children will also have their zOrder set to the given
     *            value.
     */
    public void setZOrder(int zOrder, boolean setOnChildren) {
        setZOrder(zOrder);
        if (setOnChildren) {
            if (this instanceof Node) {
                Node n = (Node) this;
                if (n.getChildren() != null) {
                    for (Spatial child : n.getChildren()) {
                        child.setZOrder(zOrder, true);
                    }
                }
            }
        }
    }

    /**
     * @see #setCullHint(CullHint)
     * @return the cull mode of this spatial, or if set to INHERIT, the cullmode
     *         of it's parent.
     */
    public CullHint getCullHint() {
        if (cullHint != CullHint.Inherit)
            return cullHint;
        else if (parent != null)
            return parent.getCullHint();
        else
            return CullHint.Dynamic;
    }

    /**
     * Returns this spatial's texture combine mode. If the mode is set to
     * inherit, then the spatial gets its combine mode from its parent.
     * 
     * @return The spatial's texture current combine mode.
     */
    public TextureCombineMode getTextureCombineMode() {
        if (textureCombineMode != TextureCombineMode.Inherit)
            return textureCombineMode;
        else if (parent != null)
            return parent.getTextureCombineMode();
        else
            return TextureCombineMode.CombineClosest;
    }

    /**
     * Returns this spatial's light combine mode. If the mode is set to inherit,
     * then the spatial gets its combine mode from its parent.
     * 
     * @return The spatial's light current combine mode.
     */
    public LightCombineMode getLightCombineMode() {
        if (lightCombineMode != LightCombineMode.Inherit)
            return lightCombineMode;
        else if (parent != null)
            return parent.getLightCombineMode();
        else
            return LightCombineMode.CombineFirst;
    }

    /**
     * Returns this spatial's renderqueue mode. If the mode is set to inherit,
     * then the spatial gets its renderqueue mode from its parent.
     * 
     * @return The spatial's current renderqueue mode.
     */
    public int getRenderQueueMode() {
        if (renderQueueMode != Renderer.QUEUE_INHERIT)
            return renderQueueMode;
        else if (parent != null)
            return parent.getRenderQueueMode();
        else
            return Renderer.QUEUE_SKIP;
    }

    /**
     * Returns this spatial's normals mode. If the mode is set to inherit, then
     * the spatial gets its normals mode from its parent.
     * 
     * @return The spatial's current normals mode.
     */
    public NormalsMode getNormalsMode() {
        if (normalsMode != NormalsMode.Inherit)
            return normalsMode;
        else if (parent != null)
            return parent.getNormalsMode();
        else
            return NormalsMode.NormalizeIfScaled;
    }

    /**
     * Called during updateRenderState(Stack[]), this function goes up the scene
     * graph tree until the parent is null and pushes RenderStates onto the
     * states Stack array.
     * 
     * @param states
     *            The Stack[] to push states onto.
     */
    @SuppressWarnings("unchecked")
    public void propagateStatesFromRoot(Stack[] states) {
        // traverse to root to allow downward state propagation
        if (parent != null)
            parent.propagateStatesFromRoot(states);

        // push states onto current render state stack
        for (int x = 0; x < RenderState.RS_MAX_STATE; x++)
            if (getRenderState(x) != null)
                states[x].push(getRenderState(x));
    }

    /**
     * <code>propagateBoundToRoot</code> passes the new world bound up the
     * tree to the root.
     */
    public void propagateBoundToRoot() {
        if (parent != null) {
            parent.updateWorldBound();
            parent.propagateBoundToRoot();
        }
    }

    /**
     * <code>calculateCollisions</code> calls findCollisions to populate the
     * CollisionResults object then processes the collision results.
     * 
     * @param scene
     *            the scene to test against.
     * @param results
     *            the results object.
     */
    public void calculateCollisions(Spatial scene, CollisionResults results) {
        findCollisions(scene, results);
        results.processCollisions();
    }


    public void calculatePick(Ray ray, PickResults results) {
        findPick(ray, results);
        results.processPick();
    }



    /**
     * Stores user define data for this Spatial.
     * 
     * @param key
     *            the key component to retrieve the data from the hash map.
     * @param data
     *            the data to store.
     */
    public void setUserData(String key, Savable data) {
        UserDataManager.getInstance().setUserData(this, key, data);
    }

    /**
     * Retrieves user data from the hashmap defined by the provided key.
     * 
     * @param key
     *            the key of the data to obtain.
     * @return the data referenced by the key. If the key is invalid, null is
     *         returned.
     */
    public Savable getUserData(String key) {
        return UserDataManager.getInstance().getUserData(this, key);
    }

    /**
     * Removes user data from the hashmap defined by the provided key.
     * 
     * @param key
     *            the key of the data to remove.
     * @return the data that has been removed, null if no data existed.
     */
    public Savable removeUserData(String key) {
        return UserDataManager.getInstance().removeUserData(this, key);
    }

    /**
     * Sets the name of this spatial.
     * 
     * @param name
     *            The spatial's new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this spatial.
     * 
     * @return This spatial's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets if this Spatial is to be used in intersection (collision and
     * picking) calculations. By default this is true.
     * 
     * @param isCollidable
     *            true if this Spatial is to be used in intersection
     *            calculations, false otherwise.
     */
    public void setIsCollidable(boolean isCollidable) {
        this.isCollidable = isCollidable;
    }

    /**
     * Defines if this Spatial is to be used in intersection (collision and
     * picking) calculations. By default this is true.
     * 
     * @return true if this Spatial is to be used in intersection calculations,
     *         false otherwise.
     */
    public boolean isCollidable() {
        return this.isCollidable;
    }

    /**
     * <code>getWorldBound</code> retrieves the world bound at this node
     * level.
     * 
     * @return the world bound at this level.
     */
    public BoundingVolume getWorldBound() {
        return e.getWorldBound();
    }

    /**
     * <code>setCullHint</code> sets how scene culling should work on this
     * spatial during drawing. CullHint.Dynamic: Determine via the defined
     * Camera planes whether or not this Spatial should be culled.
     * CullHint.Always: Always throw away this object and any children during
     * draw commands. CullHint.Never: Never throw away this object (always draw
     * it) CullHint.Inherit: Look for a non-inherit parent and use its cull
     * mode. NOTE: You must set this AFTER attaching to a parent or it will be
     * reset with the parent's cullMode value.
     * 
     * @param hint
     *            one of CullHint.Dynamic, CullHint.Always, CullHint.Inherit or
     *            CullHint.Never
     */
    public void setCullHint(CullHint hint) {
        cullHint = hint;
    }

    /**
     * @return the cullmode set on this Spatial
     */
    public CullHint getLocalCullHint() {
        return cullHint;
    }

    /**
     * Calling this method tells the scenegraph that it is not necessary to
     * traverse this Spatial or any below it during the update phase. This
     * should be called *after* any other lock call to ensure they are able to
     * update any bounds or vectors they might need to update.
     * 
     * @see #unlockBranch()
     */
    public void lockBranch() {
        e.lockBranch();
    }

    /**
     * Flags this spatial and those below it that any meshes in the specified
     * scenegraph location or lower will not have changes in vertex, texcoord,
     * normal or color data. This allows optimizations by the engine such as
     * creating display lists from the data. Calling this method does not
     * provide a guarentee that data changes will not be allowed or will/won't
     * show up in the scene. It is merely a hint to the engine. Calls
     * lockMeshes(Renderer) with the current display system's renderer.
     * 
     * @see #lockMeshes(Renderer)
     */
    public void lockMeshes() {
       e.lockMeshes();
    }

    /**
     * Convienence function for locking all aspects of a Spatial.
     * 
     * @see #lockBounds()
     * @see #lockTransforms()
     * @see #lockMeshes(Renderer)
     * @see #lockShadows()
     */
    public void lock(Renderer r) {
        lockBounds();
        lockTransforms();
        lockMeshes(r);
        lockShadows();
    }

    /**
     * Convienence function for locking all aspects of a Spatial. For lockMeshes
     * it calls:
     * <code>lockMeshes(DisplaySystem.getDisplaySystem().getRenderer());</code>
     * 
     * @see #lockBounds()
     * @see #lockTransforms()
     * @see #lockMeshes()
     * @see #lockShadows()
     */
    public void lock() {
        lockBounds();
        lockTransforms();
        lockMeshes();
        lockShadows();
    }

    /**
     * Flags this Spatial and any below it as being traversable during the
     * update phase.
     * 
     * @see #lockBranch()
     */
    public void unlockBranch() {
        e.unlockBranch();
    }

    /**
     * Flags this spatial and those below it to allow for mesh updating (the
     * default). Generally this means that any display lists setup will be
     * erased and released. Calls unlockMeshes(Renderer) with the current
     * display system's renderer.
     * 
     * @see #unlockMeshes(Renderer)
     */
    public void unlockMeshes() {
        unlockMeshes(DisplaySystem.getDisplaySystem().getRenderer());
    }

    /**
     * Convienence function for unlocking all aspects of a Spatial.
     * 
     * @see #unlockBounds()
     * @see #unlockTransforms()
     * @see #unlockMeshes(Renderer)
     * @see #unlockShadows()
     * @see #unlockBranch()
     */
    public void unlock(Renderer r) {
        unlockBounds();
        unlockTransforms();
        unlockMeshes(r);
        unlockShadows();
        unlockBranch();
    }

    /**
     * Convienence function for unlocking all aspects of a Spatial. For
     * unlockMeshes it calls:
     * <code>unlockMeshes(DisplaySystem.getDisplaySystem().getRenderer());</code>
     * 
     * @see #unlockBounds()
     * @see #unlockTransforms()
     * @see #unlockMeshes()
     * @see #unlockShadows()
     * @see #unlockBranch()
     */
    public void unlock() {
        unlockBounds();
        unlockTransforms();
        unlockMeshes();
        unlockShadows();
        unlockBranch();
    }

    /**
     * @return a bitwise combination of the current locks established on this
     *         Spatial.
     */
    public int getLocks() {
        return lockedMode;
    }

    /**
     * Note: Uses the currently set Renderer to generate a display list if
     * LOCKED_MESH_DATA is set.
     * 
     * @param locks
     *            a bitwise combination of the locks to establish on this
     *            Spatial.
     */
    public void setLocks(int locks) {
        if ((lockedMode & Spatial.LOCKED_BOUNDS) != 0)
            lockBounds();
        if ((lockedMode & Spatial.LOCKED_MESH_DATA) != 0)
            lockMeshes();
        if ((lockedMode & Spatial.LOCKED_SHADOWS) != 0)
            lockShadows();
        if ((lockedMode & Spatial.LOCKED_TRANSFORMS) != 0)
            lockTransforms();
    }

    /**
     * @param locks
     *            a bitwise combination of the locks to establish on this
     *            Spatial.
     * @param r
     *            the renderer to create display lists with if LOCKED_MESH_DATA
     *            is set.
     */
    public void setLocks(int locks, Renderer r) {
        if ((lockedMode & Spatial.LOCKED_BOUNDS) != 0)
            lockBounds();
        if ((lockedMode & Spatial.LOCKED_MESH_DATA) != 0)
            lockMeshes(r);
        if ((lockedMode & Spatial.LOCKED_SHADOWS) != 0)
            lockShadows();
        if ((lockedMode & Spatial.LOCKED_TRANSFORMS) != 0)
            lockTransforms();
    }

     /**
     * Updates the render state values of this Spatial and and children it has.
     * Should be called whenever render states change.
     */
    public void updateRenderState() {
        updateRenderState(null);
    }

    /**
     * Called internally. Updates the render states of this Spatial. The stack
     * contains parent render states.
     * 
     * @param parentStates
     *            The list of parent renderstates.
     */
    @SuppressWarnings("unchecked")
    protected void updateRenderState(Stack[] parentStates) {
        boolean initiator = (parentStates == null);

        // first we need to get all the states from parent to us.
        if (initiator) {
            // grab all states from root to here.
            parentStates = new Stack[RenderState.RS_MAX_STATE];
            for (int x = 0; x < parentStates.length; x++)
                parentStates[x] = new Stack<RenderState>();
            propagateStatesFromRoot(parentStates);
        } else {
            for (int x = 0; x < RenderState.RS_MAX_STATE; x++) {
                if (getRenderState(x) != null)
                    parentStates[x].push(getRenderState(x));
            }
        }

        applyRenderState(parentStates);

        // restore previous if we are not the initiator
        if (!initiator) {
            for (int x = 0; x < RenderState.RS_MAX_STATE; x++)
                if (getRenderState(x) != null)
                    parentStates[x].pop();
        }

    }

    /**
     * Called during updateRenderState(Stack[]), this function determines how
     * the render states are actually applied to the spatial and any children it
     * may have. By default, this function does nothing.
     * 
     * @param states
     *            An array of stacks for each state.
     */
    protected void applyRenderState(Stack<? extends RenderState>[] states) {
    }
    
    /**
     * <code>setRenderState</code> sets a render state for this node. Note,
     * there can only be one render state per type per node. That is, there can
     * only be a single BlendState a single TextureState, etc. If there is
     * already a render state for a type set the old render state will be
     * returned. Otherwise, null is returned.
     * 
     * @param rs
     *            the render state to add.
     * @return the old render state.
     */
    public RenderState setRenderState(RenderState rs) {
        if (rs == null) {
            return null;
        }

        if (renderStateList == null) {
            renderStateList = new RenderState[RenderState.RS_MAX_STATE];
        }

        RenderState oldState = renderStateList[rs.getType()];
        renderStateList[rs.getType()] = rs;
        return oldState;
    }

    /**
     * Returns the requested RenderState that this Spatial currently has set or
     * null if none is set.
     * 
     * @param type
     *            the renderstate type to retrieve
     * @return a renderstate at the given position or null
     */
    public RenderState getRenderState(int type) {
        return renderStateList != null ? renderStateList[type] : null;
    }

    /**
     * Clears a given render state index by setting it to null.
     * 
     * @param renderStateType
     *            The index of a RenderState to clear
     * @see com.jme.scene.state.RenderState#getType()
     */
    public void clearRenderState(int renderStateType) {
        if (renderStateList != null) {
            renderStateList[renderStateType] = null;
        }
    }

    /**
     * <code>setRenderQueueMode</code> determines at what phase of the
     * rendering proces this Spatial will rendered. There are 4 different
     * phases: QUEUE_SKIP - The spatial will be drawn as soon as possible,
     * before the other phases of rendering. QUEUE_OPAQUE - The renderer will
     * try to find the optimal order for rendering all objects using this mode.
     * You should use this mode for most normal objects, except transparant
     * ones, as it could give a nice performance boost to your application.
     * QUEUE_TRANSPARENT - This is the mode you should use for object with
     * transparancy in them. It will ensure the objects furthest away are
     * rendered first. That ensures when another transparent object is drawn on
     * top of previously drawn objects, you can see those (and the object drawn
     * using SKIP and OPAQUE) through the tranparant parts of the newly drawn
     * object. QUEUE_ORTHO - This is a special mode, for drawing 2D object
     * without prespective (such as GUI or HUD parts) Lastly, there is a special
     * mode, QUEUE_INHERIT, that will ensure that this spatial uses the same
     * mode as the parent Node does.
     * 
     * @param renderQueueMode
     *            The mode to use for this Spatial.
     */
    public void setRenderQueueMode(int renderQueueMode) {
        this.renderQueueMode = renderQueueMode;
    }

    /**
     * @return
     */
    public int getLocalRenderQueueMode() {
        return renderQueueMode;
    }

    /**
     * @param zOrder
     */
    public void setZOrder(int zOrder) {
        this.zOrder = zOrder;
    }

    /**
     * @return
     */
    public int getZOrder() {
        return zOrder;
    }

    /**
     * @return
     */
    public NormalsMode getLocalNormalsMode() {
        return normalsMode;
    }

    /**
     * @param mode
     */
    public void setNormalsMode(NormalsMode mode) {
        this.normalsMode = mode;
    }

    /**
     * Sets how lights from parents should be combined for this spatial.
     * 
     * @param mode
     *            The light combine mode for this spatial
     * @throws IllegalArgumentException
     *             if mode is null
     */
    public void setLightCombineMode(LightCombineMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode can not be null.");
        }
        this.lightCombineMode = mode;
    }

    /**
     * @return the lightCombineMode set on this Spatial
     */
    public LightCombineMode getLocalLightCombineMode() {
        return lightCombineMode;
    }

    /**
     * Sets how textures from parents should be combined for this Spatial.
     * 
     * @param mode
     *            The new texture combine mode for this spatial.
     * @throws IllegalArgumentException
     *             if mode is null
     */
    public void setTextureCombineMode(TextureCombineMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode can not be null.");
        }
        this.textureCombineMode = mode;
    }

    /**
     * @return the textureCombineMode set on this Spatial
     */
    public TextureCombineMode getLocalTextureCombineMode() {
        return textureCombineMode;
    }

    /**
     * Returns this spatial's last frustum intersection result. This int is set
     * when a check is made to determine if the bounds of the object fall inside
     * a camera's frustum. If a parent is found to fall outside the frustum, the
     * value for this spatial will not be updated.
     * 
     * @return The spatial's last frustum intersection result.
     */
    public Camera.FrustumIntersect getLastFrustumIntersection() {
        return frustrumIntersects;
    }

    /**
     * Overrides the last intersection result. This is useful for operations
     * that want to start rendering at the middle of a scene tree and don't want
     * the parent of that node to influence culling. (See texture renderer code
     * for example.)
     * 
     * @param intersects
     *            the new value
     */
    public void setLastFrustumIntersection(Camera.FrustumIntersect intersects) {
        e.setLastFrustumIntersection(intersects);
    }

    /**
     * Returns the Spatial's name followed by the class of the spatial <br>
     * Example: "MyNode (com.jme.scene.Spatial)
     * 
     * @return Spatial's name followed by the class of the Spatial
     */
    public String toString() {
        return e.toString() + " Wrapper";
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
/*    public boolean isStatic() {
    	return e.isStatic();
    }
*/
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
    	e.generatePhysicsGeometry( e, e, useTriangleAccurateGeometries );
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
/*    public static void generatePhysicsGeometry( Node source, PhysicsNode target, boolean useTriangleAccurateGeometries ) {
    	generatePhysicsGeometry( source, target, useTriangleAccurateGeometries, null );
    }
*/
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
/*    public static void generatePhysicsGeometry( Node source, PhysicsNode target, boolean useTriangleAccurateGeometries, Map<Spatial, PhysicsCollisionGeometry> collisionGeometryMap ) {
        generatePhysicsGeometry(source, target, useTriangleAccurateGeometries, collisionGeometryMap);
    }
*/
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
/*    public final boolean isActive() {
        return active;
    }
*/
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
     * Change material of this node. All geometries that did not have a matrial set as well as those with the same
     * material like the node inherit this nodes material.
     *
     * @param value new material
     * @see PhysicsCollisionGeometry#setMaterial
     */
    public void setMaterial( final Material value ) {
        e.setMaterial(value);
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

    /**
     * Query mass of this physical entity. Defaults to 1.
     *
     * @return mass
     */
    public float getMass() {
    	return e.getMass();
    }

    /**
     * Change mass of this object.
     *
     * @param value new mass
     */
    public void setMass( final float value ) {
    	e.setMass(value);
    }

    /**
     * Obtain the center of mass.
     *
     * @param store where to put the position (null to create a new vector)
     * @return center as vector in object coordinate space
     */
    public Vector3f getCenterOfMass( Vector3f store ) {
    	return e.getCenterOfMass(store);
    }

    /**
     * Change the center of mass for this node. This moves the attached spatials to a suited position. So this
     * method should be called _after_ configuring the geometries (e.g. after {@link #generatePhysicsGeometry()}).
     *
     * @param value new center as vector in object coordinate space
     */
    public void setCenterOfMass( final Vector3f value ) {
    	e.setCenterOfMass(value);
    }

    /**
     * Reset force on this node to 0.
     */
    public void clearForce() {
    	e.clearForce();
    }

    /**
     * Obtain the current force on this node.
     *
     * @param store where to put the force (null to create a new vector)
     * @return store
     */
    public Vector3f getForce( Vector3f store ) {
    	return e.getForce(store);
    }

    /**
     * Add a force to be applied to this node at a given relative location. The force vector is given
     * in world coordinate space. The force is applied in the next computation step and is cleared afterwards.
     * Thus this method has to be called frequently (before each physics step) if a constant force should be applied.
     *
     * @param force force to be added (world coordinate space)
     * @param at    position of the object where the force should be applied (relative to the center of mass)
     * @see PhysicsUpdateCallback
     */
    public void addForce( Vector3f force, Vector3f at ) {
    	e.addForce(force, at);
    }

    /**
     * Add a force to be applied to this node (at the center of mass). The vector is given in world coordinate space.
     *
     * @param force force to be added
     * @see #addForce(com.jme.math.Vector3f, com.jme.math.Vector3f)
     */
    public void addForce( Vector3f force ) {
        e.addForce( force );
    }

    /**
     * Reset torque on this node to 0.
     */
    public void clearTorque() {
    	e.clearTorque();
    }

    /**
     * Obtain the current torque on this node.
     *
     * @param store where to put the torque (null to create a new vector)
     * @return store
     */
    public Vector3f getTorque( Vector3f store ) {
    	return e.getTorque(store);
    }

    /**
     * Add a torque to be applied to this node. The vector is given in world coordinate space.
     *
     * @param torque torque to be added
     */
    public void addTorque( Vector3f torque ) {
    	e.addTorque(torque);
    }

    /**
     * Sets the linear velocity of this node.
     *
     * @param velocity new velocity in world coordinate space
     */
    public void setLinearVelocity( Vector3f velocity ) {
    	e.setLinearVelocity(velocity);
    }

    /**
     * Query the linear velocity of this node. The
     * passed in Vector3f will be populated with the values, and then returned.
     *
     * @param store where to store the velocity (null to create a new vector)
     * @return store
     */
    public Vector3f getLinearVelocity( Vector3f store ) {
    	return e.getLinearVelocity( store );
    }

    /**
     * Sets the angular velocity of this node.
     *
     * @param velocity new velocity in world coordinate space
     */
    public void setAngularVelocity( Vector3f velocity ) {
    	e.setAngularVelocity(velocity);
    }

    /**
     * Query the angular velocity of this node. The
     * passed in Vector3f will be populated with the values, and then returned.
     *
     * @param store where to store the velocity (null to create a new vector)
     * @return store
     */
    public Vector3f getAngularVelocity( Vector3f store ) {
    	return e.getAngularVelocity(store);
    }

    /**
     * @return true if node is affected by gravity
     * @see #setAffectedByGravity(boolean)
     */
    public boolean isAffectedByGravity() {
    	return e.isAffectedByGravity();
    }

    /**
     * Switch gravity on/off for this single node.
     *
     * @param value new value
     */
    public void setAffectedByGravity( final boolean value ) {
    	e.setAffectedByGravity(value);
    }

    /**
     * Resets all force, torque and velocities.
     */
    public void clearDynamics() {
        e.clearDynamics();
    }

    /**
     * Computes the mass for this node that would result from the attached geometries and their densities (materials).
     */
    public void computeMass() {
    	e.computeMass();
    }
    
    /**
     * This method may be called to tell the physics implementation that this node does not move by forces like gravity
     * and other internal constantly applied forces currently.
     * This can optimize the physics calculations quite a lot. Note this will cause this node to e.g. hang around
     * in the air if it's not really resting on the ground.
     * @see #unrest()
     */
    public void rest() {
    	e.rest();
    }

    /**
     * This method tells the physics implementation to check the node for movement after it has been rested (manually
     * or automatically).
     * @see #rest()
     */
    public void unrest() {
    	e.unrest();
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