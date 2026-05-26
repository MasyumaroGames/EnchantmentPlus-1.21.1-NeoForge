package com.github.masyu.enchant;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

// ★アノテーションは全部消して、普通のクラスにします
public class AnvilEventHandler {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        // 中身のコードは完全にそのままで大丈夫です！
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.isEmpty() || right.isEmpty()) return;

        ItemEnchantments leftEnchants = left.getEnchantments();
        ItemEnchantments rightEnchants = right.getEnchantments();
        if (rightEnchants.isEmpty()) return;

        ItemEnchantments.Mutable mutableEnchants = new ItemEnchantments.Mutable(leftEnchants);
        boolean isUpdated = false;

        for (Holder<Enchantment> enchantHolder : rightEnchants.keySet()) {
            if (enchantHolder == null) continue;
            int leftLevel = leftEnchants.getLevel(enchantHolder);
            int rightLevel = rightEnchants.getLevel(enchantHolder);

            if (leftLevel > 0 && leftLevel == rightLevel) {
                int nextLevel = leftLevel + 1;
                if (nextLevel <= 10) {
                    mutableEnchants.set(enchantHolder, nextLevel);
                    isUpdated = true;
                }
            } else if (rightLevel > leftLevel) {
                mutableEnchants.set(enchantHolder, rightLevel);
                isUpdated = true;
            } else if (leftLevel == 0) {
                mutableEnchants.set(enchantHolder, rightLevel);
                isUpdated = true;
            }
        }

        if (isUpdated) {
            ItemStack result = left.copy();
            if (left.is(Items.BOOK) || left.is(Items.ENCHANTED_BOOK)) {
                result = new ItemStack(Items.ENCHANTED_BOOK);
            }
            result.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS, mutableEnchants.toImmutable());
            result.set(net.minecraft.core.component.DataComponents.REPAIR_COST, 0);

            event.setOutput(result);
            event.setCost(5);
            event.setMaterialCost(1);
        }
    }
}