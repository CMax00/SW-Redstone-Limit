package com.CMax.swRedstoneLimit.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.List;
import java.util.Set;

public class SwRedstoneLimitClient implements ClientModInitializer {
    private static final int limitAmount = 128;
    private static int count = 0;

    // All the BlockEntities
    private static final List<Class<? extends BlockEntity>> blockEntityTypes = List.of(
            ChestBlockEntity.class,
            BarrelBlockEntity.class,
            DropperBlockEntity.class,
            DispenserBlockEntity.class
    );

    // All normal Blocks
    private static final Set<Block> normalBlocks = Set.of(
            Blocks.COMPARATOR,
            Blocks.REPEATER,
            Blocks.REDSTONE_WIRE,
            Blocks.REDSTONE_LAMP,
            Blocks.NOTE_BLOCK,
            Blocks.PISTON,
            Blocks.STICKY_PISTON,
            Blocks.REDSTONE_TORCH,
            Blocks.REDSTONE_BLOCK
    );

    @Override
    public void onInitializeClient() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            ClientWorld world = client.world;
            if (player == null || world == null) return;

            ChunkPos chunkPos = new ChunkPos(player.getBlockPos());
            WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);

            count = 0;

            // check BlockEntities
            for (BlockEntity be : chunk.getBlockEntities().values()) {
                for (Class<? extends BlockEntity> clazz : blockEntityTypes) {
                    if (clazz.isInstance(be)) {
                        count++;
                        break;
                    }
                }
            }

            // check normal Blocks
            for (int x = 0; x < 16; x++) {
                for (int y = world.getBottomY(); y < world.getTopY(); y++) {
                    for (int z = 0; z < 16; z++) {
                        BlockPos pos = chunkPos.getBlockPos(x, y, z);
                        Block block = chunk.getBlockState(pos).getBlock();
                        if (normalBlocks.contains(block)) {
                            count++;
                        }
                    }
                }
            }

            player.sendMessage(
                    net.minecraft.text.Text.literal("Redstonelimit: " + count + " / " + limitAmount),
                    true
            );
        });
    }
}
