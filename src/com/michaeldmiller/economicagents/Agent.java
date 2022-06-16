package com.michaeldmiller.economicagents;

import java.util.ArrayList;
import java.util.HashMap;

public class Agent {
    private String id;
    private HashMap<String, Double> inventory;
    private ArrayList<Priority> priorities;
    private HashMap<String, Consumption> consumption;
    private Profession profession;
    private double money;
    private double satisfaction;

    public Agent(String id, HashMap<String, Double> inventory, ArrayList<Priority> priorities,
                 HashMap<String, Consumption> consumption, Profession profession, double money,
                 double satisfaction) {
        this.id = id;
        this.inventory = inventory;
        this.priorities = priorities;
        this.consumption = consumption;
        this.profession = profession;
        this.money = money;
        this.satisfaction = satisfaction;
    }

    public String getId() {
        return id;
    }

    public HashMap<String, Double> getInventory() {
        return inventory;
    }

    public ArrayList<Priority> getPriorities() {
        return priorities;
    }

    public HashMap<String, Consumption> getConsumption() {
        return consumption;
    }

    public Profession getProfession() {
        return profession;
    }

    public double getMoney() {
        return money;
    }

    public double getSatisfaction() {
        return satisfaction;
    }

    public void setId(String newID) {
        id = newID;
    }

    public void setInventory(HashMap<String, Double> newInventory) {
        inventory = newInventory;
    }

    public void setPriorities(ArrayList<Priority> newPriorities) {
        priorities = newPriorities;
    }

    public void setConsumption(HashMap<String, Consumption> newConsumption) {
        consumption = newConsumption;
    }

    public void setProfession(Profession newProfession) {
        profession = newProfession;
    }

    public void setMoney(double newMoney) {
        money = newMoney;
    }

    public void setSatisfaction(double newSatisfaction) {
        satisfaction = newSatisfaction;
    }

    public String toString() {
        return ("\n\n" + "ID: " + this.getId() + ",\n" +
                "Inventory: " + this.getInventory() + ",\n" +
                "Priorities: " + this.getPriorities() + ",\n" +
                "Consumption: " + this.getConsumption() + ",\n" +
                "Profession: " + this.getProfession() + ",\n" +
                "Money: " + this.getMoney() + ",\n" +
                "Satisfaction: " + this.getSatisfaction() + ".");
    }
}
