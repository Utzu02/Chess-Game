package strategy.move;

import exceptions.InvalidCommandException;
import model.Board;
import model.Position;
import pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class KingMoveStrategy implements MoveStrategy {

    @Override
    public List<Position> getPossibleMoves(Board board, Piece piece) {
        List<Position> moves = new ArrayList<>();
        Position position = piece.getPosition();

        int[] dirY = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dirX = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            try {
                char x = (char)(position.getX() + dirX[i]);
                int y = position.getY() + dirY[i];
                Position newPos = new Position(x, y);

                Piece targetPiece = board.getPieceAt(newPos);

                if (targetPiece == null || targetPiece.getColor() != piece.getColor()) {
                    moves.add(newPos);
                }
            } catch (InvalidCommandException e) {
                // Position out of bounds, skip
            }
        }

        return moves;
    }
}
