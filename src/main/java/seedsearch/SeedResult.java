package seedsearch;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

public class SeedResult {

    public ArrayList<Reward> miscRewards;
    public ArrayList<Reward> shopRewards;
    public ArrayList<Reward> cardRewards;
    public ArrayList<NeowReward> neowRewards;
    public ArrayList<String> events;
    public ArrayList<String> bosses;
    public ArrayList<String> monsters;
    public ArrayList<String> mapPath;
    public ArrayList<String> trueMapPath;
    public ArrayList<String> bossRelics;
    public ArrayList<String> relics;
    public ArrayList<String> rawCommonRelics;
    public ArrayList<String> rawUncommonRelics;
    public ArrayList<String> rawRareRelics;
    public ArrayList<String> rawBossRelics;
    public ArrayList<String> rawShopRelics;
    public int numElites;
    public int numCombats;
    public int numRestSites;
    public long seed;

    public SeedResult(long seed) {
        this.seed = seed;
        this.miscRewards = new ArrayList<>();
        this.shopRewards = new ArrayList<>();
        this.cardRewards = new ArrayList<>();
        this.neowRewards = new ArrayList<>();
        this.events = new ArrayList<>();
        this.bosses = new ArrayList<>();
        this.monsters = new ArrayList<>();
        this.mapPath = new ArrayList<>();
        this.trueMapPath = new ArrayList<>();
        this.bossRelics = new ArrayList<>();
        this.relics = new ArrayList<>();
    }

    public void addCardReward(int floor, ArrayList<AbstractCard> cards) {
        Reward reward = Reward.makeCardReward(floor, cards);
        cardRewards.add(reward);
    }

    public void addCardReward(Reward reward) {
        cardRewards.add(reward);
    }

    public void addAllCardRewards(ArrayList<Reward> rewards) {
        cardRewards.addAll(rewards);
    }

    public void addMiscReward(Reward reward) {
        miscRewards.add(reward);
    }

    public void addShopReward(Reward reward) {
        shopRewards.add(reward);
    }

    public void addNeowRewards(ArrayList<NeowReward> neowRewards) {
        this.neowRewards = neowRewards;
    }

    public void registerCombat(String monsterName) {
        numCombats += 1;
        monsters.add(monsterName);
    }

    public void registerEliteCombat(String monsterName) {
        numElites += 1;
        registerCombat(monsterName);
    }

    public void registerBossCombat(String monsterName) {
        bosses.add(monsterName);
        registerCombat(monsterName);
    }

    public void registerEvent(String eventName) {
        events.add(eventName);
    }

    public void countRestSite() {
        numRestSites++;
    }

    public void addBossReward(ArrayList<String> bossRelics) {
        this.bossRelics.addAll(bossRelics);
    }

    public void addToMapPath(String mapSymbol) {
        mapPath.add(mapSymbol);
    }

    public void addToTrueMapPath(String mapSymbol) {
        trueMapPath.add(mapSymbol);
    }

    public void updateRelics() {
        relics = new ArrayList<>();
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            relics.add(relic.relicId);
        }
    }

    public void SetCommonRelicPool(ArrayList<String> relics){
        rawCommonRelics = new ArrayList<>(relics);
    }

    public void SetUncommonRelicPool(ArrayList<String> relics){
        rawUncommonRelics = new ArrayList<>(relics);
    }

    public void SetRareRelicPool(ArrayList<String> relics){
        rawRareRelics = new ArrayList<>(relics);
    }

    public void SetBossRelicPool(ArrayList<String> relics){
        rawBossRelics = new ArrayList<>(relics);
    }

    public void SetShopRelicPool(ArrayList<String> relics){
        rawShopRelics = new ArrayList<>(relics);
    }

    public boolean testFinalFilters(SearchSettings settings) {
        if (numCombats > settings.maximumCombats) {
            return false;
        }
        if (numCombats < settings.minimumCombats) {
            return false;
        }
        if (numElites > settings.maximumElites) {
            return false;
        }
        if (numElites < settings.minimumElites) {
            return false;
        }
        if (numRestSites < settings.minimumRestSites) {
            return false;
        }
        if (!events.containsAll(settings.requiredEvents)) {
            return false;
        }
        if (!relics.containsAll(settings.requiredRelics)) {
            return false;
        }
        if (!monsters.containsAll(settings.requiredCombats)) {
            return false;
        }
        ArrayList<String> allPotions = getAllPotionIds();
        for (String potion : settings.requiredPotions) {
            if (allPotions.contains(potion)){
                allPotions.remove(potion);
            } else{
                return false;
            }
        }
        return true;
    }

    public boolean testAct1Filters(SearchSettings settings) {
        if (!relics.containsAll(settings.requiredAct1Relics)) {
            return false;
        }
        ArrayList<String> allCards = getAllCardIds();
        for (String card : settings.bannedAct1Cards) {
            if (allCards.contains(card)) {
                return false;
            }
        }
        for (String card : settings.requiredAct1Cards) {
            if (allCards.contains(card)) {
                allCards.remove(card);
            } else {
                return false;
            }
        }
        ArrayList<String> allPotions = getAllPotionIds();
        for (String potion : settings.requiredAct1Potions) {
            if (allPotions.contains(potion)){
                allPotions.remove(potion);
            } else{
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> getAllCardIds() {
        ArrayList<String> allCards = new ArrayList<>();
        for (Reward reward : cardRewards) {
            for (AbstractCard card : reward.cards) {
                allCards.add(card.cardID);
            }
        }
        for (Reward reward : miscRewards) {
            for (AbstractCard card : reward.cards) {
                allCards.add(card.cardID);
            }
        }
        return allCards;
    }

    private ArrayList<String> getAllPotionIds() {
        ArrayList<String> allPotions = new ArrayList<>();
        for (Reward reward : miscRewards) {
            for (AbstractPotion potion : reward.potions){
                allPotions.add(potion.ID);
            }
        }
        return allPotions;
    }

    private static String removeTextFormatting(String text) {
        text = text.replaceAll("~|@(\\S+)~|@", "$1");
        return text.replaceAll("#.|NL", "");
    }

    public void printSeedStats(SearchSettings settings) {
        ArrayList<String> shopRelics = new ArrayList<>();
        ArrayList<String> shopCards = new ArrayList<>();
        ArrayList<String> shopPotions = new ArrayList<>();
        for (Reward shopReward : shopRewards) {
            shopRelics.addAll(shopReward.relics);
            for (AbstractCard card : shopReward.cards) {
                shopCards.add(card.name);
            }
            for (AbstractPotion potion : shopReward.potions)
            {
                shopPotions.add(potion.name);
            }
        }

        System.out.println(MessageFormat.format("Seed: {0} ({1})", SeedHelper.getString(seed), seed));
        if (settings.showNeowOptions) {
            System.out.println("Neow Options:");
            for (NeowReward reward : neowRewards) {
                System.out.println(removeTextFormatting(reward.optionLabel));
            }
        }
    }
}
