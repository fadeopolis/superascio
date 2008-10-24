package prealpha.core;

/**
 * Any ingame element that can be damaged and destroyed must implement this interface
 * @author fader
 *
 */
public interface Damageable {

	/**
	 * Damage the damageable by the given amount,
	 * should check if this Damageable is dead ( i.e. call kill() if the health falls below 0 )
	 * @param damage The amount of damage to deal
	 */
	public void damage( float damage );
	
	/**
	 * Kill the Damageable.
	 */
	public void kill();
	
	/**
	 * Delete the Damageable immediately, remove ( if possible ) any reference to it, so it can be finalized
	 */
	public void delete();
	
}
