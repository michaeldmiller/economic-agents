package com.michaeldmiller.economicagents;

import java.util.*;

public class MarketMain {
    /*
    I have always been enamored by the ability of computation to simulate the world around us.
    The goal of this project is to create a virtual marketplace, where economic rationals guide the
    nature of an economy as agents produce, buy, and sell various goods to fulfill their needs.

    As noted above, the idea of a marketplace here is as simply an agglomeration of various economic "agents"
    (abstracted from people, family units, companies, governments, etc. that make up the real world). Each of these
    agents is an autonomous, atomic unit of the market. Each agent can produce a good, buy and sell goods from the
    market, possess money, and has needs and priorities for goods and services which it will seek to satisfy.

    Some observations at the outset: I am interested in history, and the applications I have in mind for this
    system are set chronologically in the past. Furthermore, today's world is complicated to say the least, and for
    a fairly rudimentary system I think it is generally better to focus on modeling the past, which generally had
    an economic environment which is more approachable.
    While this system attempts to abstract relevant economic features as little as possible, for simplicity
    and sanity's sake there are a number of abstractions/cheats built into the system: behaviors and attributes
    determined outside the system.
     */

    // randomizer functions, designed in a previous project:
    public static String randomPick(ArrayList<String> lst){
        // given a list of strings, return a random choice from the list
        // does not modify the list or prevent duplicate picks
        int listLength = lst.size();
        int choiceNumber = (int) (Math.random() * listLength);
        return lst.get(choiceNumber);
    }

    public static String randomWeightedPick(ArrayList<String> choices, ArrayList<Integer> weights){
        // ArrayList<String>, ArrayList<Integer> -> String
        // given a list of choices, and a list of integer weights of the same list length
        // whose values correspond to the weights of the choices, make a weighted randomized
        // choice among the possible choices
        // eg for a list "A", "B" of the choices and a list 80, 20 of the weights, "A" is
        // chosen on average 80% of the time and "B" is chosen 20% of the time.
        // FUNCTION DOES NOT WORK IF CHOICE AND WEIGHT LIST ARE OF DIFFERENT LENGTHS

        // unpack choices and weights into list of choice-weights
        // set running total and choice-weight accumulators
        int weightTotal = 0;
        ArrayList<ChoiceWeight> weightedList = new ArrayList<ChoiceWeight>();

        // main loop
        for (int i = 0; i < choices.size(); i++){
            String choice = choices.get(i);
            int weight = weights.get(i);
            weightTotal = weightTotal + weight;
            weightedList.add(new ChoiceWeight(choice, weightTotal));
        }
        // System.out.println(weightedList);
        // now, using combined weight total, select an individual weight unit within it
        int unitSelection = (int) (Math.random() * weightTotal);
        // System.out.println(weightTotal);
        // System.out.println(unitSelection);

        // search list of choice-weight pairs, return the choice whose weight unit was selected
        String output = "";
        for (int j = 0; j < weightedList.size(); j++){
            if (weightedList.get(j).getWeight() >= unitSelection){
                output = weightedList.get(j).getChoice();
                break;
            }
        }
        return output;
    }

    // First Method: Produce
    // Given an Agent and a Market, have the agent produce a good according to its Job, deliver
    // the good to the market, and be compensated accordingly.
    // Breaks if the agent is not initialized with a job that is in the market's job output list!
    public static void agentProduce (Agent agent, Market market){
        // first determine what goods are going to be produced
        String goodType = "";
        for (JobOutput j : market.getJobOutputs()) {
            if (j.getJob().equals(agent.getProfession().getJob())) {
                goodType = j.getGood();
                break;
            }
        }

        // major modification: variable production. Agent skill level is the maximum it can produce, if there is
        // an oversupply, agent will reduce its own production down to within a variance of the market needs
        // First step: determine if market is oversupplied:
        double producedQuantity = 0;
        if (market.getProductionDifference().get(goodType) > 0){
            // if so, calculate factor by which market is overproducing
            double overproductionFactor =
                    market.getProductionDifference().get(goodType) / market.getMarketProduction().get(goodType);
            // System.out.println("The production difference is: " + market.getProductionDifference().get(goodType));
            // System.out.println("The market production is: " + market.getMarketProduction().get(goodType));
            // System.out.println("The overproduction factor is: " + overproductionFactor);

            // add variance to production factor (normal distribution, standard deviation of 7%
            Random random = new Random();
            // double variance = random.nextGaussian(0.0,0.07);
            double variance = 1 + (0.07 * random.nextGaussian());
            // get agent base production (can change later to account for good types producing different quantities)
            double baseProduction = agent.getProfession().getSkillLevel();
            // combine factors
            producedQuantity = baseProduction * (1 - (overproductionFactor + variance));
            // prevent negative production
            if (producedQuantity < 0){
                producedQuantity = 0;
            }
            // System.out.println("Production Difference: " +  market.getProductionDifference().get(goodType));
            // System.out.println("Market Production: " +  market.getMarketProduction().get(goodType));
            // System.out.println("Produced Quantity: " + goodType + " " + producedQuantity);
        }
        // if market is not oversupplied, produce normally (i.e. at agent maximum)
        else {
            producedQuantity = agent.getProfession().getSkillLevel();
            // System.out.println("Not oversupplied");
        }
        // then have the Agent produce the Good (literally produces the amount of their skill level)
        // Item agentProduction = new Item (goodType, producedQuantity);
        // compensate the Agent first (don't want agent's production to affect market price before the market has it)
        // find market price
        double currentPrice = 0;
        for (Price p : market.getPrices()){
            if (p.getGood().equals(goodType)){
                currentPrice = p.getCost();
                break;
            }
        }
        agent.getProfession().setShortRunProduction(producedQuantity);

        // pay Agent
        agent.setMoney(agent.getMoney() + (producedQuantity * currentPrice));
        // send good to market
        market.getInventory().put(goodType, market.getInventory().get(goodType) + producedQuantity);

    }

    // apply Agent production to the Market
    public static void marketProduce (Market m){
        for (Agent a : m.getAgents()){
            agentProduce(a, m);
        }
    }

    // have the Agents consume goods according to their consumption profile.
    // *this interpretation REQUIRES that inventory and consumption have the same goods in the same order*
    // (^ helps performance)
    // an Agent running out of a good significantly increases its relative need for it, having that good again resets
    // the relative need

    public static void agentConsume (Agent a, Market m){
        for (Map.Entry<String, Consumption> agentConsumption: a.getConsumption().entrySet()){
            // handle unmet needs, if they exist
            for (int i = 0; i < a.getConsumption().get(agentConsumption.getKey()).getUnmetNeeds().size(); i++){
                UnmetConsumption u = a.getConsumption().get(agentConsumption.getKey()).getUnmetNeeds().get(i);
                u.setTicksPassed(u.getTicksPassed() + 1);

                // simulate forgetting past unmet needs
                // if memory is 50 ticks old, begin reducing quantity
                if (u.getTicksPassed() >= 50){
                    u.setMissingQuantity(u.getMissingQuantity() - 0.01);
                }

                // if memory is more than 100 ticks old, reduce faster
                if (u.getTicksPassed() >= 100){
                    u.setMissingQuantity(u.getMissingQuantity() - 0.04);
                }

                // remove memory if quantity dips to or below 0
                if (u.getMissingQuantity() <= 0) {
                    a.getConsumption().get(agentConsumption.getKey()).getUnmetNeeds().remove(u);
                }
            }

            double currentInventoryAmount = a.getInventory().get(agentConsumption.getKey());
            double newInventoryAmount = currentInventoryAmount - agentConsumption.getValue().getTickConsumption();

            a.getInventory().put(agentConsumption.getKey(),
                    a.getInventory().get(agentConsumption.getKey()) - agentConsumption.getValue().getTickConsumption());
            // handle negatives: add an unmet consumption need to the list
            if (newInventoryAmount < 0){
                double shortage = currentInventoryAmount - newInventoryAmount;
                // prevent rounding error shortages from being counted
                if (Math.abs(shortage) > 0.01){
                    // add new unmet need to corresponding entry in agent's consumptions
                    a.getConsumption().get(agentConsumption.getKey()).getUnmetNeeds()
                            .add(new UnmetConsumption(0, shortage));

                }
                a.getInventory().put(agentConsumption.getKey(), 0.0);
                for (Priority p : a.getPriorities()){
                    if (p.getGood().equals(agentConsumption.getKey())){
                        // add cumulative need effect
                        p.setModifier(p.getRelativeNeed() * 1.5 + (0.1 * p.getModifier()));
                    }
                }
            }
            // reset modifier if agent has successfully acquired a sufficient amount of the good
            if (a.getInventory().get(agentConsumption.getKey()) >= 1){
                for (Priority p : a.getPriorities()){
                    if (p.getGood().equals(agentConsumption.getKey())){
                        p.setModifier(1.0);
                    }
                }
            }

        }
    }
    // apply Agent consumption to the Market
    public static void marketConsume (Market m){
        for (Agent a : m.getAgents()){
            agentConsume(a, m);
        }
    }

    // make purchasing decision (this is the hardest part)
    // splitting into two functions: one which updates agent priorities,
    // second which makes actual purchasing decision

    public static void agentPriorities (Agent a, Market m){
        // calculate current relative demand based on elasticity
        for (Priority p : a.getPriorities()){
            // change demand elasticity based on sum of remembered unmet consumption
            double totalUnmetNeed = 0;
            for (UnmetConsumption u : a.getConsumption().get(p.getGood()).getUnmetNeeds()){
                totalUnmetNeed += u.getMissingQuantity();
            }
            // set need ratio at 0.05 * (total unmet need / per tick consumption)
            double unmetNeedRatio = totalUnmetNeed / a.getConsumption().get(p.getGood()).getTickConsumption();
            // y = -1 * (1 / unmetNeedRatio * original elasticity inverse)
            // (sets decay with y intercept at original elasticity)
            p.setPriceElasticity(-1 * (1 / ((0.1 * unmetNeedRatio) +
                    (Math.pow(Math.abs(p.getOriginalPriceElasticity()), -1)))));

            // get market values (may get market average here later)
            double currentMarketCost = 0;
            double currentEquilibriumCost = 0;
            for (Price c : m.getPrices()){
                if (p.getGood().equals(c.getGood())){
                    currentMarketCost = c.getCost();
                    currentEquilibriumCost = c.getEquilibriumCost();
                    break;
                }
            }
            // with market values in hand, make elasticity calculation

            // establish cost difference: positive means market is overcharging, negative undercharging
            // relativeCostDifference is in percent
            double relativeCostDifference = ((currentMarketCost - currentEquilibriumCost)/currentEquilibriumCost) * 100;
            // combine with elasticity, set relative need
            // get consumption
            double consumedQuantity = 0;
            for (Map.Entry<String, Consumption> c : a.getConsumption().entrySet()){
                if (c.getKey().equals(p.getGood())){
                    consumedQuantity = c.getValue().getTickConsumption();
                    break;
                }
            }
            // set demand curve, maybe actually working this time
            // get price induced demand reduction/increase
            // negative * negative = positive; positive * negative = negative
            double priceElasticityOfDemand = relativeCostDifference * p.getPriceElasticity();

            // add decreasing marginal utility
            double amountInInventory = a.getInventory().get(p.getGood());
            double decreasingMarginalUtility = 1;
            if (amountInInventory > (5 * consumedQuantity)){
                decreasingMarginalUtility = (((amountInInventory - (5 * consumedQuantity))
                        / (5 * consumedQuantity)) * -100);
            }

            p.setRelativeNeed((consumedQuantity * 100) * (1 + (priceElasticityOfDemand / 100))
                                                        * (1 + (decreasingMarginalUtility / 100)));

            // set final weight
            // adding modifier prevents price aversion from overwhelming need to buy something
            p.setWeight((p.getBaseWeight() * p.getRelativeNeed()) + p.getModifier());

            // set negative weight to 0
            if (p.getWeight() < 0){
                p.setWeight(0);
            }

            // if market inventory greater than 5 times current production of each good, reduce price of the good
            // if agent tries to buy from market but can't, price goes up, relative to number of agents in the market
        }
    }
    public static void marketPriorities (Market m){
        for (Agent a : m.getAgents()){
            agentPriorities(a, m);
        }
    }

    public static void agentPurchase (Agent a, Market m){
        boolean notPurchased = true;
        double holdMoneySatisfaction = 0.5;

        ArrayList<String> goods = new ArrayList<String>();
        ArrayList<Integer> satisfactions = new ArrayList<Integer>();
        for (Priority p : a.getPriorities()) {
            goods.add(p.getGood());
            // get weights, multiply them by 100 to extend relevance out to the hundredths place,
            // cast them to an integer
            satisfactions.add((int) p.getWeight());
        }
        // start loop to pick a good to purchase
        // Only and always purchases 1 unit of a good!
        while (notPurchased) {
            // if Agent is too poor to buy anything, purchase nothing
            if (goods.size() == 0){
                break;
            }
            // make choice
            String chosenGood = randomWeightedPick(goods, satisfactions);
            // look up Good price
            double chosenGoodPrice = 0;
            for (Price c : m.getPrices()) {
                if (c.getGood().equals(chosenGood)) {
                    chosenGoodPrice = c.getCost();
                    break;
                }
            }
            // See if Agent can't afford to buy its chosen good
            if (a.getMoney() < chosenGoodPrice) {
                // find index of good
                int index = 0;
                for (int i = 0; i < goods.size(); i++) {
                    if (goods.get(i).equals(chosenGood)) {
                        index = i;
                    }
                }
                // remove good from goods and satisfactions list
                goods.remove(index);
                satisfactions.remove(index);

                // since market can get caught in a situation where agents can be too poor to buy a good they
                // really need, and cannot therefore fail to buy it and reduce satisfaction, create small
                // satisfaction decrease if agent cannot afford to buy a good
                // diminish production satisfaction of other goods
                // find job title for the good
                String jobTitle = "";
                for (JobOutput j : m.getJobOutputs()){
                    if (j.getGood().equals(chosenGood)){
                        jobTitle = j.getJob();
                        break;
                    }
                }
                for (Agent agents : m.getAgents()){
                    if (!a.getProfession().getJob().equals(jobTitle)){
                        a.setSatisfaction(a.getSatisfaction() - 0.1);
                    }
                }

                continue;
            }
            // if it can afford to buy its chosen good, see if the market doesn't have any to sell
            double availableQuantity = m.getInventory().get(chosenGood);

            if (availableQuantity < 1) {
                // find index of good
                int index = 0;
                for (int i = 0; i < goods.size(); i++) {
                    if (goods.get(i).equals(chosenGood)) {
                        index = i;
                    }
                }
                // remove good from goods and satisfactions list
                goods.remove(index);
                satisfactions.remove(index);

                // diminish production satisfaction of other goods
                // find job title for the good
                String jobTitle = "";
                for (JobOutput j : m.getJobOutputs()){
                    if (j.getGood().equals(chosenGood)){
                        jobTitle = j.getJob();
                        break;
                    }
                }
                for (Agent agents : m.getAgents()){
                    if (!a.getProfession().getJob().equals(jobTitle)){
                        a.setSatisfaction(a.getSatisfaction() - 1);
                    }
                }


                // determine weight of the agent

                continue;
            }
            // check if the gained satisfaction is not above the base threshold of keeping the money
            // find index of good
            int index = 0;
            for (int i = 0; i < goods.size(); i++){
                if (goods.get(i).equals(chosenGood)) {
                    index = i;
                    break;
                }
            }
            if (satisfactions.get(index) < holdMoneySatisfaction){
                goods.remove(index);
                satisfactions.remove(index);
                continue;
            }
            // finally, if the Agent can afford a good, the market can sell it, and it would obtain significant
            // value from the purchase, complete the transaction
            // deduct from Agent's money:
            a.setMoney(a.getMoney() - chosenGoodPrice);
            m.setMoney(m.getMoney() + chosenGoodPrice);
            // remove good from Market's inventory:
            // can change purchase amount later
            double purchaseAmount = 1;
            m.getInventory().put(chosenGood, m.getInventory().get(chosenGood) - purchaseAmount);

            // add good to Agent's inventory
            a.getInventory().put(chosenGood, a.getInventory().get(chosenGood) + purchaseAmount);

            // To Do : Fix Pricing!!!
            // Problem: Agent can sell and not buy
            // Fix method: remembering unmet consumption needs

            notPurchased = false;
            break;
            }
        }

    public static void marketPurchase (Market m){
        for (Agent a : m.getAgents()){
            agentPurchase(a, m);
        }
    }


    public static void marketPrices (Market market){
        // given a Market, calculate the Supply and Demand equilibrium for each good, then
        // use this to set the prices of each good

        // use relatively inelastic, this function is to calculate short run changes, long-term changes
        // are handled by reassigning agent professions

        // Formula for price elasticity of demand: Q = (PriceElasticity * P) + (10 * GoodConsumption)
        // Conveniently, all of the above are already available.

        // Formula for price elasticity of supply: Q = (PriceElasticity * P) - (Production * GoodMinimum)
        // Good minimum is the minimum percent of production that an Agent must produce given its profession,
        // can possibly set to 0, will do so temporarily

        // calculate equilibrium price

        for (Price p : market.getPrices()){
            double sumDemandIntercept = 0;
            double demandSum = 0;
            double sumSupplyIntercept = 0;
            double supplySum = 0;
            double numOfProducers = 1;

            // determine profession of the good:
            String jobType = "";
            for (JobOutput j : market.getJobOutputs()){
                if (j.getGood().equals(p.getGood())){
                    jobType = j.getJob();
                }
            }

            // get each agent's production and demand curves
            for (Agent a : market.getAgents()){
                // add to demand elasticities
                for (Priority r : a.getPriorities()){
                    if (r.getGood().equals(p.getGood())){
                        demandSum = demandSum + r.getPriceElasticity();
                    }
                }
                // add demand intercept to sum
                for (Map.Entry<String, Consumption> c : a.getConsumption().entrySet()) {
                    if (c.getKey().equals(p.getGood())) {
                        sumDemandIntercept += (c.getValue().getTickConsumption() * 10);

                        // also add sum of agent's unmet needs
                        double sumUnmetNeeds = 0;
                        for (UnmetConsumption u : c.getValue().getUnmetNeeds()){
                            sumUnmetNeeds += u.getMissingQuantity();
                        }
                        sumDemandIntercept += sumUnmetNeeds;

                        break;
                    }
                }

                supplySum = supplySum + a.getProfession().getPriceElasticityOfSupply();
                // good minimum not dealt with, all production has 0 minimum across all Agents

                // conditionally add to number of producers
                if (a.getProfession().getJob().equals(jobType)){
                    numOfProducers++;
                }
            }
            // reset numOfProducers if there are Producers (starts off as 1 instead of 0 to prevent div/0 errors)
            if (numOfProducers > 1){
                numOfProducers = numOfProducers - 1;
            }

            // calculate average supply and demand elasticities

            // System cannot handle Agents producing anything other than 1 of a good, production needs to be multiplied
            // by price elasticity of supply before going into below equation

            // assume equilibrium quantity, solve for P
            // (supplySum * P) + sumSupplyIntercept = (demandSum * P) + sumDemandIntercept
            // (supplySum * P) - (demandSum * P) + sumSupplyIntercept = sumDemandIntercept
            // (supplySum * P) - (demandSum * P) = sumDemandIntercept - sumSupplyIntercept
            // (supplySum - demandSum) * P = sumDemandIntercept - sumSupplyIntercept
            // P = (sumDemandIntercept - sumSupplyIntercept) / (supplySum - demandSum)

            // calculate intercept price
            // double goodPrice = (sumDemandIntercept - numOfProducers) / (0 - demandSum);
            double goodPrice = (sumDemandIntercept - sumSupplyIntercept) / (supplySum - demandSum);

            p.setEquilibriumCost(goodPrice * p.getOriginalCost());

            // calculate market quantity
            // now, given P, calculate Q
            // System.out.println(supplySum);
            double goodQuantity = (demandSum * goodPrice) + sumDemandIntercept;
            // System.out.println(goodQuantity);
        }
    }

    public static void marketProductionSatisfaction (Market market){
        // given a market, calculate cumulative consumption and production of each good, use this to determine whether
        // a good is over or under produced, then affect agent satisfaction accordingly.

        // part 1: calculate cumulative consumption and production
        HashMap<String, Double> cumulativeConsumption = new HashMap<>();
        HashMap<String, Double> cumulativeProduction = new HashMap<>();

        for (Agent a : market.getAgents()){
            // get agent consumptions, store in consumption hash map
            for (Map.Entry<String, Consumption> agentConsumption : a.getConsumption().entrySet()){
                if (!cumulativeConsumption.containsKey(agentConsumption.getKey())){
                    cumulativeConsumption.put(agentConsumption.getKey(),
                            agentConsumption.getValue().getTickConsumption());
                }
                else {
                    String key = agentConsumption.getKey();
                    cumulativeConsumption.put(key, cumulativeConsumption.get(key) +
                            agentConsumption.getValue().getTickConsumption());
                }
            }
            // get agent production, store in production hash map
            String agentJob = a.getProfession().getJob();
            String agentGoodProduced = "";
            // * NOTE: the below line will not work if Agent production calculations are changed *
            double agentQuantityProduced = a.getProfession().getSkillLevel();
            // System.out.println("Agent Quantity Produced: " + agentQuantityProduced);
            for (JobOutput j : market.getJobOutputs()){
                if (j.getJob().equals(agentJob)){
                    agentGoodProduced = j.getGood();
                }
            }
            if (!cumulativeProduction.containsKey(agentGoodProduced)){
                cumulativeProduction.put(agentGoodProduced, agentQuantityProduced);
            }
            else {
                cumulativeProduction.put(agentGoodProduced,
                        cumulativeProduction.get(agentGoodProduced) + agentQuantityProduced);
            }
        }
        market.setMarketConsumption(cumulativeConsumption);


        // function can break if there is not an agent producing a good that is being consumed. Fix this
        // by adding a zero production for all consumed goods which are not being produced
        for (Map.Entry<String, Double> consumedGood : cumulativeConsumption.entrySet()){
            if (!cumulativeProduction.containsKey(consumedGood.getKey())){
                cumulativeProduction.put(consumedGood.getKey(), 0.0);
            }
        }
        market.setMarketProduction(cumulativeProduction);

        // part 2: with cumulative consumption and production and consumption in hand, calculate difference:
        HashMap<String, Double> productionDifference = new HashMap<>();
        for (Map.Entry<String, Double> consumption : cumulativeConsumption.entrySet()){
            // get consumption value
            double amountConsumed = consumption.getValue();
            // get produced value
            double amountProduced = cumulativeProduction.get(consumption.getKey());
            productionDifference.put(consumption.getKey(), amountProduced - amountConsumed);
        }
        // now set market consumption, production, and production difference fields for market

        market.setProductionDifference(productionDifference);

        // part 3: given production differences, affect satisfaction of agents accordingly
        for (Map.Entry<String, Double> difference : productionDifference.entrySet()){
            // if a good is under produced, slightly reduce the production satisfaction of agents producing
            // every other good, reflecting that agents in the market in general have an incentive to switch
            // into producing this good
            if (difference.getValue() < 0){
                // determine shorted profession
                String shortedProfession = "";
                for (JobOutput o : market.getJobOutputs()){
                    if (o.getGood().equals(difference.getKey())){
                        shortedProfession = o.getJob();
                        break;
                    }
                }
                // loop through all agents, if they are not in the shorted profession, reduce their satisfaction by 0.5
                for (Agent agent : market.getAgents()){
                    if (!agent.getProfession().getJob().equals(shortedProfession)){
                        agent.setSatisfaction(agent.getSatisfaction() - 0.5);
                    }
                }
            }
            // if a good is not under produced, it is in equilibrium or overproduced. In this case, check to see
            // if agents producing the good should have their production satisfaction increased as an incentive
            // for being in equilibrium

            // check if market is flooded, otherwise reward the agent
            // get market inventory
            double marketInventory = market.getInventory().get(difference.getKey());

            // market is not flooded if it has less than 10 times the sum of the Agents per tick consumption on hand.
            if (marketInventory < (10 * cumulativeConsumption.get(difference.getKey()))){
                // if the market isn't flooded, reward producers of the good by increasing their satisfaction
                for (Agent goodProducer : market.getAgents()){
                    boolean producesCorrectGood = false;
                    String agentGood = "";
                    String agentJob = goodProducer.getProfession().getJob();
                    for (JobOutput jobs : market.getJobOutputs()){
                        if (jobs.getJob().equals(agentJob)){
                            agentGood = jobs.getGood();
                            break;
                        }
                    }
                    if (difference.getKey().equals(agentGood)){
                        goodProducer.setSatisfaction(goodProducer.getSatisfaction() + 1);
                    }
                }
            }
        }
        // profession switching/long-run supply change moved to separate function



    }

    public static void marketSupply (Market market){
        for (Agent changingCareer : market.getAgents()){
            // * square root of absolute value of satisfaction
            // check if the agent is unhappy about their production;
            if (changingCareer.getSatisfaction() < 0){
                // if so, get percent chance to switch
                // derived from square root of absolute value of satisfaction
                double baseChance = Math.sqrt(Math.abs(changingCareer.getSatisfaction()));
                // set threshold: if Agent does not have at least 25 unhappiness, no chance of switching
                if (baseChance < 5){
                    baseChance = 0;
                }
                // generate random number, pair with base chance, have agent switch professions if true
                // should be 100, offset by base 1/100 chance, i.e. 10000
                if ((Math.random() * 10000) < baseChance){
                    // Agent attempts to switch into a new profession
                    // make agent prioritise underutilized profession, make random weighted choice based on
                    // the size of the production deficit
                    ArrayList<String> goodChoices = new ArrayList<>();
                    ArrayList<Integer> goodWeights = new ArrayList<>();
                    for (Map.Entry<String, Double> productionDiff : market.getProductionDifference().entrySet()){
                        if (productionDiff.getValue() < 0) {
                            goodChoices.add(productionDiff.getKey());
                            goodWeights.add((int) Math.abs((productionDiff.getValue() * 100)));
                        }
                    }

                    // ensure there is a profession in deficit
                    if (goodChoices.size() > 0){
                        String professionGoodChoice = randomWeightedPick(goodChoices, goodWeights);
                        // look up profession
                        String newAgentJob = "";
                        for (JobOutput newJobPossibilities : market.getJobOutputs()){
                            if (newJobPossibilities.getGood().equals(professionGoodChoice)){
                                newAgentJob = newJobPossibilities.getJob();
                            }
                        }
                        // set new agent profession
                        // get price elasticity of supply
                        double priceElasticityOfSupply = 0;
                        for (MarketInfo marketInfo : market.getMarketProfile()){
                            if (marketInfo.getGood().equals(professionGoodChoice)){
                                priceElasticityOfSupply = marketInfo.getPriceElasticitySupply();
                            }
                        }

                        changingCareer.setProfession((new Profession(newAgentJob, 1.0,
                                1.0, priceElasticityOfSupply)));
                        // reset agent satisfaction
                        changingCareer.setSatisfaction(0.0);

                    }

                }
            }
        }
    }

    // print jobs
    public static void printJobs (Market market){
        HashMap<String, Integer> jobsTotal = new HashMap<String, Integer>();
        for (Agent a : market.getAgents()){
            if (!jobsTotal.containsKey(a.getProfession().getJob())){
                jobsTotal.put(a.getProfession().getJob(), 1);
            }
            else {
                String key = a.getProfession().getJob();
                jobsTotal.put(key, jobsTotal.get(key) + 1);
            }
        }
        System.out.println(jobsTotal);
    }

    public static void printMoney (Market market){
        double totalMoney = market.getMoney();
        for (Agent a : market.getAgents()){
            totalMoney = totalMoney + a.getMoney();
        }
        System.out.println(totalMoney);
    }

    // master controller function
    public static void runMarket (Market market, int counter) throws InterruptedException {
        marketProductionSatisfaction(market);
        marketProduce(market);
        marketConsume(market);
        marketPriorities(market);
        marketPurchase(market);
        marketPrices(market);
        marketSupply(market);

        // make sure prices don't go negative:
        for (Price c : market.getPrices()){
            if (c.getCost() <= 0){
                // System.out.println("Price went below 0!!!");
                c.setCost(c.getCost() + 0.2);
            }

            // new temporary: set cost to equilibrium cost every tick
            c.setCost(c.getEquilibriumCost());
        }

    }


    public static ArrayList<Agent> makeAgents(ArrayList<MarketInfo> marketProfile, int numberOfAgents){
        // given information about the characteristics of a market and a number of agents, produce a list of agents
        // for that market
        ArrayList<Agent> agents = new ArrayList<Agent>();
        int agentID = 1;

        while (agentID <= numberOfAgents){
            HashMap<String, Consumption> agentConsumption = new HashMap<String, Consumption>();
            HashMap<String, Double> agentInventory = new HashMap<String, Double>();
            ArrayList<Priority> agentPriorities = new ArrayList<Priority>();

            ArrayList<String> professionChoices = new ArrayList<String>();
            ArrayList<Integer> professionWeights = new ArrayList<Integer>();

            Random random = new Random();
            // add good consumptions, inventory, and priorities
            for (MarketInfo marketInfo : marketProfile){
                // put base consumption with variance (standard deviation 7%)
                // double consumptionVariance = 1 + random.nextGaussian(0.0,0.07);
                double consumptionVariance = 1 + (0.07 * random.nextGaussian());
                agentConsumption.put(marketInfo.getGood(), new Consumption(marketInfo.getBaseConsumption()
                        * consumptionVariance, new ArrayList<UnmetConsumption>()));
                // put 5 * base good consumption of good in agent's inventory
                agentInventory.put(marketInfo.getGood(), 5 * marketInfo.getBaseConsumption());
                // add good priority to agent with variance (standard deviation 2.5%)
                // double demandElasticityVariance = 1 + random.nextGaussian(0.0,0.025);
                double demandElasticityVariance = 1 + (0.025 * random.nextGaussian());
                double demandElasticity = marketInfo.getPriceElasticityDemand() * demandElasticityVariance;
                agentPriorities.add(new Priority(marketInfo.getGood(), marketInfo.getPriorityBaseWeight(),
                        1, 1, demandElasticity, demandElasticity,  1));
                professionChoices.add(marketInfo.getGood());
                professionWeights.add((int) (marketInfo.getJobChance() * 100));
            }
            // pick profession
            String professionPick = randomWeightedPick(professionChoices, professionWeights);
            Profession agentProfession = new Profession("", 1, 1, 1);
            double startingMoney = 0;
            for (MarketInfo marketInfoRound2 : marketProfile) {
                if (marketInfoRound2.getGood().equals(professionPick)){
                    agentProfession.setJob(marketInfoRound2.getJobName());
                    //double supplyElasticityVariance = 1 + random.nextGaussian(0.0,0.025);
                    double supplyElasticityVariance = 1 + (0.025 * random.nextGaussian());
                    agentProfession.setPriceElasticityOfSupply(marketInfoRound2.getPriceElasticitySupply()
                            * supplyElasticityVariance);
                    startingMoney = marketInfoRound2.getGoodCost();
                }
            }
            agents.add(new Agent(Integer.toString(agentID), agentInventory, agentPriorities, agentConsumption,
                    agentProfession, startingMoney, 0));
            agentID++;
        }

        return agents;
    }

    public static Market makeMarket(ArrayList<MarketInfo> marketProfile, ArrayList<Agent> marketAgents){
        HashMap<String, Double> marketInventory = new HashMap<String, Double>();
        ArrayList<JobOutput> marketJobs = new ArrayList<JobOutput>();
        ArrayList<Price> marketPrices = new ArrayList<Price>();

        for (MarketInfo marketInfo : marketProfile){
            marketInventory.put(marketInfo.getGood(), (double) marketAgents.size());
            marketJobs.add(new JobOutput(marketInfo.getJobName(), marketInfo.getGood()));
            marketPrices.add(new Price(marketInfo.getGood(), marketInfo.getGoodCost(),
                    marketInfo.getGoodCost(), marketInfo.getGoodCost()));
        }

        // empty market production, consumption, and production difference fields, will be reset by the first call
        // to marketProductionSatisfaction
        HashMap<String, Double> cumulativeMarketConsumption = new HashMap<String, Double>();
        HashMap<String, Double> cumulativeMarketProduction = new HashMap<String, Double>();
        HashMap<String, Double> marketProductionDifference = new HashMap<String, Double>();
        return new Market(marketAgents, marketInventory, marketJobs, marketPrices, cumulativeMarketConsumption,
                cumulativeMarketProduction, marketProductionDifference, marketProfile,
                1000 * marketAgents.size());

    }

    public static void main(String[] args) throws InterruptedException {
        // Define Market Profile



        // System.out.println(marketAgents);
        // System.out.println(market);

        // run market
        // System.out.println(a1.getInventory());
        int counterVar = 0;
        long startTime = System.currentTimeMillis();



    }
}

