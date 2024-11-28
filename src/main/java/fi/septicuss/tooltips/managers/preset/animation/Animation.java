package fi.septicuss.tooltips.managers.preset.animation;


import org.bukkit.entity.Player;

public interface Animation {

    void tick(Player player);

    void skip();

    boolean finished();

    String text();


}

