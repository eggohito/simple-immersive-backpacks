package com.github.eggohito.simple_immersive_bags.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
public class GridInventory extends SimpleInventory {

    private final int rows;
    private final int columns;

    public GridInventory(int rows, int columns) {
        super(rows * columns);
        this.rows = rows;
        this.columns = columns;
    }

    public GridInventory(int rows, int columns, ItemStack... defaultStacks) {
        this(rows, columns);
        for (int i = 0; i < this.size(); i++) {
            this.getHeldStacks().set(i, i < defaultStacks.length ? defaultStacks[i] : ItemStack.EMPTY);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

}
