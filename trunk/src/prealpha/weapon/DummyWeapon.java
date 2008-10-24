package prealpha.weapon;

import java.util.Map;

import prealpha.character.Character;

public class DummyWeapon extends Weapon {

	private static final long serialVersionUID = -1863426016345321029L;

	public DummyWeapon() {
		super(null);
	}

	@Override
	public boolean attack() {
		return false;
	}

}
