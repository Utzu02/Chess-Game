package strategy.move;

import exceptions.InvalidCommandException;
import model.Board;
import model.Position;
import pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class QueenMoveStrategy implements MoveStrategy {

    @Override
    public List<Position> getPossibleMoves(Board board, Piece piece) {
        List<Position> moves = new ArrayList<>();
        Position position = piece.getPosition();

        int[] dirY = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dirX = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int steps = 1;
            while (true) {
                try {
                    char x = (char)(position.getX() + dirX[i] * steps);
                    int y = position.getY() + dirY[i] * steps;
                    Position newPosition = new Position(x, y);

                    Piece targetPiece = board.getPieceAt(newPosition);

                    if (targetPiece == null) {
                        moves.add(newPosition);
                        steps++;
                    } else if (targetPiece.getColor() != piece.getColor()) {
                        moves.add(newPosition);
                        break;
                    } else {
                        break;
                    }
                } catch (InvalidCommandException e) {
                    break;
                }
            }
        }

        return moves;
    }
}
