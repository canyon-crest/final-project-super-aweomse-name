package watermelon;

public enum FruitType {
    EMPTY(0),
    CHERRY(2),
    STRAWBERRY(4),
    GRAPE(8),
    DEKOPON(16),
    PERSIMMON(32),
    APPLE(64),
    PEAR(128),
    PEACH(256),
    PINEAPPLE(512),
    MELON(1024),
    WATERMELON(2048);

    public final int value;

    FruitType(int value) {
        this.value = value;
    }

    public static FruitType fromValue(int value) {
        for (FruitType f : values())
            if (f.value == value) return f;
        return EMPTY;
    }

    public FruitType next() {
        FruitType[] all = values();
        int idx = this.ordinal();
        if (idx + 1 < all.length) return all[idx + 1];
        return this;
    }
}
