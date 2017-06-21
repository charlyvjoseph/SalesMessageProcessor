package com.charlyjoseph;

public class Adjustment
{
    private static int nextAdjustmentId = 0;
    private final int adjustmentId;
    private final int sequenceId;
    private final String productType;
    private final double adjustedValue;
    private final AdjustmentOperation operation;

    public Adjustment(int sequenceId, String productType, double value, AdjustmentOperation adjustmentOperations)
    {
        this.adjustmentId = getNextAdjustmentId();
        this.sequenceId = sequenceId;
        this.productType = productType;
        this.adjustedValue = value;
        this.operation = adjustmentOperations;
    }

    public enum AdjustmentOperation
    {
        ADD("add")
                {
                    @Override
                    public double operate(Double totalValue, int sumOfQuantities, Double adjustedValue)
                    {
                        return totalValue + (sumOfQuantities * adjustedValue);
                    }
                },
        SUB("subtract")
            {
                @Override
                public double operate(Double totalValue, int sumOfQuantities, Double adjustedValue)
                {
                    return totalValue - (sumOfQuantities * adjustedValue);
                }
            },
        MUL("multiply")
            {
                @Override
                public double operate(Double totalValue, int sumOfQuantities, Double adjustedValue)
                {
                    return totalValue * adjustedValue;
                }
            };

        public String getOperationString()
        {
            return operationString;
        }

        private final String operationString;

        AdjustmentOperation(String operationString)
        {
            this.operationString = operationString;
        }

        public abstract double operate(Double totalValue, int sumOfQuantities, Double adjustedValue);

        public static AdjustmentOperation getOperationFromString(String operationString)
        {
            for(AdjustmentOperation operation : AdjustmentOperation.values())
            {
                if (operation.getOperationString().equals(operationString))
                {
                    return operation;
                }
            }
            return null;
        }
    }

    private static int getNextAdjustmentId()
    {
        return nextAdjustmentId++;
    }

    public int getAdjustmentId()
    {
        return adjustmentId;
    }

    public int getSequenceId()
    {
        return sequenceId;
    }

    public String getProductType()
    {
        return productType;
    }

    public double getAdjustedValue()
    {
        return adjustedValue;
    }

    public AdjustmentOperation getOperation()
    {
        return operation;
    }
}
