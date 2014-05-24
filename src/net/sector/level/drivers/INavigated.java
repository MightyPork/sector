package net.sector.level.drivers;


import net.sector.collision.Scene;
import net.sector.entities.EFormation;
import net.sector.entities.Entity;
import net.sector.util.DeltaDoubleDeg;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * Navigator controlled entity
 * 
 * @author MightyPork
 */
public interface INavigated {
	/**
	 * Get desired motion speed (eg. 0.1)
	 * 
	 * @return desired motion speed
	 */
	public double getDesiredSpeed();

	/**
	 * Set desired ship speed (eg. 0.1)
	 * 
	 * @param speed the speed
	 */
	public void setDesiredSpeed(double speed);

	/**
	 * Set ship level, which has or has not any effect on the ship (health, shot
	 * damage, desired speed etc.)
	 * 
	 * @param level ship level
	 */
	public void setShipLevel(int level);

	/**
	 * Get entity motion vector
	 * 
	 * @return motion vector
	 */
	public Vec getMotion();

	/**
	 * Get entity position
	 * 
	 * @return position
	 */
	public Coord getPos();

	/**
	 * Get entity collider radius
	 * 
	 * @return radius
	 */
	public double getRadius();

	/**
	 * Get if entity is EMP paralyzed and can not move
	 * 
	 * @return is paralyzed
	 */
	public boolean isEmpParalyzed();

	/**
	 * Get entity parent scene
	 * 
	 * @return scene
	 */
	public Scene getScene();

	/**
	 * Get if entity is dead
	 * 
	 * @return is dead
	 */
	public boolean isDead();

	/**
	 * Set entity dead.
	 */
	public void setDead();

	/**
	 * Get delta-timed rotation angle
	 * 
	 * @return angle
	 */
	public DeltaDoubleDeg getRotAngle();

	/**
	 * heal the entity
	 * 
	 * @param add health to add
	 */
	public void addHealth(double add);


	/**
	 * Shoot one shot from appropriate gun.<br>
	 * Do nothing if can't shoot.
	 * 
	 * @param gunIndex gun index, in case this ship has multiple guns
	 */
	public void shootOnce(int gunIndex);

	/**
	 * Get gun aim direction vector
	 * 
	 * @param gunIndex gun index, in case this ship has multiple guns
	 * @return gun aim vector
	 */
	public Vec getGunShotDir(int gunIndex);

	/**
	 * Healing multiplier (for HEAL task)
	 * 
	 * @return 1, over 1 for faster, below 1 for slower healing
	 */
	public double getHealMultiplier();

	/**
	 * Set navigator driver
	 * 
	 * @param driver new task list
	 */
	public void setDriver(TaskList driver);

	/**
	 * Get entity navigator
	 * 
	 * @return navigator
	 */
	public Navigator getNavigator();

	/**
	 * Get leader of the formation
	 * 
	 * @return leader or null
	 */
	public Entity getFormationLeader();

	/**
	 * Get target entity (used for navigation)
	 * 
	 * @return target
	 */
	public Entity getTargetEntity();

	/**
	 * Set target entity (for navigation)
	 * 
	 * @param targetEntity the target entity
	 */
	public void setTargetEntity(Entity targetEntity);

	/**
	 * Set fleet formation
	 * 
	 * @param fleet the fleer
	 * @param formation the formation
	 */
	public void setFormation(Entity[] fleet, EFormation formation);

	/**
	 * Is tail?
	 * 
	 * @return is tail.
	 */
	public boolean formationIsTail();

	/**
	 * Is leader?
	 * 
	 * @return is leader.
	 */
	public boolean formationIsLeader();

	/**
	 * Get absolute health
	 * 
	 * @return health
	 */
	public double getHealth();

	/**
	 * Get relative health
	 * 
	 * @return health in percent
	 */
	public double getHealthPercent();

	/**
	 * Get health max
	 * 
	 * @return max health
	 */
	public double getHealthMax();
}
