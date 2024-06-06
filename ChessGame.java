package bin;

import java.util.Scanner;

interface Board{
    void showBoard();
    boolean updateBoard(int[] position, int[] newposition,String turn);
    void ResetBoard();
}
interface Player{
    boolean move(String move,String turn);
    void GetPlayer();
}
abstract class Pieces{
    int[] CurrentIndex = new int[2];
    int[][] PossiblePaths = new int[3][2];
    String color;
    int result = 0;
    abstract public int ValidPath(int[] index);
}
class Pawn extends Pieces{
    int[] initialPosition = new int[8]; // can be used to give the 2 step to a pawn for first turn
    public Pawn(int[] index,String color){
        this.CurrentIndex = index;
        this.color = color;
    }
    public int ValidPath(int[] index){
        if(this.color.equals("W")){
            if (this.CurrentIndex[1]!=index[1]){
                this.result=-1;
            }else{
                if(this.CurrentIndex[0]+1==index[0] && this.CurrentIndex[1]==index[1]){
                    this.result = 1;
                }else if((this.CurrentIndex[0] == 6 || this.CurrentIndex[0] == 1) && this.CurrentIndex[0]+2 == index[0] && this.CurrentIndex[1] == index[1]){
                    this.result = 1;
                }
            }
        }else{
            if (this.CurrentIndex[1]!=index[1]){
                this.result=-1;
            }else{
                if(this.CurrentIndex[0]-1==index[0] && this.CurrentIndex[1]==index[1]){
                    this.result = 1;
                }else if((this.CurrentIndex[0] == 6 || this.CurrentIndex[0] == 1) && this.CurrentIndex[0]-2 == index[0] && this.CurrentIndex[1] == index[1]){
                    this.result = 1;
                }
            }
        }
        return this.result;
    }
}
class Queen extends Pieces{
    public Queen(int[] index, String color){
        this.CurrentIndex = index;
        this.color = color;
    }
    public int ValidPath(int[] index){
        int X = this.CurrentIndex[0] - index[0];
        int Y = this.CurrentIndex[1] - index[1];
        if(X<0){
            X*=-1;
        }
        if(Y<0){
            Y*=-1;
        }
        if((index[0]>=0 || index[0] <= 8) && this.CurrentIndex[1]==index[1]){
            return 1;
        }else if((index[1]>=0 || index[1] <= 8) && this.CurrentIndex[0]==index[0]){
            return 1;
        }else if(X == Y){
            return 1;
        }
        return 0;
    }
}
class Bishop extends Pieces{ 
    public Bishop(int[] index,String color){
        this.CurrentIndex = index;
        this.color = color;
    }
    public int ValidPath(int[] index){
        int X = this.CurrentIndex[0] - index[0];
        int Y = this.CurrentIndex[1] - index[1];
        if(X<0){
            X*=-1;
        }
        if(Y<0){
            Y*=-1;
        }
        if(X == Y){
            return 1;
        }
        return 0;
    }
}
class Rook extends Pieces{
    public Rook(int[] index,String color){
        this.CurrentIndex = index;
        this.color = color;
    }
    public int ValidPath(int[] index){
        if((index[0]>=0 || index[0] <= 8) && this.CurrentIndex[1]==index[1]){
            return 1;
        }else if((index[1]>=0 || index[1] <= 8) && this.CurrentIndex[0]==index[0]){
            return 1;
        }
        return 0;
    }
}
class Knight extends Pieces{
    public Knight(int[] index,String color){
        this.CurrentIndex = index;
        this.color = color;
    }
    public int ValidPath(int[] index){
        if((this.CurrentIndex[0]+1==index[0] || this.CurrentIndex[0]-1==index[0] || this.CurrentIndex[0]+2==index[0] || this.CurrentIndex[0]-2==index[0]) && (this.CurrentIndex[1]+1==index[1] || this.CurrentIndex[1]-1==index[1] || this.CurrentIndex[1]+2==index[1] || this.CurrentIndex[1]-2==index[1])){
            return 1;
        }
        return 0;
    }
}
class King extends Pieces{
    public King(int[] index, String color){
        this.CurrentIndex = index;
        this.color = color;
    }
    public int ValidPath(int[] index){
        int X = this.CurrentIndex[0] - index[0];
        int Y = this.CurrentIndex[1] - index[1];
        if(X<0){
            X*=-1;
        }
        if(Y<0){
            Y*=-1;
        }
        if(((this.CurrentIndex[0]+1 == index[0] || this.CurrentIndex[0]-1 == index[0]) && this.CurrentIndex[1]==index[1]) || (this.CurrentIndex[0]==index[0] && (this.CurrentIndex[1]+1 == index[1] || this.CurrentIndex[1]-1 == index[1])) || ((X==Y) && (this.CurrentIndex[1]-1 == index[1] || this.CurrentIndex[1]+1 == index[1]) && (this.CurrentIndex[0]+1 == index[0] || this.CurrentIndex[0]-1 == index[0] ))){
            return 1;
        }
        return 0;
    }
}
class ChessBoard implements Board,Player{
    // important variables
    int MaxLeng = 8;
    String PlayerOne,PlayerTwo;
    int PlayerOneIndex = 0;
    int PlayerTwoIndex = 0;
    int MoveIndexOne = 0;
    int MoveIndexTwo = 0;
    String[] PlayerOneStack = new String[16];
    String[] PlayerTwoStack = new String[16];
    String[][] MoveRecord = new String[2][50];

    // private variables
    private String[][] DefaultBoard = {
        {"_a","W_Rook","W_Knight","W_Bishop","W_King","W_Queen","W_Bishop","W_Knight","W_Rook"}, 
        {"_b","W_Pawn","W_Pawn","W_Pawn","W_Pawn","W_Pawn","W_Pawn","W_Pawn","W_Pawn"},
        {"_c","","","","","","","",""},
        {"_d","","","","","","","",""},
        {"_e","","","","","","","",""},
        {"_f","","","","","","","",""},
        {"_g","B_Pawn","B_Pawn","B_Pawn","B_Pawn","B_Pawn","B_Pawn","B_Pawn","B_Pawn"},
        {"_h","B_Rook","B_Knight","B_Bishop","B_King","B_Queen","B_Bishop","B_Knight","B_Rook"},
        {"","_1","_2","_3","_4","_5","_6","_7","_8"}
    };
    private String[][] Board;
    private String[][] TestingBoard = {
        {"_a","","","","","","","",""},
        {"_b","","","","","","","",""},
        {"_c","","","","","","","",""},
        {"_d","","","","","","","",""},
        {"_e","","","","","","","",""},
        {"_f","","","","","","","",""},
        {"_g","","","","","","","",""},
        {"_h","","","","","","","",""},
        {"","_1","_2","_3","_4","_5","_6","_7","_8"}
    };

    // Constructor
    public ChessBoard(){
        this.Board=this.__Copy__(this.DefaultBoard);
    }

    // private methods
    private String AddCharacter(int n,String c){
        String string = "";
        for (int i = 0; i < n; i++) {
            string+=c;
        }
        return string;
    }
    private String CreateBlock(String str){
        int totalLength;
        int stringLength = str.length();
        String OutputString = "";
       
        if (stringLength != this.MaxLeng){
            if(stringLength == 0){
                totalLength = (this.MaxLeng)/2;
                OutputString += AddCharacter(totalLength," ")+str+AddCharacter(totalLength," ");
            }else if(this.MaxLeng > stringLength){
                if (stringLength%2 == 0){
                    totalLength = (this.MaxLeng - stringLength)/2;
                    OutputString += AddCharacter(totalLength," ")+str+AddCharacter(totalLength," ");
                }
                else{
                    totalLength = (this.MaxLeng - stringLength)/2;
                    OutputString += AddCharacter(totalLength+1," ")+str+AddCharacter(totalLength," ");
                }
            }else if (this.MaxLeng < stringLength){
                if (stringLength%2 == 0){
                    totalLength = (stringLength - this.MaxLeng)/2;
                    OutputString += AddCharacter(totalLength," ")+str+AddCharacter(totalLength," ");
                }
                else{
                    totalLength = (stringLength - this.MaxLeng)/2;
                    OutputString += AddCharacter(totalLength+1," ")+str+AddCharacter(totalLength," ");
                }
            }            else{
                totalLength = 1;
                OutputString += AddCharacter(totalLength," ")+str+AddCharacter(totalLength," ");
            }
        }else{
            totalLength = (this.MaxLeng)/2;
            OutputString += AddCharacter(totalLength," ")+str+AddCharacter(totalLength," ");
        }
        if(OutputString.length() == this.MaxLeng){
            return OutputString;
        }else{
            return "not";
        }
    }
    private int Validate(int[] position, int[] newposition){
        String[] k = this.Board[position[0]][position[1]].split("_");
        
        System.out.println("position[0],position[1] "+position[0]+", "+position[1]);
        System.out.println("newposition[0],newposition[1] "+newposition[0]+", "+newposition[1]);
        System.out.println("this.Board[position[0]][position[1]] "+this.Board[position[0]][position[1]]);

        System.out.println("{ k[0], K[1] }{ "+k[0]+", "+k[1]+" }");
        if(k[1].equals("Pawn")){
            Pawn Pawnvalidate = new Pawn(position,k[0]);
            int validcheck = Pawnvalidate.ValidPath(newposition);
            return validcheck;
        }else if(k[1].equals("Rook")){
            Rook Rook = new Rook(position, k[0]);
            int validcheck = Rook.ValidPath(newposition);
            return validcheck;
        }else if(k[1].equals("Knight")){
            Knight Knight = new Knight(position, k[0]);
            int validcheck = Knight.ValidPath(newposition);
            return validcheck;
        }else if(k[1].equals("Bishop")){
            Bishop bishop = new Bishop(position, k[0]);
            int validcheck = bishop.ValidPath(newposition);
            return validcheck;
        }else if(k[1].equals("King")){
            King King = new King(position, k[0]);
            int validcheck = King.ValidPath(newposition);
            return validcheck;
        }else if(k[1].equals("Queen")){
            Queen queen = new Queen(position,k[0]);
            int validcheck = queen.ValidPath(newposition);
            return validcheck;
        }else{
            return 0;
        }
    }
    // public methods
    public boolean PieceExists(int[] newposition){
        if(this.Board[newposition[0]][newposition[1]].equals("")){
            return false;
        }else{
            return true;
        }
    }
    public void __set_Piece__(int[] move, String Piece){
        this.Board[move[0]][move[1]]=Piece;
    }
    public void __testing_board__(){
        this.Board = this.__Copy__(this.TestingBoard);
    }
    public void __show_Array__(String[] arr){
        for (int i = 0;i<arr.length;i++) {
            if(i==arr.length-1){
                System.out.println(" }");
            }else if(i==0){
                System.out.print("{ "+arr[i]);
            }else{
                System.out.print(", "+arr[i]);
            }
        }
    }
    public void __show_Array__(String[][] arr, int index){
        for (int i = 0;i<arr[index].length;i++) {
            if(i==arr[index].length-1){
                System.out.println(" }");
            }else if(i==0){
                System.out.print("{ "+arr[index][i]);
            }else{
                System.out.print(", "+arr[index][i]);
            }
        }
    }
    public void setPlayerName(String first, String second){
        this.PlayerOne = first;
        this.PlayerTwo = second;
    }
    public void showBoard(){
        for (String string[] : this.Board) {
            System.out.print("+");
            for (int i = 0; i <= this.MaxLeng; i++) {
                System.out.print(this.AddCharacter(MaxLeng+2,"-")+"+");
            }
            System.out.println();
            System.out.print("|");
            for (String subString : string) {
                if(subString == ""){
                    System.out.print(" "+this.CreateBlock(subString));
                }
                else{
                    String[] newstring = subString.split("_");
                    System.out.print(" "+this.CreateBlock(newstring[1]));
                }
                System.out.print(" |");
            }
            System.out.println();
        }
        System.out.print("+");
        for (int i = 0; i <= this.MaxLeng; i++) {
            System.out.print(this.AddCharacter(MaxLeng+2,"-")+"+");
        }
        System.out.println();
    }
    
    public boolean updateBoard(int[] position, int[] newposition, String turn){
        int validatecheck = this.Validate(position,newposition);
        boolean k = this.PieceExists(newposition);
        String[] rmp2 = this.Board[position[0]][position[1]].split("_");
        if(turn.equals(rmp2[0])){
            System.out.println("We are in the first if condition statement");
            if(validatecheck == 1 && (k == false)){
                System.out.println("We are in the second if condition statement");
                String temp = Board[position[0]][position[1]];
                Board[position[0]][position[1]] = Board[newposition[0]][newposition[1]];
                Board[newposition[0]][newposition[1]] = temp;
                return true; // for valid path 
            }else if(validatecheck == -1 || k == true){
                System.out.println("We are in the first else if condition statement");
                String[] rmp1 = this.Board[newposition[0]][newposition[1]].split("_");
                if(k==true && !(rmp1[0].equals(rmp2[0]))){
                    if(rmp2[0].equals("W")){
                        this.PlayerOneStack[PlayerOneIndex]=Board[newposition[0]][newposition[1]];
                        PlayerOneIndex++;
                        System.out.println("player One takes");
                    }else{
                        this.PlayerTwoStack[PlayerTwoIndex]=Board[newposition[0]][newposition[1]];
                        PlayerTwoIndex++;
                        System.out.println("player One takes");
                    }
                    String temp = Board[position[0]][position[1]];
                    Board[position[0]][position[1]] = "";
                    Board[newposition[0]][newposition[1]] = temp;
                    return true; // for valid path and killing 
                }
                return false; // for no killing and invalid path
            }
            return false;// for invalid path
        }else{
            return false; // for invalid move from player
        }
    }
    private String[][] __Copy__(String[][] arr){
        String[][] newarr = new String[9][9];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                newarr[i][j] = arr[i][j];
            }
        }
        return newarr;
    }
    public void ResetBoard(){
        this.Board = this.__Copy__(DefaultBoard);
    }
    public void GetPlayer(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Player First name : ");
        this.PlayerOne = sc.nextLine();
        System.out.print("Enter Player Second name : ");
        this.PlayerTwo = sc.nextLine();
        sc.close();
    }
    public boolean move(String move, String turn){
        String[] fromto = move.split("-");
        String[] alphaIndex = {"a","b","c","d","e","f","g","h"};
        int Y=0;
        String h = String.valueOf(fromto[0].charAt(0));
        for(int i = 0; i < alphaIndex.length;i++){
            if(alphaIndex[i].equals(h)){
                Y = i;
            }
        }
        int X = Character.getNumericValue(fromto[0].charAt(1));
        int[] position = {Y,X};
        int Z = 0;
        h = String.valueOf(fromto[1].charAt(0));
        for(int i = 0;i < alphaIndex.length;i++){
            if(alphaIndex[i].equals(h)){
                Z = i;
            }
        }
        int W = Character.getNumericValue(fromto[1].charAt(1));
        System.out.println(Character.getNumericValue(fromto[1].charAt(1)));
        int[] newposition = {Z,W};
        if(this.updateBoard(position, newposition, turn)){
            if(turn.equals("W")){
                this.MoveRecord[0][MoveIndexOne] = move;
                MoveIndexOne++;
            }else{
                this.MoveRecord[1][MoveIndexTwo] = move;
                MoveIndexTwo++;
            }
            System.out.println("ok");
            return true;
        }else{
            System.out.println("some issue");
            return false;
        }
    }
}

public class ChessGame{
    public static void main(String[] args) {
        ChessBoard chessboard = new ChessBoard();    
        Scanner sc = new Scanner(System.in);
        boolean NameTaken = false;
        boolean Playerone = true;
        boolean Playertwo = false;
        String PlayerName;
        boolean DevMode = true;
        boolean PlayDevMode = false;
        boolean Game = true;
        while(Game){
            if(DevMode == true && PlayDevMode == false){
                System.out.print("DevMode > ");
                String devcmd = sc.nextLine();
                if(devcmd.equals("--set-")){
                    System.out.print("DevMode > Piece : ");
                    String piece = sc.nextLine();
                    System.out.print("DevMode > X : ");
                    int X = sc.nextInt();
                    System.out.print("DevMode > Y : ");
                    int Y = sc.nextInt();
                    int[] arr = {X,Y};
                    chessboard.__set_Piece__(arr, piece);
                    continue;
                }else if(devcmd.equals("--showboard")){
                    chessboard.showBoard();
                }else if(devcmd.equals("--play")){
                    PlayDevMode = true;
                }else if(devcmd.equals("--clear")){
                    chessboard.__testing_board__();
                }else if(devcmd.equals("--0;")){
                    break;
                }else if(devcmd.equals("--taken")){
                    System.out.println("DevMode > Which (1 or 2)? ");
                    int x = sc.nextInt();
                    if(x == 1){
                        chessboard.__show_Array__(chessboard.PlayerOneStack);
                    }else if(x == 2){
                        chessboard.__show_Array__(chessboard.PlayerTwoStack);
                    }
                }else if(devcmd.equals("--his")){
                    System.out.println("DevMode > Coming soon!");
                }else if(devcmd.equals("-0devo;")){
                    DevMode = false;
                }
            }else if(DevMode && PlayDevMode){
                chessboard.showBoard();
                System.out.print("DevMode > move and piece : ");
                String k = sc.nextLine();
                if(k.equals("--0;") || k.equals("--exit;")){
                    PlayDevMode = false;
                }else{
                    String[] m = k.split(" ");
                    chessboard.move(m[0], m[1]);
                }
            }else{
                if(NameTaken == true){
                    chessboard.showBoard();
                    if(Playerone == true){
                        PlayerName = chessboard.PlayerOne;
                    }else{
                        PlayerName = chessboard.PlayerTwo;
                    }
                    System.out.print(PlayerName+" > ");
                    String move = sc.nextLine();
                    if(move.equals("exit;") || move.equals("-0;")){
                        break;
                    }else if(move.equals("-1r;")){
                        chessboard.ResetBoard();
                        NameTaken = false;
                    }else if(move.equals("-0cms;")){
                        System.out.println("==== -1 =====\n1)-1r; - for reset the board.\n\n==== -0 ====\n1)-0; - for exit from the game terminal.\n2)-0cms; - for commands");
                    }else{
                        if(Playerone == true && Playertwo == false){
                            if(chessboard.move(move,"W")==true){
                                Playerone = false;
                                Playertwo = true;
                            }
                        }else{
                            if( chessboard.move(move,"B")==true){
                                Playertwo = false;
                                Playerone = true;
                            }
                        }
                    }
                }else{
                    System.out.print("Game > Choose a Name for player one (white) : ");
                    String a = sc.nextLine();
                    if(a.equals("exit;") || a.equals("-0;")){
                        break;
                    }else if(a.equals("-0cms;")){
                        System.out.println("==== -1 =====\n1)-1r; - for reset the board.\n\n==== -0 ====\n1)-0; - for exit from the game terminal.\n2)-0cms; - for commands");
                    }else if(a.equals("-0devm;")){
                        DevMode = true;
                    }else{
                        System.out.print("Game > Very well!\nNow for second player(black) : ");
                        String b = sc.nextLine();
                        if(b.equals("exit;") || b.equals("-0;")){
                            break;
                        }else if(b.equals("-0cms;")){
                            System.out.println("==== -1 =====\n1)-1r; - for reset the board.\n\n==== -0 ====\n1)-0; - for exit from the game terminal.\n2)-0cms; - for commands");
                        }else{
                            chessboard.setPlayerName(a,b);
                            NameTaken = true;
                        }
                    }
                }
            }
        }
        sc.close();
    }
}