package fx2048.model;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class GameModel {

    private final Random random = new Random();
    private final List<Number> model = new ArrayList<>();
    private final SimpleIntegerProperty scoreProperty = new SimpleIntegerProperty(-1);
    private final SimpleIntegerProperty recordProperty = new SimpleIntegerProperty(-1);
    private final ReadOnlyStringWrapper textScoreProperty = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper textRecordProperty = new ReadOnlyStringWrapper();
    private Consumer<Number> onNewNumber = number -> {
    };
    private Consumer<Number> onRemove = number -> {
    };
    private Consumer<Integer> onNewRecord = value -> {
    };

    public GameModel() {
        scoreProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > recordProperty.get()) {
                recordProperty.set(newValue.intValue());
            }
        });
        recordProperty.addListener((observable, oldValue, newValue) -> {
            onNewRecord.accept(newValue.intValue());
        });
        scoreProperty.addListener((observable, oldValue, newValue) -> {
            textScoreProperty.set(String.valueOf(newValue));
        });
        recordProperty.addListener((observable, oldValue, newValue) -> {
            textRecordProperty.set(String.valueOf(newValue));
        });
    }

    private Optional<Number> get(int col, int row) {
        return model.stream().filter(number -> number.getCol() == col).filter(number -> number.getRow() == row).findFirst();
    }

    private void generateRandomCell() {
        int col;
        int row;
        do {
            col = random.nextInt(4);
            row = random.nextInt(4);
        } while (get(col, row).isPresent());
        int value = (random.nextInt(4) > 0) ? 2 : 4;
        Number number = new Number(col, row, value);
        addNumber(number);
    }

    public void up() {
        AtomicBoolean moved = new AtomicBoolean(false);
        model.stream().sorted(Comparator.comparingInt(Number::getRow)).forEach(number -> {
            for (int row = number.getRow() - 1; row >= 0; row--) {
                if (tryMoveTo(number, number.getCol(), row)) {
                    moved.set(true);
                } else {
                    break;
                }
            }
        });
        if (moved.get()) {
            generateRandomCell();
        }
    }

    public void down() {
        AtomicBoolean moved = new AtomicBoolean(false);
        model.stream().sorted((n1, n2) -> -Integer.compare(n1.getRow(), n2.getRow())).forEach(number -> {
            for (int row = number.getRow() + 1; row < 4; row++) {
                if (tryMoveTo(number, number.getCol(), row)) {
                    moved.set(true);
                } else {
                    break;
                }
            }
        });
        if (moved.get()) {
            generateRandomCell();
        }
    }

    public void left() {
        AtomicBoolean moved = new AtomicBoolean(false);
        model.stream().sorted(Comparator.comparingInt(Number::getCol)).forEach(number -> {
            for (int col = number.getCol() - 1; col >= 0; col--) {
                if (tryMoveTo(number, col, number.getRow())) {
                    moved.set(true);
                } else {
                    break;
                }
            }
        });
        if (moved.get()) {
            generateRandomCell();
        }
    }

    public void right() {
        AtomicBoolean moved = new AtomicBoolean(false);
        model.stream().sorted((n1, n2) -> -Integer.compare(n1.getCol(), n2.getCol())).forEach(number -> {
            for (int col = number.getCol() + 1; col < 4; col++) {
                if (tryMoveTo(number, col, number.getRow())) {
                    moved.set(true);
                } else {
                    break;
                }
            }
        });
        if (moved.get()) {
            generateRandomCell();
        }
    }

    private boolean tryMoveTo(Number number, int col, int row) {
        Optional<Number> optionalNumber = get(col, row);
        if (!optionalNumber.isPresent()) {
            number.setCol(col);
            number.setRow(row);
            return true;
        } else {
            Number otherNumber = optionalNumber.get();
            if (otherNumber.getValue() == number.getValue()) {
                number.setCol(col);
                number.setRow(row);
                remove(number);
                remove(otherNumber);
                Number newNumber = new Number(otherNumber.getCol(), otherNumber.getRow(), otherNumber.getValue() * 2);
                addNumber(newNumber);
                addScore(newNumber.getValue());
                return true;
            } else {
                return false;
            }
        }
    }

    public void setOnNewNumber(Consumer<Number> onNewNumber) {
        this.onNewNumber = onNewNumber;
    }

    public void setOnRemove(Consumer<Number> onRemove) {
        this.onRemove = onRemove;
    }

    public void setOnNewRecord(Consumer<Integer> onNewRecord) {
        this.onNewRecord = onNewRecord;
    }

    public void restart() {
        new ArrayList<>(model).forEach(this::remove);
        scoreProperty.set(0);
        generateRandomCell();
    }

    private void remove(Number number) {
        model.remove(number);
        onRemove.accept(number);
    }

    private void addNumber(Number number) {
        model.add(number);
        onNewNumber.accept(number);
    }

    private void addScore(int score) {
        scoreProperty.set(scoreProperty.get() + score);
    }

    public ReadOnlyStringProperty textScoreProperty() {
        return textScoreProperty.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty textRecordProperty() {
        return textRecordProperty.getReadOnlyProperty();
    }

    public void setRecord(int record) {
        recordProperty.set(record);
    }
}
