

import java.util.Stack;
import java.awt.Point;

// Class board: contains the board and implements some basic functions
public class Board {
    
    int size;
    
    public int [][] board;    // board representation. We can only have numbers 0, 1 or 2 here
                              // 0 is a empty space. 1 is black and 2 is white
    public int [] nCheckers;  // number of checkers of each color still on the board
    
    // possible directions for forming a group
    int directions [][] = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
    
    // copy constructor
    public Board(Board other)
    {
        nCheckers = new int[2];
        size = other.size;   
        nCheckers[0] = other.nCheckers[0];
        nCheckers[1] = other.nCheckers[1];
        
        board = new int[size][size];
        for(int i=0;i<size;i++)
            for(int j=0;j<size;j++)
                board[i][j] = other.board[i][j];
        state = new int[size][size];    
    }
    
    // create a new board with the given size
    public Board(int size)
    {
        nCheckers = new int[2];
        this.size = size;   
        nCheckers[0] = nCheckers[1] = 2 * (size-2);
        board = new int[size][size];
        for(int i=0;i<size;i++)
            for(int j=0;j<size;j++)
                board[i][j] = 0;
        for(int i=1; i<size-1; i++)
        {
            board[0][i] = board[size-1][i] = 1;
            board[i][0] = board[i][size-1] = 2;
        }
        state = new int[size][size];
    }
    
    int [][] state; // state of each position. 0=not visited, 1=in stack, 2=visited    
    
    public float evaluate(int player, int whoami)  // evaluates the current board
     // the heuristic will compute the size of the biggest group of checkers
     // returns a value beteween 1 and -1:
     // 1 means I win
     // -1 means I loose
    {
        for(int i=0; i<size; i++)
        {
            for(int j=0;j<size; j++)
            {
                state[i][j] = 0 ;
            }
        }
        
        int bstMe = 0;                 // size of the largest group of my color
        int bstOther = 0;              // size of the largest group of the enemy's color
        int otherPlayer = 3 - player;  // color of the other player
        
        // now we will run several DFS's to get the size of each group of checkers
        for(int i=0; i<size; i++)
        {
            for(int j=0;j<size; j++)
            {
                if(state[i][j]==0)
                {
                    if(board[i][j]==player)
                    {
                        int h = dfs(i,j);
                        if(h > bstMe)
                            bstMe = h;
                    }
                    else if(board[i][j]==otherPlayer)
                    {
                        int h = dfs(i,j);
                        if(h > bstOther)
                            bstOther = h;
                    }
                }
            }
        }
        
        // first check if I win or I loose
        if(bstMe == nCheckers[player - 1]) return player == whoami ? 1.0f : -1.0f;
        if(bstOther == nCheckers[otherPlayer - 1]) return player == whoami ? -1.0f : 1.0f;
        
        // compute the relative size of the largest groups
        float bestMePct = (float)bstMe / (float)nCheckers[player - 1];
        float bestOtherPct = (float)bstOther / (float)nCheckers[otherPlayer - 1];
        
        // now compute the relative percentage compared with the other player
        float totPct = bestMePct + bestOtherPct;        
        float myPct = (bestMePct / totPct)*2.f-1.f; // range [-1,1]
        
        return player == whoami ? myPct : -myPct;
    }
    
    // returns the string representation of the board for debbuging
    @Override
    public String toString()
    {
        String r = "\n";
        for(int i=0;i<size;i++)
        {
            for(int j=0;j<size;j++)
            {
                r+=board[i][j]==0?"-":board[i][j]==1?"X":"0";
            }
            r += "\n";
        }
        return r;
    }
    
    // a DFS that counts the size of a group starting at position i,j
    int dfs(int i, int j)
    {
        int player = board[i][j];
        Stack<Point> s = new Stack<>();
        int r = 0;  // result value
        
        s.push(new Point(j,i));
        state[i][j] = 1;
        
        while(!s.empty())
        {
            Point p = s.pop();
            i = p.y;
            j = p.x;
            state[i][j] = 2;
            r++;
            
            for(int d=0;d<8;d++)
            {
                int ii = i + directions[d][0];
                int jj = j + directions[d][1];
                // check if we are on a valid position
                if(ii<0 || ii>=size || jj<0 || jj>=size) continue;
                
                // check if we moved to another checker of the same player
                if(state[ii][jj] == 0 && board[ii][jj] == player)
                {
                    state[ii][jj] = 1;
                    s.push(new Point(jj, ii));
                }
            }            
        }        
        return r;
    }
    
    // returns the size of the first found group of checkers for the given player
    public int checkGroup(int player)
    {
        for(int i=0; i<size; i++)
        {
            for(int j=0;j<size; j++)
            {
                state[i][j] = 0 ;
            }
        }
        for(int i=0; i<size; i++)
        {
            for(int j=0;j<size; j++)
            {
                if(board[i][j]==player)
                    return dfs(i,j);
            }
        }
        return 0;
    }
}
