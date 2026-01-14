package pieces;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import model.Board;
import model.Colors;
import model.Position;
import strategy.move.KnightMoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(Colors color, Position position) {
        super(color, position);
        this.moveStrategy = new KnightMoveStrategy();
    }

    public char type() {
        return 'N';
    }

    public List<Position> getPossibleMoves(Board board) {
        return moveStrategy.getPossibleMoves(board, this);
    }

    public boolean checkForCheck(Board board, Position kingPosition) {
        return getPossibleMoves(board).contains(kingPosition);
    }
}
