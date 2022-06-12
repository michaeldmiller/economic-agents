package com.michaeldmiller.economicagents;

public class UnmetConsumption {
    private int ticksPassed;
    private double missingQuantity;

    public UnmetConsumption(int ticksPassed, double missingQuantity) {
        this.ticksPassed = ticksPassed;
        this.missingQuantity = missingQuantity;
    }

    public int getTicksPassed() {
        return ticksPassed;
    }

    public double getMissingQuantity() {
        return missingQuantity;
    }

    public void setTicksPassed(int newTicksPassed) {
        ticksPassed = newTicksPassed;
    }

    public void setMissingQuantity(double newMissingQuantity) {
        missingQuantity = newMissingQuantity;
    }

    public String toString() {
        return (this.getTicksPassed() + ", " + this.getMissingQuantity());
    }
}
