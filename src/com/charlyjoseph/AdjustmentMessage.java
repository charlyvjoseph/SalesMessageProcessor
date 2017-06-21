package com.charlyjoseph;

public class AdjustmentMessage extends BaseMessage
{
    private final String operation;

    public AdjustmentMessage(int messageSequenceId, String productType, double value, String adjustmentOperation)
    {
        super(messageSequenceId, productType, value);
        this.operation = adjustmentOperation;
    }
}
