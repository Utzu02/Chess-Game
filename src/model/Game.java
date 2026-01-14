package model;

import exceptions.InvalidCommandException;
import observer.GameObserver;
import pieces.*;
import exceptions.InvalidMoveException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private int id;
    private Board board;
    private List<Player> players;
    private List<Move> moves;
    private int currentPlayerIndex;
    private Map<String, Integer> boardStates;
    private List<GameObserver> observers;

    public Game() {
        players = new ArrayList<>();
        moves = new ArrayList<>();
        board = new Board();
        currentPlayerIndex = 0;
        boardStates = new HashMap<>();
        observers = new ArrayList<>();
    }

    public Game(int id) {
        this();
        this.id = id;
    }

    public void start() throws InvalidCommandException {
        start(Colors.WHITE);
    }

    public void start(Colors perspective) throws InvalidCommandException {
        board.initialize();
        moves.clear();
        currentPlayerIndex = 0;
        boardStates.clear();
        registerBoardState();
        System.out.println("Game Started");
        board.display(perspective);
    }

    public void resume() throws InvalidCommandException {
        resume(Colors.WHITE);
    }

    public void resume(Colors perspective) throws InvalidCommandException {
        if (boardStates == null) {
            boardStates = new HashMap<>();
        }
        boardStates.clear();
        rebuildPlayerStateFromMoves();
        registerBoardState();
        System.out.println("Game Resumed");
        board.display(perspective);
    }

    public void switchPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int index) {
        this.currentPlayerIndex = index;
    }

    public void setCurrentPlayerColor(String colorStr) {
        if (colorStr == null) {
            return;
        }
        Colors color = colorStr.equals("WHITE") ? Colors.WHITE : Colors.BLACK;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getColor() == color) {
                currentPlayerIndex = i;
                break;
            }
        }
    }

    public boolean checkForCheckMate() throws InvalidMoveException {
        Player currentPlayer = getCurrentPlayer();
        Colors colorToMove = currentPlayer.getColor();

        if (!board.isKingInCheck(colorToMove)) {
            return false;
        }
        return !hasAnyLegalMove(colorToMove);
    }

    public boolean hasAnyLegalMove(Colors color) throws InvalidMoveException {
        for (ChessPair<Position, Piece> pair : board.getPieces()) {
            if (pair.getValue().getColor() != color) {
                continue;
            }
            List<Position> legalMoves = board.getLegalMoves(pair.getKey(), color);
            if (!legalMoves.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void addMove(Player player, Position from, Position to) {
        addMove(player, from, to, board.getPieceAt(to));
    }

    public void addMove(Player player, Position from, Position to, Piece capturedPiece) {
        Move move = new Move(player.getColor(), from, to);
        if (capturedPiece != null) {
            move.setCapturedPiece(capturedPiece);
        }
        moves.add(move);
    }

    public boolean checkForStalemate() throws InvalidMoveException {
        if (isThreefoldRepetition()) {
            return true;
        }
        Colors colorToMove = getCurrentPlayer().getColor();
        if (board.isKingInCheck(colorToMove)) {
            return false;
        }
        return !hasAnyLegalMove(colorToMove);
    }

    public boolean isThreefoldRepetition() {
        if (moves == null || moves.size() < 6) {
            return false;
        }

        int size = moves.size();
        Move m1 = moves.get(size - 1);
        Move m2 = moves.get(size - 2);
        Move m3 = moves.get(size - 3);
        Move m4 = moves.get(size - 4);
        Move m5 = moves.get(size - 5);
        Move m6 = moves.get(size - 6);

        boolean repeatingFirst = sameMove(m1, m3) && sameMove(m3, m5);
        boolean repeatingSecond = sameMove(m2, m4) && sameMove(m4, m6);
        boolean backAndForth = isBackAndForth(m1, m2) && isBackAndForth(m3, m4) && isBackAndForth(m5, m6);
        return repeatingFirst && repeatingSecond && backAndForth;
    }

    public void registerBoardState() {
        if (boardStates == null) {
            boardStates = new HashMap<>();
        }
        Colors turn = null;
        if (!players.isEmpty()) {
            turn = getCurrentPlayer().getColor();
        }
        String signature = board.snapshot(turn);
        boardStates.put(signature, boardStates.getOrDefault(signature, 0) + 1);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId(long id) {
        this.id = (int) id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        if (moves == null) {
            this.moves = new ArrayList<>();
        } else {
            this.moves = moves;
        }
    }

    public void rebuildPlayerStateFromMoves() {
        if (players == null) {
            return;
        }
        for (Player player : players) {
            player.getCapturedPieces().clear();
            player.setPoints(0);
        }
        if (moves == null) {
            return;
        }
        for (Move move : moves) {
            if (move.getCapturedPiece() == null) {
                continue;
            }
            for (Player player : players) {
                if (player.getColor() == move.getPlayerColor()) {
                    player.getCapturedPieces().add(move.getCapturedPiece());
                    player.setPoints(player.getPoints() + captureValue(move.getCapturedPiece()));
                    break;
                }
            }
        }
    }

    private boolean sameMove(Move a, Move b) {
        if (a == null || b == null) {
            return false;
        }
        return a.getPlayerColor() == b.getPlayerColor()
                && a.getFrom().equals(b.getFrom())
                && a.getTo().equals(b.getTo());
    }

    private boolean isBackAndForth(Move first, Move second) {
        if (first == null || second == null) {
            return false;
        }
        return first.getFrom().equals(second.getTo()) && first.getTo().equals(second.getFrom());
    }

    private int captureValue(Piece capturedPiece) {
        switch (capturedPiece.type()) {
            case 'Q':
                return 90;
            case 'R':
                return 50;
            case 'B':
            case 'N':
                return 30;
            case 'P':
                return 6;
            default:
                return 0;
        }
    }

    public void addObserver(GameObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    public void notifyMoveMade(Move move) {
        for (GameObserver observer : observers) {
            observer.onMoveMade(move);
        }
    }

    public void notifyPieceCaptured(Piece piece) {
        for (GameObserver observer : observers) {
            observer.onPieceCaptured(piece);
        }
    }

    public void notifyPlayerSwitch(Player currentPlayer) {
        for (GameObserver observer : observers) {
            observer.onPlayerSwitch(currentPlayer);
        }
    }

    public void notifyCheck(Player playerInCheck) {
        for (GameObserver observer : observers) {
            observer.onCheck(playerInCheck);
        }
    }

    public void notifyCheckmate(Player winner) {
        for (GameObserver observer : observers) {
            observer.onCheckmate(winner);
        }
    }

    public void notifyGameEnd(String result) {
        for (GameObserver observer : observers) {
            observer.onGameEnd(result);
        }
    }
}
