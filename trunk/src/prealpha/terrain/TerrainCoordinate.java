package prealpha.terrain;

public class TerrainCoordinate {
	private int x;
	private int y;
	private int z;
	
	public TerrainCoordinate() {
		
	}
	
	public TerrainCoordinate( int x, int y, int z) {
		this.x = x;
		this.y = x;
		this.z = x;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}
}
