import org.json.JSONObject;
import org.json.JSONArray;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    // Function to read the input JSON file
    public static JSONObject readJson(String filePath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(content);
    }

    // Function to decode the y-values from different bases to base 10
    public static long decodeValue(int base, String value) {
        return Long.parseLong(value, base);
    }

    // Function to perform Lagrange interpolation and find the constant term (c)
    public static double lagrangeInterpolation(List<int[]> points) {
        double constantC = 0;

        for (int i = 0; i < points.size(); i++) {
            int[] point = points.get(i);
            int xi = point[0];
            int yi = point[1];
            double basis = basisPolynomial(points, i, 0); // Evaluate at x = 0 for the constant term
            constantC += yi * basis;
        }
        return constantC;
    }

    // Helper function to calculate the basis polynomial L_i(x)
    private static double basisPolynomial(List<int[]> points, int i, double x) {
        double result = 1;
        int xi = points.get(i)[0];
        for (int j = 0; j < points.size(); j++) {
            if (i != j) {
                int xj = points.get(j)[0];
                result *= (x - xj) / (xi - xj);
            }
        }
        return result;
    }

    // Main function to execute the solution
    public static void findConstantTerm(String filePath) {
        JSONObject data = readJson(filePath);

        int n = data.getJSONObject("keys").getInt("n");
        int k = data.getJSONObject("keys").getInt("k");

        List<int[]> points = new ArrayList<>();

        // Iterate over the given points
        for (String key : data.keySet()) {
            if (key.equals("keys")) continue; // Skip the "keys" object
            int x = Integer.parseInt(key);
            int base = data.getJSONObject(key).getInt("base");
            String value = data.getJSONObject(key).getString("value");
            long y = decodeValue(base, value);
            points.add(new int[]{x, (int) y});
        }

        // Ensure that we have at least k points to solve the polynomial
        points = points.subList(0, Math.min(k, points.size()));

        // Find the constant term using Lagrange interpolation
        double constantC = lagrangeInterpolation(points);

        // Print the constant term
        System.out.println("The constant term c is: " + constantC);
    }

    // Example usage
    public static void main(String[] args) {
        findConstantTerm("input.json");
    }
}
