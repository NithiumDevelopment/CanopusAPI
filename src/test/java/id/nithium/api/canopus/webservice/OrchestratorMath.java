package id.nithium.api.canopus.webservice;

public class OrchestratorMath {

    public static void main(String[] args) {
        int[] playersEachServer = {25, 26, 27, 24, 23};

        double calculate = 0;
        for (int players : playersEachServer) {
            calculate = calculate + players;
        }

        int average = (int) calculate / playersEachServer.length;
        int lowest = (average / 15) * 5;
        if (lowest == average) { // 5 2
            lowest -= 5;
            System.out.println("changed");
        } else if (lowest < average){
            lowest += 5;
        }

        System.out.println("Average number: " + average);
        System.out.println("Lowest number: " + lowest);
    }
}
