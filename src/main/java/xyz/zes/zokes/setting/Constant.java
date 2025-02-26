package xyz.zes.zokes.setting;

import java.util.Map;

import static java.util.Map.entry;

public class Constant {
    public static final int MIN_INTERVAL_SECONDS = 10;
    public static final int MAX_INTERVAL_SECONDS = 60;

    // UI Constants
    public static final int MIN_DISPLAY_TIME = 1;  // 1 second
    public static final int MAX_DISPLAY_TIME = 30; // 30 seconds

    public static final String[] DISPLAY_MODES = {"NORMAL", "MINIMALISTIC"};
    public static final String[] NOTIFICATION_POSITIONS = {"TOP_RIGHT", "TOP_LEFT", "BOTTOM_RIGHT", "BOTTOM_LEFT"};

    // Joke Types
    public static final String[] JOKE_TYPES = {"Any", "single", "twopart"};

    // Display friendly names for UI with fun emojis
    public static final Map<String, String> DISPLAY_MODE_NAMES = Map.of(
            "NORMAL", "🎪 Full Fun Mode",
            "MINIMALISTIC", "🤏 Tiny Mode"
    );

    public static final Map<String, String> POSITION_NAMES = Map.of(
            "TOP_RIGHT", "↗️ Top Right Corner",
            "TOP_LEFT", "↖️ Top Left Corner",
            "BOTTOM_RIGHT", "↘️ Bottom Right Corner",
            "BOTTOM_LEFT", "↙️ Bottom Left Corner"
    );

    public static final Map<String, String> JOKE_TYPE_NAMES = Map.of(
            "Any", "🎲 Any Type",
            "single", "1️⃣ One-liner Jokes",
            "twopart", "2️⃣ Setup-Punchline Jokes"
    );

    public static final Map<String, String[]> TITLES = Map.ofEntries(
            entry("pun", new String[]{
                    "🥁 Ba-dum-tss!",
                    "😏 Pun Intended",
                    "🍎 A-pun-ling Humor"
            }),
            entry("programming", new String[]{
                    "💻 Code Comedian",
                    "🤓 Geek Humor.exe",
                    "🔧 Kernel of Truth"
            }),
            entry("misc", new String[]{
                    "🎭 Random Chuckles",
                    "🎪 Circus of Humor",
                    "🎯 Bullseye Jokes"
            }),
            entry("dark", new String[]{
                    "🌚 Dark Side of Humor",
                    "🕶️ Shady Laughs",
                    "🖤 Lights Out Laughter"
            }),
            entry("spooky", new String[]{
                    "👻 Boo! Did I Scare You?",
                    "🎃 Pumpkin Giggles",
                    "🧟 Zombie Jokes"
            }),
            entry("christmas", new String[]{
                    "🎄 Ho-Ho-Hilarious",
                    "⛄ Frosty's Funnies",
                    "🎁 Unwrap the Laughter"
            })
    );

}