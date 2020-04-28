package gui

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.asObservable
import tornadofx.observable
import tornadofx.observableListOf

enum class DifficultyLevel(val depth: Int) {
    EASY(1),
    MEDIUM(3),
    HARD(6)
}

enum class PlayerMove(val message: String) {
    FIRST_PLAYER_MOVE("First player move!"),
    SECOND_PLAYER_MOVE("Second player move!"),
    FIRST_AI_MOVE("First AI move!"),
    SECOND_AI_MOVE("Second AI move!"),
    NOT_STARTED("You have to start the game!")
}

class PageContext {
    val difficultyLevels = SimpleListProperty(DifficultyLevel.values().asList().asObservable())
    val players = SimpleListProperty(observableListOf("Player", "AI"))
}