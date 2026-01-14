package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String password;
    private List<Game> activeGames;
    private List<Integer> gameIds;
    private int points;

    public User() {
        this.activeGames = new ArrayList<>();
        this.gameIds = new ArrayList<>();
        this.points = 0;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.activeGames = new ArrayList<>();
        this.gameIds = new ArrayList<>();
        this.points = 0;
    }

    public User(String email, String password, int points, List<Game> activeGames) {
        this.email = email;
        this.password = password;
        this.activeGames = activeGames;
        this.gameIds = new ArrayList<>();
        this.points = points;
    }

    public void addGame(Game game) {
        activeGames.add(game);
        if (!gameIds.contains(game.getId())) {
            gameIds.add(game.getId());
        }
    }

    public void removeGame(Game game) {
        activeGames.remove(game);
        gameIds.remove(Integer.valueOf(game.getId()));
    }

    public List<Game> getActiveGames() {
        return activeGames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<Integer> getGameIds() {
        return gameIds;
    }

    public void setGames(List<Integer> gameIds) {
        this.gameIds = gameIds;
    }

    public void setActiveGames(List<Game> games) {
        this.activeGames = games;
    }
}
