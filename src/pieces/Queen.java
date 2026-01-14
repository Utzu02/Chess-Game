package pieces;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import model.Board;
import model.Colors;
import model.Position;
import strategy.move.QueenMoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(Colors color, Position position) {
        super(color,position);
        this.moveStrategy = new QueenMoveStrategy();
    }
    public char type() {
        return 'Q';
    }

    public List<Position> getPossibleMoves(Board board) throws InvalidMoveException {
        return moveStrategy.getPossibleMoves(board, this);
    }

    public boolean checkForCheck(Board board, Position kingPosition) throws InvalidMoveException {
        return getPossibleMoves(board).contains(kingPosition);
    }
}
