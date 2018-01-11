package net.novapixelnetwork.gamecore.entity;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

    private HashMap<UUID, Long> cooldowns = new HashMap<>();

    public static final int DEFAULT_COOLDOWN = 15;

    public void setCooldown(UUID player, Long time){
        if(time == null)
            cooldowns.remove(player);
        else
            cooldowns.put(player, time);
    }

    public Long getCooldown(UUID player){
        return (cooldowns.get(player) == null ? Long.valueOf(0) : cooldowns.get(player));
    }

    private CooldownManager(){}

    public static final CooldownManager INSTANCE = new CooldownManager();

}
