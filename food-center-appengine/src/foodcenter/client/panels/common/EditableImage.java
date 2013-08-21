package foodcenter.client.panels.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.client.handlers.ImageUploadedHandler;

public class EditableImage extends VerticalPanel implements ImageUploadedHandler
{

    private final ClickHandler onClickEdit;
    private Image img;
    private Button button = null;

    public EditableImage(String imgPath)
    {
        this(imgPath, null);
    }

    public EditableImage(String imgPath, ClickHandler onClickEdit)
    {
        super();
        this.onClickEdit = onClickEdit;

        updateImage(imgPath);
    }


    @Override
    public void updateImage(String url)
    {
        clear();

        img = new Image(url);
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
