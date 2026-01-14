package pieces;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import model.Board;
import model.Colors;
import model.Position;
import strategy.move.PawnMoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    private boolean firstMove;
    public Pawn(Colors color, Position position) {
        super(color, position);
        this.firstMove = true;
        this.moveStrategy = new PawnMoveStrategy();
    }

    public char type() {
        return 'P';
    }

    public List<Position> getPossibleMoves(Board board) {
        return moveStrategy.getPossibleMoves(board, this);
    }

    public boolean checkForCheck(Board board, Position kingPosition) {
        int direction = getColor() == Colors.WHITE ? 1 : -1;
        char[] captureColumns = {(char) (getPosition().getX() - 1), (char) (getPosition().getX() + 1)};
        for (char c : captureColumns) {
            try {
                Position attack = new Position(c, getPosition().getY() + direction);
                if (attack.equals(kingPosition)) {
                    return true;
                }
            } catch (InvalidCommandException e) {
                // ignore invalid squares
            }
        }
        return false;
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public boolean isFirstMove() {
        return firstMove;
    }

    public boolean shouldPromote() {
        Position pos = getPosition();
        if (getColor() == Colors.WHITE && pos.getY() == 8) {
            return true;
        }
        return getColor() == Colors.BLACK && pos.getY() == 1;
    }
}
