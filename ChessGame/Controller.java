package ChessGame;
import java.util.Scanner;

public class Controller {

    public static int numOfGames = 3;
    private static void gameStart(Game[] games, Scanner scanner) {
        for (int i = 0; i < numOfGames; i++) {
            games[i] = new Game(i + 1);
            games[i].GameStart(scanner);   //初始化棋盘并过关
        }
    }

    private static void gameOver(Game[] games) {
        System.out.println("Game over!");
        for (int i = 0; i < numOfGames; i++) {
            games[i].printInfo();  //游戏结束后打印结算界面
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Game[] games = new Game[numOfGames];
        Controller.gameStart(games, scanner);
        Controller.gameOver(games);
        scanner.close();
    }
}
