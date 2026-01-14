package pieces;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import model.Board;
import model.Colors;
import model.Position;
import strategy.move.RookMoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public Rook(Colors color, Position position) {
        super(color, position);
        this.moveStrategy = new RookMoveStrategy();
    }

    public char type() {
        return 'R';
    }

    public List<Position> getPossibleMoves(Board board) {
        return moveStrategy.getPossibleMoves(board, this);
    }

    public boolean checkForCheck(Board board, Position kingPosition) {
        return getPossibleMoves(board).contains(kingPosition);
    }
}
