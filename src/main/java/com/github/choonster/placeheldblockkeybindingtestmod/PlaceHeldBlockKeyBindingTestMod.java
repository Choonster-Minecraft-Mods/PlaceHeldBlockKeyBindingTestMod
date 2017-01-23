package com.github.choonster.placeheldblockkeybindingtestmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = PlaceHeldBlockKeyBindingTestMod.MODID, name = PlaceHeldBlockKeyBindingTestMod.NAME, clientSideOnly = true)
public class PlaceHeldBlockKeyBindingTestMod {
	public static final String MODID = "placeheldblockkeybindingtestmod";
	public static final String NAME = "Place Held Block Key Binding Test Mod";

	public static final KeyBinding PLACE_HELD_BLOCK = new KeyBinding("key.placeheldblockkeybindingtestmod:place_held_block", KeyConflictContext.IN_GAME, Keyboard.KEY_L, "key.category.placeheldblockkeybindingtestmod:general");

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ClientRegistry.registerKeyBinding(PLACE_HELD_BLOCK);
	}

	/**
	 * Handles the effects of this mod's {@link KeyBinding}.
	 *
	 * @author Choonster
	 */
	@Mod.EventBusSubscriber
	public static class EventHandler {
		private static final Minecraft MINECRAFT = Minecraft.getMinecraft();

		/**
		 * Handle the effects of this mod's {@link KeyBinding}.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void clientTick(TickEvent.ClientTickEvent event) {
			if (event.phase != TickEvent.Phase.END) return;

			if (PLACE_HELD_BLOCK.isKeyDown()) {
				placeHeldBlock();
			}
		}

		/**
		 * Attempt to place a block from the player's hand below them.
		 * <p>
		 * Adapted from {@link Minecraft#rightClickMouse}.
		 * <p>
		 * Test for this thread:
		 * http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/modification-development/2786461-how-to-get-minecraftserver-instance
		 */
		private static void placeHeldBlock() {
			final EntityPlayerSP clientPlayer = MINECRAFT.player;

			for (final EnumHand hand : EnumHand.values()) {
				final ItemStack heldItem = clientPlayer.getHeldItem(hand);
				final int heldItemCount = heldItem.getCount();

				final BlockPos pos = new BlockPos(clientPlayer).down();

				final EnumActionResult actionResult = MINECRAFT.playerController.processRightClickBlock(clientPlayer, MINECRAFT.world, pos, EnumFacing.UP, new Vec3d(0, 0, 0), hand);

				if (actionResult == EnumActionResult.SUCCESS) {
					clientPlayer.swingArm(hand);

					if (!heldItem.isEmpty() && (heldItem.getCount() != heldItemCount || MINECRAFT.playerController.isInCreativeMode())) {
						MINECRAFT.entityRenderer.itemRenderer.resetEquippedProgress(hand);
					}

					return;
				}
			}
		}
	}
}
