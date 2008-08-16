package prealpha.ascio.weapon;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;
import com.jmex.physics.PhysicsSpace;

import prealpha.ascio.weapon.Weapon;;

public class Jumper extends Weapon {
	private static final long serialVersionUID = 1893972450201560642L;

	public Jumper(PhysicsSpace space) {
		super(space);
		
		JumperMesh mesh1 = new JumperMesh("JumperMesh 1");
		JumperMesh mesh2 = new JumperMesh("JumperMesh 2");

		mesh1.setLocalScale(.75f);
		mesh2.setLocalScale(.75f);
		mesh2.getLocalTranslation().addLocal(0, 0, 1);
		
		this.attachChild(mesh1);
		this.attachChild(mesh2);
	}
	@Override
	public boolean fire() {
		// TODO Auto-generated method stub
		return false;
	}

}

class JumperMesh extends TriMesh {
	private static final long serialVersionUID = 9147208810053682009L;
	
	public JumperMesh(String name) {
		super(name);
        // Vertex positions for the mesh
        Vector3f[] vertexes= {
        		//leftarrow
            new Vector3f( .3f,  0, 1.25f ),
            new Vector3f( .3f, -.75f, 0 ),
            new Vector3f( .3f, -1.2f, .5f ),
            new Vector3f( .3f, 0, 2f ),
            new Vector3f( .3f, .75f, 0 ),
            new Vector3f( .3f, 1.2f, .5f ),
            	//rightarrow
            new Vector3f( -.3f,  0, 1.25f ),
            new Vector3f( -.3f, -.75f, 0 ),
            new Vector3f( -.3f, -1.2f, .5f ),
            new Vector3f( -.3f, 0, 2f ),
            new Vector3f( -.3f, .75f, 0 ),
            new Vector3f( -.3f, 1.2f, .5f )
        };

        // Normal directions for each vertex position
        Vector3f[] normals={
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1)
        };

        // Color for each vertex position
        ColorRGBA[] colors={
            new ColorRGBA(.75f,.75f,.75f,.75f),
            new ColorRGBA(.75f,.75f,.75f,.75f),
            new ColorRGBA(.75f,.75f,.75f,.75f),
            new ColorRGBA(.75f,.75f,.75f,.75f),
            new ColorRGBA(.75f,.75f,.75f,.75f),
            new ColorRGBA(.75f,.75f,.75f,.75f),
            new ColorRGBA(.75f,.75f,.75f,.75f),
            new ColorRGBA(.75f,.75f,.75f,.75f),
            new ColorRGBA(.75f,.75f,.75f,.75f),
            new ColorRGBA(.75f,.75f,.75f,.75f),
            new ColorRGBA(.75f,.75f,.75f,.75f),
            new ColorRGBA(.75f,.75f,.75f,.75f)
        };

        // Texture Coordinates for each position
        Vector2f[] texCoords={
            new Vector2f(0,0),
            new Vector2f(1,0),
            new Vector2f(0,1),
            new Vector2f(1,1),
            new Vector2f(0,1),
            new Vector2f(1,0),
            new Vector2f(1,1),
            new Vector2f(0,0),
            new Vector2f(1,0),
            new Vector2f(0,1),
            new Vector2f(1,1),
            new Vector2f(0,1)
        };

        // The indexes of Vertex/Normal/Color/TexCoord sets.  Every 3 makes a triangle.
        int[] indexes={
        		//left arrow
            0,1,2, 0,2,3, 0,3,4, 3,4,5,
            	//right arrow
            6,7,8, 6,8,9, 6,9,10, 9,10,11,
            	//sides
            0,4,6, 4,6,10, 0,1,6, 1,6,7, 
            1,2,7, 2,7,8, 4,5,10, 5,10,11,
            2,3,8, 3,8,9, 3,5,9, 5,9,11
        };

        // Feed the information to the TriMesh
        this.reconstruct(BufferUtils.createFloatBuffer(vertexes), BufferUtils.createFloatBuffer(normals),
                BufferUtils.createFloatBuffer(colors), TexCoords.makeNew(texCoords), BufferUtils.createIntBuffer(indexes));
}
}