package ChessGame;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Map;

public class Board {
    private Chess[][] board;

    private int row = 5;
    private int col = 5;
    private char[][] originalChesses;
    
    //constructor
    public Board() {
        board = new Chess[row][col];
        originalChesses = new char[row][col];
    }

    //constructor for test
    public Board(char[][] exitBoard) {
        originalChesses = new char[row][col];
        board = new Chess[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = new Chess(exitBoard[i][j]);
                originalChesses[i][j] = exitBoard[i][j];
            }
        }
    }

    //根据xy坐标返回对应Chess/检查越界
    public Chess chessAt(int x, int y) {
        if (x >= 5 || x < 0 || y >= 5 || y < 0) return null;
        return board[y][x];
    }

    //读入用户输入并改变棋盘
    public void boardInit(Scanner scanner) {
        for (int i = 0; i < 5; i++) {
            System.out.println("Please type in a row of letters: ");
            String row = scanner.nextLine();
            for (int j = 0; j < row.length(); j += 2) {
                board[i][j / 2] = new Chess(row.charAt(j));
                originalChesses[i][j / 2] = row.charAt(j);
            }
        }
        return;
    }

    //打印棋盘
    public void displayCmd() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(board[i][j].value());
                if (j != 4) System.out.print(" ");
            }
            System.out.println("\n");
        }
    }

    //执行at命令
    public void atCmd(int x, int y) {
        int[] rowArray = new int[]{x, x, x, x - 1, x + 1};
        int[] colArray = new int[]{y, y - 1, y + 1, y, y};
        System.out.print("[");
        for (int i = 0; i < 5; i++) {
            if (this.chessAt(rowArray[i], colArray[i]) != null) {
                if (i >= 1) {
                    System.out.print(", ");
                }
                System.out.print("(" + rowArray[i]  + "," + colArray[i] + "," + 
                    this.chessAt(rowArray[i], colArray[i]).value() + ")");
            }
        }
        System.out.print("]\n");
    }

    /**
     * 负责更新游戏分数的函数，将下一步将消去的棋子组成的Set作为参数，修改currScore
     */
    private void scoreUpdate(Map<Character, Integer> currScore, Set<Chess> chesses) {
        if (chesses.isEmpty()) return; //若没有棋子即将被消去则不修改分数，直接返回
        char value = ' ';
        int pastScore = 0;
        for (Chess chess: chesses) {
            value = chess.value();
            if (currScore.get(chess.value()) == null) {
                currScore.put(value, 0);
            } else {
                pastScore = currScore.get(chess.value());
            }
            break;
        }
        currScore.put(value, pastScore + chesses.size());
    }
    
    //执行clear命令
    public void clearCmd(int x, int y, Map<Character, Integer> currScore) {
        Set<Chess> chessToClear = getClearSet(x, y);
        if (chessToClear.isEmpty()) {
            System.out.println("Input error.");
            return;
        }
        scoreUpdate(currScore, chessToClear);
        for (Chess chess: chessToClear) {
            chess.setSpace();
        }
        this.fallChesses(); 
        this.displayCmd();
    }

    private Set<Chess> getClearSet(int x, int y) {
        Set<Chess> chessToClear = new HashSet<Chess>();
        this.getClearSetHelper(x, y, chessToClear, "x");
        this.getClearSetHelper(x, y, chessToClear, "y");
        this.getClearAsMiddle(x, y, chessToClear);
        return chessToClear;
    }

    /**
     * 用数组取巧，实现一个函数检查x,y处的棋子仅左/右侧或上/下侧能否构成多消
     * @param x
     * @param y
     * @param chessToClear 能消去棋子的集合
     * @param xOrY 标志在考察x还是y轴
     */
    private void getClearSetHelper(int x, int y, Set<Chess> chessToClear, String xOrY) {
        int[] xyRight;
        if (xOrY.equals("x")) {
            xyRight = new int[]{1, 0};  //xyRight数组，表示x，y的权重，使后续循环只该变x/y中的一个
        } else {
            xyRight = new int[]{0, 1};
        }
        int[] stepRight = new int[]{-1, 1};  //每次step的值，-1代表向左/上方考察，1代表向右/下方考察
        for (int i = 0; i < 2; i++) {
            int step = stepRight[i];
            for (int j = step; this.chessAt(x + xyRight[0] * j, y + xyRight[1] * j) != null; j += step) {
                if (this.chessAt(x + xyRight[0] * j, y + xyRight[1] * j).value() != this.chessAt(x, y).value()) {
                    break;
                } else {
                    if (j == 2 || j == -2) {
                        chessToClear.add(this.chessAt(x, y));
                        chessToClear.add(this.chessAt(x + xyRight[0] * step, y + xyRight[1] * step));
                        chessToClear.add(this.chessAt(x + 2 * xyRight[0] * step, y + 2 * xyRight[1] * step));
                    } else if (j > 2 || j < -2) {
                        chessToClear.add(this.chessAt(x + xyRight[0] * j, y + xyRight[1] * j));
                    }
                }
            }
        }
    }
    
    /**
        考察x，y点作为中间元素能否构成三消
     */
    private void getClearAsMiddle(int x, int y, Set<Chess> chessToClear) {
        if (this.chessAt(x, y - 1) != null && this.chessAt(x, y + 1) != null 
            && this.chessAt(x, y - 1).value() == this.chessAt(x, y).value()
            && this.chessAt(x, y).value() == this.chessAt(x, y + 1).value()) {
            chessToClear.add(this.chessAt(x, y));
            chessToClear.add(this.chessAt(x, y - 1));
            chessToClear.add(this.chessAt(x, y + 1));
        }
        if (this.chessAt(x - 1, y) != null && this.chessAt(x + 1, y) != null 
            && this.chessAt(x - 1, y).value() == this.chessAt(x, y).value()
            && this.chessAt(x, y).value() == this.chessAt(x + 1, y).value()) {
            chessToClear.add(this.chessAt(x, y));
            chessToClear.add(this.chessAt(x - 1, y));
            chessToClear.add(this.chessAt(x + 1, y));
        }
    }

    //执行swap命令
    public void swapCmd(int x, int y, Map<Character, Integer> currScore) {  //
        int[] rowArray = new int[]{x, x, x - 1, x + 1};
        int[] colArray = new int[]{y - 1, y + 1, y, y};
        for (int i = 0; i < 4; i++) {
            if (this.chessAt(rowArray[i], colArray[i]) == null) continue;
            this.swapTwoChess(this.chessAt(x, y), this.chessAt(rowArray[i], colArray[i]));
            Set<Chess> chesses1 = this.getClearSet(x, y);
            Set<Chess> chesses2 = this.getClearSet(rowArray[i], colArray[i]);
            if (chesses1.isEmpty() && chesses2.isEmpty()) {  //判断交换后两个棋子附近能否消去
                this.swapTwoChess(this.chessAt(x, y), this.chessAt(rowArray[i], colArray[i]));
                continue;
            } else {
                scoreUpdate(currScore, chesses1);
                scoreUpdate(currScore, chesses2);  //通过scoreUpdate方法更新游戏分数
                chesses1.addAll(chesses2);
                for (Chess chess: chesses1) {
                    chess.setSpace();
                }
                this.fallChesses();
                this.displayCmd();
                return;
            }
        }
        System.out.println("Input error");
        return;
    }

    private void swapTwoChess(Chess one, Chess two) {
        char tmp = one.value();
        one.setValue(two.value());
        two.setValue(tmp);
    }
    
    //将元素消除后，执行下落/随机填充任务
    private void fallChesses() {
        Random random = new Random();
        for (int i = 0; i < col; i++) {
            fallChessesAtCol(i, random);
        }
    }

    //对特定列执行下落/填充
    private void fallChessesAtCol(int x, Random random) {
        char[] chessExist = new char[row];
        int num = 0;
        for (int i = row - 1; i >= 0; i--) {
            if (this.chessAt(x, i).value() != ' ') chessExist[num++] = this.chessAt(x, i).value();
        }
        int numUsed = 0;
        for (int i = row - 1; i >= 0; i--) {
            if (numUsed >= num) {
                this.chessAt(x, i).setValue((char) (random.nextInt(5) + 'A'));
            } else {
                this.chessAt(x, i).setValue(chessExist[numUsed++]);
            }
        }
        return;
    }

    
    //执行restart命令                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   ```
    public void restartCmd(Scanner scanner, Map<Character, Integer> currScore) {
        currScore.clear();
        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {
                board[i][j].setValue(originalChesses[i][j]);
            }
        }
        this.displayCmd();
    }

    // 原来的exit命令，lab3貌似用不到了
    // public void exitCmd() {
    //     System.out.println("Game exited, thanks for playing.");
    // }
}


