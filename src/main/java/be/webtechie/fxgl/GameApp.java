package be.webtechie.fxgl;

import be.webtechie.fxgl.GameFactory.EntityType;
import be.webtechie.fxgl.component.PlayerComponent;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameApp extends GameApplication {

    /**
     * Reference to the factory which will defines how all the types must be created.
     */
    private final GameFactory gameFactory = new GameFactory();

    /**
     * Player object we are going to use to provide to the factory so it can start a bullet from the player center.
     */
    private Entity player;

    /**
     * Main entry point where the application starts.
     *
     * @param args Start-up arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * General game settings.
     *
     * @param settings The settings of the game which can be further extended here.
     */
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);
        settings.setTitle("Oracle Java Magazine - FXGL");
    }

    /**
     * Input configuration, here you configure all the input events like key presses, mouse clicks, etc.
     */
    @Override
    protected void initInput() {
        //onKey(KeyCode.LEFT, "left", () -> player.getComponent(PlayerComponent.class).left());
        //onKey(KeyCode.RIGHT, "right", () -> player.getComponent(PlayerComponent.class).right());
        //onKey(KeyCode.UP, "up", () -> player.getComponent(PlayerComponent.class).up());
        //onKey(KeyCode.DOWN, "down", () -> player.getComponent(PlayerComponent.class).down());
        //onKeyDown(KeyCode.SPACE, "Bullet", () -> player.getComponent(PlayerComponent.class).shoot());
    }

    /**
     * General game variables. Used to hold the points and lives.
     *
     * @param vars The variables of the game which can be further extended here.
     */
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("lives", 5);
    }

    /**
     * Initialization of the game by providing the {@link EntityFactory}.
     */
    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(gameFactory);
        spawn("background", new SpawnData(0, 0).put("width", getAppWidth())
                .put("height", getAppHeight()));
        int circleRadius = 80;
        spawn("center", new SpawnData((getAppWidth() / 2) - (circleRadius / 2), (getAppHeight() / 2) - (circleRadius / 2))
                .put("x", (circleRadius / 2))
                .put("y", (circleRadius / 2))
                .put("radius", circleRadius));

        // Add the player
        player = spawn("duke", 0, 0);
    }

    /**
     * Initialization of the collision handlers.
     */
    @Override
    protected void initPhysics() {
        onCollisionBegin(EntityType.DUKE, EntityType.CENTER, (duke, center) -> player.getComponent(PlayerComponent.class).die());
        onCollisionBegin(EntityType.DUKE, EntityType.CLOUD, (enemy, cloud) -> player.getComponent(PlayerComponent.class).die());
        onCollisionBegin(EntityType.BULLET, EntityType.CLOUD, (bullet, cloud) -> {
            inc("score", 1);
            bullet.removeFromWorld();
            cloud.removeFromWorld();
        });
    }

    /**
     * Configuration of the user interface.
     */
    @Override
    protected void initUI() {
        Text scoreLabel = getUIFactoryService().newText("Score", Color.BLACK, 22);
        Text scoreValue = getUIFactoryService().newText("", Color.BLACK, 22);
        Text livesLabel = getUIFactoryService().newText("Lives", Color.BLACK, 22);
        Text livesValue = getUIFactoryService().newText("", Color.BLACK, 22);

        scoreLabel.setTranslateX(20);
        scoreLabel.setTranslateY(20);

        scoreValue.setTranslateX(90);
        scoreValue.setTranslateY(20);

        livesLabel.setTranslateX(getAppWidth() - 150);
        livesLabel.setTranslateY(20);

        livesValue.setTranslateX(getAppWidth() - 80);
        livesValue.setTranslateY(20);

        scoreValue.textProperty().bind(getWorldProperties().intProperty("score").asString());
        livesValue.textProperty().bind(getWorldProperties().intProperty("lives").asString());

        var joystick = getInput().createVirtualJoystick();
        joystick.setTranslateX(25);
        joystick.setTranslateY(getAppHeight() - 280D);

        var controller = getInput().createXboxVirtualControllerView();
        controller.setTranslateX(getAppWidth() - 340D);
        controller.setTranslateY(getAppHeight() - 280D);
        controller.setTranslateZ(0.2);

        getInput().addAction(new UserAction("LEFT") {
             @Override
             protected void onAction() {
                 player.getComponent(PlayerComponent.class).left();
             }
        }, KeyCode.LEFT, VirtualButton.LEFT);
        getInput().addAction(new UserAction("RIGHT") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).right();
            }
        }, KeyCode.RIGHT, VirtualButton.RIGHT);
        getInput().addAction(new UserAction("UP") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).up();
            }
        }, KeyCode.UP, VirtualButton.UP);
        getInput().addAction(new UserAction("DOWN") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).down();
            }
        }, KeyCode.DOWN, VirtualButton.DOWN);
        getInput().addAction(new UserAction("SHOOT") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).shoot();
            }
        }, KeyCode.SPACE, VirtualButton.Y);

        getGameScene().addUINodes(scoreLabel, scoreValue, livesLabel, livesValue, joystick, controller);
    }

    /**
     * Gets called every frame _only_ in Play state.
     */
    @Override
    protected void onUpdate(double tpf) {
        if (getGameWorld().getEntitiesByType(EntityType.CLOUD).size() < 10) {
            spawn("cloud", getAppWidth() / 2, getAppHeight() / 2);
        }
    }
}