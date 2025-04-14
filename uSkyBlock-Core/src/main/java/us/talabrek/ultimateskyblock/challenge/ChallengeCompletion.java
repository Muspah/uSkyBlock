package us.talabrek.ultimateskyblock.challenge;

import org.jetbrains.annotations.NotNull;
<<<<<<< HEAD
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

public class ChallengeCompletion implements us.talabrek.ultimateskyblock.api.ChallengeCompletion {
    private String name;
    private Instant cooldownUntil;
    private int timesCompleted;
    private int timesCompletedInCooldown;

    public ChallengeCompletion(final String name, @Nullable Instant cooldownUntil, final int timesCompleted, final int timesCompletedInCooldown) {
        super();
        this.name = name;
        this.cooldownUntil = cooldownUntil;
        this.timesCompleted = timesCompleted;
        this.timesCompletedInCooldown = timesCompletedInCooldown;
=======

import java.time.Instant;
import java.util.Objects;

public class ChallengeCompletion implements us.talabrek.ultimateskyblock.api.ChallengeCompletion {
    private final us.talabrek.ultimateskyblock.api.model.ChallengeCompletion completion;

    public ChallengeCompletion(@NotNull us.talabrek.ultimateskyblock.api.model.ChallengeCompletion completion) {
        Objects.requireNonNull(completion);
        this.completion = completion;
>>>>>>> sql
    }

    @Override
    public String getName() {
        return completion.getChallenge();
    }

<<<<<<< HEAD
    @Override
    public @Nullable Instant cooldownUntil() {
        return this.cooldownUntil;
=======
    public long getCooldownUntil() {
        return completion.getCooldownUntil().toEpochMilli();
>>>>>>> sql
    }

    @Override
    public boolean isOnCooldown() {
<<<<<<< HEAD
        return getCooldown().isPositive();
    }

    @Override
    public @NotNull Duration getCooldown() {
        if (cooldownUntil == null) {
            return Duration.ZERO;
        }
        Duration remainingCooldown = Duration.between(Instant.now(), cooldownUntil);
        return remainingCooldown.isNegative() ? Duration.ZERO : remainingCooldown;
=======
        return completion.getCooldownUntil().toEpochMilli() < 0 || completion.getCooldownUntil().toEpochMilli() > System.currentTimeMillis();
    }

    @Override
    public long getCooldownInMillis() {
        if (completion.getCooldownUntil().toEpochMilli() < 0) {
            return -1;
        }
        long now = System.currentTimeMillis();
        return completion.getCooldownUntil().toEpochMilli() > now ? completion.getCooldownUntil().toEpochMilli() - now : 0;
>>>>>>> sql
    }

    @Override
    public int getTimesCompleted() {
        return completion.getTimesCompleted();
    }

    public int getTimesCompletedInCooldown() {
        return isOnCooldown() ? completion.getTimesCompletedInCooldown() : completion.getTimesCompleted() > 0 ? 1 : 0;
    }

<<<<<<< HEAD
    public void setCooldownUntil(@Nullable Instant newCooldown) {
        this.cooldownUntil = newCooldown;
        this.timesCompletedInCooldown = 0;
=======
    public void setCooldownUntil(final long newCompleted) {
        completion.setCooldownUntil(Instant.ofEpochMilli(newCompleted));
        completion.setTimesCompletedInCooldown(0);
>>>>>>> sql
    }

    public void setTimesCompleted(final int newCompleted) {
        completion.setTimesCompleted(newCompleted);
        completion.setTimesCompletedInCooldown(newCompleted);
    }

    public void addTimesCompleted() {
        completion.addTimesCompleted();
        completion.addTimesCompletedInCooldown();
    }
}
