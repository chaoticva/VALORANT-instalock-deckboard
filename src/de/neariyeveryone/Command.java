package de.neariyeveryone;

enum Action {
    lclick,
    move
}

public record Command(Action action, String x, String y) {
}
