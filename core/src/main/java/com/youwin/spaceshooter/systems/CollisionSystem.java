package com.youwin.spaceshooter.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.youwin.spaceshooter.components.HitboxComponent;
import com.youwin.spaceshooter.components.NameComponent;
import com.youwin.spaceshooter.components.PositionComponent;
import com.youwin.spaceshooter.utils.GameManager;
import com.youwin.spaceshooter.utils.CollisionLayerEnum.Layer;

import org.mini2Dx.core.collisions.RegionQuadTree;
import org.mini2Dx.core.engine.geom.CollisionBox;
import org.mini2Dx.core.engine.geom.CollisionShape;

/*
It's most efficient for the collision system to encompass the entire play area, probably.
*/
public class CollisionSystem extends IteratingSystem {
    private static final Logger LOG = new Logger("[CollisionSystem]", Logger.INFO);

    private ComponentMapper<HitboxComponent> hitboxMapper;
    private ComponentMapper<PositionComponent> positionMapper;
    // TODO debug only maybe
    private ComponentMapper<NameComponent> nameMapper;

    private static RegionQuadTree<CollisionBox> collisions;

    public CollisionSystem() {
        super(Aspect.all(HitboxComponent.class, PositionComponent.class));
        collisions = new RegionQuadTree<CollisionBox>(100, 4, -10, -10, GameManager.screenWidth + 10,
                GameManager.screenHeight + 10);
    }

    // public CollisionSystem(float width, float height) {
    // super(Aspect.all(HitboxComponent.class, PositionComponent.class));
    // collisions = new RegionQuadTree<CollisionBox>(9, 4, 0, 0, width, height);
    // }

    // public CollisionSystem(float x, float y, float width, float height) {
    // super(Aspect.all(HitboxComponent.class, PositionComponent.class));
    // collisions = new RegionQuadTree<CollisionBox>(9, x, y, width, height);
    // }

    @Override
    protected void process(int entityId) {
        HitboxComponent hitbox = hitboxMapper.get(entityId);

        // If this object only lets other objects collide with it, don't do any
        // collision checking
        if (hitbox.getSearchLayers().get(0) == Layer.NONE) {
            return;
        }

        Array<CollisionBox> collisionList = collisions.getElementsWithinArea(hitbox.getCollisionBox());

        // An object can collide with itself
        if (collisionList.size > 1) {
            for (CollisionShape collision : collisionList) {
                if (collision.getId() != entityId) {
                    // If the search layer is all, the entity should't have any other search layers
                    if (hitbox.getSearchLayers().get(0) == Layer.ALL) {
                        adjustPreviousPoint(hitbox, entityId);
                    } else {
                        HitboxComponent collisionHitbox = hitboxMapper.getSafe(collision.getId(), null);
                        if (collisionHitbox != null) {
                            collisionHitbox.getListenLayers().forEach(listenLayer -> {
                                if (hitbox.getSearchLayers().contains(listenLayer)) {
                                    adjustPreviousPoint(hitbox, entityId);
                                    // TODO might need to break out of this loop if there are multiple collisions
                                }
                            });
                        }
                    }
                }
            }

        }

    }

    public static RegionQuadTree<CollisionBox> getCollisions() {
        return collisions;
    }

    private void adjustPreviousPoint(HitboxComponent hitbox, int entityId) {
        PositionComponent position = positionMapper.get(entityId);
        position.setPoint(position.getPreviousPoint());
        hitbox.getCollisionBox().set(position.getPreviousPoint());
    }

}