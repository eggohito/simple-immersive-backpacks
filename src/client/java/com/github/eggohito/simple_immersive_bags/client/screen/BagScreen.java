package com.github.eggohito.simple_immersive_bags.client.screen;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBagsClient;
import com.github.eggohito.simple_immersive_bags.client.duck.IdentifiableButtonWidget;
import com.github.eggohito.simple_immersive_bags.content.item.DyeableBagItem;
import com.github.eggohito.simple_immersive_bags.mixin.client.HandledScreenAccessor;
import com.github.eggohito.simple_immersive_bags.mixin.client.ScreenAccessor;
import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

@SuppressWarnings("unused")
public class BagScreen extends InventoryScreen {

    private static final Identifier BACKGROUND_TEXTURE = SimpleImmersiveBags.id("textures/gui/bag/background.png");

    private static final int BACKGROUND_TEXTURE_WIDTH = 256;
    private static final int BACKGROUND_TEXTURE_HEIGHT = 256;

    private final BagScreenHandler bagScreenHandler;
    private final Text title;

    private final int defaultTextColor;

    private float red;
    private float green;
    private float blue;

    public BagScreen(PlayerScreenHandler bagScreenHandler, PlayerInventory playerInventory, Text title) {
        super(playerInventory.player);

        this.title = title;
        this.defaultTextColor = ColorHelper.Argb.getArgb(255, 222, 222, 222);

        //  Override the screen handler constant set in InventoryScreen
        this.bagScreenHandler = (BagScreenHandler) bagScreenHandler;
        ((HandledScreenAccessor) this).setHandler(this.bagScreenHandler);

    }

    @Override
    protected void init() {

        this.backgroundHeight = 224 + BagScreenHandler.BAG_TITLE_Y_OFFSET;

        this.initColor();
        super.init();

    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {

        super.drawForeground(context, mouseX, mouseY);
        context.drawText(textRenderer, title, bagScreenHandler.getTopPos().x + 1, bagScreenHandler.getTopPos().y + 1, defaultTextColor, true);

        this.moveRecipeButtonWidget();

    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

        //  Draw the background texture before drawing the texture with a color tint
        drawTintedTexture(context, BACKGROUND_TEXTURE, x, y, 1.0f, 1.0f, 1.0f);
        drawTintedTexture(context, bagScreenHandler.getScreenTextureId(), x, y, red, green, blue);

        //  Draw the entity paper doll
        if (client != null && client.player != null) {
            drawEntity(context, x + 26, y + 8, x + 75, y + 78, 30, 0.0625f, mouseX, mouseY, client.player);
        }

        this.moveRecipeButtonWidget();

    }

    public BagScreenHandler getBagHandler() {
        return bagScreenHandler;
    }

    public void initColor() {

        ItemStack sourceStack = bagScreenHandler.getSourceStack();
        float[] rgb = DyeableBagItem.unpackRgb(sourceStack);

        this.red = rgb[0];
        this.green = rgb[1];
        this.blue = rgb[2];

    }

    protected void drawTintedTexture(DrawContext context, Identifier textureId, int x1, int y1, float red, float green, float blue) {

        int x2 = x1 + backgroundWidth;
        int y2 = y1 + backgroundHeight;

        float u2 = (float) backgroundWidth / BACKGROUND_TEXTURE_WIDTH;
        float v2 = (float) backgroundHeight / BACKGROUND_TEXTURE_HEIGHT;

        //  Set up the shader's texture, color, and enable blending
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(red, green, blue, 1.0f);
        RenderSystem.setShaderTexture(0, textureId);
        RenderSystem.enableBlend();

        Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        //  Map the texture according to the specified X, Y, U and V values
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(positionMatrix, x1, y1, 0).texture(0, 0).next();
        bufferBuilder.vertex(positionMatrix, x1, y2, 0).texture(0, v2).next();
        bufferBuilder.vertex(positionMatrix, x2, y2, 0).texture(u2, v2).next();
        bufferBuilder.vertex(positionMatrix, x2, y1, 0).texture(u2, 0).next();

        //  Build the buffer and draw the texture
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        //  Reset the shader's color and disable blending
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f , 1.0f);
        RenderSystem.disableBlend();

    }

    private void moveRecipeButtonWidget() {

        for (Drawable drawable : ((ScreenAccessor) this).getDrawables()) {

            if (!(drawable instanceof TexturedButtonWidget buttonWidget) || !(buttonWidget instanceof IdentifiableButtonWidget identifiedButtonWidget)) {
                continue;
            }

            if (identifiedButtonWidget.sib$equals(SimpleImmersiveBagsClient.VANILLA_RECIPE_BOOK_WIDGET_ID)) {
                buttonWidget.setPosition(buttonWidget.getX(), bagScreenHandler.getOffhandPos().y);
            }

        }

    }

}
