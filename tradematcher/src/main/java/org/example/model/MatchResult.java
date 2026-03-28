package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class MatchResult {

    private final List<Match> matches;
    private final List<UnfilledOrder> unfilledOrders;

    public MatchResult() {
        this.matches = new ArrayList<>();
        this.unfilledOrders = new ArrayList<>();
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public void addUnfilledOrder(UnfilledOrder order) {
        unfilledOrders.add(order);
    }

    public List<Match> getMatches() {
        return new ArrayList<>(matches);
    }

    public List<UnfilledOrder> getUnfilledOrders() {
        return new ArrayList<>(unfilledOrders);
    }
}
