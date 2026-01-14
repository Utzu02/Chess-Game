package strategy.scoring;

public class GameEndScoringStrategy implements ScoringStrategy {

    public enum GameResult {
        CHECKMATE_WIN,
        CHECKMATE_LOSS,
        DRAW,
        RESIGN,
        SAVE_AND_EXIT
    }

    @Override
    public int calculatePoints(Object context) {
        if (!(context instanceof GameResult)) {
            return 0;
        }

        GameResult result = (GameResult) context;

        return switch (result) {
            case CHECKMATE_WIN -> 300;
            case CHECKMATE_LOSS -> -300;
            case DRAW -> 150;
            case RESIGN -> -150;
            case SAVE_AND_EXIT -> 0;
        };
    }

    public int getGameEndBonus(GameResult result) {
        return calculatePoints(result);
    }
}
