package util;

import exceptions.InvalidCommandException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.*;
import pieces.*;

public final class JsonReaderUtil {

    private JsonReaderUtil() {
    }

    public static List<User> readUsers(Path path) throws IOException, ParseException {
        if (path == null || !Files.exists(path)) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            Object root = parser.parse(reader);
            JSONArray arr = asArray(root);
            List<User> result = new ArrayList<>();

            if (arr == null) {
                return result;
            }

            for (Object item : arr) {
                JSONObject obj = asObject(item);
                if (obj == null) {
                    continue;
                }

                User acc = new User();
                acc.setEmail(asString(obj.get("email")));
                acc.setPassword(asString(obj.get("password")));
                acc.setPoints(asInt(obj.get("points")));
                List<Integer> gameIds = new ArrayList<>();
                JSONArray games = asArray(obj.get("games"));
                if (games != null) {
                    for (Object gid : games) {
                        gameIds.add(asInt(gid));
                    }
                }
                acc.setGames(gameIds);
                result.add(acc);
            }
            return result;
        }
    }

    public static Map<Long, Game> readGamesAsMap(Path path) throws IOException, ParseException {
        Map<Long, Game> map = new HashMap<>();
        if (path == null || !Files.exists(path)) {
            return map;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            Object root = parser.parse(reader);
            JSONArray arr = asArray(root);
            if (arr == null) return map;
            for (Object item : arr) {
                JSONObject obj = asObject(item);
                if (obj == null) {
                    continue;
                }
                long id = asLong(obj.get("id"));
                if (id < 0) {
                    continue;
                }
                Game g = new Game();
                g.setId((int) id);

                JSONArray playersArr = asArray(obj.get("players"));
                if (playersArr != null) {
                    List<Player> players = new ArrayList<>();
                    for (Object pItem : playersArr) {
                        JSONObject pObj = asObject(pItem);
                        if (pObj == null) {
                            continue;
                        }
                        String email = asString(pObj.get("email"));
                        String color = asString(pObj.get("color"));
                        Colors parsedColor = "BLACK".equalsIgnoreCase(color) ? Colors.BLACK : Colors.WHITE;
                        players.add(new Player(email, parsedColor));
                    }
                    g.setPlayers(players);
                }

                g.setCurrentPlayerColor(asString(obj.get("currentPlayerColor")));

                JSONArray boardArr = asArray(obj.get("board"));
                if (boardArr != null) {
                    Board board = new Board();
                    for (Object bItem : boardArr) {
                        JSONObject bObj = asObject(bItem);
                        if (bObj == null) {
                            continue;
                        }

                        String type = asString(bObj.get("type"));
                        String colorStr = asString(bObj.get("color"));
                        String positionStr = asString(bObj.get("position"));

                        Colors color = colorStr.equals("WHITE") ? Colors.WHITE : Colors.BLACK;
                        Position position = new Position(positionStr);

                        Piece piece = null;
                        if (type != null && !type.isEmpty()) {
                            char pieceType = type.charAt(0);
                            piece = switch (pieceType) {
                                case 'K' -> new King(color, position);
                                case 'Q' -> new Queen(color, position);
                                case 'R' -> new Rook(color, position);
                                case 'B' -> new Bishop(color, position);
                                case 'N' -> new Knight(color, position);
                                case 'P' -> new Pawn(color, position);
                                default -> piece;
                            };
                        }
                        if (piece != null) {
                            if (piece instanceof Pawn pawn) {
                                boolean onStart = (color == Colors.WHITE && position.getY() == 2)
                                        || (color == Colors.BLACK && position.getY() == 7);
                                pawn.setFirstMove(onStart);
                            }
                            board.getPieces().add(new ChessPair<>(position, piece));
                        }
                    }
                    g.setBoard(board);
                }

                JSONArray movesArr = asArray(obj.get("moves"));
                if (movesArr != null) {
                    List<Move> moves = new ArrayList<>();
                    for (Object mItem : movesArr) {
                        JSONObject mObj = asObject(mItem);
                        if (mObj == null) {
                            continue;
                        }

                        String playerColor = asString(mObj.get("playerColor"));
                        String from = asString(mObj.get("from"));
                        String to = asString(mObj.get("to"));
                        Colors parsedColor = "BLACK".equalsIgnoreCase(playerColor) ? Colors.BLACK : Colors.WHITE;
                        Position fromPos = new Position(from);
                        Position toPos = new Position(to);
                        Move move = new Move(parsedColor, fromPos, toPos);

                        JSONObject capturedObj = asObject(mObj.get("captured"));
                        if (capturedObj != null) {
                            Piece capturedPiece = buildCapturedPiece(capturedObj, toPos);
                            if (capturedPiece != null) {
                                move.setCapturedPiece(capturedPiece);
                            }
                        }
                        moves.add(move);
                    }
                    g.setMoves(moves);
                }
                map.put(id, g);
            }
        } catch (InvalidCommandException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static void writeUsers(Path path, List<User> users) throws IOException {
        JSONArray arr = new JSONArray();

        for (User user : users) {
            JSONObject obj = new JSONObject();
            obj.put("email", user.getEmail());
            obj.put("password", user.getPassword());
            obj.put("points", user.getPoints());

            JSONArray gameIds = new JSONArray();
            gameIds.addAll(user.getGameIds());
            obj.put("games", gameIds);
            arr.add(obj);
        }

        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(arr.toJSONString());
        }
    }

    public static void writeGames(Path path, Map<Integer, Game> games) throws IOException {
        JSONArray arr = new JSONArray();

        for (Game game : games.values()) {
            JSONObject obj = new JSONObject();
            obj.put("id", game.getId());

            JSONArray playersArr = new JSONArray();
            List<Player> players = game.getPlayers();
            for (Player player : players) {
                JSONObject pObj = new JSONObject();
                String email = player.isComputer() ? "computer" : player.getName();
                pObj.put("email", email);
                pObj.put("color", colorToString(player.getColor()));
                playersArr.add(pObj);
            }
            obj.put("players", playersArr);

            Colors currentColor = Colors.WHITE;
            if (!players.isEmpty()) {
                int currentIndex = game.getCurrentPlayerIndex();
                if (currentIndex >= 0 && currentIndex < players.size()) {
                    currentColor = players.get(currentIndex).getColor();
                } else {
                    currentColor = players.get(0).getColor();
                }
            }
            obj.put("currentPlayerColor", colorToString(currentColor));

            JSONArray boardArr = new JSONArray();
            for (ChessPair<Position, Piece> pair : game.getBoard().getPieces()) {
                JSONObject pieceObj = new JSONObject();
                pieceObj.put("type", String.valueOf(pair.getValue().type()));
                pieceObj.put("color", colorToString(pair.getValue().getColor()));
                pieceObj.put("position", pair.getKey().toString());
                boardArr.add(pieceObj);
            }
            obj.put("board", boardArr);

            JSONArray movesArr = new JSONArray();
            for (Move move : game.getMoves()) {
                JSONObject moveObj = new JSONObject();
                moveObj.put("playerColor", colorToString(move.getPlayerColor()));
                moveObj.put("from", move.getFrom().toString());
                moveObj.put("to", move.getTo().toString());
                if (move.getCapturedPiece() != null) {
                    JSONObject capturedObj = new JSONObject();
                    capturedObj.put("type", String.valueOf(move.getCapturedPiece().type()));
                    capturedObj.put("color", colorToString(move.getCapturedPiece().getColor()));
                    moveObj.put("captured", capturedObj);
                }
                movesArr.add(moveObj);
            }
            obj.put("moves", movesArr);

            arr.add(obj);
        }

        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(arr.toJSONString());
        }
    }

    private static Piece buildCapturedPiece(JSONObject capturedObj, Position fallbackPosition) throws InvalidCommandException {
        if (capturedObj == null) {
            return null;
        }
        String type = asString(capturedObj.get("type"));
        String colorStr = asString(capturedObj.get("color"));
        if (type == null || type.isEmpty() || colorStr == null) {
            return null;
        }

        Colors color = "BLACK".equalsIgnoreCase(colorStr) ? Colors.BLACK : Colors.WHITE;
        Position position = fallbackPosition != null
                ? new Position(fallbackPosition.getX(), fallbackPosition.getY())
                : new Position("A1");

        char pieceType = type.charAt(0);
        return switch (pieceType) {
            case 'K' -> new King(color, position);
            case 'Q' -> new Queen(color, position);
            case 'R' -> new Rook(color, position);
            case 'B' -> new Bishop(color, position);
            case 'N' -> new Knight(color, position);
            case 'P' -> new Pawn(color, position);
            default -> null;
        };
    }

    private static JSONArray asArray(Object o) {
        return (o instanceof JSONArray) ? (JSONArray) o : null;
    }

    private static JSONObject asObject(Object o) {
        return (o instanceof JSONObject) ? (JSONObject) o : null;
    }

    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static int asInt(Object o) {
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return o != null ? Integer.parseInt(String.valueOf(o)) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static long asLong(Object o) {
        if (o instanceof Number) return ((Number) o).longValue();
        try {
            return o != null ? Long.parseLong(String.valueOf(o)) : (long) -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String colorToString(Colors color) {
        return color == Colors.BLACK ? "BLACK" : "WHITE";
    }
}
