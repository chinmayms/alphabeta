
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.Point;

// Canvas where we draw the board and process the mouse events
public class BoardCanvas extends Canvas {
    
    BoardFrame boardFrame;
    Board board;
    Game game;
    
    int size;
    
    Point currentSelected;        // coordinates of the current selected checker
    ArrayList<Point> validMoves;  // list of valid moves coordinates
    
    public BoardCanvas(BoardFrame board)
    {
        this.boardFrame = board;
        size = 0; 
        currentSelected = null;
        validMoves = new ArrayList<>();
        
        addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    mouse_Pressed(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }
            });
    }
    
    // method for processing the mouse pressed event
    public void mouse_Pressed(MouseEvent e)
    {
        if(!game.playing) return;  // only process this event if the game if running
        
        if(game.humanPlays())  // check if it is human's turn
        {
            int humanColor = game.humanPlayer; // get human's color
            int x = e.getX();
            int y = e.getY();
            
            int w = getWidth();
            int h = getHeight();
            
            int wc = w / size - 1;
            int hc = h / size - 1;
            
            int i = y / hc;
            int j = x / wc;          // i,j is the position in the board where we clicked
            
            if(currentSelected == null)  // check if there is already a selected checker
            {
                if(board.board[i][j] == humanColor)
                {
                    // check if it's my checker
                    validMoves.clear();
                    validMoves.addAll(game.getValidMoves(i, j));
                    currentSelected = new Point(j,i);
                    repaint();
                }                
            }
            else
            {
                if(board.board[i][j] == humanColor) // we are selecting a new checker to move
                {
                    validMoves.clear();
                    validMoves.addAll(game.getValidMoves(i, j));
                    currentSelected = new Point(j,i);
                    repaint();
                }
                else // check if we are moving to a valid position
                {
                    if(validMoves.contains(new Point(j,i))) // now complete the move
                    {
                        game.move(currentSelected.y, currentSelected.x, i, j);
                        currentSelected = null;
                        validMoves.clear();
                        Graphics g = getGraphics();
                        paint(g);
                        if(game.playing)
                        {
                            game.computerMove();
                            revalidate();
                            repaint();
                        }
                    }
                }
            }
            
        }
    }
    
    // forces the painting of the board
    public void forcepaint()
    {
        Graphics g = getGraphics();
        paint(g);        
    }
    
    // initializes the board
    public void init(int size, Board board, Game game)
    {
        this.size = size;
        this.board = board;
        this.game = game;
    }
    
    // repaints the board
    public void paint(Graphics g)
    {
        // all we need to do is simply draw the paint method of the TronBoard class
        int w = getWidth();
        int h = getHeight();
        
        // check if there is something to draw
        if(size>0)
        {
            Color boardColors [] = new Color[2];
            Color checkerColors [] = new Color[2];
            
            // colors for the board
            boardColors[0] = Color.getHSBColor(0.091f,0.36f,1.f);
            boardColors[1] = Color.getHSBColor(0.091f,0.73f,0.8f);
            
            // colors for the checkers
            checkerColors[0] = Color.getHSBColor(0.061f,0.79f,.53f);
            checkerColors[1] = Color.getHSBColor(0.14f,0.65f,.89f);
            
            int wc = w / size - 1;
            int hc = h / size - 1;
            
            // now we paint everything
            for(int i=0;i<size;i++)
            {
                int y = hc * i;
                int colorIndex = i%2;
                for(int j=0;j<size;j++)
                {                    
                    int x = wc * j;
                    g.setColor(boardColors[colorIndex]);
                    g.fillRect(x, y, wc, hc);
                    colorIndex = 1 - colorIndex;
                    
                    int checker = board.board[i][j];
                    if(checker>0)  // we have a checker here!
                    {
                        g.setColor(checkerColors[checker-1]);
                        g.fillOval(x+2, y+2, wc-4, hc-4);
                    }                    
                    
                }
            }
            
            // check if we need to paint the valid moves
            if(currentSelected != null && validMoves.size()>0)
            {
               g.setColor(Color.BLACK); 
               int i1 = currentSelected.y;
               int j1 = currentSelected.x;
               
               int x1 = j1 * wc + wc / 2;
               int y1 = i1 * hc + hc / 2;
               
               for(Point p : validMoves)
               {
                   int i2 = p.y;
                   int j2 = p.x;
                   
                   int x2 = j2 * wc + wc / 2;
                   int y2 = i2 * hc + hc / 2;
                   
                   g.drawLine(x1, y1, x2, y2);
               }               
            }
        }
    }    
    
}
