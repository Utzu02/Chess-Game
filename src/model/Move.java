package model;

import pieces.Piece;

public class Move {
    private Colors playerColor;
    private Position from;
    private Position to;
    private Piece capturedPiece;

    public Move(Colors payerColor, Position startPosition, Position endPosition) {
        this.playerColor = payerColor;
        this.from = startPosition;
        this.to = endPosition;
        this.capturedPiece = null;
    }

    public Move(Colors payerColor, Position startPosition, Position endPosition, Piece capturedPiece) {
        this.playerColor = payerColor;
        this.from = startPosition;
        this.to = endPosition;
        this.capturedPiece = capturedPiece;
    }

    public Colors getPlayerColor() {
        return playerColor;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(Piece piece) {
        this.capturedPiece = piece;
    }

    public String toString() {
        return from + "-" + to + (capturedPiece != null ? " (captured " + capturedPiece + ")" : "");
    }
}
