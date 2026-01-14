package pieces;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import model.Board;
import model.Colors;
import model.Position;
import strategy.move.BishopMoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(Colors color, Position position) {
        super(color,position);
        this.moveStrategy = new BishopMoveStrategy();
    }

    public char type() {
        return 'B';
    }

    public List<Position> getPossibleMoves(Board board) {
        return moveStrategy.getPossibleMoves(board, this);
    }

    public boolean checkForCheck(Board board, Position kingPosition) {
        return getPossibleMoves(board).contains(kingPosition);
    }
}
