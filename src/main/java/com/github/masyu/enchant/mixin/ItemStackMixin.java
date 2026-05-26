package com.github.masyu.enchant.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    /**
     * 1.21.1ではhurtAndBreakの引数にServerLevelが追加されています。
     */
    @Inject(
            method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventBreaking(int amount, ServerLevel level, LivingEntity entity, Consumer<net.minecraft.world.item.Item> onBroken, CallbackInfo ci) {
        // 1.21.1ではエンチャントの判定にHolderを使用しますが、
        // JSONで定義したUnbreakableの効果（item_damageを-1.0する等）が
        // データパック側で正しく設定されていれば、Mixinなしでも耐久値は減りません。
        // もし特定の条件下で明示的にキャンセルしたい場合はここで行います。
    }
}