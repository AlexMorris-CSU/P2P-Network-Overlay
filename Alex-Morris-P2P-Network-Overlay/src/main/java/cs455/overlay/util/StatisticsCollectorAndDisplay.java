package cs455.overlay.util;

import java.util.ArrayList;

public class StatisticsCollectorAndDisplay {

    private int totalSent;
    private long totalSentSum;
    private int totalReceived;
    private long totalReceivedSum;
    private int totalRelayed;

    public StatisticsCollectorAndDisplay(ArrayList<TrafficSummary> trafficSummaryArray){
        System.out.println("--------------------------------------------------------------------------------");
        System.out.format("%9s%10s%10s%10s%20s%20s", "Node ID", "Sent", "Received", "Relayed", "Sent payload", "Received payload");
        System.out.println("");
        System.out.println("--------------------------------------------------------------------------------");


        for(TrafficSummary i : trafficSummaryArray){
            totalSent += i.getSent();
            totalSentSum += i.getSentSum();
            totalReceived += i.getReceived();
            totalReceivedSum += i.getReceivedSum();
            totalRelayed += i.getRelayed();
            System.out.format("%9s%10s%10s%10s%20s%20s", i.getNodeID(), i.getSent(), i.getReceived(), i.getRelayed(), i.getSentSum(), i.getReceivedSum());
            System.out.println("");
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.format("%9s%10s%10s%10s%20s%20s", "SUM", totalSent, totalReceived, totalRelayed, totalSentSum, totalReceivedSum);
        System.out.println("");
        resetStats();

    }

    private void resetStats(){
        totalSent = 0;
        totalSentSum = 0;
        totalReceived = 0;
        totalReceivedSum = 0;
        totalRelayed = 0;
    }
}
