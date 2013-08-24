package foodcenter.client.panels.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.client.callbacks.ImageUploadedCallback;
import foodcenter.server.service.servlet.ImageServlet;

public class FileUploadPanel
{
    private final ImageUploadedCallback callback;
    private final String restId;
    private final String compId;
    private final PopupPanel popupHolder;
    private FormPanel form;
    private FileUpload upload;

    public FileUploadPanel(ImageUploadedCallback callback, String restId, String compId)
    {
        this.callback = callback;
        this.restId = restId;
        this.compId = compId;

        this.popupHolder = new PopupPanel(false);

        redraw();
    }

    public void redraw()
    {
        // Create a FormPanel and point it at a service.
        form = new FormPanel();
        form.setAction("/blobservlet");

        // Because we're going to add a FileUpload widget, we'll need to set the
        // form to use the POST method, and multipart MIME encoding.
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        VerticalPanel panel = new VerticalPanel();
        form.setWidget(panel);

        // Create a File Upload, giving it a name so that it will be submitted.
        upload = new FileUpload();
        upload.setName(ImageServlet.BLOB_UPLOAD_FILE_PARAM);
        panel.add(upload);

        if (null != restId && !"".equals(restId))
        {
            TextBox restBoxId = new TextBox();
            restBoxId.setName(ImageServlet.BLOB_UPLOAD_REST_ID_PARAM);
            restBoxId.setText(restId);
            restBoxId.setVisible(false);
            panel.add(restBoxId);
        }
        else if (null != compId && !"".equals(compId))
        {
            TextBox compBoxId = new TextBox();
            compBoxId.setName(ImageServlet.BLOB_UPLOAD_COMP_ID_PARAM);
            compBoxId.setText(compId);
            compBoxId.setVisible(false);
            panel.add(compBoxId);
        }

        Button submit = new Button("Submit");
        submit.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                form.submit();
            }
        });

        panel.add(submit);

        Button cancel = new Button("Cancel");
        cancel.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                form.removeFromParent();
                popupHolder.removeFromParent();
            }
        });

        panel.add(cancel);

           
        form.addSubmitHandler(new SubmitHandler()
        {

            @Override
            public void onSubmit(SubmitEvent event)
            {
                if ((null == upload) || (null == upload.getFilename())
                    || (0 == upload.getFilename().length()))
                {
                    Window.alert("Please select a file 1st...");
                    event.cancel();
                }
            }
        });

        form.addSubmitCompleteHandler(new SubmitCompleteHandler()
        {
            @Override
            public void onSubmitComplete(SubmitCompleteEvent event)
            {
                String url = event.getResults();
                callback.updateImage(url);
                popupHolder.removeFromParent();
            }
        });

        popupHolder.setWidget(form);
        popupHolder.center();
        popupHolder.show();
    }
}
