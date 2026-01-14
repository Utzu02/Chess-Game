package test;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import model.*;
import pieces.*;
import util.JsonReaderUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeSet;

public class Test {
    private static int passed = 0;
    private static int failed = 0;

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        run("testPositionParsing", Test::testPositionParsing);
        run("testChessPairEquality", Test::testChessPairEquality);
        run("testBoardInitialization", Test::testBoardInitialization);
        run("testPieceMovementRules", Test::testPieceMovementRules);
        run("testPawnMovement", Test::testPawnMovement);
        run("testKingSafetyPinnedPiece", Test::testKingSafetyPinnedPiece);
        run("testCheckDetection", Test::testCheckDetection);
        run("testPlayerCaptureScoring", Test::testPlayerCaptureScoring);
        run("testPawnPromotion", Test::testPawnPromotion);
        run("testCheckmate", Test::testCheckmate);
        run("testStalemateRepetition", Test::testStalemateRepetition);
        run("testJsonRoundTrip", Test::testJsonRoundTrip);

        System.out.println("\nSummary: " + passed + " passed, " + failed + " failed.");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void run(String name, ThrowingRunnable test) {
        try {
            test.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable t) {
            failed++;
            System.out.println("[FAIL] " + name + " -> " + t.getMessage());
            t.printStackTrace(System.out);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " (expected=" + expected + ", actual=" + actual + ")");
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " (expected=" + expected + ", actual=" + actual + ")");
        }
    }

    private static void assertThrows(Class<? extends Throwable> expected, ThrowingRunnable runnable, String message) {
        try {
            runnable.run();
        } catch (Throwable t) {
            if (expected.isInstance(t)) {
                return;
            }
            throw new AssertionError(message + " (expected " + expected.getSimpleName() + " but got " + t + ")");
        }
        throw new AssertionError(message + " (expected " + expected.getSimpleName() + " but nothing was thrown)");
    }

    private static Position pos(String coord) {
        try {
            return new Position(coord);
        } catch (InvalidCommandException e) {
            throw new RuntimeException(e);
        }
    }

    private static ChessPair<Position, Piece> pair(Position position, Piece piece) {
        return new ChessPair<>(position, piece);
    }

    private static King wKing(String square) {
        return new King(Colors.WHITE, pos(square));
    }

    private static King bKing(String square) {
        return new King(Colors.BLACK, pos(square));
    }

    private static void testPositionParsing() throws Exception {
        assertEquals("A2", pos("a2").toString(), "Lowercase should normalize to uppercase");
        assertEquals("A2", pos(" A2 ").toString(), "Position should trim whitespace");

        assertThrows(InvalidCommandException.class, () -> new Position("A9"), "Row 9 is invalid");
        assertThrows(InvalidCommandException.class, () -> new Position("I1"), "Column I is invalid");
        assertThrows(InvalidCommandException.class, () -> new Position(""), "Empty string is invalid");
        assertThrows(InvalidCommandException.class, () -> new Position(null), "Null input is invalid");
    }

    private static void testChessPairEquality() throws Exception {
        Position position = pos("A1");
        Piece rook = new Rook(Colors.WHITE, position);
        TreeSet<ChessPair<Position, Piece>> set = new TreeSet<>();
        set.add(new ChessPair<>(position, rook));

        boolean removed = set.remove(new ChessPair<>(pos("A1"), rook));
        assertTrue(removed, "Removal using equal key should succeed");
        assertTrue(set.isEmpty(), "TreeSet should be empty after removal");
    }

    private static void testBoardInitialization() throws Exception {
        Board board = new Board();
        board.initialize();

        assertEquals(32, board.getPieces().size(), "Board should start with 32 pieces");
        Piece a1 = board.getPieceAt(pos("A1"));
        Piece e1 = board.getPieceAt(pos("E1"));
        Piece a8 = board.getPieceAt(pos("A8"));
        Piece e8 = board.getPieceAt(pos("E8"));

        assertTrue(a1 instanceof Rook && a1.getColor() == Colors.WHITE, "A1 should be white rook");
        assertTrue(e1 instanceof King && e1.getColor() == Colors.WHITE, "E1 should be white king");
        assertTrue(a8 instanceof Rook && a8.getColor() == Colors.BLACK, "A8 should be black rook");
        assertTrue(e8 instanceof King && e8.getColor() == Colors.BLACK, "E8 should be black king");

        for (char c = 'A'; c <= 'H'; c++) {
            Position whitePawnPos = pos(c + "2");
            Position blackPawnPos = pos(c + "7");
            Piece wp = board.getPieceAt(whitePawnPos);
            Piece bp = board.getPieceAt(blackPawnPos);
            assertTrue(wp instanceof Pawn && wp.getColor() == Colors.WHITE, "White pawn missing at " + whitePawnPos);
            assertTrue(bp instanceof Pawn && bp.getColor() == Colors.BLACK, "Black pawn missing at " + blackPawnPos);
        }
    }

    private static void testPieceMovementRules() throws Exception {
        Board board = new Board();
        board.getPieces().clear();
        Piece knight = new Knight(Colors.WHITE, pos("D4"));
        board.getPieces().add(pair(pos("D4"), knight));
        List<Position> centerKnightMoves = board.getLegalMoves(pos("D4"), Colors.WHITE);
        assertEquals(8, centerKnightMoves.size(), "Knight at D4 should have 8 moves");

        board.getPieces().clear();
        Piece cornerKnight = new Knight(Colors.WHITE, pos("A1"));
        board.getPieces().add(pair(pos("A1"), cornerKnight));
        List<Position> cornerKnightMoves = board.getLegalMoves(pos("A1"), Colors.WHITE);
        assertEquals(2, cornerKnightMoves.size(), "Knight at A1 should have 2 moves");
        assertTrue(cornerKnightMoves.contains(pos("B3")) && cornerKnightMoves.contains(pos("C2")),
                "Corner knight moves should be B3 and C2");

        board.getPieces().clear();
        Piece bishop = new Bishop(Colors.WHITE, pos("C1"));
        board.getPieces().add(pair(pos("C1"), bishop));
        Piece enemy = new Pawn(Colors.BLACK, pos("D2"));
        board.getPieces().add(pair(pos("D2"), enemy));
        Piece friendlyBlocker = new Pawn(Colors.WHITE, pos("E3"));
        board.getPieces().add(pair(pos("E3"), friendlyBlocker));
        List<Position> bishopMoves = board.getLegalMoves(pos("C1"), Colors.WHITE);
        assertTrue(bishopMoves.contains(pos("D2")), "Bishop should capture enemy on D2");
        assertTrue(!bishopMoves.contains(pos("E3")), "Bishop cannot land on own piece");
        assertTrue(!bishopMoves.contains(pos("F4")), "Bishop cannot jump beyond blocker");

        board.getPieces().clear();
        Piece rook = new Rook(Colors.WHITE, pos("A1"));
        board.getPieces().add(pair(pos("A1"), rook));
        Piece blockingEnemy = new Pawn(Colors.BLACK, pos("A3"));
        board.getPieces().add(pair(pos("A3"), blockingEnemy));
        List<Position> rookMoves = board.getLegalMoves(pos("A1"), Colors.WHITE);
        assertTrue(rookMoves.contains(pos("A2")), "Rook can move to empty square before blocker");
        assertTrue(rookMoves.contains(pos("A3")), "Rook can capture enemy on A3");
        assertTrue(!rookMoves.contains(pos("A4")), "Rook cannot jump beyond captured piece");
    }

    private static void testPawnMovement() throws Exception {
        Board board = new Board();
        board.getPieces().clear();
        Piece pawn = new Pawn(Colors.WHITE, pos("D2"));
        board.getPieces().add(pair(pos("D2"), pawn));
        List<Position> firstMoves = board.getLegalMoves(pos("D2"), Colors.WHITE);
        assertTrue(firstMoves.contains(pos("D3")) && firstMoves.contains(pos("D4")),
                "Pawn should move one or two squares on first move");
        assertTrue(!firstMoves.contains(pos("C3")) && !firstMoves.contains(pos("E3")),
                "Pawn cannot capture diagonally without enemy");

        board.getPieces().clear();
        Piece blockedPawn = new Pawn(Colors.WHITE, pos("D2"));
        board.getPieces().add(pair(pos("D2"), blockedPawn));
        board.getPieces().add(pair(pos("D3"), new Knight(Colors.WHITE, pos("D3"))));
        List<Position> blockedMoves = board.getLegalMoves(pos("D2"), Colors.WHITE);
        assertEquals(0, blockedMoves.size(), "Blocked pawn should have no forward moves");

        board.getPieces().clear();
        Piece capturingPawn = new Pawn(Colors.WHITE, pos("D2"));
        board.getPieces().add(pair(pos("D2"), capturingPawn));
        board.getPieces().add(pair(pos("C3"), new Pawn(Colors.BLACK, pos("C3"))));
        List<Position> captureMoves = board.getLegalMoves(pos("D2"), Colors.WHITE);
        assertTrue(captureMoves.contains(pos("C3")), "Pawn should capture diagonally when enemy present");
    }

    private static void testKingSafetyPinnedPiece() throws Exception {
        Board board = new Board();
        board.getPieces().clear();
        board.getPieces().add(pair(pos("E1"), wKing("E1")));
        board.getPieces().add(pair(pos("E2"), new Rook(Colors.WHITE, pos("E2"))));
        board.getPieces().add(pair(pos("E8"), new Rook(Colors.BLACK, pos("E8"))));

        List<Position> legal = board.getLegalMoves(pos("E2"), Colors.WHITE);
        assertTrue(legal.contains(pos("E3")), "Pinned rook can slide along file");
        assertTrue(!legal.contains(pos("D2")), "Pinned rook cannot expose king to check");
        assertThrows(InvalidMoveException.class,
                () -> board.isValidMove(pos("E2"), pos("D2"), Colors.WHITE),
                "isValidMove should reject exposing moves");
    }

    private static void testCheckDetection() throws Exception {
        Board board = new Board();
        board.getPieces().clear();
        board.getPieces().add(pair(pos("E1"), wKing("E1")));
        board.getPieces().add(pair(pos("E8"), new Rook(Colors.BLACK, pos("E8"))));
        assertTrue(board.isKingInCheck(Colors.WHITE), "White king should be in check from rook");

        Board board2 = new Board();
        board2.getPieces().clear();
        board2.getPieces().add(pair(pos("E4"), wKing("E4")));
        board2.getPieces().add(pair(pos("D5"), new Pawn(Colors.BLACK, pos("D5"))));
        assertTrue(board2.isKingInCheck(Colors.WHITE), "White king should be in check from pawn diagonal");
    }

    private static void testPlayerCaptureScoring() throws Exception {
        Board board = new Board();
        board.getPieces().clear();
        Piece queen = new Queen(Colors.WHITE, pos("D1"));
        Piece targetPawn = new Pawn(Colors.BLACK, pos("D7"));
        board.getPieces().add(pair(pos("D1"), queen));
        board.getPieces().add(pair(pos("D7"), targetPawn));

        Player white = new Player("Alice", Colors.WHITE);
        white.makeMove(pos("D1"), pos("D7"), board);

        assertEquals(1, white.getCapturedPieces().size(), "Captured list should include pawn");
        assertEquals(6, white.getPoints(), "Capturing pawn should add 6 points");
        Piece newPiece = board.getPieceAt(pos("D7"));
        assertTrue(newPiece instanceof Queen, "Queen should now occupy captured square");
    }

    private static void testPawnPromotion() throws Exception {
        Board board = new Board();
        board.getPieces().clear();
        Piece pawn = new Pawn(Colors.WHITE, pos("A7"));
        board.getPieces().add(pair(pos("A7"), pawn));

        board.movePiece(pos("A7"), pos("A8"), Colors.WHITE, false);

        Piece promoted = board.getPieceAt(pos("A8"));
        assertTrue(promoted instanceof Queen, "Pawn should promote to queen by default");
        assertEquals('Q', promoted.type(), "Promoted piece type should be Q");
    }

    private static void testCheckmate() throws Exception {
        Board board = new Board();
        board.getPieces().clear();
        board.getPieces().add(pair(pos("A1"), wKing("A1")));
        board.getPieces().add(pair(pos("H8"), bKing("H8")));
        board.getPieces().add(pair(pos("A2"), new Rook(Colors.BLACK, pos("A2"))));
        board.getPieces().add(pair(pos("B2"), new Rook(Colors.BLACK, pos("B2"))));

        Game game = new Game();
        game.setBoard(board);
        game.setPlayers(Arrays.asList(new Player("White", Colors.WHITE), new Player("Black", Colors.BLACK)));
        game.setCurrentPlayerIndex(0); // White to move

        assertTrue(game.checkForCheckMate(), "Position should be checkmate for white");
    }

    private static void testStalemateRepetition() throws Exception {
        Game game = new Game();
        game.setPlayers(Arrays.asList(new Player("White", Colors.WHITE), new Player("Black", Colors.BLACK)));
        List<Move> repetition = Arrays.asList(
                new Move(Colors.WHITE, pos("A1"), pos("A2")),
                new Move(Colors.BLACK, pos("A2"), pos("A1")),
                new Move(Colors.WHITE, pos("A1"), pos("A2")),
                new Move(Colors.BLACK, pos("A2"), pos("A1")),
                new Move(Colors.WHITE, pos("A1"), pos("A2")),
                new Move(Colors.BLACK, pos("A2"), pos("A1"))
        );
        game.setMoves(new ArrayList<>(repetition));

        assertTrue(game.isThreefoldRepetition(), "Back-and-forth repetition should be detected");
        assertTrue(game.checkForStalemate(), "Repetition should count as draw");
    }

    private static void testJsonRoundTrip() throws Exception {
        List<User> users = new ArrayList<>();
        User u1 = new User();
        u1.setEmail("u1@example.com");
        u1.setPassword("secret");
        u1.setPoints(15);
        u1.setGames(Arrays.asList(1, 2, 3));
        users.add(u1);

        Path usersFile = Files.createTempFile("users", ".json");
        JsonReaderUtil.writeUsers(usersFile, users);
        List<User> readUsers = JsonReaderUtil.readUsers(usersFile);
        assertEquals(users.size(), readUsers.size(), "User count should roundtrip");
        User readU1 = readUsers.get(0);
        assertEquals(u1.getEmail(), readU1.getEmail(), "Email should roundtrip");
        assertEquals(u1.getPassword(), readU1.getPassword(), "Password should roundtrip");
        assertEquals(u1.getPoints(), readU1.getPoints(), "Points should roundtrip");
        assertEquals(u1.getGameIds(), readU1.getGameIds(), "Game IDs should roundtrip");
        Files.deleteIfExists(usersFile);

        Game game = new Game();
        game.setId(99);
        game.setPlayers(Arrays.asList(new Player("W", Colors.WHITE), new Player("B", Colors.BLACK)));
        game.setCurrentPlayerIndex(1);

        Board board = new Board();
        board.getPieces().clear();
        board.getPieces().add(pair(pos("E1"), wKing("E1")));
        board.getPieces().add(pair(pos("E8"), bKing("E8")));
        board.getPieces().add(pair(pos("D1"), new Queen(Colors.WHITE, pos("D1"))));
        Piece capturedPiece = new Pawn(Colors.BLACK, pos("D5"));
        board.getPieces().add(pair(pos("D5"), capturedPiece));
        game.setBoard(board);

        Move move = new Move(Colors.WHITE, pos("D1"), pos("D5"));
        move.setCapturedPiece(capturedPiece);
        List<Move> moves = new ArrayList<>();
        moves.add(move);
        game.setMoves(moves);

        Map<Integer, Game> games = new HashMap<>();
        games.put(game.getId(), game);

        Path gamesFile = Files.createTempFile("games", ".json");
        JsonReaderUtil.writeGames(gamesFile, games);
        Map<Long, Game> readGames = JsonReaderUtil.readGamesAsMap(gamesFile);
        Files.deleteIfExists(gamesFile);

        Game loaded = readGames.get((long) game.getId());
        assertTrue(loaded != null, "Game should be loaded by id");
        assertEquals(game.getPlayers().size(), loaded.getPlayers().size(), "Player count should roundtrip");
        assertEquals(Colors.BLACK, loaded.getCurrentPlayer().getColor(), "Current player color should roundtrip");
        assertEquals(board.getPieces().size(), loaded.getBoard().getPieces().size(), "Board piece count should roundtrip");
        assertEquals(moves.size(), loaded.getMoves().size(), "Moves should roundtrip");
        Move loadedMove = loaded.getMoves().get(0);
        assertEquals(move.getPlayerColor(), loadedMove.getPlayerColor(), "Move player color should roundtrip");
        assertEquals(move.getFrom().toString(), loadedMove.getFrom().toString(), "Move source should roundtrip");
        assertEquals(move.getTo().toString(), loadedMove.getTo().toString(), "Move destination should roundtrip");
        assertTrue(loadedMove.getCapturedPiece() != null, "Captured piece should roundtrip");
        assertEquals(capturedPiece.type(), loadedMove.getCapturedPiece().type(), "Captured piece type should roundtrip");
        assertEquals(capturedPiece.getColor(), loadedMove.getCapturedPiece().getColor(), "Captured piece color should roundtrip");
    }
}
