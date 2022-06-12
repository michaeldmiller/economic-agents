package com.michaeldmiller.economicagents;

public class ChoiceWeight {
    String choice;
    int weight;

    public ChoiceWeight(String choice, int weight) {
        this.choice = choice;
        this.weight = weight;
    }

    public String getChoice() {
        return choice;
    }

    public int getWeight() {
        return weight;
    }

    public String toString() {
        return (this.getChoice() + "-" +
                this.getWeight());
    }
}
