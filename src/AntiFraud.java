import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

class Neighbor {
    int vertex_num;
    Neighbor next;
    String neighbor;
    public Neighbor(int vertex, String neighbor, Neighbor next) {
        this.vertex_num = vertex;
        this.neighbor = neighbor;
        this.next = next;
    }
    @Override
    public String toString() { return ":-->" + neighbor +  next; }
}

class Vertex {
    String person;
    Neighbor adjList;
    Boolean visited;
    int degree;
    int whoBroughtYouIn;
    Vertex(String person, Neighbor neighbor) {
        this.person = person;
        this.adjList = neighbor;
        visited = false;
        degree = -1;
        whoBroughtYouIn = -1;
    }
    @Override
    public String toString () {
        return person + " : " + adjList;
    }
}



public class AntiFraud
{
    public static void main(String[] args) {
        AntiFraud a = new AntiFraud();
        Graph g = a.BuildInitialStateUsingBatchPaymentTxt("paymo_input/batch_payment.txt");
        a.ObtainOutputDataUsingStreamPaymentTxt(g, "paymo_input/stream_payment.txt");
    }

    public Graph BuildInitialStateUsingBatchPaymentTxt(String file) {
        // Init Graph class
        Graph graph = new Graph(20000);

        // Build initial state using "batch_payment.txt"
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            StringTokenizer st;
            String line = br.readLine(); // read heading
            String[] persons = new String[2];
            while ((line = br.readLine()) != null) {
                st = new StringTokenizer(line, ",");
                st.nextToken();
                persons[0] = st.nextToken().trim();
                persons[1] = st.nextToken().trim();

                // Ignore stand alone persons. This data is useless for finding 6 degrees of separation.
                if (persons.length > 1)
                    graph.makeEdge(persons[0], persons[1]);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }

    public void ObtainOutputDataUsingStreamPaymentTxt(Graph graph, String file)
    {
        ClearOutputFiles();

        // Run stream using "stream_payment.txt "
        try {
            BufferedReader br1 = new BufferedReader(new FileReader(file));
            StringTokenizer st;
            String line = br1.readLine(); // read heading and skip
            while ((line = br1.readLine()) != null)
            {
                st = new StringTokenizer(line,",");
                st.nextToken();
                String person1= st.nextToken().trim();
                String person2= st.nextToken().trim();

                // Ignore stand alone persons. This data is useless for finding  6 degrees of separation.
                int degreeOfSeparation = CalculateDegreeOfSeparation(graph, person1, person2);

                // Calculate results and write to Output files
                FeatureVerification(person1, person2, degreeOfSeparation);
            }
            br1.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String Feature1Test(Graph graph, String person1, String person2)
    {
        int degreeOfSeparation = CalculateDegreeOfSeparation(graph, person1, person2);

        if(degreeOfSeparation > -1 && degreeOfSeparation <=4) {
            // Direct friends
            return ("trusted, " + person1 + " -> " + person2 + ": " + "(Friend link exists)");
        }
        else if (degreeOfSeparation == -1) {
            // Not Direct friends
            return ("unverified, " + person1 + " -> " + person2 + ": " + "(Friend link Non-Existent)");
        }

        return "Not Applicable";
    }

    public String Feature2Test(Graph graph, String person1, String person2)
    {
        int degreeOfSeparation = CalculateDegreeOfSeparation(graph, person1, person2);

        if(degreeOfSeparation == 0) {
            // Direct friends
            return ("trusted, " + person1 + " -> " + person2 + ": " + "Degree Of Separation == 0 (Direct Friends)");
        }
        else if(degreeOfSeparation == 1) {
            // DOS = 2
            return ("unverified, " + person1 + " -> " + person2 + ": " + "Degree Of Separation = 1 (Friend of Friend)");
        }

        return "Not Applicable";
    }


    public String Feature3Test(Graph graph, String person1, String person2)
    {
        int degreeOfSeparation = CalculateDegreeOfSeparation(graph, person1, person2);

        if(degreeOfSeparation == 2 || degreeOfSeparation == 3 || degreeOfSeparation == 4)
        {
            // Direct friends
            if(degreeOfSeparation == 2)
                return ("trusted, " + person1 + " -> " + person2 + ": " + "Degree Of Separation == 2");
            else if(degreeOfSeparation == 3)
                return ("trusted, " + person1 + " -> " + person2 + ": " + "Degree Of Separation == 3");
            else if(degreeOfSeparation == 4)
                return ("trusted, " + person1 + " -> " + person2 + ": " + "Degree Of Separation == 4");
        }
        else if (degreeOfSeparation > 4 || degreeOfSeparation == -2)
        {
            // DOS > 4
            return ("unverified, " + person1 + " -> " + person2 + ": " + "Degree Of Separation > 4");
        }

        return "Not Applicable";
    }

    private void QuickTest(Graph graph) {
        String person1 = "49466";
        String person2 = "32639";

        int degreeOfSeparation = CalculateDegreeOfSeparation(graph, person1, person2);
        FeatureVerification(person1, person2, degreeOfSeparation);

        //
        person1 = "49466";
        person2 = "32639";

        degreeOfSeparation =  CalculateDegreeOfSeparation(graph, person1, person2);
        FeatureVerification(person1, person2, degreeOfSeparation);
    }

    private int CalculateDegreeOfSeparation(Graph graph, String person1, String person2) {
        int vertIndex = graph.computePaths(person1, person2);
        return (vertIndex < 0)? vertIndex : graph.adjLists[vertIndex].degree;
    }

    public void ClearOutputFiles() {
        try(FileWriter writer1 = new FileWriter("paymo_output/output1.txt");
            FileWriter writer2 = new FileWriter("paymo_output/output2.txt");
            FileWriter writer3 = new FileWriter("paymo_output/output3.txt");)
        {
        }
        catch (IOException ioex) { ioex.printStackTrace(); }
    }

    private void FeatureVerification(String person1, String person2, int degreeOfSeparation)
    {
        /*
            FEATURE_1 > Output1.txt
        */
        if(degreeOfSeparation > -1 && degreeOfSeparation <=4) {
            // Direct friends
            WriteToFile("output1.txt","trusted, " + person1 + " -> " + person2 + ": " + "(Friend link exists)");
        }
        else if (degreeOfSeparation == -1) {
            // Not Direct friends
            WriteToFile("output1.txt","unverified, " + person1 + " -> " + person2 + ": " + "(Friend link Non-Existent)");
        }

        /*
            FEATURE_2 > Output2.txt
        */
        if(degreeOfSeparation == 0) {
            // Direct friends
            WriteToFile("output2.txt","trusted, " + person1 + " -> " + person2 + ": " + "Degree Of Separation == 0 (Direct Friends)");
        }
        else if(degreeOfSeparation == 1) {
            // DOS = 2
            WriteToFile("output2.txt","unverified, " + person1 + " -> " + person2 + ": " + "Degree Of Separation = 1 (Friend of Friend)");
        }

        /*
            FEATURE_3 > Output3.txt
        */
        if(degreeOfSeparation == 2 || degreeOfSeparation == 3 || degreeOfSeparation == 4) {
            // Direct friends
            if(degreeOfSeparation == 2)
                WriteToFile("output3.txt","trusted, " + person1 + " -> " + person2 + ": " + "Degree Of Separation == 2");
            else if(degreeOfSeparation == 3)
                WriteToFile("output3.txt","trusted, " + person1 + " -> " + person2 + ": " + "Degree Of Separation == 3");
            else if(degreeOfSeparation == 4)
                WriteToFile("output3.txt","trusted, " + person1 + " -> " + person2 + ": " + "Degree Of Separation == 4");
        }
        else if (degreeOfSeparation > 4 || degreeOfSeparation == -2) {
            // DOS > 4
            WriteToFile("output3.txt","unverified, " + person1 + " -> " + person2 + ": " + "Degree Of Separation > 4");
        }
    }

    public void WriteToFile(String fileName, String text) {
        try(FileWriter writer = new FileWriter("paymo_output/"+fileName, true))
        {
            writer.write(text + "\n");
        }
        catch (IOException ioex) { ioex.printStackTrace(); }
    }
}
