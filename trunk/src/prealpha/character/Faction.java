package prealpha.character;

public enum Faction {
	None(0),
	Ascio(1), Antivirus(1),
	ZomBies(-1), Virus(-1), Headcrabs(-1);

	private int i;
	
	private Faction( int i ) {
		this.i = i;
	}
	
	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public boolean isEnemy( Faction f ) {
		return Math.abs(i - f.i) > 0;
	}
}
