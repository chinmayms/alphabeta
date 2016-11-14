
import java.awt.Point;



// Class action, represents an action (move a checker from point a to b)
public class Action{
    public Point a,b;
    
    public Action()
    {
        a = b = new Point(0,0);
    }
    
    public Action(Point a, Point b)
    {
        this.a = a;
        this.b = b;
    }
    
    public void update(Action a)
    {
        this.a = a.a;
        this.b = a.b;
    }
    
    @Override
    public String toString()
    {
        return "From "+a.y+", "+a.x+" to "+b.y+", "+b.x;
    }
}
