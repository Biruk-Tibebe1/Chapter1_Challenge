package chapter1_challenge_1_4;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            File file = new File("config.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line1 = br.readLine();
            String line2 = br.readLine();
            br.close();
            
            int version = Integer.parseInt(line1);
            if (version < 2) {
                throw new Exception("Config version too old!");
            }
            
            File target = new File(line2);
            if (!target.exists()) {
                throw new IOException("Target file does not exist!");
            }
            
            // Good case (unreachable with test file)
            System.out.println("Config loaded: Version " + version + ", Path OK: " + line2);
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: Config file not found.");
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number in config version.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            System.out.println("Config read attempt finished.");
        }
    }
}