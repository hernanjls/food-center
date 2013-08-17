package foodcenter.client.handlers;



public interface EmailHandler
{
 
    /**
     * Do something with the given email (add/remove from server)
     * @param email - is the email to handle
     * @param panel - is the panel to redraw when done
     */
    public void handle(String email, RedrawablePannel panel);
}