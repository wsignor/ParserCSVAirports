import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * Created by Wagner on 8 de Maio de 2017.
 */
public class ParserCSVTopGastos {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    public static void main(String[] args) throws Exception {
        String csvFile = "C:\\Users\\Wagner\\IdeaProjects\\ParserCSVAirports\\src\\Ano-2017.csv";

        BufferedReader br = null;
        String line = "";

        Map<String, Integer> map1 = new HashMap<String, Integer>();

        Map<String, Double> topGastos = new HashMap<String, Double>();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                String str = values[23].trim();
                if(!str.equals("") && str.length() < 8){

                    str = values[0] + "," + values[23].replace("/", ",");


                    if(values[16] != null) {
                        String replace = values[16].replace(",", ".");
                        replace = replace.replace("-","");
                        if (topGastos.get(values[0]) != null) {
                            topGastos.put(values[0], topGastos.get(values[0]) + Double.parseDouble(replace));
                        } else {
                            topGastos.put(values[0], Double.parseDouble(replace));
                        }
                    }

                    if(map1.get(str) != null){
                        map1.put(str, map1.get(str)+1);
                    } else {
                        map1.put(str, 1);
                    }
                }
            }

            topGastos = sortByValue(topGastos);

            topGastos.forEach((k,v) -> System.out.println(k + "," + v));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }


    private static Map<String, Double> sortByValue(Map<String, Double> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Double>> list =
                new LinkedList<>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/


        return sortedMap;
    }

}
