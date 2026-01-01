package Chapter1_Challenge_1_2;

public class Main {
    public static void main(String[] args) {
        String[] winningNumbers = {"12-34-56-78-90", "33-44-11-66-22", "01-02-03-04-05"};
        
        double maxAvg = -1;
        int maxIndex = -1;
        
        for (int i = 0; i < winningNumbers.length; i++) {
            String clean = winningNumbers[i].replace("-", "");
            char[] chars = clean.toCharArray();
            int[] digits = new int[chars.length];
            int j = 0;
            for (char c : chars) {
                digits[j++] = Character.getNumericValue(c);
            }
            
            int sum = 0;
            for (int d : digits) {
                sum += d;
            }
            double avg = (double) sum / digits.length;
            
            System.out.println("Analyzing: " + winningNumbers[i]);
            System.out.println("Digit Sum: " + sum + ", Digit Average: " + avg);
            
            if (avg > maxAvg) {
                maxAvg = avg;
                maxIndex = i;
            }
        }
        
        System.out.println("The winning number with the highest average is: " + 
                           winningNumbers[maxIndex] + " with an average of " + maxAvg);
    }
}