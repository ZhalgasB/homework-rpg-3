package com.narxoz.rpg.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BattleEngine {
    private static BattleEngine instance;
    private Random random = new Random(1L);

    private BattleEngine() {
    }

    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }

    public BattleEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public void reset() {
    }

    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {
        List<Combatant> attackers = new ArrayList<>(teamA);
        List<Combatant> defenders = new ArrayList<>(teamB);

        EncounterResult result = new EncounterResult();
        int round = 0;

        result.addLog("Battle begins: " + teamSizeInfo(attackers) + " vs " + teamSizeInfo(defenders));

        while (!attackers.isEmpty() && !defenders.isEmpty()) {
            round++;
            result.addLog("Round: " + round);

            roundAttacks(attackers, defenders, result, "Heroes");

            if (defenders.isEmpty()) break;

            roundAttacks(defenders, attackers, result, "Enemies");
        }

        String winner;
        if (attackers.isEmpty()) {
            winner = "Enemies";
        } else {
            winner = "Heroes";
        }

        result.setWinner(winner);
        result.setRounds(round);

        result.addLog("Battle ended after " + round + " rounds.");

        if (attackers.isEmpty()) {
            result.addLog("Winner: " + winner + " " + teamSizeInfo(defenders) + " alive");
        } else {
            result.addLog("Winner: " + winner + " " + teamSizeInfo(attackers) + " alive");
        }

        return result;
    }

    private void roundAttacks(List<Combatant> attackers, List<Combatant> defenders, EncounterResult result, String teamName) {
        List<Combatant> currentAttackers = new ArrayList<>(attackers);

        for (Combatant attacker : currentAttackers) {
            if (!attacker.isAlive()) continue;

            if (defenders.isEmpty()) break;

            Combatant target = findFirstAlive(defenders);
            if (target == null) break;

            int damage  = attacker.getAttackPower();


            if (random.nextDouble() < 0.10) {
                result.addLog(attacker.getName() + " misses " + target.getName());
                continue;
            }

            if (random.nextDouble() < 0.15) {
                damage *= 2;
                result.addLog(attacker.getName() + " made CRITICAL HIT to " + target.getName() + " for " + damage + " damage!");
            } else {
                result.addLog(attacker.getName() + " attacks " + target.getName() + " for " + damage + " damage");
            }

            target.takeDamage(damage);

            if (!target.isAlive()) {
                result.addLog(target.getName() + " is defeated!");
                defenders.remove(target);
            }
        }
    }

    private Combatant findFirstAlive(List<Combatant> team) {
        for (Combatant c : team) {
            if (c.isAlive()) return c;
        }
        return null;
    }

    private String teamSizeInfo(List<Combatant> team) {
        int alive = 0;
        for (Combatant c : team) {
            if (c.isAlive()){
                alive++;
            }
        }
        return alive + " alive";
    }
}