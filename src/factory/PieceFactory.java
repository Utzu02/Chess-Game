package factory;

import exceptions.InvalidCommandException;
import model.Colors;
import model.Position;
import pieces.*;

public class PieceFactory {

    public static Piece createPiece(String type, Colors color, Position position) {
        if (type == null || color == null || position == null) {
            throw new IllegalArgumentException("Type, color, and position cannot be null");
        }

        switch (type.toUpperCase()) {
            case "KING":
                return new King(color, position);
            case "QUEEN":
                return new Queen(color, position);
            case "ROOK":
                return new Rook(color, position);
            case "BISHOP":
                return new Bishop(color, position);
            case "KNIGHT":
                return new Knight(color, position);
            case "PAWN":
                return new Pawn(color, position);
            default:
                throw new IllegalArgumentException("Unknown piece type: " + type);
        }
    }

    public static Piece createPiece(String type, Colors color, String positionStr) throws InvalidCommandException {
        Position position = new Position(positionStr);
        return createPiece(type, color, position);
    }

    public static Piece copyPiece(Piece piece, Position newPosition) {
        if (piece == null || newPosition == null) {
            throw new IllegalArgumentException("Piece and new position cannot be null");
        }

        String type = String.valueOf(piece.type());
        Colors color = piece.getColor();
        Piece newPiece = createPiece(type, color, newPosition);

        if (piece instanceof Pawn && newPiece instanceof Pawn) {
            ((Pawn) newPiece).setFirstMove(((Pawn) piece).isFirstMove());
        }

        return newPiece;
    }

    public static Piece createPromotedPiece(String promotionType, Colors color, Position position) {
        String upperType = promotionType.toUpperCase();

        if (upperType.equals("PAWN") || upperType.equals("KING")) {
            throw new IllegalArgumentException("Cannot promote to " + promotionType);
        }

        return createPiece(upperType, color, position);
    }
}
