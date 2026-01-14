package model;

import exceptions.InvalidCommandException;

public class Position implements Comparable<Position> {
    private char x;
    private int y;

    public Position(String positionStr) throws InvalidCommandException {
        if (positionStr == null) {
            throw new InvalidCommandException("Invalid position format");
        }
        positionStr = positionStr.trim().toUpperCase();
        if (positionStr.length() != 2) {
            throw new InvalidCommandException("Invalid position format");
        }
        x = positionStr.charAt(0);
        y = Integer.parseInt(positionStr.substring(1));
        ValidateMove();
    }

    public Position(char x, int y) throws InvalidCommandException {
        this.x = x;
        this.y = y;
        ValidateMove();
    }

    private void ValidateMove() throws InvalidCommandException {
        if(x < 'A' || x > 'H' || y < 1 || y > 8) {
            throw new InvalidCommandException("Invalid position format");
        }
    }

    @Override
    public int compareTo(Position o) {
        if (this.y != o.y)  {
            return Integer.compare(this.y, o.y);
        } else {
            return Character.compare(this.x, o.x);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Position)) {
            return false;
        }
        Position o = (Position) obj;
        return this.x == o.x && this.y == o.y;
    }

    @Override
    public int hashCode() {
        int result = Character.hashCode(x);
        result = 31 * result + Integer.hashCode(y);
        return result;
    }

    public String toString() {
        return "" + x + y;
    }

    public char getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
