import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by jvargh on 11/13/2016.
 */
public class Graph {
    Vertex[] adjLists;
    private int numPersons;

    public Graph(int x) {
        adjLists = new Vertex[x];
        numPersons = 0;
    }

    // Add connections between persons 1 and 2 to Adjacency list
    public void makeEdge(String person1, String person2) {
        int i,j;
        i = getVertex(person1);
        j = getVertex(person2);
        if(i == -1) {
            adjLists[numPersons] = new Vertex(person1, null);
            i=numPersons;
            numPersons++;
        }
        if(j == -1) {
            adjLists[numPersons] = new Vertex(person2, null);
            j=numPersons;
            numPersons++;
        }
        adjLists[i].adjList = new Neighbor(j, person2, adjLists[i].adjList);
        adjLists[j].adjList = new Neighbor(i, person1, adjLists[j].adjList);
    }

    // Returns vertex index if it is present, else it returns -1.
    public int getVertex (String person) {
        for (int i = 0; i < numPersons; i++) {
            if (adjLists[i].person.equals(person))
                return i;
        }
        return -1;
    }

    // Returns index of vertex in list when given Vertex
    public int getVertex (Vertex person) {
        for (int i = 0; i < numPersons; i++) {
            if (adjLists[i].equals(person))
                return i;
        }
        return -1;
    }

    // For two given persons, method returns common that connected them.
    public String getPerson(int person1Index, int person2Index) {
        for (Neighbor nbr = adjLists[person1Index].adjList; nbr != null; nbr = nbr.next)
        {
            if (nbr.vertex_num == person2Index)
                return nbr.vertex_num + "";
        }
        return null;
    }

    /* Compute path for given vector. Uses bfs algorithm to compute the path
     * from person1 to person2 following relationships.
     */
    public int computePaths(String person1, String person2)
    {
        // Init visited=false
        for(int i=0; i<adjLists.length; i++) {
            try {
                adjLists[i].visited = false;
            }
            catch (Exception ex) {
                continue;
            }

        }

        // Vertex path = new Vertex(person1, null);
        Queue<Vertex> vertexQueue = new LinkedList<Vertex>();
        // Array of visited values.

        // On queue, vertices are added in terms of levels. 1 level has person1 only.
        // Next level of additions to queue is after all neighbors of person1 are enqueued.
        // Next level is after all neighbors of neighbors of ac1 are enqueued. And so on.
        // We only go to the next level, degree of separation, after one level of neighbors is dequeued.

        int vertIndex = 0;  // Index to index into LinkedList array for persons.
        vertIndex = getVertex(person1);

        // Add vertex to Queue if it exists. Else return sentinel, there is no path!
        if (vertIndex != -1)
            vertexQueue.add(adjLists[vertIndex]);
        else
            return -1;

        // If person2 is not in our List, return sentinel, there is no path!
        if (getVertex(person2) == -1 )
            return -1;

        while(!vertexQueue.isEmpty())
        {
            // Pop vertex from Queue
            Vertex act1 = vertexQueue.poll();
            // Mark Vertex as visited.
            vertIndex = getVertex(act1);
            adjLists[vertIndex].visited = true;


            for (Neighbor nbr = act1.adjList; nbr!=null; nbr = nbr.next)
            {
                // Add all vertex neighbors to queue, if they are not visited yet.
                if (!adjLists[nbr.vertex_num].visited ) {
                    vertexQueue.add(adjLists[nbr.vertex_num]);
                    adjLists[nbr.vertex_num].visited = true;
                    adjLists[nbr.vertex_num].degree = act1.degree + 1;  // Incrementing degree basing on parent.
                    adjLists[nbr.vertex_num].whoBroughtYouIn = vertIndex;

                    // Abandon search if degree is greater than 6
                    if (adjLists[nbr.vertex_num].degree > 6)
                        return -2;  // Sentinel value for high degree
                }
                // If we find needed person, return degree.
                if (adjLists[nbr.vertex_num].person.equals(person2))
                    return nbr.vertex_num;
            }
        }
        return -1;
    }

    public void printGraph() {

        for (int i = 0; i < numPersons; i++)
        {
            System.out.print(adjLists[i].person);
            for (Neighbor nbr = adjLists[i].adjList; nbr != null; nbr = nbr.next)
            {
                System.out.print(" --> " + adjLists[nbr.vertex_num].person + ": " + nbr.vertex_num);
                System.out.println();
            }
            System.out.println("========================================================================");
        }

    }
}
