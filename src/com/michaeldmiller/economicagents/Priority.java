package com.michaeldmiller.economicagents;

// A 'Priorities' is an ArrayList of com.michaeldmiller.economicagents.Priority
public class Priority {
    private String good;
    private double baseWeight;
    private double relativeNeed;
    private double modifier;
    private double priceElasticity;
    private double originalPriceElasticity;
    private double weight;

    public Priority(String good, double baseWeight, double relativeNeed,
                    double modifier, double priceElasticity, double originalPriceElasticity,
                    double weight) {
        this.good = good;
        this.baseWeight = baseWeight;
        this.relativeNeed = relativeNeed;
        this.modifier = modifier;
        this.priceElasticity = priceElasticity;
        this.originalPriceElasticity = originalPriceElasticity;
        this.weight = weight;
    }

    public String getGood() {
        return good;
    }

    public double getBaseWeight() {
        return baseWeight;
    }

    public double getRelativeNeed() {
        return relativeNeed;
    }

    public double getModifier() {
        return modifier;
    }

    public double getPriceElasticity() {
        return priceElasticity;
    }

    public double getOriginalPriceElasticity() {
        return originalPriceElasticity;
    }

    public double getWeight() {
        return weight;
    }

    public void setGood(String newGood) {
        good = newGood;
    }

    public void setBaseWeight(double newBaseWeight) {
        baseWeight = newBaseWeight;
    }

    public void setRelativeNeed(double newRelativeNeed) {
        relativeNeed = newRelativeNeed;
    }

    public void setModifier(double newModifier) {
        modifier = newModifier;
    }

    public void setPriceElasticity(double newPriceElasticity) {
        priceElasticity = newPriceElasticity;
    }

    public void setOriginalPriceElasticity(double newOriginalPriceElasticity) {
        originalPriceElasticity = newOriginalPriceElasticity;
    }

    public void setWeight(double newWeight) {
        weight = newWeight;
    }

    public String toString() {
        return ("\n" + this.getGood() + ": " +
                "base weight: " + this.getBaseWeight() + ", " +
                "relative need: " + this.getRelativeNeed() + ", " +
                "modifier: " + this.getModifier() + ", " +
                "price elasticity: " + this.getPriceElasticity() + ", " +
                "original price elasticity: " + this.getOriginalPriceElasticity() + ", " +
                "final weight: " + this.getWeight() + ".");
    }
}
