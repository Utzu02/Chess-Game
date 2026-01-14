package strategy.move;

import exceptions.InvalidCommandException;
import model.Board;
import model.Position;
import pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class KnightMoveStrategy implements MoveStrategy {

    @Override
    public List<Position> getPossibleMoves(Board board, Piece piece) {
        List<Position> moves = new ArrayList<>();
        Position position = piece.getPosition();

        int[] dirY = {2, 2, -2, -2, 1, 1, -1, -1};
        int[] dirX = {1, -1, 1, -1, 2, -2, 2, -2};

        for (int i = 0; i < 8; i++) {
            try {
                char x = (char) (position.getX() + dirX[i]);
                int y = position.getY() + dirY[i];
                Position newPosition = new Position(x, y);

                Piece targetPiece = board.getPieceAt(newPosition);

                if (targetPiece == null) {
                    moves.add(newPosition);
                } else if (targetPiece.getColor() != piece.getColor()) {
                    moves.add(newPosition);
                }
            } catch (InvalidCommandException e) {
                // Position out of bounds, skip
            }
        }

        return moves;
    }
}
