package ChessGame;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class Game {
    private Board gameBoard;
    private Map<Character, Integer> condition; //过关条件
    private Map<Character, Integer> currScore; //当前分数
    private int index;  //第几关
    private Boolean ifLose;  //是否输入过giveup

    /**
     * 
     * @param index 在每个关卡中构造棋盘，并加载不同过关要求
     */
    public Game(int index) {  
        this.index = index;
        ifLose = false;
        condition = new HashMap<Character,Integer>();
        currScore = new HashMap<Character,Integer>();
        if (index == 1) {
            char[][] chesses = {{'C', 'E', 'B', 'A', 'C'}, 
                                {'B', 'A', 'C', 'C', 'E'},
                                {'A', 'B', 'A', 'E', 'A'},
                                {'D', 'A', 'D', 'A', 'D'},
                                {'E', 'A', 'D', 'C', 'B'}};
            gameBoard = new Board(chesses);
            condition.put('A', 6);
        } else if (index == 2) {
            char[][] chesses = {{'C', 'E', 'B', 'A', 'C'}, 
                                {'B', 'A', 'C', 'C', 'E'},
                                {'A', 'B', 'A', 'E', 'D'},
                                {'D', 'B', 'D', 'B', 'D'},
                                {'E', 'A', 'B', 'A', 'B'}};
            gameBoard = new Board(chesses);
            condition.put('B', 6);
        } else if (index == 3) {
            char[][] chesses = {{'C', 'E', 'B', 'A', 'C'}, 
                                {'B', 'A', 'C', 'C', 'E'},
                                {'A', 'B', 'A', 'E', 'D'},
                                {'D', 'B', 'D', 'C', 'D'},
                                {'E', 'A', 'D', 'C', 'B'}};
            gameBoard = new Board(chesses);
            condition.put('B', 3);
            condition.put('C', 3);
        }
    }

    /**
     * 打印关卡目前状态，若选择giveup则直接打印lose语句
     */
    public void printInfo() {
        if (!ifLose) {
            System.out.print("Level " + index + ": ");
            int demands = 0;
            for (Character key: condition.keySet()) { //遍历condition中要求消除的每种元素
                if (demands >= 1) System.out.print(" and ");
                int score = 0;
                if (currScore.get(key) != null) {
                    score = currScore.get(key);  //currScore的key中含该元素则直接打印，不含则说明为0
                }
                System.out.print(score + "/" + this.condition.get(key) + " of " + key);
                demands++;
            }
            System.out.print(" has been eliminated.\n");
        } else {
            System.out.println("Level " + index + ": You lose!");
        }
    }

    /*
     *通过比较当前消去元素个数和目标元素个数，判断关卡标准是否达到 
     */
    private Boolean gameHasSucceeded() {
        for (Character key: condition.keySet()) {
            if (currScore.get(key) == null || currScore.get(key) < condition.get(key)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 打印当前关卡消去的每种元素及对应个数，主要用于调试
     */
    private void printScore() {
        for (Character key: currScore.keySet()) {
            System.out.println(key + " : " + currScore.get(key));
        }
    }

    /**
     * 处理命令行输入的命令，调用Board类对应的方法
     * 每次输入后检查游戏是否通过，若游戏任务已达到则打印成功信息并退出
     * giveup或达成成功条件则跳出循环
     */
    private void handleCmd(Scanner scanner) {
        while (true) {
            if (gameHasSucceeded()) {
                System.out.println("You've passed Level " + index + "!");
                break;
            }
            String cmd = scanner.nextLine();
            if (cmd.equals("display")) {
                gameBoard.displayCmd();
            } else if (cmd.startsWith("at")) {
                gameBoard.atCmd((cmd.charAt(3) - '0'), cmd.charAt(5) - '0');
            } else if (cmd.startsWith("clear")) {
                gameBoard.clearCmd((cmd.charAt(6) - '0'), cmd.charAt(8) - '0', currScore);
                this.printInfo();
            } else if (cmd.startsWith("swap")) {
                gameBoard.swapCmd((cmd.charAt(5) - '0'), cmd.charAt(7) - '0', currScore);
                this.printInfo();
            } else if (cmd.equals("restart")) {
                gameBoard.restartCmd(scanner, currScore);
                this.printInfo();
            } else if (cmd.equals("giveup")) {
                ifLose = true;
                this.printInfo();
                break;
            } else {
                System.out.println("Invalid command, please try again.");
            }
        }
    }

    /**
     * Game类被main函数调用的方法
     */
    public void GameStart(Scanner scanner) {
        gameBoard.displayCmd();
        this.printInfo();
        this.handleCmd(scanner);
    }
}
