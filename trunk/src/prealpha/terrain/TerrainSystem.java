package prealpha.terrain;

import java.io.IOException;
import java.util.ArrayList;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.PhysicsSpace;

public class TerrainSystem {
	private static TerrainSystem instance;
	
	private PhysicsSpace space;
	
	public static Node terrainRoot;
	
	private static int eventHorizon = 1;
	
	private TerrainSystem( PhysicsSpace space ) {
		this.space = space;
		
		terrainRoot = new Node();
		terrainRoot.setName("terrainRoot");
		
		array = new LevelBlock[30][10][10];
	}
	
	public static void create( PhysicsSpace space ) {
		if ( instance == null ) instance = new TerrainSystem( space );
	}	
	
	public static TerrainSystem getSystem() {
		return instance;
	}
	 
	LevelBlock[][][] array;
	
	public LevelBlock get( int x, int y, int z) throws IOException {
		LevelBlock lvl = array[x][y][z];
		
		if ( lvl == null ) {
			lvl = new LevelBlock(x,y,z, space.createStaticNode() );
			terrainRoot.attachChild(lvl);
			put(lvl);			
		}
		return lvl;
	}

	public void put( LevelBlock lvl ) {
		array[lvl.coordinate.getX()][lvl.coordinate.getY()][lvl.coordinate.getZ()] = lvl;		
	}

	public LevelBlock locate(Vector3f point) throws IOException {
		return get((int)point.x/100,(int)point.y/100,(int)point.z/100);
	}
}