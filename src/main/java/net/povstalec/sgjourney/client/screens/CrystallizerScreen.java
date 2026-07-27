package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.FluidTankRenderer;
import net.povstalec.sgjourney.client.widgets.DumpTankButton;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractCrystallizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AdvancedCrystallizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.CrystallizerEntity;
import net.povstalec.sgjourney.common.menu.CrystallizerMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import org.jetbrains.annotations.NotNull;

public class CrystallizerScreen<T extends AbstractCrystallizerEntity<?>> extends SGJourneyContainerScreen<CrystallizerMenu<T>>
{
	public static final int HINT_OFFSET_Y = 174;
	public static final int BUCKET_HINT_OFFSET_X = 0;
	public static final int ENERGY_HINT_OFFSET_X = 16;
	public static final int CRYSTAL_BASE_HINT_OFFSET_X = 32;
	
	private final ResourceLocation texture;
	private FluidTankRenderer fluidTankRenderer;
	
    public CrystallizerScreen(CrystallizerMenu<T> menu, ResourceLocation texture, Inventory inventory, Component component)
    {
        super(menu, inventory, component);
		this.texture = texture;
    }
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		
		super.init();
		
		assignFluidRenderer();
		this.addRenderableWidget(new DumpTankButton(x + 52, y + 16, button -> menu.pressDumpButton()));
	}
	
	private void assignFluidRenderer()
	{
		this.fluidTankRenderer = new FluidTankRenderer(menu.blockEntity.inputFluidTankCapacity(), true, 16, 52);
	}

    @Override
    protected void renderBg(@NotNull PoseStack stack, float partialTick, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(stack, x, y, 0, 0, imageWidth, imageHeight);
		
		this.itemHint(stack, x + 8, y + 17, BUCKET_HINT_OFFSET_X, HINT_OFFSET_Y, 4);
		this.itemHint(stack, x + 142, y + 17, ENERGY_HINT_OFFSET_X, HINT_OFFSET_Y, 5);
		this.itemHint(stack, x + 71, y + 17, CRYSTAL_BASE_HINT_OFFSET_X, HINT_OFFSET_Y, 0);
		
		this.renderEnergyVertical(stack, x + 162, y + 17, 6, 52, 176, 0, this.menu.getEnergy(), this.menu.getEnergyCapacity());
        this.renderProgress(stack, x + 52, y + 40);
        
        fluidTankRenderer.render(stack, x + 34, y + 17, menu.getFluidStack());
    }

	@Override
	public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float delta)
    {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        renderTooltip(stack, mouseX, mouseY);
		
		this.energyTooltip(stack, mouseX, mouseY, 162, 17, 6, 52, this.menu.getEnergy(), this.menu.getEnergyCapacity());
        this.liquidNaquadahTooltip(stack, mouseX, mouseY);
	}
    
    @Override
    protected void renderLabels(@NotNull PoseStack stack, int mouseX, int mouseY)
	{
		this.font.draw(stack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	    this.font.draw(stack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }
    
    protected void renderProgress(PoseStack stack, int x, int y)
    {
    	float percentage = (float) this.menu.getProgress() / this.menu.getMaxProgress();
    	int actual = Math.round(54 * percentage);
    	this.blit(stack, x, y, 0, 166, actual, 8);
    }
    
    protected void liquidNaquadahTooltip(PoseStack matrixStack, int mouseX, int mouseY)
    {
    	if(this.isHovering(34, 17, 16, 54, mouseX, mouseY))
	    {
			FluidStack fluidStack = this.menu.getFluidStack();
			String name = fluidStack.isEmpty() ? "tooltip.sgjourney.empty" : fluidStack.getTranslationKey();
	    	renderTooltip(matrixStack, ComponentHelper.unchangingFluidAmountComponent(name, fluidStack.getAmount(), menu.blockEntity.inputFluidTankCapacity(), ComponentHelper.fluidComponentColor(fluidStack.getFluid())), mouseX, mouseY);
	    }
    }
	
	@Override
	protected boolean hasItem(int slot)
	{
		return switch(slot)
		{
			case 0 -> !menu.blockEntity.crystalBaseHandler.getStackInSlot(0).isEmpty();
			case 1 -> !menu.blockEntity.primaryIngredientHandler.getStackInSlot(0).isEmpty();
			case 2 -> !menu.blockEntity.secondaryIngredientHandler.getStackInSlot(0).isEmpty();
			case 3 -> !menu.blockEntity.outputHandler.getStackInSlot(0).isEmpty();
			case 4 -> !menu.blockEntity.fluidInputHandler.getStackInSlot(0).isEmpty();
			default -> false;
		};
	}
	
	
	
	public static class Crystallizer extends CrystallizerScreen<CrystallizerEntity>
	{
		public Crystallizer(CrystallizerMenu<CrystallizerEntity> menu, Inventory inventory, Component component)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/crystallizer_gui.png"), inventory, component);
		}
	}
	
	public static class AdvancedCrystallizer extends CrystallizerScreen<AdvancedCrystallizerEntity>
	{
		public AdvancedCrystallizer(CrystallizerMenu<AdvancedCrystallizerEntity> menu, Inventory inventory, Component component)
		{
			super(menu, StargateJourney.sgjourneyLocation("textures/gui/advanced_crystallizer_gui.png"), inventory, component);
		}
	}
}
