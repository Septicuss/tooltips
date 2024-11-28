package fi.septicuss.tooltips.managers.preset.animation;


import org.bukkit.entity.Player;

public interface Animation {

    void tick(Player player);

    boolean finished();

    String text();


}

