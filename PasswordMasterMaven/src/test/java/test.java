
import java.io.*;

class test {

    public static void main(String[] args) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String cmds[] = {"cmd", "/c", "tasklist"};
        Process proc = runtime.exec(cmds);
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        String line;
        
        while ((line = bufferedreader.readLine()) != null) {
            if(line.contains("java"))
            System.out.println(line);
        }
        bufferedreader.close();
        inputstreamreader.close();
        inputstream.close();
    }
}
