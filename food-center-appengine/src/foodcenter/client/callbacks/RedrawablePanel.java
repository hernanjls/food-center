package foodcenter.client.callbacks;


public interface RedrawablePanel
{
    /**
     * redraw the panel
     */
    public void redraw();

    /**
     * closes this pannel. <br>
     * should be called from callback.close()
     */
    public void close();

}
