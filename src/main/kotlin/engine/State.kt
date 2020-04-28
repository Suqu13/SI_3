package engine

enum class State(val token: String, val winnerMessage: String?) {
    FIRST_PLAYER("1", "First player won!"),
    SECOND_PLAYER("2", "Second Player won!"),
    FIRST_AI_PLAYER("3", "First AI won!"),
    SECOND_AI_PLAYER("4", "Second AI won!"),
    EMPTY(".", null),
}