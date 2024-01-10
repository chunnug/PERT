package cxg230014;
import cxg230014.Graph;
import cxg230014.Graph.Vertex;
import cxg230014.Graph.Edge;
import cxg230014.Graph.GraphAlgorithm;
import cxg230014.Graph.Factory;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

class DFS extends Graph.GraphAlgorithm<DFS.DFSVertex> {
	
     public enum VertexState { //to determine if a DFS is a DAG, we initialize three states the vertex could be in
    	 //determine if the vertex is new, inside the stack, or out of the stack
        NEW, ACTIVE, FINISHED
    }
     
    LinkedList<Vertex> topList; //list of vertices in topological order

    // Class to store information about vertices during DFS
    public static class DFSVertex implements Factory {
	boolean seen; //mark if a vertex has been visited
	Vertex parent;
    VertexState state; 
        
	
	public DFSVertex(Vertex u) {
	    seen = false;
	    parent = null;
	    
	}
	public DFSVertex make(Vertex u) { return new DFSVertex(u); }
    }

    // code to initialize storage for vertex properties is in GraphAlgorithm class
    public DFS(Graph g) {
	super(g, new DFSVertex(null));
        topList = new LinkedList<>(); // initialize topList
      
    }
    /**
     * Wrapper method visits all nodes in a DFS order
     */
public void dfsAll()
    {
      
        for (Vertex u :g)
        {
        	//initialize all fields
            get(u).seen = false;
            get(u).parent = null;
        }
        for (Vertex u : g)
        {
        	//if it has not been seen, go to the DFS method
            if (get(u).seen == false)
            dfs(u);
            
        }
    }
/**
 * Visits all nodes in a DFS order
 * Should create a Topological order based on what pops out of the stack first
 * @param Vertex U
 */

    public void dfs(Vertex u) {
	
        
        get(u).seen = true; //mark node 
        
        
            for (Edge e : g.incident(u)) //go through all neighbors
            {
                Vertex v = e.otherEnd(u);
                if (!get(v).seen) 
                {
                    get(v).parent = u; 
                   dfs(v); 
                }
              
            }
              post(u); //call post after the node is out of the stack and it has completed its run
        }
    
    /**
     * initialize topList with nodes in topological order
     * @param Vertex u
     */
 public void post(Vertex u)
 {
  
  topList.addFirst(u); //all elements should be added behind the first element instead of pushed forward to get a reverse post-order
 }
 
 /**
  * wrapper method to determine from all nodes in a graph if a DFS is also a DAG
  */
 
public boolean isDagAll() {
    for (Vertex u : g) {
        get(u).state = VertexState.NEW; //initially, all vertexes are in the state new, meaning they have not entered the stack
    }

    for (Vertex u : g) {
        if (get(u).state == VertexState.NEW) 
        { //if the vertex is not a DAG via our isDag method, we return false here as well
            if (!isDag(u)) {
                return false;
            }
        }
    }
    return true;
}
/**
 * Determine if a DFS graph is also a DAG by locating back edges if present to find cycles
 * @param Vertex u
 * @return boolean, either it is a DAG or it is false
 */

public boolean isDag(Vertex u) {
    get(u).state = VertexState.ACTIVE; //vertex has entered the stack 

    for (Edge e : g.incident(u)) {
        Vertex v = e.otherEnd(u);
        if (get(v).state == VertexState.ACTIVE) {
            return false; //found a back edge, indicating a cycle
        } else if (get(v).state == VertexState.NEW) {
            if (!isDag(v)) {
                return false; //cycle found in DFS
            }
        }
    }

    get(u).state = VertexState.FINISHED; //if no back edge found, we change the state to finished
    return true;
}



}







public class PERT extends GraphAlgorithm<PERT.PERTVertex> {
    LinkedList<Vertex> finishList; //this is the same as our topological list, but we only update it if the graph is a DAG
	
    public static class PERTVertex implements Factory {
	// Add fields to represent attributes of vertices here
        int d; //duration of task
        int ES; //earliest start
        int EF; //earliest finish
        int LS; //latest start
        int LF; //latest finish
        int SL; //slack we are allowed
	
	public PERTVertex(Vertex u) {
	}
	public PERTVertex make(Vertex u) { return new PERTVertex(u); }
    }

    // Constructor for PERT is private. Create PERT instances with static method pert().
    private PERT(Graph g) {
	super(g, new PERTVertex(null));
        finishList = new LinkedList<>(); //initialize finishList
        
    }
/**
 * Setter, to update the duration of a task
 * @param u
 * @param d
 */
    public void setDuration(Vertex u, int d) {
        get(u).d = d;
    }

    // Implement the PERT algorithm. Returns false if the graph g is not a DAG.
    
    //initialize variables
    private boolean isDagChecked = false; //have we checked if it is DAG yet
private boolean isDag = false; //is it a DAG?
private int criticalPathLength = -1; //initial critical path length

/**
 * Initialize all nodes in a DFS DAG with earliest start, finish, and latest start, finish
 * Find the slack possible for each task
 * @return boolean if it is a DAG graph or not.
 */
public boolean pert() {
	//to prevent overflow, we see if we have checked for DAG
    if (!isDagChecked) {
        DFS dfs = new DFS(g);
        dfs.dfsAll(); 
        isDag = dfs.isDagAll();
        isDagChecked = true; //initialized because it has been checked
    }

    if (!isDag) {
        return false; // graph is not a DAG
    } else {
       
        for (Vertex u : g) {
            get(u).ES = 0; //initially, all vertexes earliest start is 0 so the first node will always have a value
           
        }

        //go through nodes in topological order
        for (Vertex u : topologicalOrder()) {
            get(u).EF = get(u).ES + get(u).d; //earliest finish is the earliest start plus duration
           

            for (Edge e : g.incident(u)) {
                Vertex v = e.otherEnd(u);
                if (get(v).ES < get(u).EF) { //neighbors start time cannot be earlier than the vertex's finish time
                    get(v).ES = get(u).EF; //update to new ES based on the maximum EF from all incident edges on vertex
                   
                }
            }
        }
            
Iterator<Vertex> it = finishList.descendingIterator(); //topological in reverse order to go back up from last task
int criticalPath =  criticalPath(); 
for (Vertex u : g)
{
    get(u).LF = criticalPath; //this is the maximum of all nodes earliest finish
}
while (it.hasNext()) {
    Vertex u = it.next();
    get(u).LS = get(u).LF - get(u).d; 
    get(u).SL = get(u).LF - get(u).EF; //Initialize latest start and slack
   

   for (Edge e : g.inEdges(u)) { //this accesses the incoming edges of u, we are going from the edge back out
        Vertex v = e.fromVertex(); //this is the vertex the incoming edge originates from
        if (get(v).LF > get(u).LS) { //a predecessor task's latest finish cannot be greater than the nodes latest start 
            get(v).LF = get(u).LS; //Initialize LF based on minimum LS from all incoming edge's vertexes 
           
        }
    }
}          
         }
         return true;
         
    }
/**
 * Find a topological order of g using DFS
 * @return Topological LinkedList
 */
    
    LinkedList<Vertex> topologicalOrder() {
    if (finishList == null) {
        finishList = new LinkedList<>();
    } 

    if (!isDagChecked) {
        DFS dfs = new DFS(g);
        dfs.dfsAll(); 
        isDag = dfs.isDagAll();
        isDagChecked = true;
      
    }

    if (isDag) {
        DFS dfs = new DFS(g);  // create a new DFS instance
        dfs.dfsAll();          // Run DFS again
        for (Vertex u : dfs.topList) {
            finishList.add(u); //add elements from topological list to finishlist
        }
    } 
    return finishList;
}

    // The following methods are called after calling pert().
    
    
    /**
     * Earliest time at which task u can be started
     * @param u
     * @return earliest finish field
     */
    public int ec(Vertex u) {
        
	return get(u).EF;
    }

    /**
     * Latest time at which task u can be completed
     * @param u
     * @return Latest finish field
     */
    public int lc(Vertex u) {
        
	return get(u).LF;
    }
    
    /**
     * 	Slack each task is allowed
     * @param u
     * @return slack field
     */
    public int slack(Vertex u) {
        
	return get(u).SL;
    }
    
    /**
     * length of a critical path (time taken to complete project)
     * @return integer
     */

    public int criticalPath() {
        if (isDag) {
            int max = 0;
             for (Vertex u : g)
             {
              if (max < get(u).EF)  
              {
                  max = get(u).EF;
              }
              
             }
             criticalPathLength = max;
             
        }
	return criticalPathLength; //will return -1 if it is not a DAG, otherwise we update the length
    }
    
/**
 * Determine if parameter vertex U is a critical vertex
 * @param u
 * @return boolean
 */
    public boolean critical(Vertex u) {
        if (slack(u) == 0) //if there is no slack allocated, the vertex is critical
            return true;
	else
        return false;
    }

    /**
     * Find number of critical vertices of g
     * @return integer
     */
    public int numCritical() {
        int count = 0;
        for (Vertex u : g)
        {
            if (slack(u) == 0) 
                count++;
        }
	return count;
    }

    /* Create a PERT instance on g, runs the algorithm.
     * Returns PERT instance if successful. Returns null if G is not a DAG.
     */
    public static PERT pert(Graph g, int[] duration) {
	PERT p = new PERT(g);
	for(Vertex u: g) {
	    p.setDuration(u, duration[u.getIndex()]);
	}
	// Run PERT algorithm.  Returns false if g is not a DAG
	if(p.pert()) {
	    return p;
	} else {
	    return null;
	}
    }
    
  public static void main(String[] args) throws Exception {
	String graph = "10 13   1 2 1   2 4 1   2 5 1   3 5 1   3 6 1   4 7 1   5 7 1   5 8 1   6 8 1   6 9 1   7 10 1   8 10 1   9 10 1      0 3 2 3 2 1 3 2 4 1";
	Scanner in;
	// If there is a command line argument, use it as file from which
	// input is read, otherwise use input from string.
	in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(graph);
	Graph g = Graph.readDirectedGraph(in);
	g.printGraph(false);

	int[] duration = new int[g.size()];
	for(int i=0; i<g.size(); i++) {
	    duration[i] = in.nextInt();
	}
	PERT p = pert(g, duration);
	if(p == null) {
	    System.out.println("Invalid graph: not a DAG");
	} else {
	    System.out.println("Number of critical vertices: " + p.numCritical());
	    System.out.println("u\tEC\tLC\tSlack\tCritical");
	    for(Vertex u: g) {
		System.out.println(u + "\t" + p.ec(u) + "\t" + p.lc(u) + "\t" + p.slack(u) + "\t" + p.critical(u));
	    }
	}
    }


}