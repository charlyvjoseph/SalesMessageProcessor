package com.charlyjoseph;

public interface SalesListener
{
    /**
     * This method gets called each time a sales update of type BaseMessage Type 1(contains the details of 1 sale) is sent by an
     * external company.
     * @param productType the product type
     * @param value the value of the corresponding product type
     * @return {@code true} if the sales data was accepted to this queue, else {@code false}
     */
    boolean updateSalesDetails(String productType, double value);

    /**
     * This method gets called each time a sales update of type BaseMessage Type 2(contains the details of a sale and the number
     *          of occurrences of that sale.) is sent by an external company.
     * @param productType the product type
     * @param value the value of the corresponding product type
     * @param quantity the number of sales of the specific productType, each having the value specified by value
     * @return {@code true} if the sales data was accepted to this queue, else {@code false}
     */
    boolean updateSalesDetails(String productType, double value, int quantity);

    /**
     * This method gets called each time a sales update of type BaseMessage Type 3(contains the details of a sale and an
     *          adjustment operation to be applied to all stored sales of this product type. Operations can be add, subtract,     *          or multiply) is sent by an external company.
     * @param productType the product type
     * @param value the value of the corresponding product type
     * @param operation accepts any one of the string values add, subtract or multiply specifying the type of adjustment to       *          be made
     * @return {@code true} if the sales data was accepted to this queue, else {@code false}
     */
    boolean updateSalesDetails(String productType, double value, String operation);
}
