package com.youwin.spaceshooter.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class EnemyControllerComponent extends Component {
    private Vector2 targetDirection;

    public EnemyControllerComponent() {
        targetDirection = new Vector2(Vector2.Zero);
    }

    public Vector2 getTargetDirection() {
        return targetDirection;
    }

    public void setTargetDirection(Vector2 newTargetDirection) {
        targetDirection = newTargetDirection;
    }
}