package xyz.zes.zokes.setting;

import java.util.Map;

import static java.util.Map.entry;

public class Constant {
    public static final int MIN_INTERVAL_SECONDS = 10;
    public static final int MAX_INTERVAL_SECONDS = 60;


    public static final Map<String, String[]> TITLES = Map.ofEntries(
            entry("pun", new String[]{
                    "Here we go, some word playing jokes",
                    "Pun intended",
                    "Pun jokes is always fun"

            }),
            entry("programming", new String[]{
                    "Jokes for computer nerds",
                    "Programming jokes is here",
                    "Sorry to disturb you introverted nerds"
            }),

            entry("misc", new String[]{
                    "This one should be funny at least",
                    "Random topic jokes",
                    "Just want to entertain"
            }),
            entry("dark", new String[]{
                    "The dark is coming at you",
                    "Darkness arrive",
                    "This one might be not so dark"
            }),
            entry("spooky", new String[]{
                    "Hope you are not scared",
                    "Just want to test your fear",
                    "Halloween jokes"
            }),
            entry("christmas", new String[]{
                    "Joyful jokes",
                    "Blessed jokes for blessed people"
            })
    );
}
