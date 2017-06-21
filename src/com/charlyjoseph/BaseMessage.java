package com.charlyjoseph;

import java.util.Map;

public class BaseMessage
{
    final int messageSequenceId;
    final String productType;
    final double value;

    public BaseMessage(int messageSequenceId, String productType, double value)
    {
        this.messageSequenceId = messageSequenceId;
        this.productType = productType;
        this.value = value;
    }

    public void mutateProduct(Map<String, Double> productTypeToValueMap)
    {
        Double existingValue = productTypeToValueMap.get(this.productType);
        if(existingValue == null)
        {
            productTypeToValueMap.put(this.productType, this.value);
        }
        else
        {
            productTypeToValueMap.put(this.productType, this.value+existingValue);
        }
    }

    public int getMessageSequenceId()
    {
        return messageSequenceId;
    }

    public String getProductType()
    {
        return productType;
    }

    public double getValue()
    {
        return value;
    }
}
