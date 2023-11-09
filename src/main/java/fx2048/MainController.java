package fx2048;

import fx2048.model.GameModel;
import fx2048.model.Number;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MainController {

    private static final int GRID_SIZE = 80;
    private static final double ANIMATION_DURATION = 80;
    private static final String RECORD_FILE = "record";
    private final ObservableSet<Transition> transitions = FXCollections.observableSet();
    private final Queue<Runnable> postTransitionActions = new ArrayDeque<>();
    private final GameModel gameModel = new GameModel();
    private final Map<KeyCode, Runnable> keyActions = new HashMap<>();
    @FXML
    private Label scoreLabel;
    @FXML
    private Label recordLabel;
    @FXML
    private Pane gamePane;

    private static void saveRecord(int value) {
        try (DataOutputStream os = new DataOutputStream(Files.newOutputStream(Paths.get(RECORD_FILE)))) {
            os.writeInt(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int loadRecord() {
        try (DataInputStream inputStream = new DataInputStream(Files.newInputStream(Paths.get(RECORD_FILE)))) {
            return inputStream.readInt();
        } catch (IOException e) {
            return 0;
        }
    }

    @FXML
    private void initialize() {
        gameModel.setOnNewNumber(number -> {
            doAfterTransitions(() -> {
                NumberTile label = addNumber(number);
                label.setScaleX(0);
                label.setScaleY(0);
                ScaleTransition tt = new ScaleTransition(new Duration(ANIMATION_DURATION), label);
                tt.setToX(1);
                tt.setToY(1);
                runTransition(tt);
            });
        });

        gameModel.setOnRemove(number -> {
            doAfterTransitions(() -> removeNumber(number));
        });

        gameModel.setOnNewRecord(MainController::saveRecord);

        scoreLabel.textProperty().bind(gameModel.textScoreProperty());
        recordLabel.textProperty().bind(gameModel.textRecordProperty());

        keyActions.put(KeyCode.UP, gameModel::up);
        keyActions.put(KeyCode.DOWN, gameModel::down);
        keyActions.put(KeyCode.LEFT, gameModel::left);
        keyActions.put(KeyCode.RIGHT, gameModel::right);
        keyActions.put(KeyCode.R, gameModel::restart);
        keyActions.put(KeyCode.Z, gameModel::undo);

        gamePane.sceneProperty().addListener((observable, oldValue, scene) -> {
            scene.setOnKeyPressed(event -> {
                for (Transition transition : transitions) {
                    transition.jumpTo(transition.getTotalDuration());
                }
                Runnable runnable = keyActions.get(event.getCode());
                if (runnable != null) {
                    runnable.run();
                }
            });
        });

        transitions.addListener((SetChangeListener<? super Transition>) c -> {
            if (transitions.isEmpty()) {
                runActions();
            }
        });

        gameModel.setRecord(loadRecord());

        gameModel.restart();
    }

    private void runTransition(Transition transition) {
        transitions.add(transition);
        transition.setOnFinished(event -> {
            transitions.remove(transition);
        });
        transition.play();
    }

    private void runActions() {
        while (!postTransitionActions.isEmpty()) {
            postTransitionActions.poll().run();
        }
    }

    private NumberTile addNumber(Number number) {
        NumberTile tile = new NumberTile(number);
        tile.setPrefSize(GRID_SIZE, GRID_SIZE);
        tile.setTranslateX(number.getCol() * GRID_SIZE);
        tile.setTranslateY(number.getRow() * GRID_SIZE);
        gamePane.getChildren().add(tile);

        number.setOnMove(() -> {
            final int xDest = number.getCol() * GRID_SIZE;
            final int yDest = number.getRow() * GRID_SIZE;
            TranslateTransition tt = new TranslateTransition(new Duration(ANIMATION_DURATION), tile);
            tt.setToX(xDest);
            tt.setToY(yDest);
            runTransition(tt);
        });

        return tile;
    }

    private void doAfterTransitions(Runnable runnable) {
        if (transitions.isEmpty()) {
            runnable.run();
        } else {
            postTransitionActions.add(runnable);
        }
    }

    private void removeNumber(Number number) {
        NumberTile numberTile = getNumberTile(number);
        gamePane.getChildren().remove(numberTile);
    }

    private NumberTile getNumberTile(Number number) {
        return gamePane.getChildren().stream()
                .filter(node -> node instanceof NumberTile)
                .map(node -> (NumberTile) node)
                .filter(numberTile -> numberTile.getNumber() == number)
                .findFirst()
                .orElse(null);
    }

    @FXML
    private void onRestart() {
        gameModel.restart();
    }

    @FXML
    private void onSettings() throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(Main.class.getResource("settings.fxml"))));
        stage.setResizable(false);
        stage.sizeToScene();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

}
