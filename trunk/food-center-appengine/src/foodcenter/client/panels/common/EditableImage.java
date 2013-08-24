package foodcenter.client.panels.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.client.callbacks.ImageUploadedCallback;

public class EditableImage extends VerticalPanel implements ImageUploadedCallback
{

//    private final static String DEFAULT_WIDTH = "50px";
//    private final static String DEFAULT_HEIGHT = "50px";
    
    private ClickHandler onClickEdit;
    private Image img;
    private Button button = null;

    
    public EditableImage()
    {
        this(null, null);
    }
    
    public EditableImage(String imgPath)
    {    
        this(imgPath, null);
    }

    public EditableImage(String imgPath, ClickHandler onClickEdit)
    {
        super();
        
        this.onClickEdit = onClickEdit;

        if (null != imgPath)
        {
            updateImage(imgPath);
        }
    }

    public void setClickHandler(ClickHandler onClick)
    {
        this.onClickEdit = onClick;
    }

    @Override
    public final void updateImage(String url)
    {
        updateImage(url, null, null);
    }
    
    @Override
    public final void updateImage(String url, String width, String height)
    {
        clear();
        
        if (null == url)
        {
            return;
        }
        
        img = new Image(url);
        if (null != width && null != height)
        {
            setSize(width, height);
            img.setSize(width, height);
        }
        
        if (null != onClickEdit)
        {
            button = new Button();
            button.addClickHandler(onClickEdit);
            button.getElement().appendChild(img.getElement());
            add(button);
        }
        else
        {
            add(img);
        }
    }
}
