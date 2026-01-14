package strategy.move;

import exceptions.InvalidCommandException;
import model.Board;
import model.Colors;
import model.Position;
import pieces.Pawn;
import pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class PawnMoveStrategy implements MoveStrategy {

    @Override
    public List<Position> getPossibleMoves(Board board, Piece piece) {
        List<Position> moves = new ArrayList<>();
        Position position = piece.getPosition();
        Colors color = piece.getColor();

        int direction = (color == Colors.WHITE) ? 1 : -1;

        boolean firstMove = (piece instanceof Pawn) && ((Pawn) piece).isFirstMove();

        try {
            Position oneStep = new Position(position.getX(), position.getY() + direction);
            Piece oneStepPiece = board.getPieceAt(oneStep);

            if (oneStepPiece == null) {
                moves.add(oneStep);

                if (firstMove) {
                    Position twoSteps = new Position(position.getX(), position.getY() + direction * 2);
                    Piece twoStepPiece = board.getPieceAt(twoSteps);

                    if (twoStepPiece == null) {
                        moves.add(twoSteps);
                    }
                }
            }
        } catch (InvalidCommandException e) {
            // Outside of board; ignore
        }

        char[] captureColumns = {
                (char)(position.getX() - 1),
                (char)(position.getX() + 1)
        };

        for (char c : captureColumns) {
            try {
                Position capturePos = new Position(c, position.getY() + direction);
                Piece pieceAtCapture = board.getPieceAt(capturePos);

                if (pieceAtCapture != null && pieceAtCapture.getColor() != color) {
                    moves.add(capturePos);
                }
            } catch (InvalidCommandException e) {
            }
        }

        return moves;
    }
}
