package strategy.scoring;

import pieces.Piece;

public class PieceCaptureScoringStrategy implements ScoringStrategy {

    @Override
    public int calculatePoints(Object context) {
        if (!(context instanceof Piece)) {
            return 0;
        }

        Piece piece = (Piece) context;
        char type = piece.type();

        return switch (type) {
            case 'Q' -> 90;
            case 'R' -> 50;
            case 'B' -> 30;
            case 'N' -> 30;
            case 'P' -> 10;
            case 'K' -> 0;
            default -> 0;
        };
    }

    public int getPieceValue(Piece piece) {
        return calculatePoints(piece);
    }
}
