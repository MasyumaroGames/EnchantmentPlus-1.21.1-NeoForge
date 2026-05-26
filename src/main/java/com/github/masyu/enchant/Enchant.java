package com.github.masyu.enchant;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Enchant.MODID)
public class Enchant {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "enchant";

    // Directly reference a slf4j logger
    public Enchant(IEventBus modEventBus) {
        // クリエイティブタブへの追加イベントを登録
        modEventBus.addListener(this::addCreativeContents);
        NeoForge.EVENT_BUS.register(com.github.masyu.enchant.AnvilEventHandler.class);
    }

    private void addCreativeContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("enchant", "unbreakable");
            ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, id);

            event.getParameters().holders().lookup(Registries.ENCHANTMENT).ifPresent(registry -> {
                registry.get(key).ifPresent(enchantmentHolder -> {
                    ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                    ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                    mutable.set(enchantmentHolder, 1);
                    book.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());

                    // try-catch で重複エラーを回避
                    try {
                        event.accept(book);
                    } catch (IllegalArgumentException e) {
                        // すでに追加されている場合は何もしない
                        System.out.println("Unbreakable book was already added to this tab.");
                    }
                });
            });
        }
    }
}
