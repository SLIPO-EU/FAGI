package gr.athena.innovation.fagi.learning;

import gr.athena.innovation.fagi.exception.ApplicationException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class reading frequencies.
 * 
 * @author Giorgos Kostoulas
 */
public class ReadFrequent {

    /**
     * Return the frequency values as a List.
     * 
     * @param path the file-path of the frequencies.
     * @param size the size.
     * @param flter_size the filter size.
     * @return the list containing the frequencies as string values.
     * 
     * @throws FileNotFoundException FileNotFoundException.
     */
    public static List<String> getFrequencies(String path, int size, int flter_size) throws FileNotFoundException {
        ArrayList<String> frequencies = new ArrayList<>();
        String cvsSplitBy = "=";

        String line;

        BufferedReader br = new BufferedReader(new FileReader(path));
        try {

            int l = -1;
            while ((line = br.readLine()) != null) {
                if (l < 0) {
                    l++;
                    continue;
                }

                String[] spl = line.split(cvsSplitBy);

                if (flter_size < spl[0].length()) {
                    frequencies.add(l, spl[0]);
                    l++;
                }
                if (l > size) {
                    break;
                }
            }
        } catch (IOException | RuntimeException ex) {
            throw new ApplicationException(ex.getMessage());
        }

        return frequencies;
    }
}
