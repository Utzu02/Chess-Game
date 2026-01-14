package pieces;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import model.Board;
import model.Colors;
import model.Position;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(Colors color, Position position) {
        super(color,position);
    }
    public char type() {
        return 'Q';
    }

    public List<Position> getPossibleMoves(Board board) throws InvalidMoveException {
        List<Position> moves = new ArrayList<>();
        Position position = getPosition();

        int[] dirY = {-1,-1,-1,0,0,1,1,1};
        int[] dirX = {-1,0,1,-1,1,-1,0,1};

        for (int i = 0; i < 8; i++) {
            int steps = 1;
            while (true) {
                try {
                    char x = (char)(position.getX() + dirX[i] * steps);
                    int y = position.getY() + dirY[i] * steps;
                    Position newPosition = new Position(x, y);

                    Piece piece = board.getPieceAt(newPosition);

                    if (piece == null) {
                        moves.add(newPosition);
                        steps++;
                    } else if (piece.getColor() != getColor()) {
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

    public boolean checkForCheck(Board board, Position kingPosition) throws InvalidMoveException {
        return getPossibleMoves(board).contains(kingPosition);
    }
}
