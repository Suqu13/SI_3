package engine

enum class State(val token: String, val winnerMessage: String?) {
    FIRST_PLAYER("1", "You won!"),
    AI_PLAYER("2", "AI won!"),
    EMPTY(".", null),
}