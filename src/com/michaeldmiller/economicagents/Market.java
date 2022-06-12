package com.michaeldmiller.economicagents;

import java.util.ArrayList;
import java.util.HashMap;

public class Market {
    private ArrayList<Agent> agents;
    private HashMap<String, Double> inventory;
    private ArrayList<JobOutput> jobOutputs;
    private ArrayList<Price> prices;
    private HashMap<String, Double> marketConsumption;
    private HashMap<String, Double> marketProduction;
    private HashMap<String, Double> productionDifference;
    private ArrayList<MarketInfo> marketProfile;
    private double money;

    public Market(ArrayList<Agent> agents, HashMap<String, Double> inventory, ArrayList<JobOutput> jobOutputs,
                  ArrayList<Price> prices, HashMap<String, Double> marketConsumption,
                  HashMap<String, Double> marketProduction, HashMap<String, Double> productionDifference,
                  ArrayList<MarketInfo> marketProfile, double money) {
        this.agents = agents;
        this.inventory = inventory;
        this.jobOutputs = jobOutputs;
        this.prices = prices;
        this.marketConsumption = marketConsumption;
        this.marketProduction = marketProduction;
        this.productionDifference = productionDifference;
        this.marketProfile = marketProfile;
        this.money = money;
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public HashMap<String, Double> getInventory() {
        return inventory;
    }

    public ArrayList<JobOutput> getJobOutputs() {
        return jobOutputs;
    }

    public ArrayList<Price> getPrices() {
        return prices;
    }

    public HashMap<String, Double> getMarketConsumption() {
        return marketConsumption;
    }

    public HashMap<String, Double> getMarketProduction() {
        return marketProduction;
    }

    public HashMap<String, Double> getProductionDifference() {
        return productionDifference;
    }

    public ArrayList<MarketInfo> getMarketProfile() {
        return marketProfile;
    }

    public double getMoney() {
        return money;
    }

    public void setAgents(ArrayList<Agent> newAgents) {
        agents = newAgents;
    }

    public void setInventory(HashMap<String, Double> newInventory) {
        inventory = newInventory;
    }

    public void setJobOutputs(ArrayList<JobOutput> newJobOutputs) {
        jobOutputs = newJobOutputs;
    }

    public void setPrices(ArrayList<Price> newPrices) {
        prices = newPrices;
    }

    public void setMarketConsumption(HashMap<String, Double> newMarketConsumption) {
        marketConsumption = newMarketConsumption;
    }

    public void setMarketProduction(HashMap<String, Double> newMarketProduction) {
        marketProduction = newMarketProduction;
    }

    public void setProductionDifference(HashMap<String, Double> newProductionDifference) {
        productionDifference = newProductionDifference;
    }

    public void setMarketProfile(ArrayList<MarketInfo> newMarketProfile) {
        marketProfile = newMarketProfile;
    }

    public void setMoney(double newMoney) {
        money = newMoney;
    }

    public String toString() {
        return ("This market has the following agents: \n" + this.getAgents() + "\n" +
                "The market inventory is: " + this.getInventory() + "\n" +
                "It permits the following job->output combinations: " + this.getJobOutputs() + "\n" +
                "The market has these prices: " + this.getPrices() + ".");
    }
}
