package strategy.move;

import model.Board;
import model.Position;
import pieces.Piece;

import java.util.List;

public interface MoveStrategy {
    List<Position> getPossibleMoves(Board board, Piece piece);
}
