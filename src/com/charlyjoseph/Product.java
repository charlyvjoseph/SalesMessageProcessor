package com.charlyjoseph;

public class Product
{
    private static int nextProductId = 0;
    private final int productId;
    private final String productType;
    private final double value;
    private final int quantity;
    private final int sequenceId;

    public Product(int sequenceId, String productType, double value, int quantity)
    {
        this.productId = getNextProductId();
        this.sequenceId = sequenceId;
        this.productType = productType;
        this.value = value;
        this.quantity = quantity;
    }

    private static int getNextProductId()
    {
        return nextProductId++;
    }

    public int getProductId()
    {
        return productId;
    }

    public String getProductType()
    {
        return productType;
    }

    public double getValue()
    {
        return value;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public int getSequenceId()
    {
        return sequenceId;
    }
}
