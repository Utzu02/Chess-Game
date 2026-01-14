package model;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import factory.PieceFactory;
import pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

public class Board {
    private final TreeSet<ChessPair<Position, Piece>> pieces;

    public Board() {
        pieces = new TreeSet<ChessPair<Position, Piece>>();
    }

    public void initialize() throws InvalidCommandException {
        pieces.clear();

        addPiece(PieceFactory.createPiece("ROOK", Colors.WHITE, "A1"));
        addPiece(PieceFactory.createPiece("KNIGHT", Colors.WHITE, "B1"));
        addPiece(PieceFactory.createPiece("BISHOP", Colors.WHITE, "C1"));
        addPiece(PieceFactory.createPiece("QUEEN", Colors.WHITE, "D1"));
        addPiece(PieceFactory.createPiece("KING", Colors.WHITE, "E1"));
        addPiece(PieceFactory.createPiece("BISHOP", Colors.WHITE, "F1"));
        addPiece(PieceFactory.createPiece("KNIGHT", Colors.WHITE, "G1"));
        addPiece(PieceFactory.createPiece("ROOK", Colors.WHITE, "H1"));

        for (char col = 'A'; col <= 'H'; col++) {
            addPiece(PieceFactory.createPiece("PAWN", Colors.WHITE, new Position(col, 2)));
        }

        addPiece(PieceFactory.createPiece("ROOK", Colors.BLACK, "A8"));
        addPiece(PieceFactory.createPiece("KNIGHT", Colors.BLACK, "B8"));
        addPiece(PieceFactory.createPiece("BISHOP", Colors.BLACK, "C8"));
        addPiece(PieceFactory.createPiece("QUEEN", Colors.BLACK, "D8"));
        addPiece(PieceFactory.createPiece("KING", Colors.BLACK, "E8"));
        addPiece(PieceFactory.createPiece("BISHOP", Colors.BLACK, "F8"));
        addPiece(PieceFactory.createPiece("KNIGHT", Colors.BLACK, "G8"));
        addPiece(PieceFactory.createPiece("ROOK", Colors.BLACK, "H8"));

        for (char col = 'A'; col <= 'H'; col++) {
            addPiece(PieceFactory.createPiece("PAWN", Colors.BLACK, new Position(col, 7)));
        }
    }

    private void addPiece(Piece p) {
        pieces.add(new ChessPair<>(p.getPosition(), p));
    }

    public Piece getPieceAt(Position p) {
        for (ChessPair<Position, Piece> chessPair : pieces) {
            if (chessPair.getKey().equals(p)) {
                return chessPair.getValue();
            }
        }
        return null;
    }

    public boolean isOnBoard(Position position) {
        return position.getX() >= 'A' && position.getX() <= 'H'
                && position.getY() >= 1 && position.getY() <= 8;
    }

    public List<Position> getLegalMoves(Position from, Colors moverColor) throws InvalidMoveException {
        Piece piece = getPieceAt(from);

        if (piece == null) {
            throw new InvalidMoveException("No piece at " + from);
        }
        if (moverColor != null && piece.getColor() != moverColor) {
            throw new InvalidMoveException("You cannot move opponent's piece");
        }

        List<Position> rawMoves = piece.getPossibleMoves(this);
        List<Position> legalMoves = new ArrayList<>();

        for (Position target : rawMoves) {
            if (!isOnBoard(target)) {
                continue;
            }
            if (!wouldLeaveKingInCheck(from, target, piece.getColor())) {
                legalMoves.add(target);
            }
        }
        return legalMoves;
    }

    public boolean isValidMove(Position from, Position to) throws InvalidMoveException {
        return isValidMove(from, to, null);
    }

    public boolean isValidMove(Position from, Position to, Colors moverColor) throws InvalidMoveException {
        if (from.equals(to)) {
            throw new InvalidMoveException("Source and destination are the same");
        }
        if (!isOnBoard(to)) {
            throw new InvalidMoveException("Destination out of the board");
        }
        Piece piece = getPieceAt(from);
        if (piece == null) {
            throw new InvalidMoveException("No piece at " + from);
        }
        if (moverColor != null && piece.getColor() != moverColor) {
            throw new InvalidMoveException("Selected piece does not belong to current player");
        }

        List<Position> legalMoves = getLegalMoves(from, piece.getColor());
        if (!legalMoves.contains(to)) {
            throw new InvalidMoveException("Illegal move for selected piece");
        }
        return true;
    }

    public void movePiece(Position from, Position to, Colors moverColor, boolean allowPromotionChoice)
            throws InvalidMoveException, InvalidCommandException {
        isValidMove(from, to, moverColor);
        performMove(from, to, allowPromotionChoice);
    }

    public void movePiece(Position from, Position to) throws InvalidMoveException, InvalidCommandException {
        isValidMove(from, to);
        performMove(from, to, true);
    }

    private boolean wouldLeaveKingInCheck(Position from, Position to, Colors moverColor) throws InvalidMoveException {
        Board simulated = cloneBoard();
        simulated.applyMoveWithoutValidation(from, to);
        return simulated.isKingInCheck(moverColor);
    }

    private Board cloneBoard() throws InvalidMoveException {
        Board clone = new Board();
        clone.pieces.clear();
        for (ChessPair<Position, Piece> pair : pieces) {
            try {
                Piece pieceCopy = copyPiece(pair.getValue(), pair.getKey());
                clone.pieces.add(new ChessPair<>(pieceCopy.getPosition(), pieceCopy));
            } catch (InvalidCommandException e) {
                throw new InvalidMoveException("Failed to clone board state");
            }
        }
        return clone;
    }

    private Piece copyPiece(Piece piece, Position position) throws InvalidCommandException {
        Position copiedPosition = new Position(position.getX(), position.getY());
        Colors color = piece.getColor();

        if (piece instanceof King) {
            return new King(color, copiedPosition);
        } else if (piece instanceof Queen) {
            return new Queen(color, copiedPosition);
        } else if (piece instanceof Rook) {
            return new Rook(color, copiedPosition);
        } else if (piece instanceof Bishop) {
            return new Bishop(color, copiedPosition);
        } else if (piece instanceof Knight) {
            return new Knight(color, copiedPosition);
        } else if (piece instanceof Pawn) {
            Pawn originalPawn = (Pawn) piece;
            Pawn pawnCopy = new Pawn(color, copiedPosition);
            pawnCopy.setFirstMove(originalPawn.isFirstMove());
            return pawnCopy;
        }
        throw new InvalidCommandException("Unknown piece type");
    }

    private void applyMoveWithoutValidation(Position from, Position to) throws InvalidMoveException {
        Piece movingPiece = getPieceAt(from);
        if (movingPiece == null) {
            throw new InvalidMoveException("No piece at " + from);
        }
        Piece capturedPiece = getPieceAt(to);

        pieces.remove(new ChessPair<>(from, movingPiece));
        if (capturedPiece != null) {
            pieces.remove(new ChessPair<>(to, capturedPiece));
        }

        Position targetPosition;
        try {
            targetPosition = new Position(to.getX(), to.getY());
        } catch (InvalidCommandException e) {
            throw new InvalidMoveException("Invalid target position");
        }

        movingPiece.setPosition(targetPosition);
        pieces.add(new ChessPair<>(targetPosition, movingPiece));

        if (movingPiece instanceof Pawn pawn) {
            pawn.setFirstMove(false);
        }
    }

    private void performMove(Position from, Position to, boolean allowPromotionChoice)
            throws InvalidMoveException, InvalidCommandException {
        Piece movingPiece = getPieceAt(from);
        Piece capturedPiece = getPieceAt(to);
        ChessPair<Position, Piece> removedPieceFrom = new ChessPair<>(from, movingPiece);

        pieces.remove(removedPieceFrom);
        if (capturedPiece != null) {
            ChessPair<Position, Piece> removedPieceTo = new ChessPair<>(to, capturedPiece);
            pieces.remove(removedPieceTo);
        }

        movingPiece.setPosition(to);
        if (movingPiece instanceof Pawn) {
            ((Pawn) movingPiece).setFirstMove(false);
        }
        pieces.add(new ChessPair<>(to, movingPiece));

        if (movingPiece instanceof Pawn pawn && pawn.shouldPromote()) {
            promotePawn(to, movingPiece.getColor(), allowPromotionChoice);
        }
    }

    public boolean isKingInCheck(Colors color) throws InvalidMoveException {
        Position kingPos = findKingPosition(color);
        if (kingPos == null) {
            return false;
        }
        Colors opponentColor = color == Colors.WHITE ? Colors.BLACK : Colors.WHITE;
        return isSquareUnderAttack(kingPos, opponentColor);
    }

    private Position findKingPosition(Colors color) {
        for (ChessPair<Position, Piece> pair : pieces) {
            Piece piece = pair.getValue();
            if (piece instanceof King && piece.getColor() == color) {
                return pair.getKey();
            }
        }
        return null;
    }

    private boolean isSquareUnderAttack(Position position, Colors attackerColor) throws InvalidMoveException {
        for (ChessPair<Position, Piece> pair : pieces) {
            Piece piece = pair.getValue();
            if (piece.getColor() != attackerColor) {
                continue;
            }

            if (piece instanceof Pawn) {
                if (pawnAttacks(position, (Pawn) piece)) {
                    return true;
                }
            } else if (piece instanceof King) {
                if (kingAttacks(position, (King) piece)) {
                    return true;
                }
            } else {
                if (piece.getPossibleMoves(this).contains(position)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean pawnAttacks(Position target, Pawn pawn) {
        int direction = pawn.getColor() == Colors.WHITE ? 1 : -1;
        char[] cols = {(char) (pawn.getPosition().getX() - 1), (char) (pawn.getPosition().getX() + 1)};

        for (char c : cols) {
            try {
                Position attackPos = new Position(c, pawn.getPosition().getY() + direction);
                if (attackPos.equals(target)) {
                    return true;
                }
            } catch (InvalidCommandException e) {
                // ignore off-board attack squares
            }
        }
        return false;
    }

    private boolean kingAttacks(Position target, King king) throws InvalidMoveException {
        List<Position> moves = king.getPossibleMoves(this);
        return moves.contains(target);
    }

    private void promotePawn(Position position, Colors color, boolean allowPromotionChoice) throws InvalidCommandException {
        String choice = "QUEEN";
        if (allowPromotionChoice) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Promote pawn to (Q/R/B/N)");
            String userChoice = sc.nextLine().trim().toUpperCase();
            choice = switch (userChoice) {
                case "R" -> "ROOK";
                case "B" -> "BISHOP";
                case "N" -> "KNIGHT";
                case "Q" -> "QUEEN";
                default -> "QUEEN";
            };
        }

        Piece newPiece = PieceFactory.createPromotedPiece(choice, color, position);

        Piece old = getPieceAt(position);
        if (old != null) {
            pieces.remove(new ChessPair<>(position, old));
        }
        pieces.add(new ChessPair<>(position, newPiece));
    }

    public void promotePawnTo(Position position, Colors color, String pieceType) throws InvalidCommandException {
        Piece newPiece = PieceFactory.createPromotedPiece(pieceType, color, position);

        Piece old = getPieceAt(position);
        if (old != null) {
            pieces.remove(new ChessPair<>(position, old));
        }
        pieces.add(new ChessPair<>(position, newPiece));
    }

    public void display() throws InvalidCommandException {
        display(Colors.WHITE);
    }

    public void display(Colors perspective) throws InvalidCommandException {
        Colors view = perspective == null ? Colors.WHITE : perspective;
        boolean flip = view == Colors.BLACK;

        char startCol = flip ? 'H' : 'A';
        char endCol = flip ? 'A' : 'H';
        int colStep = flip ? -1 : 1;

        int startRow = flip ? 1 : 8;
        int endRow = flip ? 8 : 1;
        int rowStep = flip ? 1 : -1;

        System.out.print("\n   ");
        for (char col = startCol; ; col += colStep) {
            System.out.print(col + "   ");
            if (col == endCol) {
                break;
            }
        }
        System.out.println();

        for (int row = startRow; ; row += rowStep) {
            System.out.print(row + " ");
            for (char col = startCol; ; col += colStep) {
                Position pos = new Position(col, row);
                Piece piece = getPieceAt(pos);
                if (piece != null) {
                    System.out.print(piece + " ");
                } else {
                    System.out.print("--- ");
                }
                if (col == endCol) {
                    break;
                }
            }
            System.out.println(row);
            if (row == endRow) {
                break;
            }
        }

        System.out.print("   ");
        for (char col = startCol; ; col += colStep) {
            System.out.print(col + "   ");
            if (col == endCol) {
                break;
            }
        }
        System.out.println();
    }

    public TreeSet<ChessPair<Position, Piece>> getPieces() {
        return pieces;
    }

    public String snapshot(Colors currentTurn) {
        StringBuilder sb = new StringBuilder();
        sb.append(currentTurn != null ? currentTurn.name() : "NONE").append("|");
        for (ChessPair<Position, Piece> pair : pieces) {
            Piece piece = pair.getValue();
            sb.append(piece.type()).append("-").append(piece.getColor().name()).append("@").append(pair.getKey()).append(";");
        }
        return sb.toString();
    }
}
