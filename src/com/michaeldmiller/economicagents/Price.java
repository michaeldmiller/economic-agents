package com.michaeldmiller.economicagents;

class Price {
    private String good;
    private double cost;
    private double equilibriumCost;
    private final double originalCost;

    public Price(String good, double cost, double equilibriumCost, double originalCost) {
        this.good = good;
        this.cost = cost;
        this.equilibriumCost = equilibriumCost;
        this.originalCost = originalCost;
    }

    public String getGood() {
        return good;
    }

    public double getCost() {
        return cost;
    }

    public double getEquilibriumCost() {
        return equilibriumCost;
    }

    public double getOriginalCost() {
        return originalCost;
    }

    public void setGood(String newGood) {
        good = newGood;
    }

    public void setCost(double newCost) {
        cost = newCost;
    }

    public void setEquilibriumCost(double newEquilibriumCost) {
        equilibriumCost = newEquilibriumCost;
    }

    public String toString() {
        return ("\n" + this.getGood() + ", " +
                "Cost: " + this.getCost() + ", " +
                "Equilibrium Cost: " + this.getEquilibriumCost() + ", " +
                "Original Cost: " + this.getOriginalCost());
    }
}
