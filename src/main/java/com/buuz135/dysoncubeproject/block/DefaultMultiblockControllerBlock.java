package com.buuz135.dysoncubeproject.block;

import com.buuz135.dysoncubeproject.block.tile.MultiblockStructureBlockEntity;
import com.buuz135.dysoncubeproject.multiblock.MultiblockStructure;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

public abstract class DefaultMultiblockControllerBlock<T extends BasicTile<T>> extends RotatableBlock<T> {

    static {
        EventManager.forge(BlockEvent.EntityPlaceEvent.class, EventPriority.HIGHEST).process(event -> {

        }).subscribe();
    }

    public DefaultMultiblockControllerBlock(String name, Properties properties, Class<T> tileClass) {
        super(name, properties, tileClass);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    public abstract MultiblockStructure getMultiblockStructure();

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        if (this.getMultiblockStructure().validateSpace(context.getLevel(), context.getClickedPos())) {
            return this.defaultBlockState();
        }
        return null;
    }
}
