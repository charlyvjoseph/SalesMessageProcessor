package com.charlyjoseph;

public interface SalesUpdateService
{
    /**
     * Register a SalesListener to read updates from an external company.
     *
     * @param salesListener listener for external company updates
     */
    void subscribeToExternalUpdates(SalesListener salesListener);

}