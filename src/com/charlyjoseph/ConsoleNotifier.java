package com.charlyjoseph;

public class ConsoleNotifier implements Notifier
{
    @Override
    public void sendNotification(String message)
    {
        System.out.println(message);
    }
}
