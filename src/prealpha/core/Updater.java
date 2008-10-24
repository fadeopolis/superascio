package prealpha.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

public class Updater implements Updateable {

	private Updater updater;

	private Collection<Updateable> children;
	private Collection<Updateable> toRemove;
	
	public Updater() {
		this(null);
	}
 
	public Updater( Updater parent ) {
		this.updater = parent;
	}

	@Override
	public void update(float time) {
		if ( children == null ) children = new LinkedList<Updateable>();
		if ( toRemove != null ) for ( Updateable u : toRemove ) {
			children.remove(u);
			toRemove.remove(u);
		}
		for ( Updateable u : children ) u.update(time);
	}

	public void add( Updateable u ) {
		if ( children == null ) children = new LinkedList<Updateable>();
		children.add(u);
	}
	
	@Override
	public void removeFromUpdater() {
		// TODO Auto-generated method stub
		
	}

	public void remove( Updateable u ) {
		if ( toRemove == null ) toRemove = new ConcurrentLinkedQueue<Updateable>();
		toRemove.add(u);
	}
}
