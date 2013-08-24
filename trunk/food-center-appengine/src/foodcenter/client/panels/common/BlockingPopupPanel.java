package foodcenter.client.panels.common;

import com.google.gwt.user.client.ui.PopupPanel;

public class BlockingPopupPanel extends PopupPanel
{
    public BlockingPopupPanel()
    {
        this(false);
    }

    public BlockingPopupPanel(boolean isAutoHide)
    {
        super(isAutoHide);
        setStyleName("blocking-popup-holder");
    }
}
