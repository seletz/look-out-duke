package com.nexiles.examples.duke.component;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.component.Component;
import com.nexiles.examples.duke.GameFactory;
import javafx.geometry.Point2D;

import java.util.Optional;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.nexiles.examples.duke.GameFactory.EntityType.DUKE;

public class NexilesComponent extends Component {

    private Point2D direction = new Point2D(FXGLMath.random(-1D, 1D), FXGLMath.random(-1D, 1D));

    @Override
    public void onUpdate(double tpf) {
        entity.translate(direction.multiply(3));
        checkForBounds();

        GameWorld world = getGameWorld();

        Optional<Entity> player = world.getEntitiesByType(DUKE).stream().findFirst();
        player.ifPresent(value -> direction = value.getPosition().subtract(entity.getPosition()).normalize());

    }

    private void checkForBounds() {
        if (entity.getX() < 0) {
            remove();
        }
        if (entity.getX() >= getAppWidth()) {
            remove();
        }
        if (entity.getY() < 0) {
            remove();
        }
        if (entity.getY() >= getAppHeight()) {
            remove();
        }
    }

    public void remove() {
        entity.removeFromWorld();
    }
}
