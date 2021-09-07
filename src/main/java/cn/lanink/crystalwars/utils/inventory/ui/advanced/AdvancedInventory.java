package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.lanink.crystalwars.entity.CrystalWarsEntityMerchant;
import cn.lanink.gamecore.GameCore;
import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryEvent;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author iGxnon
 * @date 2021/9/6
 */
@SuppressWarnings("unused")
public class AdvancedInventory extends ContainerInventory {

    private Consumer<Player> inventoryCloseConsumer;

    protected Map<Integer, AdvancedClickItem> advancedClickItemMap = new HashMap<>();

    // slotPos , Player
    protected final BiConsumer<InventoryClickEvent, Player> inventoryClickedConsumer = ((clickEvent, player) -> {
        int slotPos = clickEvent.getSlot();
        if(advancedClickItemMap.containsKey(slotPos)) {
            advancedClickItemMap.get(slotPos).callClick(clickEvent, player);
        }
    });

    public AdvancedInventory(@NotNull CrystalWarsEntityMerchant merchant) {
        super(merchant, InventoryType.CHEST);
    }

    public AdvancedInventory(@NotNull CrystalWarsEntityMerchant merchant, @NotNull String overrideTitle) {
        super(merchant, InventoryType.CHEST, new HashMap<>(), InventoryType.CHEST.getDefaultSize(), overrideTitle);
    }

    public AdvancedInventory(InventoryHolder holder, InventoryType type) {
        super(holder, type);
    }

    public AdvancedInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items) {
        super(holder, type, items);
    }

    public AdvancedInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items, Integer overrideSize) {
        super(holder, type, items, overrideSize);
    }

    public AdvancedInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items, Integer overrideSize, String overrideTitle) {
        super(holder, type, items, overrideSize, overrideTitle);
    }

    @Override
    public boolean setItem(int slotPos, Item item) {
        return super.setItem(slotPos, item);
    }

    public boolean setItem(int slotPos, AdvancedClickItem item) {
        boolean result = super.setItem(slotPos, item);
        this.advancedClickItemMap.put(slotPos, item);
        return result;
    }

    public boolean setItem(int slotPos, @NotNull Item item, @NotNull BiConsumer<InventoryClickEvent, Player> clickConsumer) {
        return setItem(slotPos, new AdvancedClickItem(item.getId(), item.getDamage(), item.getCount(), item.getName()).onClick(clickConsumer).setCustomName(item.getCustomName()));
    }


    public AdvancedInventory onClose(@NotNull Consumer<Player> listener) {
        this.inventoryCloseConsumer = listener;
        return this;
    }

    private void callClick(InventoryClickEvent clickEvent, Player player) {
        this.inventoryClickedConsumer.accept(clickEvent, player);
    }

    private void callClose(@NotNull Player player) {
        if(this.inventoryCloseConsumer != null) {
            this.inventoryCloseConsumer.accept(player);
        }
    }

    public static void onEvent(@NotNull InventoryEvent event) {
        Inventory inventory = event.getInventory();
        if(!(inventory instanceof AdvancedInventory)) {
            return;
        }
        if(event instanceof InventoryClickEvent) {
            Player player = ((InventoryClickEvent) event).getPlayer();
            ((AdvancedInventory) inventory).callClick((InventoryClickEvent) event, player);
            event.setCancelled(true);
        }else if(event instanceof InventoryCloseEvent) {
            ((AdvancedInventory) inventory).callClose(((InventoryCloseEvent) event).getPlayer());
        }
    }

    public String getJSONData() {
        return GameCore.GSON.toJson(this, ContainerInventory.class);
    }

}