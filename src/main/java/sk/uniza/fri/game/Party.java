package sk.uniza.fri.game;

import com.badlogic.gdx.utils.Null;
import sk.uniza.fri.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class Party implements Iterable<Pokemon> {
    private static final int MAX_PARTY_SIZE = 6;
    private final ArrayList<Pokemon> pokemons;
    private int powerPoints;
    private int size;

    public Party() {
        this.powerPoints = 0;
        this.pokemons = new ArrayList<>();
    }

    public void addPokemon(Pokemon pokemon) {
        if (this.size >= MAX_PARTY_SIZE) {
            System.out.println("Party is full");
        }
        this.pokemons.add(pokemon);
        this.size++;
        this.powerPoints += pokemon.getPowerPoints();

        this.pokemons.sort(Comparator.comparingInt(Pokemon::getSpeed).reversed());
    }

    public int getMaxLevel() {
        int maxLevel = 0;
        for (Pokemon pokemon : this.pokemons) {
            if (pokemon.getLevel() > maxLevel) {
                maxLevel = pokemon.getLevel();
            }
        }
        return maxLevel;
    }

    public void removePokemon(Pokemon pokemon) {
        if (!this.pokemons.contains(pokemon)) {
            throw new IllegalStateException("Pokemon is not in party");
        } else if (this.getSize() <= 1) {
            throw new IllegalStateException("Party must have at least one pokemon");
        }

        this.pokemons.remove(pokemon);
        this.size--;
        this.powerPoints -= pokemon.getPowerPoints();
    }

    public int getPowerPoints() {
        return this.powerPoints;
    }

    public int getSize() {
        return this.size;
    }

    public Pokemon getPokemon(Pokemon pokemon) {
        return this.pokemons.get(this.pokemons.indexOf(pokemon));
    }

    @Null
    public Pokemon getFirstPokemon() {
        if (this.pokemons.isEmpty()) {
            return null;
        } else {
            return this.pokemons.get(0);
        }
    }

    @Override
    public Iterator<Pokemon> iterator() {
        return this.pokemons.iterator();
    }
}
