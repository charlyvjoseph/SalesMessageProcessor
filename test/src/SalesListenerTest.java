import com.charlyjoseph.*;
import org.junit.Assert;
import org.junit.Test;

public class SalesListenerTest
{

    private static final String ADD = Adjustment.AdjustmentOperation.ADD.getOperationString();
    private static final String SUBTRACT = Adjustment.AdjustmentOperation.SUB.getOperationString();
    private static final String MULTIPLY = Adjustment.AdjustmentOperation.MUL.getOperationString();

    @Test
    public void testSalesListener()
    {

        StringBuilder adjustmentsMessageBuilder = new StringBuilder("Application is pausing after 50 messages. No more requests will be acceted \n");
        SalesListenerImpl salesListener = new SalesListenerImpl();
        TestNotifier testNotifer = new TestNotifier();
        salesListener.setNotifer(testNotifer);

        //First 10 runs, we add 1 unit to company a 7 times and then adjust the value by adding 1. This should give company a a value of 7*(1+1)=16. We also add 2 quanities of value 2 to company b making it worth 4. Note the very first adjustment of add has not effect but it is still recorded as a message and would be displayed at the end along with all the other adjustments

        updateAdjustments(salesListener, "company a", 1.0, ADD, adjustmentsMessageBuilder);
        int i = 0;
        for (; i < 7; i++)
        {
            salesListener.updateSalesDetails("company a", 1);
        }

        salesListener.updateSalesDetails("company b", 2, 2);
        testNotifer.setExpectedMessage(salesListener.constructOutputMessage("company a", i * 2).append(salesListener.constructOutputMessage("company b", 4)).toString());
        updateAdjustments(salesListener, "company a", 1.0, ADD, adjustmentsMessageBuilder);

        //Second 10 runs, we start off the run by subtracting 1 unit from company a essentially revoking the add from the previous step bringing the value down to 7, we then add 1 unit 9 times with a quantity 2 to company b making its value 22.
        updateAdjustments(salesListener, "company a", 1.0, SUBTRACT, adjustmentsMessageBuilder);
        int j = 0;
        for (; j < 8; j++)
        {
            salesListener.updateSalesDetails("company b", 1, 2);
        }

        testNotifer.setExpectedMessage(salesListener.constructOutputMessage("company a", i).append(salesListener.constructOutputMessage("company b", (++j * 2) + 4)).toString());
        salesListener.updateSalesDetails("company b", 1, 2);


        //Third 10 runs, we run 3 successive updates on company a with the first and third ones being adjustments to add and subtract respectively. The second update on a is cancelled out completely by the subtract adjustment. All other previous updates to a remain intact since the add counters the subtract. Thus a remians at 7. We run 7 updates on b with negative 1 reducing its value by 7 and completing the cycle
        updateAdjustments(salesListener, "company a", 1.0, ADD, adjustmentsMessageBuilder);
        salesListener.updateSalesDetails("company a", 1);
        updateAdjustments(salesListener, "company a", 1.0, SUBTRACT, adjustmentsMessageBuilder);

        j = 0;
        for (; j < 6; j++)
        {
            salesListener.updateSalesDetails("company b", -1, 2);
        }

        testNotifer.setExpectedMessage(salesListener.constructOutputMessage("company a", i).append(salesListener.constructOutputMessage("company b", 8)).toString());
        salesListener.updateSalesDetails("company b", -1, 2);

        //Fourth set of 10 updates, we run a multiply adjustment on company a for a factor of 2 making it 14. We then complete the cycle with b
        updateAdjustments(salesListener, "company a", 2.0, MULTIPLY, adjustmentsMessageBuilder);
        j = 0;
        for (; j < 8; j++)
        {
            salesListener.updateSalesDetails("company b", 1);
        }
        testNotifer.setExpectedMessage(salesListener.constructOutputMessage("company a", i * 2).append(salesListener.constructOutputMessage("company b", 17)).toString());
        salesListener.updateSalesDetails("company b", 1);

//Final 10 updates We multiply a with a 1 leaving it unchanged and b with a 0 clearing its value. We then run some updates to complete the cycle and ensure the adjustments list and pausing messages are printed after 50 messages.
        updateAdjustments(salesListener, "company a", 1.0, MULTIPLY, adjustmentsMessageBuilder);
        updateAdjustments(salesListener, "company b", 0.0, MULTIPLY, adjustmentsMessageBuilder);
        j = 0;
        for (; j < 7; j++)
        {
            salesListener.updateSalesDetails("company b", 1);
        }

        testNotifer.setExpectedMessage(salesListener.constructOutputMessage("company a", 14.0).append(salesListener.constructOutputMessage("company b", ++j)).toString());
        testNotifer.setFinalMessage(adjustmentsMessageBuilder.toString());
        Assert.assertTrue(salesListener.updateSalesDetails("company b", 1));

        //Additional updates past the 50 mark returns a false
        Assert.assertFalse(salesListener.updateSalesDetails("company a", 1));
    }

    private void updateAdjustments(SalesListenerImpl salesListener, String productType, double value, String operation, StringBuilder stringBuilder)
    {
        salesListener.updateSalesDetails(productType, value, operation);
        stringBuilder.append(salesListener.constructFinalOutputMessage(productType, operation, value));
    }

    @Test
    public void testInvalidAdjustmentMessages()
    {
        SalesListenerImpl salesListener = new SalesListenerImpl();
        Assert.assertFalse(salesListener.updateSalesDetails("company a", 1, "invalid"));
    }

    class TestNotifier implements Notifier
    {
        private String expectedMessage = "";
        private String finalMessage = "UNSET";

        void setExpectedMessage(String expectedMessage)
        {
            this.expectedMessage = expectedMessage;
        }

        void setFinalMessage(String finalMessage)
        {
            this.finalMessage = finalMessage;
        }

        @Override
        public void sendNotification(String message)
        {
            if (!expectedMessage.equals(""))
            {
                Assert.assertEquals(this.expectedMessage, message);
                this.expectedMessage = "";
            }
            else
            {
                Assert.assertEquals(this.finalMessage, message);
            }
        }
    }
}
