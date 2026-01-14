package main;

import model.*;
import pieces.Piece;
import exceptions.*;
import util.JsonReaderUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private List<User> users;
    private Map<Integer, Game> games;
    private User currentUser;
    private Scanner scanner;

    private static class TurnResult {
        boolean continueGame;
        boolean turnConsumed;

        TurnResult(boolean continueGame, boolean turnConsumed) {
            this.continueGame = continueGame;
            this.turnConsumed = turnConsumed;
        }
    }

    public Main() {
        users = new ArrayList<>();
        games = new HashMap<>();
        scanner = new Scanner(System.in);
    }

    public void read() {
        try {
            Path accountsPath = Paths.get("src/input/accounts.json");
            Path gamesPath = Paths.get("src/input/games.json");

            users = JsonReaderUtil.readUsers(accountsPath);

            Map<Long, Game> gamesLongMap = JsonReaderUtil.readGamesAsMap(gamesPath);

            games = new HashMap<>();
            for (Map.Entry<Long, Game> entry : gamesLongMap.entrySet()) {
                games.put(entry.getKey().intValue(), entry.getValue());
            }

            for (User user : users) {
                for (Integer gameId : user.getGameIds()) {
                    if (games.containsKey(gameId)) {
                        user.addGame(games.get(gameId));
                    }
                }
            }

            System.out.println("Loaded " + users.size() + " users and " + games.size() + " games.");
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    public void write() {
        try {
            Path accountsPath = Paths.get("src/input/accounts.json");
            Path gamesPath = Paths.get("src/input/games.json");

            JsonReaderUtil.writeUsers(accountsPath, users);

            JsonReaderUtil.writeGames(gamesPath, games);

            System.out.println("Data saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                currentUser = user;
                return user;
            }
        }
        return null;
    }

    public User newAccount(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                System.out.println("Email already exists!");
                return null;
            }
        }

        User user = new User(email, password);
        users.add(user);
        currentUser = user;
        return user;
    }

    public void run() {
        System.out.println("====================================");
        System.out.println("    CHESS GAME - POO PROJECT");
        System.out.println("====================================\n");

        while (true) {
            if (!authenticate()) {
                return;
            }
            mainMenu();
        }
    }

    private boolean authenticate() {
        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Create Account");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            try {
                String choiceStr = scanner.nextLine().trim();
                int choice = Integer.parseInt(choiceStr);

                if (choice == 1) {
                    System.out.print("Email: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String password = scanner.nextLine().trim();

                    if (login(email, password) != null) {
                        System.out.println("\nLogin successful. Welcome " + currentUser.getEmail());
                        System.out.println("Your points: " + currentUser.getPoints() + "\n");
                        return true;
                    } else {
                        System.out.println("\nInvalid credentials. Please try again.\n");
                    }
                } else if (choice == 2) {
                    System.out.print("Email: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String password = scanner.nextLine().trim();

                    if (newAccount(email, password) != null) {
                        System.out.println("\nAccount created successfully. Welcome " + currentUser.getEmail() + "\n");
                        return true;
                    }
                } else if (choice == 3) {
                    System.out.println("Goodbye!");
                    return false;
                } else {
                    System.out.println("\nInvalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number.\n");
            }
        }
    }

    private void mainMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n====================================");
            System.out.println("         MAIN MENU");
            System.out.println("====================================");
            System.out.println("1. Start New Game");
            System.out.println("2. View Games in Progress");
            System.out.println("3. Logout");
            System.out.print("Choose option: ");

            try {
                String choiceStr = scanner.nextLine().trim();
                int choice = Integer.parseInt(choiceStr);

                switch (choice) {
                    case 1:
                        startNewGame();
                        break;
                    case 2:
                        viewGamesInProgress();
                        break;
                    case 3:
                        write(); // Save before logout
                        running = false;
                        currentUser = null;
                        System.out.println("\nLogged out successfully.");
                        break;
                    default:
                        System.out.println("\nInvalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number.");
            }
        }
    }

    private void startNewGame() {
        System.out.println("\n====================================");
        System.out.println("        START NEW GAME");
        System.out.println("====================================");

        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine().trim();

        System.out.print("Choose color (WHITE/BLACK): ");
        String colorStr = scanner.nextLine().trim().toUpperCase();

        if (!colorStr.equals("WHITE") && !colorStr.equals("BLACK")) {
            colorStr = "WHITE";
            System.out.println("Invalid color, defaulting to WHITE");
        }

        Colors playerColor = colorStr.equals("WHITE") ? Colors.WHITE : Colors.BLACK;

        int newId = games.isEmpty() ? 1 : Collections.max(games.keySet()) + 1;

        Game game = new Game(newId);

        Player humanPlayer = new Player(playerName, playerColor);
        Player computerPlayer = new Player("computer",
                playerColor == Colors.WHITE ? Colors.BLACK : Colors.WHITE);

        List<Player> players = new ArrayList<>();
        if (playerColor == Colors.WHITE) {
            players.add(humanPlayer);
            players.add(computerPlayer);
        } else {
            players.add(computerPlayer);
            players.add(humanPlayer);
        }

        game.setPlayers(players);
        games.put(newId, game);
        currentUser.addGame(game);

        try {
            game.start(humanPlayer.getColor());
            playGame(game, humanPlayer);
        } catch (InvalidCommandException e) {
            System.out.println("Could not start game: " + e.getMessage());
        }
    }

    private void playGame(Game game, Player humanPlayer) {
        boolean gameRunning = true;

        while (gameRunning) {
            try {
                boolean checkmate = game.checkForCheckMate();
                boolean draw = !checkmate && game.checkForStalemate();

                if (checkmate) {
                    announceCheckmate(game, humanPlayer);
                    break;
                }

                if (draw) {
                    announceDraw(game, humanPlayer);
                    break;
                }
            } catch (InvalidMoveException e) {
                System.out.println("Error while evaluating board: " + e.getMessage());
                break;
            }

            Player currentPlayer = game.getCurrentPlayer();
            System.out.println("\n--- Current turn: " + currentPlayer.getName() +
                    " (" + (currentPlayer.getColor() == Colors.WHITE ? "WHITE" : "BLACK") + ") ---");

            try {
                game.getBoard().display(humanPlayer.getColor());
            } catch (InvalidCommandException e) {
                System.out.println("Could not display board: " + e.getMessage());
            }

            TurnResult result;
            if (currentPlayer.isComputer()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // ignore
                }
                result = makeComputerMove(game, humanPlayer.getColor());
            } else {
                result = makeHumanMove(game, humanPlayer);
            }

            if (!result.continueGame) {
                break;
            }

            if (result.turnConsumed) {
                game.switchPlayer();
                game.registerBoardState();
            }
            gameRunning = true;
        }

        System.out.println("\nYour total points: " + currentUser.getPoints());
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void announceCheckmate(Game game, Player humanPlayer) {
        Player winner = game.getPlayers().get((game.getCurrentPlayerIndex() + 1) % 2);
        boolean humanWon = winner.getColor() == humanPlayer.getColor();
        int updatedPoints = currentUser.getPoints() + humanPlayer.getPoints() + (humanWon ? 300 : -300);
        currentUser.setPoints(updatedPoints);
        System.out.println("\nCHECKMATE");
        System.out.println("Winner: " + winner.getName());

        currentUser.removeGame(game);
        games.remove(game.getId());
    }

    private void announceDraw(Game game, Player humanPlayer) {
        int updatedPoints = currentUser.getPoints() + humanPlayer.getPoints() + 150;
        currentUser.setPoints(updatedPoints);
        System.out.println("\nDRAW");
        System.out.println("Game ends in a draw.");

        currentUser.removeGame(game);
        games.remove(game.getId());
    }

    private TurnResult makeHumanMove(Game game, Player humanPlayer) {
        System.out.println("\nOptions:");
        System.out.println("  - Enter position to see possible moves (e.g., A2)");
        System.out.println("  - Make a move (e.g., A2-A3)");
        System.out.println("  - Type 'resign' to resign");
        System.out.println("  - Type 'save' to save and exit");
        System.out.print("\nYour input: ");

        String input = scanner.nextLine().trim().toUpperCase();

        try {
            if (input.equals("RESIGN")) {
                System.out.println("\nYou resigned.");
                int updatedPoints = currentUser.getPoints() + humanPlayer.getPoints() - 150;
                currentUser.setPoints(updatedPoints);
                currentUser.removeGame(game);
                games.remove(game.getId());
                return new TurnResult(false, false);
            } else if (input.equals("SAVE")) {
                System.out.println("\nGame saved.");
                write();
                return new TurnResult(false, false);
            } else if (input.contains("-")) {
                String[] parts = input.split("-");
                if (parts.length != 2) {
                    throw new InvalidCommandException("Invalid move format. Use: A2-A3");
                }

                Position from = new Position(parts[0].trim());
                Position to = new Position(parts[1].trim());

                Piece capturedPiece = game.getBoard().getPieceAt(to);
                humanPlayer.makeMove(from, to, game.getBoard());
                game.addMove(humanPlayer, from, to, capturedPiece);

                System.out.println("Move successful: " + from + " -> " + to);
                try {
                    game.getBoard().display(humanPlayer.getColor());
                } catch (InvalidCommandException e) {
                    System.out.println("Could not display board: " + e.getMessage());
                }
                return new TurnResult(true, true);
            } else if (input.length() == 2) {
                Position pos = new Position(input);
                Piece piece = game.getBoard().getPieceAt(pos);

                if (piece == null) {
                    System.out.println("No piece at " + pos);
                } else if (piece.getColor() != humanPlayer.getColor()) {
                    System.out.println("That is not your piece.");
                } else {
                    List<Position> moves = game.getBoard().getLegalMoves(pos, humanPlayer.getColor());
                    if (moves.isEmpty()) {
                        System.out.println("No possible moves for piece at " + pos);
                    } else {
                        System.out.println("Possible moves for " + piece + " at " + pos + ":");
                        System.out.println(moves);
                    }
                }
                return new TurnResult(true, false);
            } else {
                throw new InvalidCommandException("Invalid command");
            }
        } catch (InvalidMoveException e) {
            System.out.println("\nInvalid move: " + e.getMessage());
            return new TurnResult(true, false);
        } catch (InvalidCommandException e) {
            System.out.println("\n" + e.getMessage());
            return new TurnResult(true, false);
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            return new TurnResult(true, false);
        }
    }

    private TurnResult makeComputerMove(Game game, Colors humanColor) {
        Colors computerColor = game.getCurrentPlayer().getColor();
        List<ChessPair<Position, Position>> candidates = new ArrayList<>();

        for (ChessPair<Position, Piece> pair : game.getBoard().getPieces()) {
            if (pair.getValue().getColor() != computerColor) {
                continue;
            }
            try {
                List<Position> legalMoves = game.getBoard().getLegalMoves(pair.getKey(), computerColor);
                for (Position move : legalMoves) {
                    candidates.add(new ChessPair<>(pair.getKey(), move));
                }
            } catch (InvalidMoveException e) {
                // ignore pieces that cannot move
            }
        }

        if (candidates.isEmpty()) {
            System.out.println("Computer has no legal moves.");
            return new TurnResult(false, false);
        }

        Random random = new Random();
        ChessPair<Position, Position> choice = candidates.get(random.nextInt(candidates.size()));
        Position from = choice.getKey();
        Position to = choice.getValue();
        Piece captured = game.getBoard().getPieceAt(to);

        try {
            game.getCurrentPlayer().makeMove(from, to, game.getBoard());
            game.addMove(game.getCurrentPlayer(), from, to, captured);
            System.out.println("Computer moved: " + from + " -> " + to);
            game.getBoard().display(humanColor);
            return new TurnResult(true, true);
        } catch (InvalidMoveException | InvalidCommandException e) {
            System.out.println("Computer failed to move: " + e.getMessage());
            return new TurnResult(false, false);
        }
    }

    private Player getHumanPlayer(Game game) {
        for (Player player : game.getPlayers()) {
            if (!player.isComputer()) {
                return player;
            }
        }
        return null;
    }

    private Colors getHumanColor(Game game) {
        Player human = getHumanPlayer(game);
        return human != null ? human.getColor() : Colors.WHITE;
    }

    private void viewGamesInProgress() {
        List<Game> userGames = currentUser.getActiveGames();

        if (userGames.isEmpty()) {
            System.out.println("\nNo games in progress.");
            return;
        }

        System.out.println("\n====================================");
        System.out.println("     GAMES IN PROGRESS");
        System.out.println("====================================");

        for (Game game : userGames) {
            System.out.println("Game ID: " + game.getId() +
                    " - Moves: " + game.getMoves().size());
        }

        System.out.print("\nSelect game by ID (0 to return): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) {
                return;
            }
            Game selectedGame = null;
            for (Game game : userGames) {
                if (game.getId() == choice) {
                    selectedGame = game;
                    break;
                }
            }
            if (selectedGame != null) {
                gameDetailsMenu(selectedGame);
            } else {
                System.out.println("\nGame ID not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input.");
        }
    }

    private void gameDetailsMenu(Game game) {
        System.out.println("\n====================================");
        System.out.println("       GAME #" + game.getId());
        System.out.println("====================================");
        System.out.println("1. View details");
        System.out.println("2. Continue game");
        System.out.println("3. Delete game");
        System.out.println("0. Back");
        System.out.print("Choose option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    Colors perspective = getHumanColor(game);
                    System.out.println("\nPlayers:");
                    for (Player player : game.getPlayers()) {
                        System.out.println("  " + player.getName() + " - " + player.getColor());
                    }
                    System.out.println("Current player color: " + game.getCurrentPlayer().getColor());
                    game.getBoard().display(perspective);
                    System.out.println("\nMove history:");
                    for (Move move : game.getMoves()) {
                        System.out.println("  " + move);
                    }
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    break;
                case 2:
                    Player humanPlayer = getHumanPlayer(game);
                    if (humanPlayer == null) {
                        System.out.println("\nNo human player found for this game.");
                        break;
                    }
                    Colors humanColor = humanPlayer != null ? humanPlayer.getColor() : Colors.WHITE;
                    game.resume(humanColor);
                    playGame(game, humanPlayer);
                    break;
                case 3:
                    currentUser.removeGame(game);
                    games.remove(game.getId());
                    System.out.println("\nGame deleted.");
                    break;
            }
        } catch (NumberFormatException | InvalidCommandException e) {
            System.out.println("\nInvalid input!");
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.read();
        app.run();
        app.write();
        System.out.println("\nThank you for playing!");
    }
}
