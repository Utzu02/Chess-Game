package observer;

import model.Move;
import model.Player;
import pieces.Piece;

public interface GameObserver {
    void onMoveMade(Move move);

    void onPieceCaptured(Piece piece);

    void onPlayerSwitch(Player currentPlayer);

    void onCheck(Player playerInCheck);

    void onCheckmate(Player winner);

    void onGameEnd(String result);
}
