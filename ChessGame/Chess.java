package ChessGame;
public class Chess {

    private char value;
    
    public Chess(char value) {
        this.value = value;
    }
    
    public char value() {
        return value;
    }

    public void setSpace() {
        this.value = ' ';
    }

    public void setValue(char a) {
        this.value = a;
    }
}
