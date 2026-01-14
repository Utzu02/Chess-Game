package pieces;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import model.Board;
import model.Colors;
import model.Position;
import strategy.move.KingMoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(Colors color, Position position) {
        super(color, position);
        this.moveStrategy = new KingMoveStrategy();
    }

    public char type() {
        return 'K';
    }

    public List<Position> getPossibleMoves(Board board) {
        return moveStrategy.getPossibleMoves(board, this);
    }

    public boolean checkForCheck(Board board, Position kingPosition) {
        return getPossibleMoves(board).contains(kingPosition);
    }
}
