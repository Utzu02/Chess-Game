package pieces;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import model.Board;
import model.Colors;
import model.Position;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    private boolean firstMove;
    public Pawn(Colors color, Position position) {
        super(color, position);
        this.firstMove = true;
    }

    public char type() {
        return 'P';
    }

    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        Position position = getPosition();

        Colors color = getColor();
        int direction;

        if(color == Colors.WHITE) {
            direction = 1;
        } else {
            direction = -1;
        }

        try {
            Position oneStep = new Position(position.getX(), position.getY() + direction);
            Piece oneStepPiece = board.getPieceAt(oneStep);

            if(oneStepPiece == null) {
                moves.add(oneStep);

                if(firstMove) {
                    Position twoSteps = new Position(position.getX(), position.getY() + direction * 2);
                    Piece twoStepPiece = board.getPieceAt(twoSteps);

                    if (twoStepPiece == null) {
                        moves.add(twoSteps);
                    }
                }
            }
        } catch (InvalidCommandException e) {
            // outside of board; ignore
        }

        char[] captureColumns = {
                (char)(position.getX() - 1),  // Left diagonal
                (char)(position.getX() + 1)   // Right diagonal
        };

        for(char c : captureColumns) {
            try {
                Position capturePos = new Position(c, position.getY() + direction);
                Piece pieceAtCapture = board.getPieceAt(capturePos);

                // Can only capture if there's an enemy piece
                if (pieceAtCapture != null && pieceAtCapture.getColor() != getColor()) {
                    moves.add(capturePos);
                }
            } catch (InvalidCommandException e) {
                continue;
            }
        }
        return moves;
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
