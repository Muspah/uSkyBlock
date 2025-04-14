package us.talabrek.ultimateskyblock.challenge;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
<<<<<<< HEAD
import dk.lockfuglsang.minecraft.file.FileUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import us.talabrek.ultimateskyblock.island.IslandInfo;
import us.talabrek.ultimateskyblock.player.PlayerInfo;
import us.talabrek.ultimateskyblock.uSkyBlock;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
=======
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import us.talabrek.ultimateskyblock.api.model.ChallengeCompletionSet;
import us.talabrek.ultimateskyblock.player.PlayerInfo;
import us.talabrek.ultimateskyblock.uSkyBlock;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
>>>>>>> sql
import java.util.concurrent.ExecutionException;

/**
 * Responsible for handling ChallengeCompletions
 */
public class ChallengeCompletionLogic {
    private final uSkyBlock plugin;
    private final boolean storeOnIsland;
    private final LoadingCache<UUID, ChallengeCompletionSet> completionSetCache;

    public ChallengeCompletionLogic(uSkyBlock plugin, FileConfiguration config) {
        this.plugin = plugin;
        storeOnIsland = config.getString("challengeSharing", "island").equalsIgnoreCase("island");
<<<<<<< HEAD
        completionCache = CacheBuilder
            .from(plugin.getConfig().getString("options.advanced.completionCache", "maximumSize=200,expireAfterWrite=15m,expireAfterAccess=10m"))
            .removalListener((RemovalListener<String, Map<String, ChallengeCompletion>>) removal -> saveToFile(removal.getKey(), removal.getValue()))
            .build(new CacheLoader<>() {
                       @Override
                       public @NotNull Map<String, ChallengeCompletion> load(@NotNull String id) {
                           return loadFromFile(id);
                       }
                   }
            );
        storageFolder = new File(plugin.getDataFolder(), "completion");
        if (!storageFolder.exists() || !storageFolder.isDirectory()) {
            storageFolder.mkdirs();
        }
    }

    private void saveToFile(String id, Map<String, ChallengeCompletion> map) {
        File configFile = new File(storageFolder, id + ".yml");
        FileConfiguration fileConfiguration = new YamlConfiguration();
        saveToConfiguration(fileConfiguration, map);
        try {
            fileConfiguration.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Unable to store challenge-completion to " + configFile, e);
        }
    }

    private void saveToConfiguration(FileConfiguration configuration, Map<String, ChallengeCompletion> map) {
        for (Map.Entry<String, ChallengeCompletion> entry : map.entrySet()) {
            String challengeName = entry.getKey();
            ChallengeCompletion completion = entry.getValue();
            ConfigurationSection section = configuration.createSection(challengeName);
            Instant cooldownUntil = completion.cooldownUntil();
            Long cooldown = cooldownUntil != null ? cooldownUntil.toEpochMilli() : null;
            section.set("firstCompleted", cooldown);
            section.set("timesCompleted", completion.getTimesCompleted());
            section.set("timesCompletedSinceTimer", completion.getTimesCompletedInCooldown());
        }
    }

    private Map<String, ChallengeCompletion> loadFromFile(String id) {
        File configFile = new File(storageFolder, id + ".yml");
        if (!configFile.exists() && storeOnIsland) {
            IslandInfo islandInfo = plugin.getIslandInfo(id);
            if (islandInfo != null && islandInfo.getLeader() != null && islandInfo.getLeaderUniqueId() != null) {
                File leaderFile = new File(storageFolder, islandInfo.getLeaderUniqueId().toString() + ".yml");
                if (leaderFile.exists()) {
                    leaderFile.renameTo(configFile);
=======
        completionSetCache = CacheBuilder
            .from(plugin.getConfig().getString("options.advanced.completionCache", "maximumSize=200,expireAfterWrite=15m,expireAfterAccess=10m"))
            .removalListener((RemovalListener<UUID, ChallengeCompletionSet>) removal ->
                plugin.getStorage().saveChallengeCompletion(removal.getValue()))
            .build(
                new CacheLoader<>() {
                    @Override
                    public @NotNull ChallengeCompletionSet load(@NotNull UUID uuid) {
                        ChallengeCompletionSet set = plugin.getStorage().getChallengeCompletion(uuid).join();
                        return Objects.requireNonNullElseGet(set, () -> new ChallengeCompletionSet(uuid, plugin.getChallengeLogic().getSharingType()));
                    }
>>>>>>> sql
                }
            );
    }

<<<<<<< HEAD
    private Map<String, ChallengeCompletion> loadFromConfiguration(ConfigurationSection root) {
        Map<String, ChallengeCompletion> challengeMap = new ConcurrentHashMap<>();
        plugin.getChallengeLogic().populateChallenges(challengeMap);
        if (root != null) {
            for (String challengeName : challengeMap.keySet()) {
                long firstCompleted = root.getLong(challengeName + ".firstCompleted", 0);
                Instant firstCompletedDuration = firstCompleted > 0 ? Instant.ofEpochMilli(firstCompleted) : null;
                challengeMap.put(challengeName, new ChallengeCompletion(
                    challengeName,
                    firstCompletedDuration,
                    root.getInt(challengeName + ".timesCompleted", 0),
                    root.getInt(challengeName + ".timesCompletedSinceTimer", 0)
                ));
            }
=======
    public UUID getSharingUuid(PlayerInfo playerInfo) {
        if (plugin.getChallengeLogic().isIslandSharing()) {
            return plugin.getStorage().getPlayerIsland(playerInfo.getUniqueId()).join();
>>>>>>> sql
        }

        return playerInfo.getUniqueId();
    }

    public ChallengeCompletionSet getIslandChallenges(String islandName) {
        UUID islandUuid = plugin.getStorage().getIslandByName(islandName).join();
        if (storeOnIsland && islandUuid != null) {
            try {
                return completionSetCache.get(islandUuid);
            } catch (ExecutionException ex) {
                plugin.getLog4JLogger().warn("Error fetching challenge-completion for id {}", islandName, ex);
            }
        }

        return null;
    }

    public ChallengeCompletionSet getChallenges(PlayerInfo playerInfo) {
        if (playerInfo == null) {
            return null;
        }

        try {
            return completionSetCache.get(getSharingUuid(playerInfo));
        } catch (ExecutionException ex) {
            plugin.getLog4JLogger().warn("Error fetching challenge-completion for id {}", playerInfo.getUniqueId(), ex);
        }
<<<<<<< HEAD
        if (challengeMap.isEmpty()) {
            // Fetch from the player-yml file
            challengeMap = loadFromConfiguration(playerInfo.getConfig().getConfigurationSection("player.challenges"));
            if (!challengeMap.isEmpty()) {
                completionCache.put(id, challengeMap);
            }
            // Wipe it
            playerInfo.getConfig().set("player.challenges", null);
            playerInfo.save();
        }
        return challengeMap;
    }

    private String getCacheId(PlayerInfo playerInfo) {
        return storeOnIsland ? playerInfo.locationForParty() : playerInfo.getUniqueId().toString();
    }

    public void completeChallenge(PlayerInfo playerInfo, String challengeName) {
        Map<String, ChallengeCompletion> challenges = getChallenges(playerInfo);
        if (challenges.containsKey(challengeName)) {
            ChallengeCompletion completion = challenges.get(challengeName);
            if (!completion.isOnCooldown()) {
                Duration resetDuration = plugin.getChallengeLogic().getResetDuration(challengeName);
                if (resetDuration.isPositive()) {
                    Instant now = Instant.now();
                    completion.setCooldownUntil(now.plus(resetDuration));
                } else {
                    completion.setCooldownUntil(null);
=======
        return new ChallengeCompletionSet(playerInfo.getUniqueId(), plugin.getChallengeLogic().getSharingType());
    }

    public void completeChallenge(PlayerInfo playerInfo, String challengeName) {
        try {
            ChallengeCompletionSet completionSet = completionSetCache.get(getSharingUuid(playerInfo));
            completionSet.getCompletionMap().values().forEach(completion -> {
                if (!completion.isOnCooldown()) {
                    long resetInMillis = uSkyBlock.getInstance().getChallengeLogic().getResetInMillis(challengeName);
                    if (resetInMillis >= 0) {
                        completion.setCooldownUntil(Instant.now().plusMillis(resetInMillis));
                    } else {
                        completion.setCooldownUntil(Instant.EPOCH);
                    }
>>>>>>> sql
                }
                completion.addTimesCompleted();
                completion.addTimesCompletedInCooldown();
            });

        } catch (ExecutionException ex) {
            plugin.getLog4JLogger().warn("Error completing challenge-completion {} for player {}", challengeName, playerInfo.getUniqueId(), ex);
        }
    }

<<<<<<< HEAD
    public void resetChallenge(PlayerInfo playerInfo, String challenge) {
        Map<String, ChallengeCompletion> challenges = getChallenges(playerInfo);
        if (challenges.containsKey(challenge)) {
            challenges.get(challenge).setTimesCompleted(0);
            challenges.get(challenge).setCooldownUntil(null);
=======
    public void resetChallenge(PlayerInfo playerInfo, String challengeName) {
        try {
            ChallengeCompletionSet set = completionSetCache.get(getSharingUuid(playerInfo));
            if (set.getCompletion(challengeName) != null) {
                set.getCompletion(challengeName).setTimesCompleted(0);
                set.getCompletion(challengeName).setCooldownUntil(Instant.ofEpochMilli(0));
            }
        } catch (ExecutionException ex) {
            plugin.getLog4JLogger().warn("Error resetting challenge-completion for id {}", challengeName, ex);
>>>>>>> sql
        }
    }

    public int checkChallenge(PlayerInfo playerInfo, String challengeName) {
        try {
            ChallengeCompletionSet set = completionSetCache.get(getSharingUuid(playerInfo));
            if (set.getCompletion(challengeName) != null) {
                return set.getCompletion(challengeName).getTimesCompleted();
            }
        } catch (ExecutionException ex) {
            plugin.getLog4JLogger().warn("Error checking challenge-completion for id {}", challengeName, ex);
        }

        return 0;
    }

    public ChallengeCompletion getChallenge(PlayerInfo playerInfo, String challenge) {
        try {
            return new ChallengeCompletion(completionSetCache.get(getSharingUuid(playerInfo)).getCompletion(challenge));
        } catch (ExecutionException ex) {
            plugin.getLog4JLogger().warn("Error fetching challenge-completion for id {}", challenge, ex);
        }

        return null;
    }

    private ChallengeCompletionSet populateChallenges(ChallengeCompletionSet set) {
        plugin.getChallengeLogic().getRanks().forEach(rank -> rank.getChallenges().forEach(challenge ->
            set.setCompletion(
                challenge.getName().toLowerCase(),
                new us.talabrek.ultimateskyblock.api.model.ChallengeCompletion(set.getUuid(), challenge.getName().toLowerCase()))));
        return set;
    }

    public void resetAllChallenges(PlayerInfo playerInfo) {
        try {
            ChallengeCompletionSet set = completionSetCache.get(getSharingUuid(playerInfo));
            set.reset();
            completionSetCache.put(playerInfo.getPlayerId(), populateChallenges(set));
        } catch (ExecutionException ex) {
            plugin.getLog4JLogger().warn("Error resetting challenge-completion for UUID {}", playerInfo.getPlayerId(), ex);
        }
    }

    public void shutdown() {
        flushCache();
    }

    public long flushCache() {
        long size = completionSetCache.size();
        completionSetCache.invalidateAll();
        return size;
    }

    public boolean isIslandSharing() {
        return storeOnIsland;
    }
}
