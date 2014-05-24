package net.sector.entities.shots;


import java.util.Set;

import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.models.Models;
import net.sector.util.Utils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


public class EntityMissileGuided extends EntityMissileDirect {


	public EntityMissileGuided(Coord pos, Vec speed, Entity origin, int techLevel) {
		super(pos, speed, origin, techLevel);
		this.model = Models.rocketFat;
		this.scaleRender = model.renderScale;
		this.shotDamage = 6 * Math.pow(techLevel, 1.5);
		this.health = 0.4 * techLevel * techLevel;
	}

	private Entity target = null;

	@Override
	public void onUpdate() {
		rot.pushLast();
		rot.add(rotSpeed);

		if (target == null || target.isDead()) {
			Set<Entity> entities = scene.getEntitiesInRange(getPos(), 60);
			double mini = 180;
			double mind = 1000;
			for (Entity entity : entities) {
				if (entity.getType() != EEntity.ENEMY) continue;
				if (entity == this) continue;
				double i = 0;
				double d = entity.getPos().distTo(getPos());
				i = Utils.observerAngleToCoord(getPos(), entity.getPos(), motion);
				if (i < 70 && i < mini && d < mind) {
					target = entity;
					mini = i;
					mind = d;
				}
			}
		}

		if (target != null) {
			Vec direction = collider.pos.vecTo(target.getPos());
			direction.norm_ip(shotSpeed);
			double dist = target.getPos().distTo(getPos()) - target.getRadius();
			if (dist < 0) dist = 0.00001;
			double spd = 0.2 / dist;
			if (spd < 0.05) spd = 0.05;
			motion.add_ip(direction.norm(spd));
			motion.norm_ip(shotSpeed);

//			//getPos().add_ip(motion);
//			Vec toTg = getPos().vecTo(target.getPos()).norm(0.06);
//			motion.add_ip(toTg);
		}

		// avoid obstacles
		Set<Entity> ents = scene.getEntitiesInRange(getPos(), collider.radius + 3);
		if (!ents.isEmpty()) {
			for (Entity e : ents) {
				if (e == this) continue;
				if (e.getType() == EEntity.NATURAL) {
					double dist = e.getPos().distTo(getPos()) - e.getRadius();
					if (dist < 0) dist = 0.00001;
					double move = 0.05 / dist;
					move = Calc.clampd(move, 0.001, 0.2);
					motion.add_ip(((Vec) e.getPos().vecTo(getPos()).setY(0)).norm(move));
					motion.norm_ip(shotSpeed);
				}
			}
		}


		motion.norm_ip(shotSpeed);
		addEffect();
	}

}