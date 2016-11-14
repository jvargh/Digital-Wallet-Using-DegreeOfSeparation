import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class AntiFraud_Test_1_Paymo_Trans
{
    AntiFraud a;
    Graph  graph;
    String batchPaymentFile = "insight_testsuite/tests/test-1-paymo-trans/paymo_input/batch_payment.txt";
    String streamPaymentFile = "insight_testsuite/tests/test-1-paymo-trans/paymo_input/stream_payment.txt";
    String output1 = "insight_testsuite/tests/test-1-paymo-trans/paymo_output/output1.txt";
    String output2 = "insight_testsuite/tests/test-1-paymo-trans/paymo_output/output2.txt";
    String output3 = "insight_testsuite/tests/test-1-paymo-trans/paymo_output/output3.txt";

    @Before
    public void Initialize()
    {
        graph = BuildInitialStateUsingBatchPaymentTxt();
    }

    @Test
    public void ObtainOutputDataUsingStreamPaymentTxt()
    {
        ClearOutputFiles();

        // Run stream using "stream_payment.txt "
        try {
            BufferedReader br1 = new BufferedReader(new FileReader(streamPaymentFile));
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

    public Graph BuildInitialStateUsingBatchPaymentTxt() {
        // Init Graph class
        Graph graph = new Graph(20000);

        // Build initial state using "batch_payment.txt"
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(batchPaymentFile));
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

    public void ClearOutputFiles()
    {
        try(FileWriter writer1 = new FileWriter(output1);
            FileWriter writer2 = new FileWriter(output2);
            FileWriter writer3 = new FileWriter(output3);)
        {
        }
        catch (IOException ioex) { ioex.printStackTrace(); }
    }

    private int CalculateDegreeOfSeparation(Graph graph, String person1, String person2) {
        int vertIndex = graph.computePaths(person1, person2);
        return (vertIndex < 0)? vertIndex : graph.adjLists[vertIndex].degree;
    }

    private void FeatureVerification(String person1, String person2, int degreeOfSeparation)
    {
        /*
            FEATURE_1 > Output1.txt
        */
        if(degreeOfSeparation > -1 && degreeOfSeparation <= 4) {
            // Direct friends
            WriteToFile(output1, "trusted, " + person1 + " -> " + person2 + ": " + "(Friend link exists)");
            System.out.println("[PASS]: test-1-paymo-trans (output1.txt)");
        }
        else if (degreeOfSeparation == -1) {
            // Not Direct friends
            WriteToFile(output1, "unverified, " + person1 + " -> " + person2 + ": " + "(Friend link Non-Existent)");
            System.out.println("[FAIL]: test-1-paymo-trans (output1.txt)");
        }

        /*
            FEATURE_2 > Output2.txt
        */
        if(degreeOfSeparation == 0) {
            // Direct friends
            WriteToFile(output2, "trusted, " + person1 + " -> " + person2 + ": " + "Degree Of Separation == 0 (Direct Friends)");
            System.out.println("[PASS]: test-1-paymo-trans (output2.txt)");
        }
        else if(degreeOfSeparation == 1) {
            // DOS = 2
            WriteToFile(output2, "unverified, " + person1 + " -> " + person2 + ": " + "Degree Of Separation = 1 (Friend of Friend)");
            System.out.println("[FAIL]: test-1-paymo-trans (output2.txt)");
        }

        /*
            FEATURE_3 > Output3.txt
        */
        if(degreeOfSeparation == 2 || degreeOfSeparation == 3 || degreeOfSeparation == 4) {
            // Direct friends
            if(degreeOfSeparation == 2)
                WriteToFile(output3, "trusted, " + person1 + " -> " + person2 + ": " + "Degree Of Separation == 2");
            else if(degreeOfSeparation == 3)
                WriteToFile(output3, "trusted, " + person1 + " -> " + person2 + ": " + "Degree Of Separation == 3");
            else if(degreeOfSeparation == 4)
                WriteToFile(output3, "trusted, " + person1 + " -> " + person2 + ": " + "Degree Of Separation == 4");

            System.out.println("[PASS]: test-1-paymo-trans (output3.txt)");
        }
        else if (degreeOfSeparation > 4 || degreeOfSeparation == -2) {
            // DOS > 4
            WriteToFile(output3, "unverified, " + person1 + " -> " + person2 + ": " + "Degree Of Separation > 4");
            System.out.println("[FAIL]: test-1-paymo-trans (output3.txt)");
        }
    }

    public void WriteToFile(String fileName, String text) {
        try(FileWriter writer = new FileWriter(fileName, true))
        {
            writer.write(text + "\n");
        }
        catch (IOException ioex) { ioex.printStackTrace(); }
    }
}