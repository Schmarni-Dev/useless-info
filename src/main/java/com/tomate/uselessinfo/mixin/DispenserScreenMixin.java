package com.tomate.uselessinfo.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserScreen.class)
public abstract class DispenserScreenMixin extends AbstractContainerScreen<DispenserMenu> {
    public DispenserScreenMixin(DispenserMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Unique
    private static final String[] VIBRATIONS = {
            "All",
            "Walk/Jump/Swim/Climb/Crawl",
            "Projectile Land/Hit Ground/Splash",
            "Item Interact Finish/Projectile Shoot",
            "Entity Action/Elytra Glide",
            "Entity Dismount/Equip",
            "Entity Mount/Entity Interact/Shear",
            "Entity Damage",
            "Drink/Eat",
            "Container Close/Block Close/Block Deactivate",
            "Container Open/Block Open/Block Activate/Note Block Play",
            "Block Change",
            "Block Destroy/Fluid Pickup",
            "Block Place/Fluid Place",
            "Entity Place/Lightning Strike/Teleport",
            "Entity Die/Explode"
    };

    @Unique
    private static final int SLOTS = 9;
    @Unique
    private static final int MAX_COUNT = 64;

    @Inject(method = "render", at = @At("TAIL"))
    void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        var signalStrength = getSignalStrength();
        var mc = Minecraft.getInstance();
        var signal_string = "Redstone Signal: %d".formatted(signalStrength);
        var vibration_string = "▶ %s".formatted(VIBRATIONS[signalStrength]);
        // var max_string_width = Integer.max(mc.font.width(signal_string), mc.font.width(vibration_string));
        // var horizontal_pos = (guiGraphics.guiWidth() / 2) - (max_string_width / 2);
        guiGraphics.drawString(mc.font, signal_string,
                (guiGraphics.guiWidth() / 2) - (mc.font.width(signal_string) / 2), 9,
                0xffffff, true);
        guiGraphics.drawString(mc.font, vibration_string,
                (guiGraphics.guiWidth() / 2) - (mc.font.width(vibration_string) / 2), 9 + 16, 0xffffff, true);
    }

    @Unique
    int getSignalStrength() {
        var f = 0f;
        for (int i = 0; i < SLOTS; ++i) {
            ItemStack itemStack = menu.slots.get(i).getItem();
            if (itemStack.isEmpty())
                continue;
            f += (float) itemStack.getCount() / (float) Math.min(MAX_COUNT, itemStack.getMaxStackSize());
        }
        return Mth.lerpDiscrete(f / (float) SLOTS, 0, 15);
    }
}
