package gui

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.asObservable

enum class DifficultyLevel(val depth: Int) {
    EASY(1),
    MEDIUM(3),
    HARD(6)
}

enum class PlayerMove(val message: String) {
    YOUR_MOVE("Your move!"),
    OPPONENT_MOVE("Opponent move!"),
    NOT_STARTED("You have to start the game!")
}

class PageContext {
    val difficultyLevels = SimpleListProperty(DifficultyLevel.values().asList().asObservable())
}