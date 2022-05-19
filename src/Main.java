import java.util.*;
import java.lang.Thread;

public class Main {
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
    determined outside the system: namely the consumption profile of an Agent and the BaseCost of each Good.

    Data Definitions:
    Professor Shan would be proud: we start with data definitions.

    The base unit of this market simulation is the Agent. It is important to discuss what an Agent is.
    An Agent is a Class, obviously, this is Java. At the outset, I anticipate an Agent will need the following
    attributes to function properly in the Market:
    - ID
    - Inventory
    - Priorities
    - Consumptions
    - Profession
    - Money
    - Satisfaction
    Aside from the ID, which is largely there for administrative purposes, all of these attributes play off one another
    to generate economic behaviors.
    In concept, I propose that in each tick of the market, each agent takes the following actions:
    based on its Profession, it produces a certain number of Goods, which are then sold into the Market (which has
    its own Inventory). Based on the current Price of the Good, the Agent then receives an Amount of Money for its
    Goods (goods may be services in this context, "economic output" somehow seems like a worse name, even if it is
    a bit more descriptive.)  Based on the amount of Money it has, and its Priorities, the Agent calculates the purchase
    (or lack thereof), which would increase its Satisfaction the most. This calculation is deceptively complex.
    Priorities, which would be refreshed prior to the calculation, would weigh the per tick Consumptions of the Agent,
    the per-unit Satisfaction gain from each Good, against the available Money resources to determine what action
    would increase its Satisfaction the most. This allows the opportunity for scarcity to affect demand: an Agent
    which is running low on something it consumes will derive more Satisfaction from making sure its consumption
    of that Good can continue, than of purchasing some normal good. Since scarcity changes the per-unit Weight in the
    Priority of the Good, it will be willing to pay more. The elasticity of this response to scarcity is an important,
    but advanced feature, which, being unique to each Agent must also be stored in the Priorities of each Agent for
    appropriate calculation. With all of this determined, the Agent makes a weighted random choice. I feel this step is
    very essential: in real life people do not always make the most rational economic choice at all times, doing
    so in simulation would trend the simulation towards reflecting a perfect equilibrium which arises from pure
    economic rationality. As this is unrealistic, the Agent will use the result of the calculations to weight its
    possible actions, generally picking the most rational choice, but not always. Once the agent makes its choice,
    it then buys the Good from the Market, removing a unit of that Good from the Market's Inventory, and deducting
    an appropriate amount of Money from the Agent's Inventory.

    I do not know at outset whether the Market needs to keep track of this money: the Market is already closed in
    terms of goods and services and there is no mechanism which would decrease or increase the money supply. Testing
    will have to reveal whether money disappears and prices skyrocket under this interpretation.
    I also believe currently that for performance's sake the prices should only be recalculated once per tick, meaning
    each agent interacts with the market in each tick as it was at the start of the tick. If there is 1000 Agents, the
    999th Agent acts as though the previous 998 Agents have not changed the character of the Market. I will need to see
    how intensive it is to re-weight the prices on the market, as recalculating more frequently might potentially be
    more realistic.

    Since none of the types I discussed as being associated with an Agent are built-in (at least at first glance), we
    need to drill down further.

    An ID is a String. Plain and simple.

    An 'Inventory' is another class. It consists of an ArrayList of Items.
    An Item isn't built-in either. It consists of a Good and a Quantity.
    A Good is a String from a specific, global ArrayList of Strings, e.g. "Fish" or "Lumber" or whatever.
    A Quantity is just a Number. Based on how I want to do this, I do not think it has to be just an Integer, at least
    at the Market level. Currently, I feel as though it is important for manageability for an Agent to only be able
    to buy one value of a Good, preferably one, but the production model I have in mind would produce a wide variety
    of values. I will have to see how that shakes out.

    A 'Priorities' is similar to an Inventory, containing an ArrayList of Priority.
    A Priority is similar to an Item, also containing a Good, but it contains several other numbers:
    a Weight is the most relevant number, used in the final calculation, reflecting the per-unit Satisfaction
    the Agent would derive from that good. More numbers are needed to calculate this Weight.
    A BaseWeight is a Number, the base, unmodified per-unit Satisfaction derived from a particular Good.
    An Elasticity is also a Number, reflecting how an Agent will respond to change in the prices for that Good.
    i.e. an Agent would be relatively inelastic to price changes in food, paying whatever it takes, but relatively
    price elastic for a luxury item like fancy clothes.
    A RelativeNeed is another Number. Elasticity should be a constant value for a given Agent, reflecting their
    consumption habits and the nature of a Good. However, I also want personal scarcity to impact Weight: i.e. running
    out of a Good should motivate the Agent to purchase said Good. i.e. if the Agent was running low on food, it would
    be far more heavily motivated to buy food than it would be under normal circumstances, regardless of the current
    price of food (that is covered by regular elasticity)
    A Modifier is yet another Number, a placeholder for now. I anticipate needing it later to handle events at the
    least, although I suspect there is another aspect of demand calculation I am forgetting, and I want to have
    a slot for it when I figure out what it is.
    An Agent will also need to have a Satisfaction associated with holding onto Money, otherwise it will never be
    able to buy any Good which is more expensive than the amount of Money it makes in a single tick. This may
    need to be influenced by its desire to buy an expensive good, and may also exist as a minimum action, i.e. if no
    purchase action will increase Satisfaction by a certain amount the Agent does nothing.
    {Side note: with each Agent limited to one purchase per tick, the function to determine the optimal choice only
    needs to pick the highest Weight in Priorities. Changing this to allow multiple or an unlimited number of actions
    would involve opening up the Agent's choices, allowing it to buy fractional amounts of as many goods as it can
    afford. Doing this would require putting all the Priorities together, using linear algebra and calculus/
    multivariable calculus to solve for the maximum of a function with as many variables as there are goods.}

    A 'Consumptions' is also like an Inventory, it is an ArrayList of Items.
    While not representing the same thing (the quantity here represent the amount of goods used per tick, not
    the amount possessed), fundamentally the same data structure works for both use cases.

    A 'Professions' is much different from the previous three.
    A Professions consists of a Job and a SkillLevel.
    A Job is a String, selected from a global ArrayList of Strings, like "Fisherman" or "Lumberjack" or whatever.
    each Job corresponds to one of those globally defined professions. I am not sure about the performance implications
    of this, but I do not want to store the output good individually, as I would like Agents to be able to switch
    professions easily (this is how I intend for supply to change in markets with supply levels below equilibrium:
    price goes up and Agents, based on their current Profession [Job and SkillLevel], may choose to switch to working
    in the in demand Job at the lower SkillLevel, calculating their current and expected income, then if the condition
    is met, roll to see if they actually change. Willingness to change professions will need to be dialed in through
    testing.)
    A SkillLevel is a Number. Useful in the context above, it goes up over time as an Agent adjusts to working
    in their Job. i.e. a master fisherman is going to catch/produce more fish than a total novice. This factor
    determines how much of its specified Good an Agent produces in a given tick, and serves as a disincentive to
    switching Jobs, beyond the global willingness to change modifier.

    Finally, we get to two simple attributes:
    A 'Money' is a Number, literally just the amount of money currently in the Agent's possession. While a simple
    attribute, it affects many other things.
    A 'Satisfaction' is a Number. It represents the current Satisfaction of an Agent. I might use this in aggregate to
    determine the move of a market towards or away from equilibrium over time. Its primary use, however, is as
    discussed above: as the metric by which an Agent makes its purchasing decisions.

    That defines an Agent. However, Agents are not the only components of a Market. A Market is defined as follows:
    A Market is a Class containing:
    - An ArrayList of Agents
    - An Inventory
    - An ArrayList of JobOutputs
    - An ArrayList of Prices

    The ArrayList of Agents is fairly self-explanatory: A Market is just a thing to facilitate their interaction.
    The Inventory is a bit more interesting. The Market has an inventory just like each of its Agents, representing
    the amount of goods available for sale at any given time. Since all Agent interactions are with the Market, not
    each other, the Market needs to be able to store Goods in the time between when each Agent produces according
    to its Profession and consumes to increase its Satisfaction.
    JobOutput has not yet been defined. A JobOutput is a simple Class containing a Job and a Good. The ArrayList can
    be queried by Agents to determine what Good they are supposed to produce based on their Job.
    Finally, the ArrayList of Prices, the global Price List, essentially, is the last critical component. It is a list
    of Prices, a class which contains several attributes. Good has been discussed before and is self-explanatory.
    It also contains Cost, which is a Number. This represents the current market price of any particular Good.
    Finally, it contains a equilibriumCost, which is the equilibrium/"correct" price of a Good. I have not yet
    fleshed out the details, but intend to add special behaviors if the Cost of a Good is a certain factor above
    or below its equilibriumCost, perhaps affecting the Modifier attribute of Agents' Priorities.

    Note: also added an unchanging list of original prices through a final variable OriginalCost in a given price.
     This will enable events to change the equilibriumCost of a good, shifting equilibrium rather than simply disturbing
    the market. Without the OriginalCost, there is no mechanism for the baseCost to be reset to its original value.
    This enables direct manipulation of prices. The other method is to have off-map high-demand Agent(s), an event increasing
    or decreasing demand would involve changing the demand of off-map Agent(s) and letting the market equilibrium change
    accordingly. Tests will be needed to see which approach works better.

    This covers the proposed data definitions for the Economic Agents and their Market. Next up is implementing the
    definitions, then setting up functions to facilitate the interactions.
    In general, economics is a complicated thing. I do not have an economics PhD, and to my knowledge even they spend
    their whole careers writing programs trying their best to simulate market forces. There are inevitably going to
    be a thousand things I am not thinking of right now which will cause this simulated Market to crash the moment
    it opens, but hopefully I have enough pieces in the right places to get started.
     */

    // randomizer functions, designed in a previous project:
    static String randomPick(ArrayList<String> lst){
        // given a list of strings, return a random choice from the list
        // does not modify the list or prevent duplicate picks
        int listLength = lst.size();
        int choiceNumber = (int) (Math.random() * listLength);
        return lst.get(choiceNumber);
    }

    static String randomWeightedPick(ArrayList<String> choices, ArrayList<Integer> weights){
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
    static void agentProduce (Agent agent, Market market){
        // first determine what goods are going to be produced
        String goodType = "";
        for (JobOutput j : market.getJobOutputs()) {
            if (j.getJob().equals(agent.getProfession().getJob())) {
                goodType = j.getGood();
                break;
            }
        }
        // then have the Agent produce the Good (literally produces the amount of their skill level)
        Item agentProduction = new Item (goodType, agent.getProfession().getSkillLevel());
        // compensate the Agent first (don't want agent's production to affect market price before the market has it)
        // find market price
        double currentPrice = 0;
        for (Price p : market.getPrices()){
            if (p.getGood().equals(agentProduction.getGood())){
                currentPrice = p.getCost();
                break;
            }
        }
        // pay Agent if Market has money
        if (market.getMoney() > ((agentProduction.getQuantity()) * currentPrice)){
            market.setMoney(market.getMoney() - ((agentProduction.getQuantity()) * currentPrice));
            agent.setMoney(agent.getMoney() + ((agentProduction.getQuantity()) * currentPrice));
            // deliver Good to Market
            for (Item i : market.getInventory()){
                if (i.getGood().equals(agentProduction.getGood())){
                    i.setQuantity(i.getQuantity() + agentProduction.getQuantity());
                }
            }
        }
        // Note: Agent does not produce if Market cannot pay!

    }

    // apply Agent production to the Market
    static void marketProduce (Market m){
        for (Agent a : m.getAgents()){
            agentProduce(a, m);
        }
    }

    // have the Agents consume goods according to their consumption profile.
    // *this interpretation REQUIRES that inventory and consumption have the same goods in the same order*
    // (^ helps performance)
    // an Agent running out of a good significantly increases its relative need for it, having that good again resets
    // the relative need

    static void agentConsume (Agent a, Market m){
        for (int i = 0; (i < a.getConsumption().size()); i++ ){
            Item newItem = new Item(a.getInventory().get(i).getGood(),
                    (a.getInventory().get(i).getQuantity() - a.getConsumption().get(i).getQuantity()));
            // now protect against negatives and set need modifier
            if (newItem.getQuantity() <= 0){
                newItem.setQuantity(0);
                for (Priority p : a.getPriorities()){
                    if (p.getGood().equals(newItem.getGood())){
                        // add cumulative need effect
                        p.setModifier(p.getRelativeNeed() * 1.5 + (0.1 * p.getModifier()));
                    }
                }
            }
            a.getInventory().set(i, newItem);
            // reset modifier if agent has successfully acquired a sufficient amount of the good
            if (a.getInventory().get(i).getQuantity() >= 1){
                for (Priority p : a.getPriorities()){
                    if (p.getGood().equals(newItem.getGood())){
                        p.setModifier(1.0);
                    }
                }
            }

        }
    }
    // apply Agent consumption to the Market
    static void marketConsume (Market m){
        for (Agent a : m.getAgents()){
            agentConsume(a, m);
        }
    }

    // make purchasing decision (this is the hardest part)
    // splitting into two functions: one which updates agent priorities,
    // second which makes actual purchasing decision

    static void agentPriorities (Agent a, Market m){
        // thanks to inclusion of elasticity field in priorities, this is relatively simple, but subject
        // to outside intervention: the given elasticity value must be ~generally correct~ in order for this to
        // act correctly, as it is not generated from within. Thankfully, there are economic studies which reveal
        // actual real-world price elasticity values, but whether this translates correctly to this model is
        // currently unknown

        // calculate current relative demand based on elasticity
        for (Priority p : a.getPriorities()){
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
            for (Item c : a.getConsumption()){
                if (c.getGood().equals(p.getGood())){
                    consumedQuantity = c.getQuantity();
                    break;
                }
            }
            // set demand curve, maybe actually working this time
            // get price induced demand reduction/increase
            // negative * negative = positive; positive * negative = negative
            double priceElasticityOfDemand = relativeCostDifference * p.getPriceElasticity();

            // add decreasing marginal utility
            double amountInInventory = 0;
            for (Item i : a.getInventory()) {
                if (i.getGood().equals(p.getGood())) {
                    amountInInventory = i.getQuantity();
                    break;
                }
            }
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
    static void marketPriorities (Market m){
        for (Agent a : m.getAgents()){
            agentPriorities(a, m);
        }
    }

    static void agentPurchase (Agent a, Market m){
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
                continue;
            }
            // if it can afford to buy its chosen good, see if the market doesn't have any to sell
            double availableQuantity = 0;
            for (Item i : m.getInventory()) {
                if (i.getGood().equals(chosenGood)) {
                    availableQuantity = i.getQuantity();
                }
            }
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
            for (Item i : m.getInventory()){
                // System.out.println("removing from inventory");
                if (i.getGood().equals(chosenGood)){
                    i.setQuantity(i.getQuantity() - 1);
                    break;
                }
            }
            // add good to Agent's inventory
            for (Item t : a.getInventory()){
                if (t.getGood().equals(chosenGood)) {
                    t.setQuantity(t.getQuantity() + 1);
                    break;
                }
            }
            // To Do : Fix Pricing!!!
            // Problem: Agent can sell and not buy
            // This creates money out of thin air and oversupply on the market
            // If we need a perfectly closed system, keep track of buying and selling choices and
            // only reward an Agent with money if it buys.
            // If we want a partially closed system, we need to keep track of how much money the market has,
            // Agents are paid out of the market's money supply
            /*
            // alter price!
            // determine weight of the agent
            double agentImportance = 1.0 / m.getAgents().size();
            // increase price by agentImportance * equilibriumPrice
            for (Price c : m.getPrices())
                if (c.getGood().equals(chosenGood)) {
                    c.setCost(c.getCost() + (Math.abs (agentImportance * 0.1 * c.getOriginalCost())));
                }

            */

            /*
            for (Item marketItem : m.getInventory()){
                // crude version of production
                if (marketItem.getQuantity() > (10 * m.getAgents().size())) {
                    for (Price r : m.getPrices()){
                        if (r.getGood().equals(marketItem.getGood())){
                            r.setCost(0.99 * r.getCost());
                        }
                    }
                }
            }
             */

            notPurchased = false;
            break;
            }
        }

    static void marketPurchase (Market m){
        for (Agent a : m.getAgents()){
            agentPurchase(a, m);
        }
    }


    // print jobs
    static void printJobs (Market market){
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

    static void printMoney (Market market){
        double totalMoney = market.getMoney();
        for (Agent a : market.getAgents()){
            totalMoney = totalMoney + a.getMoney();
        }
        System.out.println(totalMoney);
    }

    static void marketPrices (Market market){
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
                for (Item i : a.getConsumption()){
                    if (i.getGood().equals(p.getGood())){
                        sumDemandIntercept = sumDemandIntercept + (i.getQuantity() * 10);
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

            p.setEquilibriumCost(goodPrice);

            // calculate market quantity
            // now, given P, calculate Q
            // System.out.println(supplySum);
            double goodQuantity = (demandSum * goodPrice) + sumDemandIntercept;
            // System.out.println(goodQuantity);
        }
    }

    static void marketProductionSatisfaction (Market market){
        // given a market, calculate cumulative consumption and production of each good, use this to determine whether
        // a good is over or under produced, then affect agent satisfaction accordingly.

        // part 1: calculate cumulative consumption and production
        HashMap<String, Double> cumulativeConsumption = new HashMap<>();
        HashMap<String, Double> cumulativeProduction = new HashMap<>();

        for (Agent a : market.getAgents()){
            // get agent consumptions, store in consumption hash map
            for (Item i : a.getConsumption()){
                if (!cumulativeConsumption.containsKey(i.getGood())){
                    cumulativeConsumption.put(i.getGood(), i.getQuantity());
                }
                else {
                    String key = i.getGood();
                    cumulativeConsumption.put(key, cumulativeConsumption.get(key) + i.getQuantity());
                }
            }
            // get agent production, store in production hash map
            String agentJob = a.getProfession().getJob();
            String agentGoodProduced = "";
            // * NOTE: the below line will not work if Agent production calculations are changed *
            double agentQuantityProduced = a.getProfession().getSkillLevel();
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

        // part 2: with cumulative consumption and production and consumption in hand, calculate difference:
        HashMap<String, Double> productionDifference = new HashMap<>();
        for (Map.Entry<String, Double> consumption : cumulativeConsumption.entrySet()){
            // get consumption value
            double amountConsumed = consumption.getValue();
            // get produced value
            double amountProduced = cumulativeProduction.get(consumption.getKey());
            productionDifference.put(consumption.getKey(), amountProduced - amountConsumed);
            /*
            if (!productionDifference.containsKey(consumption.getKey())){
                productionDifference.put(consumption.getKey(), amountProduced - amountConsumed);
            }
            else {
                productionDifference.put(consumption.getKey(),
                        productionDifference.get(consumption.getKey()) + amountProduced - amountConsumed);
            }
             */
        }

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
            double marketInventory = 0;
            for (Item marketGood : market.getInventory()){
                if (marketGood.getGood().equals(difference.getKey())){
                    marketInventory = marketGood.getQuantity();
                    break;
                }
            }
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

        // Agent profession switching being implemented here, so that it can access production difference.
        // To separate this into another function, have the market store the cumulative production and consumption
        // hashmaps as a field of the market class.

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
                // should be 100, offset by base 1/250 chance, i.e. 25000
                if ((Math.random() * 25000) < baseChance){
                    // Agent attempts to switch into a new profession
                    // make agent prioritise underutilized profession, make random weighted choice based on
                    // the size of the production deficit
                    ArrayList<String> goodChoices = new ArrayList<>();
                    ArrayList<Integer> goodWeights = new ArrayList<>();
                    for (Map.Entry<String, Double> productionDiff : productionDifference.entrySet()){
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
                        changingCareer.setProfession((new Profession(newAgentJob, 1.0,
                                1.0, 0.7)));
                        // reset agent satisfaction
                        changingCareer.setSatisfaction(0.0);

                    }

                }
            }
        }

    }


    // master controller function
    static void runMarket (Market market, int counter) throws InterruptedException {
        marketProduce(market);
        marketConsume(market);
        marketPriorities(market);
        marketPurchase(market);
        marketPrices(market);
        marketProductionSatisfaction(market);
        // changeSupply(market);

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


    public static void main(String[] args) throws InterruptedException {
        // Create a test Market, to ensure that classes have been created correctly.
        // In principle, I intend to test this system on a Market with 10 Agents and 2 Goods,
        // Fish and Lumber, respectively.

        // Agent Needs:
        // a Priorities default for Fishermen and Lumberjack Agents and a Consumptions default for each type.
        // A Profession and skill level for each (will set SkillLevel to 1 for now)
        // Basic defaults: generation of ID String ("1" - "10"), empty inventory, 10 money to start, 0 satisfaction
        // 8 Fishermen, 2 Lumberjacks to start, equilibrium 7 Fishermen, 3 Lumberjacks

        // Market Needs:
        // compile agents 1-10 into an ArrayList, basic empty default inventory
        // ArrayList of JobTypes: Fisherman -> Fish, Lumberjack -> Lumber
        // ArrayList of Prices: (Fish, 1.5, 2, 2); (Lumber 3.5, 3, 3) <- Fish oversupplied, in testing try to get
        //      an agent to switch professions so the price returns to equilibrium.

        ArrayList<Item> inventoryFish = new ArrayList<Item>(List.of (new Item("Fish", 2.0),
                new Item("Lumber", 2.0)));
        ArrayList<Item> inventoryLumber = new ArrayList<Item>(List.of (new Item("Fish", 1.5),
                new Item("Lumber", 3.0)));
        ArrayList<Item> inventoryMarket = new ArrayList<Item>(List.of (new Item("Fish", 10.0),
                new Item("Lumber", 2.0)));
        ArrayList<Item> consumption = new ArrayList<Item>(List.of (new Item("Fish", 0.7),
                new Item("Lumber", 0.3))); //<-- 7:3 consumption rate, should force equilibrium

        // no SkillLevel code implemented, setting all to 1
        Profession fisherman = new Profession("Fisherman", 1.0, 1, 0.7);
        Profession lumberjack = new Profession("Lumberjack", 1.0, 1, 0.7);
        // All Agents have the same priorities
        // https://en.wikipedia.org/wiki/Price_elasticity_of_demand
        // Systematic approach to instantiating a market will have to be implemented in short order.
        ArrayList<Priority> priorities = new ArrayList<Priority>(List.of(
                new Priority("Fish", 1, 1, 1, -0.5, 0.35),
                new Priority("Lumber", 1, 1, 1, -0.7, 0.15)));

        ArrayList<JobOutput> marketJobs = new ArrayList<JobOutput>(List.of(new JobOutput("Fisherman", "Fish"),
                new JobOutput("Lumberjack", "Lumber")));
        ArrayList<Price> marketPrices = new ArrayList<Price>(List.of(
                new Price("Fish", 1.5, 2, 2),
                new Price("Lumber", 3.5, 3, 3)));
        // Define Agents
        // random number of agents
        int numberOfAgents = (int) ((70 * Math.random()) + 5);
        int agentInitializer = 0;
        ArrayList<Agent> agents = new ArrayList<Agent>();
        int agentID = 1;
        while (agentInitializer < numberOfAgents){
            double jobChoice = Math.random();
            // Lumberjack 30% of the time
            if (jobChoice < 0.9){
                agents.add(new Agent(Integer.toString(agentID),
                        new ArrayList<Item>(List.of (new Item("Fish", 1.5),
                                new Item("Lumber", 3.0))),
                        new ArrayList<Priority>(List.of(
                                new Priority("Fish", 1, 1, 1, -0.5, 0.35),
                                new Priority("Lumber", 1, 1, 1, -0.7, 0.15))),
                        new ArrayList<Item>(List.of (new Item("Fish", 0.75),
                                new Item("Lumber", 0.3))),
                        new Profession("Lumberjack", 1.0, 1, 0.7), 0, 0));
            }
            // otherwise Fisherman
            else{
                agents.add(new Agent(Integer.toString(agentID),
                        new ArrayList<Item>(List.of (new Item("Fish", 2.0),
                                new Item("Lumber", 2.0))),
                        new ArrayList<Priority>(List.of(
                                new Priority("Fish", 1, 1, 1, -0.5, 0.35),
                                new Priority("Lumber", 1, 1, 1, -0.7, 0.15))),
                        new ArrayList<Item>(List.of (new Item("Fish", 0.65),
                                new Item("Lumber", 0.3))),
                        new Profession("Fisherman", 1.0, 1, 0.7), 0, 0));
            }
            agentInitializer++;
            agentID++;

        }

        // Lastly, define Market
        //ArrayList<Agent> agents = new ArrayList<Agent>(List.of(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10));
        Market market = new Market(agents, inventoryMarket, marketJobs, marketPrices, 1000 * agents.size());

        // run market
        // System.out.println(a1.getInventory());
        int counterVar = 0;

        while (true){
            runMarket(market, counterVar);
            counterVar++;
            if (counterVar % 10 == 0){
                System.out.println(market.getPrices());
                System.out.println(market.getInventory());
                printJobs(market);
            }
            if (counterVar % 100 == 0) {
                System.out.println(market.getAgents());
            }
            Thread.sleep(50);
        }

    }
}

class ChoiceWeight {
    String choice;
    int weight;

    public ChoiceWeight(String choice, int weight){
        this.choice = choice;
        this.weight = weight;
    }

    public String getChoice(){
        return choice;
    }

    public int getWeight(){
        return weight;
    }

    public String toString() {
        return (this.getChoice() + "-" +
                this.getWeight());
    }
}

// First, define the classes needed for an Agent:
// An Inventory is an ArrayList of Item
class Item {
    // Variable Instantiation
    private String good;
    private double quantity;
    // Constructor
    public Item (String good, double quantity){
        this.good = good;
        this.quantity = quantity;
    }
    // Accessors
    public String getGood(){
        return good;
    }
    public double getQuantity(){
        return quantity;
    }
    // Mutators
    public void setGood(String newGood){
        good = newGood;
    }
    public void setQuantity(double newQuantity){
        quantity = newQuantity;
    }
    // Override of toString to make it useful
    public String toString(){
        return(this.getGood() + ", " + this.getQuantity());
    }
}

// A 'Priorities' is an ArrayList of Priority
class Priority{
    private String good;
    private double baseWeight;
    private double relativeNeed;
    private double modifier;
    private double priceElasticity;
    private double weight;
    public Priority (String good, double baseWeight, double relativeNeed,
                     double modifier, double priceElasticity, double weight){
        this.good = good;
        this.baseWeight = baseWeight;
        this.relativeNeed = relativeNeed;
        this.modifier = modifier;
        this.priceElasticity = priceElasticity;
        this.weight = weight;
    }
    public String getGood(){
        return good;
    }
    public double getBaseWeight(){
        return baseWeight;
    }
    public double getRelativeNeed(){
        return relativeNeed;
    }
    public double getModifier(){
        return modifier;
    }
    public double getPriceElasticity(){
        return priceElasticity;
    }
    public double getWeight(){
        return weight;
    }
    public void setGood(String newGood){
        good = newGood;
    }
    public void setBaseWeight(double newBaseWeight){
        baseWeight = newBaseWeight;
    }
    public void setRelativeNeed(double newRelativeNeed){
        relativeNeed = newRelativeNeed;
    }
    public void setModifier(double newModifier){
        modifier = newModifier;
    }
    public void setPriceElasticity(double newPriceElasticity){
        priceElasticity = newPriceElasticity;
    }
    public void setWeight(double newWeight){
        weight = newWeight;
    }
    public String toString(){
        return ("\n" + this.getGood() + ": " +
                "base weight: " + this.getBaseWeight() + ", " +
                "relative need: " + this.getRelativeNeed() + ", " +
                "modifier: " + this.getModifier() + ", " +
                "price elasticity: " + this.getPriceElasticity() + ", " +
                "final weight: " + this.getWeight() + ".");
    }
}

// A 'Consumptions' is an Inventory

class Profession {
    private String job;
    private double skillLevel;
    private double shortRunProduction;
    private double priceElasticityOfSupply;

    // if deficiency in short run production vs market quantity, permit switch
    // problem: market quantity higher than it is possible for any combination of agents to produce
    // solution: derive production and demand curves from consumption and production /capacity/ of agents

    public Profession (String job, double skillLevel, double shortRunProduction, double priceElasticityOfSupply){
        this.job = job;
        this.skillLevel = skillLevel;
        this.shortRunProduction = shortRunProduction;
        this.priceElasticityOfSupply = priceElasticityOfSupply;
    }
    public String getJob(){
        return job;
    }
    public double getSkillLevel(){
        return skillLevel;
    }
    public double getShortRunProduction(){
        return shortRunProduction;
    }
    public double getPriceElasticityOfSupply(){
        return priceElasticityOfSupply;
    }

    public void setJob(String newJob){
        job = newJob;
    }
    public void setSkillLevel(double newSkillLevel){
        skillLevel = newSkillLevel;
    }
    public void setPriceElasticityOfSupply(double newPriceElasticity){
        priceElasticityOfSupply = newPriceElasticity;
    }
    public void setShortRunProduction(double newProduction){
        shortRunProduction = newProduction;
    }

    public String toString(){
        return(this.getJob() + ", skill level: " +
                this.getSkillLevel());
    }
}

class Agent {
    private String id;
    private ArrayList<Item> inventory;
    private ArrayList<Priority> priorities;
    private ArrayList<Item> consumption;
    private Profession profession;
    private double money;
    private double satisfaction;

    public Agent (String id, ArrayList<Item> inventory, ArrayList<Priority> priorities,
                  ArrayList<Item> consumption, Profession profession, double money,
                  double satisfaction){
        this.id = id;
        this.inventory = inventory;
        this.priorities = priorities;
        this.consumption = consumption;
        this.profession = profession;
        this.money = money;
        this.satisfaction = satisfaction;
    }
    public String getId(){
        return id;
    }
    public ArrayList<Item> getInventory(){
        return inventory;
    }
    public ArrayList<Priority> getPriorities(){
        return priorities;
    }
    public ArrayList<Item> getConsumption(){
        return consumption;
    }
    public Profession getProfession(){
        return profession;
    }
    public double getMoney(){
        return money;
    }
    public double getSatisfaction(){
        return satisfaction;
    }
    public void setId(String newID){
        id = newID;
    }
    public void setInventory(ArrayList<Item> newInventory){
        inventory = newInventory;
    }
    public void setPriorities(ArrayList<Priority> newPriorities){
        priorities = newPriorities;
    }
    public void setConsumption(ArrayList<Item> newConsumption){
        consumption = newConsumption;
    }
    public void setProfession(Profession newProfession){
        profession = newProfession;
    }
    public void setMoney(double newMoney){
        money = newMoney;
    }
    public void setSatisfaction(double newSatisfaction){
        satisfaction = newSatisfaction;
    }
    public String toString(){
        return ("\n\n" + "ID: " + this.getId() + ",\n" +
                "Inventory: " + this.getInventory() + ",\n" +
                "Priorities: " + this.getPriorities() + ",\n" +
                "Consumption: " + this.getConsumption() + ",\n" +
                "Profession: " + this.getProfession() + ",\n" +
                "Money: " + this.getMoney() + ",\n" +
                "Satisfaction: " + this.getSatisfaction() + ".");
    }
}
// Next up is defining the Market, for the Agents to interact.
class JobOutput{
    private String job;
    private String good;
    public JobOutput (String job, String good){
        this.job = job;
        this.good = good;
    }
    public String getJob(){
        return job;
    }
    public String getGood(){
        return good;
    }
    public void setJob(String newJob){
        job = newJob;
    }
    public void setGood(String newGood){
        good = newGood;
    }
    public String toString(){
        return ("\n" + this.getJob() + " -> " + this.getGood());
    }
}

class Price{
    private String good;
    private double cost;
    private double equilibriumCost;
    private final double originalCost;

    public Price (String good, double cost, double equilibriumCost, double originalCost){
        this.good = good;
        this.cost = cost;
        this.equilibriumCost= equilibriumCost;
        this.originalCost = originalCost;
    }

    public String getGood(){
        return good;
    }
    public double getCost(){
        return cost;
    }
    public double getEquilibriumCost(){
        return equilibriumCost;
    }
    public double getOriginalCost(){
        return originalCost;
    }
    public void setGood(String newGood){
        good = newGood;
    }
    public void setCost(double newCost){
        cost = newCost;
    }
    public void setEquilibriumCost(double newEquilibriumCost){
        equilibriumCost = newEquilibriumCost;
    }
    public String toString(){
        return ("\n" + this.getGood() + ", " +
                "Cost: " + this.getCost() + ", " +
                "Equilibrium Cost: " + this.getEquilibriumCost() + ", " +
                "Original Cost: " + this.getOriginalCost());
    }
}


class Market {
    private ArrayList<Agent> agents;
    private ArrayList<Item> inventory;
    private ArrayList<JobOutput> jobOutputs;
    private ArrayList<Price> prices;
    private double money;

    public Market(ArrayList<Agent> agents, ArrayList<Item> inventory,
                  ArrayList<JobOutput> jobOutputs, ArrayList<Price> prices, double money){
        this.agents = agents;
        this.inventory = inventory;
        this.jobOutputs = jobOutputs;
        this.prices = prices;
        this.money = money;
    }
    public ArrayList<Agent> getAgents(){
        return agents;
    }
    public ArrayList<Item> getInventory(){
        return inventory;
    }
    public ArrayList<JobOutput> getJobOutputs(){
        return jobOutputs;
    }
    public ArrayList<Price> getPrices(){
        return prices;
    }
    public double getMoney(){
        return money;
    }
    public void setAgents(ArrayList<Agent> newAgents){
        agents = newAgents;
    }
    public void setInventory(ArrayList<Item> newInventory){
        inventory = newInventory;
    }
    public void setJobOutputs(ArrayList<JobOutput> newJobOutputs){
        jobOutputs = newJobOutputs;
    }
    public void setPrices(ArrayList<Price> newPrices){
        prices = newPrices;
    }
    public void setMoney(double newMoney){
        money = newMoney;
    }
    public String toString(){
        return ("This market has the following agents: \n" + this.getAgents() + "\n" +
                "The market inventory is: " + this.getInventory() + "\n" +
                "It permits the following job->output combinations: " + this.getJobOutputs() + "\n" +
                "The market has these prices: " + this.getPrices() + ".");
    }
}
