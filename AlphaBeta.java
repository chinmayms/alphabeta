
import java.awt.Point;
import java.util.ArrayList;
import java.math.*;

/*
   Class that implements the Alpha Beta search
*/
public class AlphaBeta {
    
    // attributes for statistics
    static int maxDepth;
    static int totalNodes;
    static int evalCallsMax, evalCallsMin, pruneMax, pruneMin;
    static long startingTime;    
   
    /*
       This is the main method which shall be called from outside
    */
    public static int alphabeta(Game game, Board state, int player, Action action)
    {
        // first we clear all statistical variables
        maxDepth = 0;
        totalNodes = 0;
        evalCallsMax = evalCallsMin = pruneMax = pruneMin = 0;
        System.out.println("**************************************************");
        System.out.println("AlphaBetaSearch called");
        startingTime = System.currentTimeMillis();   // starting time
        int r = alphabeta(game, state, player, 20, -99999, 99999, true, action, player, 0); 
        long totalTime = System.currentTimeMillis() - startingTime; // compute the total time
        
        System.out.println("Maximum depth: "+ maxDepth);
        System.out.println("Total nodes generated: "+ totalNodes);
        System.out.println("Evaluation function in maxvalue: "+ evalCallsMax);
        System.out.println("Evaluation function in minvalue: "+ evalCallsMin);
        System.out.println("Prunes in maxvalue: "+ pruneMax);
        System.out.println("Prunes in minvalue: "+ pruneMin);
        System.out.println("Total time: "+totalTime+"ms");
        System.out.println("**************************************************");
        return r;
    }
    
    /* this is the method that actually does the alpha beta search */
    static int alphabeta(Game game, Board state, int player, int depth, int alpha, int beta, boolean maximize, Action returnAction, int whoami, int currentDepth)
    {        
        // update the maximum depth
        if(currentDepth > maxDepth)
            maxDepth = currentDepth;
        
        totalNodes++; // count one more node generated
        
        // we didn't implement two functions, but one which does all. Here we count the number of evaluations depending if
        // we are maximizing or minimizing
        if(maximize)
            evalCallsMax++;
        else
            evalCallsMin++;
        
        // now evaluate the current state
        int h = (int)(100.f*state.evaluate(player, whoami)); // compute the heuristic
        if(depth==0 || h==100 || h==-100) // if we arrived to a leaf in the tree
            return h;
        
        // check the time!
        if (System.currentTimeMillis() - startingTime >= 10000) // 10 seconds
        {
            return h;
        }
        
        // this part implements the max alpha beta
        if(maximize)
        {
            int v = -99999;
            ArrayList<Action> actions = game.getAllValidMoves(player, state);
            for(Action action : actions)
            {
                Board newState = game.applyAction(action, state, player);
                Action abAction = new Action();
                int newV = alphabeta(game, newState, 3-player, depth-1,alpha,beta, false, abAction, whoami, currentDepth+1);
                //System.out.println(newV+" vs "+v);
                if(newV > v)
                {
                   // System.out.println("Depth: "+depth+". Updating "+v+" to "+newV+": "+action);
                    v = newV;
                    returnAction.update(action);
                }
                alpha = Math.max(alpha, v);
                if(beta <= alpha)
                {
                    pruneMax++;
                    break;
                }
            }
          //  System.out.println("**** Returning: depth: "+depth+". V: "+v+". Action: "+returnAction);
            return v;                
        }
        else // this part implements the min alpha beta
        {
            int v = 99999;
            ArrayList<Action> actions = game.getAllValidMoves(player, state);
            for(Action action : actions)
            {
                Board newState = game.applyAction(action, state, player);
                Action abAction = new Action();
                int newV = alphabeta(game, newState, 3-player, depth-1,alpha,beta, true, abAction, whoami, currentDepth+1);
              //  System.out.println(newV+" vs "+v);
                if(newV < v)
                {
                  //  System.out.println("Depth: "+depth+". Updating "+v+" to "+newV+": "+action);
                    v = newV;
                    returnAction.update(action);
                }
                beta = Math.min(beta, v);
                if(beta <= alpha)
                {
                    pruneMin++;
                    break;
                }
            }
           // System.out.println("**** Returning: depth: "+depth+". V: "+v+". Action: "+returnAction);
            return v;
        }
    }
}
