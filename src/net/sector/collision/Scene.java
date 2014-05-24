package net.sector.collision;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sector.Constants;
import net.sector.GameConfig;
import net.sector.effects.ParticleManager;
import net.sector.effects.particles.Particle;
import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.entities.player.EntityPlayerShip;
import net.sector.entities.shots.EntityShotBase;
import net.sector.models.Models;
import net.sector.util.Utils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;
import com.porcupine.util.StringUtils;


/**
 * 2D collider map with Z-axis zones
 * 
 * @author MightyPork
 */
public class Scene {

	// number of zones
	private static final int ZONES = 18;
	// size of one zone in GL units
	private static final double ZONE_WIDTH = 6;

	/** collision zones */
	protected ColliderZone[] zones = new ColliderZone[ZONES];

	/** set of all entities in this map */
	public ArrayList<Entity> allEntities = new ArrayList<Entity>();

	/** set of all entities to add when we can */
	public ArrayList<Entity> toAdd = new ArrayList<Entity>();

	/** Particle manager */
	public ParticleManager particles = new ParticleManager();

	/** Current player ship instance */
	public EntityPlayerShip playerShip = null;
	private long lastt;

	/**
	 * Add effect to particle manager
	 * 
	 * @param particle particle added
	 */
	public void addEffect(Particle particle) {
		particles.add(particle);
	}

	/**
	 * Make new collider map
	 */
	public Scene() {
		for (int i = 0; i < ZONES; i++) {
			zones[i] = new ColliderZone(i * ZONE_WIDTH, (i + 1) * ZONE_WIDTH, (i == 0 ? -1 : i == ZONES - 1 ? 1 : 0));
		}
	}

	/**
	 * Zone of entities within this collider.
	 * 
	 * @author MightyPork
	 */
	private class ColliderZone extends HashSet<Entity> {

		public double zFrom = 0;
		public double zTo = 10;

		/**
		 * Collider zone
		 * 
		 * @param zFrom starting z index
		 * @param zTo ending z index
		 * @param position position. -1 first, 0 middle, 1 last;
		 */
		public ColliderZone(double zFrom, double zTo, int position) {
			this.zFrom = zFrom;
			this.zTo = zTo;
			if (position == -1) this.zFrom = -20;
			if (position == 1) this.zTo = ZONE_WIDTH * (ZONES + 20);
		}

		/**
		 * Make sure all entities are in the proper zones.
		 */
		public void arrangeEntities() {

			// move entities that no longer belong to this zone to other zones
			// remove dead entities
			Iterator<Entity> i = this.iterator();
			while (i.hasNext()) {
				Entity e = i.next();

				if (e.isDead()) {
					allEntities.remove(e);
					i.remove();
					continue;
				}
			}
		}

		/**
		 * Check all entities for collisions
		 */
		public void collideAndReact() {
			
			Iterator<Entity> i = this.iterator();
			loop1:
			while (i.hasNext()) {
				
				Entity entity1 = i.next();
				if (entity1.isDead()) {
					allEntities.remove(entity1);
					i.remove();
					continue;
				}
				
				if (entity1.isDead()) continue loop1;

				Iterator<Entity> i2 = this.iterator();
				loop2:
				while (i2.hasNext()) {
					Entity entity2 = i2.next();
					if (entity2.isDead()) continue loop2;

					if (entity1 != entity2) {
						if (entity1.collidesWith(entity2) && entity2.collidesWith(entity1)) {
							if (entity1.collidePriority > entity2.collidePriority) {
								entity1.react(entity2);
							} else {
								entity2.react(entity1);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Add new entity to the correct zones and to global list
	 * 
	 * @param added added entity
	 */
	public void add(Entity added) {
		if (added.getType() == EEntity.PLAYER) playerShip = (EntityPlayerShip) added;
		toAdd.add(added);
	}

	private void addAllWaiting() {
		for (Entity added : toAdd) {
			added.setScene(this);
			allEntities.add(added);
			added.onAddedToScene();
			for (ColliderZone zone : zones) {
				if (added.belongsToZone(zone.zFrom, zone.zTo)) {
					zone.add(added);
				}
			}
		}

		toAdd.clear();
	}

	/**
	 * Remove entity from all lists and zones
	 * 
	 * @param removed removed entity
	 */
	public void remove(Entity removed) {
		//remove(removed, false);
		removed.setDead();
	}


//    List<Entity> removeList = new ArrayList<Entity>();
//    
//    public void remove(Entity removed, boolean now) {
//            if(now) {
//                    if(removeList==null||removeList.isEmpty()) return;
//                    for(Entity nowRemoved:removeList) {
//                            allEntities.remove(nowRemoved);
//                            for (ColliderZone zone : zones) {
//                                    zone.remove(nowRemoved);
//                            }
//                    }
//                    removeList.clear();
//            }else {
//                    removeList.add(removed);
//            }
//    }



	/**
	 * Render all contained entities
	 * 
	 * @param delta
	 */
	public void render(float delta) {
		Collections.sort(allEntities);

		Models.renderBegin();

		for (Entity entity : allEntities) {
			if (entity.isDead()) continue;
			if (!Utils.canSkipRendering(entity.getPos())) {

				entity.render(delta);

				if (GameConfig.colliderWireframe) {
					entity.collider.render();
				}
			}
		}

		Models.renderEnd();
		particles.render(delta);
	}

	/**
	 * Get global movement vector
	 * 
	 * @return global movement vector
	 */
	public Vec getGlobalMovement() {
		return globalMovement;
	}

	/**
	 * Set global movement vector
	 * 
	 * @param newMovement new global movement
	 */
	public void setGlobalMovement(Vec newMovement) {
		globalMovement.setTo(newMovement);
	}

	/**
	 * Update all contained entities
	 */
	public void update() {

		particles.moveAllParticles(globalMovement.scale(Constants.SPEED_MUL));

		particles.update();
		
		// add entities waiting to be added
		addAllWaiting();

		for (Entity entity : allEntities) {
			if (entity.isDead()) continue;

			entity.update();
			
			// assign to zone.
			for (ColliderZone zone : zones) {
				if(!zone.contains(entity)) {
					if (entity.belongsToZone(zone.zFrom, zone.zTo)) {								
						zone.add(entity);
					}
				}else {
					if (!entity.belongsToZone(zone.zFrom, zone.zTo)) {								
						zone.remove(entity);
					}
				}
			}
			
			if (entity instanceof EntityPlayerShip) continue;

			if (entity.getPos().x < -60) {
				if (entity instanceof EntityShotBase) {
					entity.setDead();
				} else {
					entity.getMotion().x *= -1;
				}
			}
			if (entity.getPos().x > 60) {
				if (entity instanceof EntityShotBase) {
					entity.setDead();
				} else {
					entity.getMotion().x *= -1;
				}
			}

			if (entity.getPos().z < -15) entity.setDead();
			if (entity.getPos().z > 160) entity.setDead();
			

		}

		// remove dead and other entities
		for (ColliderZone zone : zones) {
			zone.arrangeEntities();
		}
		
		for (ColliderZone zone : zones) {
			// check for collisions, do reactions if needed
			zone.collideAndReact();
		}
		
		if(Constants.LOG_ZONES) {
			long newt;
			if((newt = System.currentTimeMillis())-lastt > 1000) {
				lastt = newt;
				System.out.println("\n### ZONE MAP");
				for(ColliderZone zone: zones) {
					System.out.println("Zone[ "+(int)zone.zFrom+" , "+(int)zone.zTo+" ] = "+StringUtils.repeat("(#)", zone.size()));
				}
				
				System.out.println("### ZONE MAP\n");
			}
		}
	}

	/**
	 * Get set of entities in given range. Their center points may be outside
	 * the range, only their colliders are checked.
	 * 
	 * @param center central point
	 * @param range radius of collision sphere to get entities from
	 * @return set of the entities
	 */
	public Set<Entity> getEntitiesInRange(Coord center, double range) {
		Set<Entity> buffer = new HashSet<Entity>();
		Collider rangeCol = new ColliderSphere(center, range);

		for (Entity entity : allEntities) {
			if (entity.isDead()) continue;
			if (entity.collider.collidesWith(rangeCol)) {
				buffer.add(entity);
			}
		}

		return buffer;
	}

	/**
	 * Get entities in line of sight (can be hit by weapon)
	 * 
	 * @param origin observer pos
	 * @param direction look direction
	 * @param maxDistanceFromLine max distance to side from direct line
	 * @param lengthOfSight max distance from observer
	 * @return set of matching entities
	 */
	public Set<Entity> getEntitiesInLineOfSight(Coord origin, Vec direction, double maxDistanceFromLine, double lengthOfSight) {
		Set<Entity> buffer = new HashSet<Entity>();

		for (Entity entity : allEntities) {
			if (entity.isDead()) continue;

			// too far?
			if (entity.getPos().distTo(origin) > lengthOfSight) continue;

			double dist = Calc.linePointDistXZ(direction, origin, entity.getPos());
			dist -= entity.collider.radius;
			if (dist < 0) dist = 0;

			if (dist <= maxDistanceFromLine) buffer.add(entity);
		}

		return buffer;
	}

	/** Global movement vector */
	private Vec globalMovement = new Vec(0, 0, -0.1);

	/**
	 * Get player ship instance (if any)
	 * 
	 * @return player ship
	 */
	public EntityPlayerShip getPlayerShip() {
		return playerShip;
	}


}
