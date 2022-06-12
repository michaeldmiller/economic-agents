package com.michaeldmiller.economicagents;

import java.util.ArrayList;

// A 'Consumptions' is a HashMap of com.michaeldmiller.economicagents.Consumption
public class Consumption {
    private double tickConsumption;
    private ArrayList<UnmetConsumption> unmetNeeds;
    /*
    private double status;
    // slope * ln(status - offset) + intercept
    private double slope;
    private double offset;
    private double intercept;
    */

    public Consumption(double tickConsumption, ArrayList<UnmetConsumption> unmetNeeds
                       //, double status, double slope, double offset, double intercept
    ) {
        this.tickConsumption = tickConsumption;
        this.unmetNeeds = unmetNeeds;
        /*
        this.status = status;
        this.slope = slope;
        this.offset = offset;
        this.intercept = intercept;
        */
    }

    public double getTickConsumption() {
        return tickConsumption;
    }

    public ArrayList<UnmetConsumption> getUnmetNeeds() {
        return unmetNeeds;
    }

    /*
    public double getStatus(){
        return status;
    }
    public double getSlope(){
        return slope;
    }
    public double getOffset(){
        return offset;
    }
    public double getIntercept(){
        return intercept;
    }
    */
    public void setTickConsumption(double newTickConsumption) {
        tickConsumption = newTickConsumption;
    }

    public void setUnmetNeeds(ArrayList<UnmetConsumption> newUnmetNeeds) {
        unmetNeeds = newUnmetNeeds;
    }

    /*
    public void setStatus(double newStatus){
        status = newStatus;
    }
    public void setSlope(double newSlope){
        slope = newSlope;
    }
    public void setOffset(double newOffset){
        offset = newOffset;
    }
    public void setIntercept(double newIntercept){
        intercept = newIntercept;
    }
    */
    public String toString() {
        return ("Tick com.michaeldmiller.economicagents.Consumption: " + this.getTickConsumption() + ", " +
                "Unmet Needs: " + this.getUnmetNeeds()
                /*
                + ", " + "Socioeconomic Status: " + this.getStatus() + ", " +
                "Demand function slope: " + this.getSlope() + ", " +
                "offset: " + this.getOffset() + ", " +
                "intercept: " + this.getIntercept()
                */
        );
    }


}
