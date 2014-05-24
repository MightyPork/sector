package net.sector.level.sequence;


import java.util.HashSet;

import com.porcupine.math.Calc;

import net.sector.entities.Entity;


/**
 * Wave of entities
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class EnemyWave extends HashSet<Entity> {

	/**
	 * Check if all entities in this wave are dead.
	 * 
	 * @return all are dead
	 */
	public boolean isDead() {
		for (Entity e : this) {
			if (!e.isDead()) return false;
		}
		
		clear();
		return true;
	}

	/**
	 * Kill all entities in wave (add damage and set dead)
	 */
	public void killAll() {
		for (Entity e : this) {
			e.addDamage(null, 100000);
			e.setDead();
		}
	}
	
	@Override
	public String toString() {
		String s = "";
		s += "\n+---\n";
		s += " Enemy Wave, size = "+size()+"\n";
		for(Entity e: this) {
			System.out.println(" -- name:"+Calc.className(e)+", hp:"+e.getHealth()+", dead:"+e.isDead()+", pos: "+e.getPos());
		}
		return s;
	}
}
