package com.charlyjoseph;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class SalesListenerImpl implements SalesListener
{
    private static final int MAX_SIZE = 50;
    private static final int NOTIFICATION_THRESHOLD = 10;
    private Queue<BaseMessage> messagesList = new ArrayBlockingQueue<>(MAX_SIZE);
    private Map<String, List<Product>> productTypeToProductsMap = new HashMap<>();
    private Map<String, List<Adjustment>> productTypeToAdjustmentsMap = new HashMap<>();
    private Notifier notifier = new ConsoleNotifier();

    @Override
    public boolean updateSalesDetails(String productType, double value)
    {
        final BaseMessage message = new BaseMessage(messagesList.size() + 1, productType, value);
        Product product = new Product(message.getMessageSequenceId(), productType, value, 1);
        return processMessage(product, message, productTypeToProductsMap);
    }

    @Override
    public boolean updateSalesDetails(String productType, double value, int quantity)
    {
        final BulkUpdateMessage message = new BulkUpdateMessage(messagesList.size() + 1, productType, value, quantity);
        Product product = new Product(message.getMessageSequenceId(), productType, value, quantity);
        return processMessage(product, message, productTypeToProductsMap);
    }


    @Override
    public boolean updateSalesDetails(String productType, double value, String operation)
    {
        final AdjustmentMessage message = new AdjustmentMessage(messagesList.size() + 1, productType, value, operation);
        Adjustment.AdjustmentOperation operationFromString = Adjustment.AdjustmentOperation.getOperationFromString(operation);
        if(operationFromString == null)
        {
            return false;
        }
        Adjustment adjustment = new Adjustment(message.getMessageSequenceId(), productType, value, operationFromString);
        return processMessage(adjustment, message, productTypeToAdjustmentsMap);
    }

    private <T> boolean processMessage(T instrumentType, BaseMessage message, Map<String, List<T>> productTypeToInstrumentTypeMap)
    {
        boolean messageProcessedSuccessfully = messagesList.offer(message);

        if (messageProcessedSuccessfully)
        {
            List<T> instruments = productTypeToInstrumentTypeMap.get(message.getProductType());
            if (instruments == null)
            {
                instruments = new LinkedList<>();
                productTypeToInstrumentTypeMap.put(message.getProductType(), instruments);
            }
            instruments.add(instrumentType);

            if (messagesList.size() % NOTIFICATION_THRESHOLD == 0)
            {
                sendNotificationForTheTenthMessage();
            }

            if (messagesList.size() == MAX_SIZE)
            {
                pauseAndSendFinalAdjustmentNotification();
            }

        }
        return messageProcessedSuccessfully;
    }

    private void sendNotificationForTheTenthMessage()
    {
        StringBuilder outputMessage = new StringBuilder("");
        for (Map.Entry<String, List<Product>> entrySet : productTypeToProductsMap.entrySet())
        {
            String productType = entrySet.getKey();
            List<Product> products = entrySet.getValue();
            List<Adjustment> adjustments = productTypeToAdjustmentsMap.get(productType);
            double totalValue = 0.0;
            int quantitySum = 0;

            if(adjustments!= null)
            {
                int adjustmentIndex = 0, productIndex = 0;
                while( adjustmentIndex < adjustments.size() && productIndex < products.size())
                {
                    Adjustment adjustment = adjustments.get(adjustmentIndex);
                    Product product = products.get(productIndex);

                    if (product.getSequenceId() < adjustment.getSequenceId())
                    {
                        totalValue += product.getValue() * product.getQuantity();
                        quantitySum += product.getQuantity();
                        productIndex++;
                    }
                    else if (adjustment.getSequenceId() < product.getSequenceId())
                    {
                        totalValue=adjustment.getOperation().operate(totalValue, quantitySum, adjustment.getAdjustedValue());
                        adjustmentIndex++;
                    }
                }
                while(adjustmentIndex < adjustments.size())
                {
                    Adjustment adjustment = adjustments.get(adjustmentIndex);
                    totalValue=adjustment.getOperation().operate(totalValue, quantitySum, adjustment.getAdjustedValue());
                    adjustmentIndex++;
                }


                while(productIndex < products.size())
                {
                    Product product = products.get(productIndex);
                    totalValue += product.getValue() * product.getQuantity();
                    quantitySum += product.getQuantity();
                    productIndex++;
                }
            }
            else
            {
                for (int productIndex = 0; productIndex < products.size(); productIndex++)
                {
                    Product product = products.get(productIndex);

                        totalValue += product.getValue() * product.getQuantity();
                        quantitySum += product.getQuantity();
                }
            }
            outputMessage.append(constructOutputMessage(productType, totalValue));
        }
        this.notifier.sendNotification(outputMessage.toString());
    }

    public StringBuilder constructOutputMessage(String productType, double totalValue)
    {
        return new StringBuilder("product: ").append(productType).append(" values: ").append(totalValue).append("\n");
    }

    private void pauseAndSendFinalAdjustmentNotification()
    {
        StringBuilder outputMessage = new StringBuilder("Application is pausing after " + MAX_SIZE + " messages. No more requests will be acceted \n");
        for (Map.Entry<String, List<Adjustment>> entrySet : productTypeToAdjustmentsMap.entrySet())
        {
            String productType = entrySet.getKey();
            List<Adjustment> adjustments = entrySet.getValue();
            for (int adjustmentIndex =0; adjustmentIndex < adjustments.size(); adjustmentIndex++)
            {
                Adjustment adjustment = adjustments.get(adjustmentIndex);
                outputMessage.append(constructFinalOutputMessage(productType, adjustment.getOperation().getOperationString(), adjustment.getAdjustedValue()));
            }
        }
        this.notifier.sendNotification(outputMessage.toString());
    }

    public StringBuilder constructFinalOutputMessage(String productType, String operationString, double totalValue)
    {
        return new StringBuilder("product type: ").append(productType).append(" adjustment type: ").append(operationString).append(" adjusted values: ").append(totalValue).append("\n");
    }

    public void setNotifer(Notifier notifier)
    {
        this.notifier = notifier;
    }
}
