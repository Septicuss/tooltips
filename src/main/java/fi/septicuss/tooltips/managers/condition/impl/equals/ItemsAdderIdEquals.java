package fi.septicuss.tooltips.managers.condition.impl.equals;

import dev.lone.itemsadder.api.CustomStack;
import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderIdEquals implements Condition {

    private static final String[] SLOT = { "slot", "s" };
    private static final String[] MATERIAL = { "type", "m", "mat", "material" };

    public boolean check(Player player, Arguments args) {
        EquipmentSlot slot = EquipmentSlot.HAND;
        if (args.has(SLOT)) {
            slot = EquipmentSlot.valueOf(args.get(SLOT).getAsString().strip().toUpperCase());
        }

        ItemStack item = player.getInventory().getItem(slot);

        if (item == null || item.getType().isAir()) {
            return false;
        }

        CustomStack customStack = CustomStack.byItemStack(item);
        if (customStack != null) {
            String material = args.get(MATERIAL).getAsString();
            String id = customStack.getNamespace() + ":" + customStack.getId();
            if (material.equals(id)) {
                return true;
            }
        }

        return false;
    }

    public Validity valid(Arguments args) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            return Validity.of(false, "Cannot use itemsadderidequals because ItemsAdder is not installed");
        }

        if (!args.has(MATERIAL)) {
            return Validity.of(false, "Type argument is required");
        }

        if (args.has(SLOT)) {
            String slot = args.get(SLOT).getAsString();
            try {
                return Validity.of(false, "Unknown slot " + quote(slot));
            } catch (IllegalArgumentException e) {
                return Validity.of(false, null);
            }
        }

        return Validity.TRUE;
    }

    @Override
    public String id() {
        return "itemsadderidequals";
    }
}
