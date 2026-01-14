package model;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Player {
    private String name;
    private Colors color;
    private List<Piece> capturedPieces;
    private TreeSet<ChessPair<Position, Piece>> ownedPieces;
    private int points;

    public Player(String name, Colors color) {
        this.name = name;
        this.color = color;
        this.ownedPieces = new TreeSet<ChessPair<Position, Piece>>();
        this.points = 0;
        this.capturedPieces = new ArrayList<Piece>();
    }

    public void makeMove(Position from, Position to, Board board) throws InvalidMoveException, InvalidCommandException {
        Piece piece = board.getPieceAt(from);

        if (piece == null) {
            throw new InvalidMoveException("Piece not found");
        }

        if (piece.getColor() != this.color) {
            throw new InvalidMoveException("Cannot move opponent s piece");
        }

        Piece capturedPiece = board.getPieceAt(to);
        boolean allowPromotionChoice = !isComputer();
        board.movePiece(from, to, this.color, allowPromotionChoice);

        if (capturedPiece != null) {
            capturedPieces.add(capturedPiece);
            updatePoints(capturedPiece);
        }

        updateOwnedPiece(board);
    }

    private void updatePoints(Piece capturedPiece) {
        switch (capturedPiece.type()) {
            case 'Q':
                points += 90;
                break;
            case 'R':
                points += 50;
                break;
            case 'B':
                points += 30;
                break;
            case 'N':
                points += 30;
                break;
            case 'P':
                points += 6;
                break;
        }
    }

    private void updateOwnedPiece(Board board) {
        ownedPieces.clear();
        for (ChessPair<Position, Piece> chessPair : board.getPieces()) {
            if (chessPair.getValue().getColor() == this.color) {
                ownedPieces.add(chessPair);
            }
        }
    }

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public List<ChessPair<Position, Piece>> getOwnedPieces() {
        return new ArrayList<>(ownedPieces);
    }

    public int getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }

    public Colors getColor() {
        return color;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isComputer() {
        return "COMPUTER".equalsIgnoreCase(name);
    }
}
