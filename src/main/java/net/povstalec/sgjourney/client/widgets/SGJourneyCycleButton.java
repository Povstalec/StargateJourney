package net.povstalec.sgjourney.client.widgets;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class SGJourneyCycleButton<T> extends CycleButton<T>
{
	protected final ResourceLocation texture;
	
	protected final int xOffset;
	protected final int yOffset;
	
	public int xImageOffset = 0;
	
	public SGJourneyCycleButton(ResourceLocation texture, int x, int y, int width, int height, int xOffset, int yOffset, Component message,
	                            Component name, int index, T value, ValueListSupplier<T> values, Function<T, Component> valueStringifier,
								Function<CycleButton<T>, MutableComponent> narrationProvider, OnValueChange<T> onValueChange,
								OptionInstance.TooltipSupplier<T> tooltipSupplier, boolean displayOnlyValue)
	{
		super(x, y, width, height, message, name, index, value, values, valueStringifier, narrationProvider, onValueChange, tooltipSupplier, displayOnlyValue);
		
		this.texture = texture;
		
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	protected int getXImage()
	{
		return xImageOffset;
	}
	
	protected int getYImage(boolean isHovered)
    {
		if(!this.active)
			return 0;
		
    	return isHovered ? 2 : 1;
	}
	
	protected boolean isHovered(int x, int y)
	{
		return x >= this.getX() && y >= this.getY() && x < this.getX() + this.width && y < this.getY() + this.height;
	}
	
	@Override
    public void renderButton(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTick)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		int x = this.getXImage();
        int y = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(stack, this.getX(), this.getY(), xOffset + x * this.width, yOffset + y * this.height, this.width, this.height);
        this.renderBg(stack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(stack, font, this.getMessage(), this.getX() + this.width / 2 , this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
	}
	
	
	
	public static class Builder<T>
	{
		private int initialIndex;
		@Nullable
		private T initialValue;
		private final Function<T, Component> valueStringifier;
		private OptionInstance.TooltipSupplier<T> tooltipSupplier = (value) -> null;
		private Function<CycleButton<T>, MutableComponent> narrationProvider = CycleButton::createDefaultNarrationMessage;
		private ValueListSupplier<T> values = ValueListSupplier.create(ImmutableList.of());
		private boolean displayOnlyValue;
		
		public Builder(Function<T, Component> valueStringifier)
		{
			this.valueStringifier = valueStringifier;
		}
		
		public Builder<T> withValues(Collection<T> values)
		{
			return this.withValues(ValueListSupplier.create(values));
		}
		
		@SafeVarargs
		public final Builder<T> withValues(T... values)
		{
			return this.withValues(ImmutableList.copyOf(values));
		}
		
		public Builder<T> withValues(List<T> defaultList, List<T> selectedList)
		{
			return this.withValues(ValueListSupplier.create(DEFAULT_ALT_LIST_SELECTOR, defaultList, selectedList));
		}
		
		public Builder<T> withValues(BooleanSupplier altListSelector, List<T> defaultList, List<T> selectedList)
		{
			return this.withValues(ValueListSupplier.create(altListSelector, defaultList, selectedList));
		}
		
		public Builder<T> withValues(ValueListSupplier<T> values)
		{
			this.values = values;
			return this;
		}
		
		public Builder<T> withTooltip(OptionInstance.TooltipSupplier<T> tooltipSupplier)
		{
			this.tooltipSupplier = tooltipSupplier;
			return this;
		}
		
		public Builder<T> withInitialValue(T initialValue)
		{
			this.initialValue = initialValue;
			int $$1 = this.values.getDefaultList().indexOf(initialValue);
			if($$1 != -1)
				this.initialIndex = $$1;
			
			return this;
		}
		
		public Builder<T> withCustomNarration(Function<CycleButton<T>, MutableComponent> narrationProvider)
		{
			this.narrationProvider = narrationProvider;
			return this;
		}
		
		public Builder<T> displayOnlyValue()
		{
			this.displayOnlyValue = true;
			return this;
		}
		
		public SGJourneyCycleButton<T> create(ResourceLocation texture, int x, int y, int width, int height, int xOffset, int yOffset, Component name)
		{
			return this.create(texture, x, y, width, height, xOffset, yOffset, name, (button, value) -> {});
		}
		
		public SGJourneyCycleButton<T> create(ResourceLocation texture, int x, int y, int width, int height, int xOffset, int yOffset, Component name, OnValueChange<T> onValueChange)
		{
			List<T> defaultList = this.values.getDefaultList();
			if(defaultList.isEmpty())
				throw new IllegalStateException("No values for cycle button");
			else
			{
				T initialValue = this.initialValue != null ? this.initialValue : defaultList.get(this.initialIndex);
				Component initialValueName = this.valueStringifier.apply(initialValue);
				Component initialMessage = this.displayOnlyValue ? initialValueName : CommonComponents.optionNameValue(name, initialValueName);
				return new SGJourneyCycleButton<>(texture, x, y, width, height, xOffset, yOffset, initialMessage, name, this.initialIndex, initialValue, this.values, this.valueStringifier, this.narrationProvider, onValueChange, this.tooltipSupplier, this.displayOnlyValue);
			}
		}
	}
}
