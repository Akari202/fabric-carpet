package carpet.mixins;

import carpet.settings.CarpetSettings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//needs tests, WIP
@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManager_ghostBlocksMixin
{
    @Shadow public ServerPlayerEntity player;

    @Redirect(method = "method_14263", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F")

    )
    private float noCheatBlockBreakSpeed(BlockState blockState, PlayerEntity playerEntity_1, BlockView blockView_1, BlockPos blockPos_1,
                                         // main args
                                         BlockPos blockPos_1a, PlayerActionC2SPacket.Action playerActionC2SPacket$Action_1, Direction direction_1, int int_1)
    {
        float progress = blockState.calcBlockBreakingDelta(playerEntity_1, blockView_1, blockPos_1);
        if (CarpetSettings.miningGhostBlockFix &&
                (playerActionC2SPacket$Action_1 == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) &&
                !playerEntity_1.onGround &&
                progress >= 0.2
        )
            return progress*5;
        if (CarpetSettings.miningGhostBlockFix &&
                (playerActionC2SPacket$Action_1 == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK)
        )
            return 1.0f;
        return progress;
    }

    /*
    @ModifyConstant(method = "method_14263",
            constant = @Constant(doubleValue = 36D))
    private double addDistance(double original) {
        if (CarpetSettings.miningGhostBlockFix)
            return 1024D;
        return original;
    }
    @Inject(method = "method_14263", at = @At("HEAD"))
    private void logAction(BlockPos blockPos_1, PlayerActionC2SPacket.Action playerActionC2SPacket$Action_1, Direction direction_1, int int_1, CallbackInfo ci)
    {
        CarpetSettings.LOG.error("Got action: "+playerActionC2SPacket$Action_1+"("+int_1+")");
    }
    */
}
