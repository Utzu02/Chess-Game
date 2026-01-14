package pieces;

import model.Colors;
import model.Position;

public abstract class Piece implements ChessPiece {
    private final Colors color;
    private Position position;

    public Piece(Colors color, Position position) {
        this.color = color;
        this.position = position;
    }

    public Colors getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String toString() {
        return type() + "-" + (color == Colors.WHITE ? "W" : "B");
    }
}
