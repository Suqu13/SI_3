package gui

import algorithm.Heuristic
import algorithm.PromoteCenteredCellsHeuristic
import algorithm.ScoreContentOfFourHeuristic
import javafx.beans.property.SimpleListProperty
import tornadofx.asObservable
import tornadofx.observableListOf

enum class DifficultyLevel(val depth: Int) {
    EASY(1),
    MEDIUM(3),
    HARD(6)
}

enum class HeuristicKind(val heuristic: Heuristic) {
    SCORE_CONTENT_OF_FOUR(ScoreContentOfFourHeuristic()),
    PROMOTE_CENTERED_CELLS(PromoteCenteredCellsHeuristic())
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
    val heuristics = SimpleListProperty(HeuristicKind.values().asList().asObservable())
    val players = SimpleListProperty(observableListOf("Player", "AI"))
}