package pieces;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import model.Board;
import model.Colors;
import model.Position;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(Colors color, Position position) {
        super(color, position);
    }

    public char type() {
        return 'K';
    }

    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        Position position = getPosition();

        int[] dirY = {-1,-1,-1,0,0,1,1,1};
        int[] dirX = {-1,0,1,-1,1,-1,0,1};

        for (int i = 0; i < 8; i++) {
            try {
                char x = (char)(position.getX() + dirX[i]);
                int y = position.getY() + dirY[i];
                Position newPos = new Position(x, y);

                Piece newPiece = board.getPieceAt(newPos);

                if (newPiece == null || newPiece.getColor() != getColor()) {
                    moves.add(newPos);
                }
            } catch (InvalidCommandException e) {
                continue;
            }
        }

        return moves;
    }

    public boolean checkForCheck(Board board, Position kingPosition) {
        return getPossibleMoves(board).contains(kingPosition);
    }
}
