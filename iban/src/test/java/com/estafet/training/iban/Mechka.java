package com.estafet.training.iban;

/**
 * Created by Delcho Delov on 02/12/16.
 */
final class Mechka {
    private final String name;
    private final boolean meatEating;

    Mechka(String name, boolean meatEating) {
        this.name = name;
        this.meatEating = meatEating;
    }

    String getName() {
        return name;
    }

    boolean isMeatEating() {
        return meatEating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mechka mechka = (Mechka) o;

        return meatEating == mechka.meatEating && name.equals(mechka.name);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (meatEating ? 1 : 0);
        return result;
    }

}
