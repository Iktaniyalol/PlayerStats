package com.artemis.the.gr8.playerstats.api.enums;

import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * All the units PlayerStats can display statistics in, separated
 * by {@link Unit.Type}.
 */
public enum Unit {
    NUMBER (Type.UNTYPED, "Times"),
    KM (Type.DISTANCE, "km"),
    MILE (Type.DISTANCE, "Miles"),
    BLOCK (Type.DISTANCE, "Blocks"),
    CM (Type.DISTANCE, "cm"),
    HP (Type.DAMAGE, "HP"),
    HEART (Type.DAMAGE, "Hearts"),
    DAY (Type.TIME, "days"),
    HOUR (Type.TIME, "hours"),
    MINUTE (Type.TIME, "minutes"),
    SECOND (Type.TIME, "seconds"),
    TICK (Type.TIME, "ticks");

    private final Type type;
    private final String label;

    Unit(Type type, String label) {
        this.type = type;
        this.label = label;
    }

    /**
     * Gets the pretty name belonging to this enum constant.
     *
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Gets the Type this enum constant belongs to.
     *
     * @return the Type
     */
    public Type getType() {
        return this.type;
    }

    /**
     * For Type Time, Damage and Distance, this will return a smaller Unit
     * than the current one (if there is a smaller Unit, that is, otherwise
     * it will return itself). So for DAY, for example, it can return HOUR,
     * MINUTE or SECOND.
     *
     * @param stepsSmaller how many steps smaller the returned Unit should be
     * @return the smaller Unit
     */
    public Unit getSmallerUnit(int stepsSmaller) {
        switch (this) {
            case DAY -> {
                if (stepsSmaller >= 3) {
                    return Unit.SECOND;
                } else if (stepsSmaller == 2) {
                    return Unit.MINUTE;
                } else if (stepsSmaller == 1) {
                    return Unit.HOUR;
                } else {
                    return this;
                }
            }
            case HOUR -> {
                if (stepsSmaller >= 2) {
                    return Unit.SECOND;
                } else if (stepsSmaller == 1) {
                    return Unit.MINUTE;
                } else {
                    return this;
                }
            }
            case MINUTE -> {
                if (stepsSmaller >= 1) {
                    return Unit.SECOND;
                } else {
                    return this;
                }
            }
            case KM -> {
                if (stepsSmaller >= 2) {
                    return Unit.CM;
                } else if (stepsSmaller == 1) {
                    return Unit.BLOCK;
                } else {
                    return this;
                }
            }
            case BLOCK -> {
                if (stepsSmaller >= 1) {
                    return Unit.CM;
                } else {
                    return this;
                }
            }
            case HEART -> {
                if (stepsSmaller >= 1) {
                    return Unit.HP;
                } else {
                    return this;
                }
            }
            default -> {
                return this;
            }
        }
    }

    /**
     * Converts the current Unit into seconds (and returns
     * -1 if the current Unit is not of {@link Unit.Type} TIME).
     *
     * @return this Unit in seconds
     */
    public double getSeconds() {
        return switch (this) {
            case DAY -> 86400;
            case HOUR -> 3600;
            case MINUTE -> 60;
            case SECOND -> 1;
            case TICK -> 1 / 20.0;
            default -> -1;
        };
    }

    /** Converts the current Unit into a short label (and returns a '?' if the current Unit is not of Type TIME)*/
    public char getShortLabel(){
        return switch (this) {
            case DAY -> 'д';
            case HOUR -> 'ч';
            case MINUTE -> 'м';
            case SECOND -> 'с';
            default -> '?';
        };
    }

    /** Returns the Unit corresponding to the given String. This String does NOT need to
     match exactly (it can be "day" or "days", for example), and is case-insensitive.
     @param unitName an approximation of the name belonging to the desired Unit, case-insensitive */
    public static @NotNull Unit fromString(@NotNull String unitName) {
        return switch (unitName.toLowerCase(Locale.ENGLISH)) {
            case "cm" -> Unit.CM;
            case "m", "block", "blocks" -> Unit.BLOCK;
            case "mile", "miles" -> Unit.MILE;
            case "km" -> Unit.KM;
            case "hp" -> Unit.HP;
            case "heart", "hearts" -> Unit.HEART;
            case "day", "days" -> Unit.DAY;
            case "hour", "hours" -> Unit.HOUR;
            case "minute", "minutes", "min" -> Unit.MINUTE;
            case "second", "seconds", "sec" -> Unit.SECOND;
            case "tick", "ticks" -> Unit.TICK;
            default -> Unit.NUMBER;
        };
    }

    /**
     * Gets the Unit.Type of this Statistic, which can be Untyped,
     * Distance, Damage, or Time.
     *
     * @param statistic the Statistic enum constant
     * @return the Type of this Unit
     */
    public static @NotNull Type getTypeFromStatistic(Statistic statistic) {
        String name = statistic.toString().toLowerCase(Locale.ENGLISH);
        if (name.contains("one_cm")) {
            return Type.DISTANCE;
        } else if (name.contains("damage")) {
            return Type.DAMAGE;
        } else if (name.contains("time") || name.contains("one_minute")) {
            return Type.TIME;
        } else {
            return Type.UNTYPED;
        }
    }

    /**
     * Gets the largest Unit this number can be expressed in as a whole number.
     * For example, for Type TIME a value of 80.000 would return Unit.HOUR
     * (80.000 ticks equals 4.000 seconds, 67 minutes, or 1 hour)
     *
     * @param type the Unit.Type of this statistic
     * @param number the statistic value in ticks as returned by Player.getStatistic()
     * @return the Unit
     */
    public static Unit getMostSuitableUnit(Unit.Type type, long number) {
        switch (type) {
            case TIME -> {
                long statNumber = number / 20;
                if (statNumber >= 86400) {
                    return Unit.DAY;
                } else if (statNumber >= 3600) {
                    return Unit.HOUR;
                } else if (statNumber >= 60) {
                    return Unit.MINUTE;
                } else {
                    return Unit.SECOND;
                }
            }
            case DISTANCE -> {
                if (number >= 100000) {
                    return Unit.KM;
                } else {
                    return Unit.BLOCK;
                }
            }
            case DAMAGE -> {
                return Unit.HEART;
            }
            default -> {
                return Unit.NUMBER;
            }
        }
    }

    public enum Type{
        DAMAGE, //7 statistics
        DISTANCE, //15 statistics
        TIME, //5 statistics
        UNTYPED;

        Type() {
        }
    }
}