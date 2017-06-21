package com.charlyjoseph;

import java.util.Map;

public class BulkUpdateMessage extends BaseMessage
{
    private final int quantity;

    public BulkUpdateMessage(int messageSequenceId, String productType, double value, int quantity)
    {
        super(messageSequenceId, productType, value);
        this.quantity = quantity;
    }

    @Override
    public void mutateProduct(Map<String, Double> productTypeToValueMap)
    {
        Double existingValue = productTypeToValueMap.get(this.productType);
        if(existingValue == null)
        {
            productTypeToValueMap.put(this.productType, this.value);
        }
        else
        {
            productTypeToValueMap.put(this.productType, this.quantity*(this.value+existingValue));
        }
    }

    public int getQuantity()
    {
        return quantity;
    }

}
