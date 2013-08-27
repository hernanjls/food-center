package foodcenter.client.panels.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.client.callbacks.ImageUploadedCallback;
import foodcenter.service.proxies.UserProxy;

public class HeaderProfilePanel extends HorizontalPanel implements ImageUploadedCallback
{

    private final static int POPUP_WIDTH_PX = 350;

    private final static int BIG_PROFILE_IMAGE_WIDTH_PX = 100;
    private final static int BIG_PROFILE_IMAGE_HEIGHT_PX = 100;

    private final static int SMALL_PROFILE_IMAGE_WIDTH_PX = 30;
    private final static int SMALL_PROFILE_IMAGE_HEIGHT_PX = 30;

    private final EditableImage profileImg;
    private PopupPanel profilePopup;

    private final UserProxy user;
    private String imgUrl;

    public HeaderProfilePanel(UserProxy user)
    {
        super();

        this.user = user;
        this.profileImg = new EditableImage(null, new OnClickProfileImage());

        imgUrl = user.getImageUrl();
        profileImg.updateImage(imgUrl,
                               SMALL_PROFILE_IMAGE_WIDTH_PX + "px",
                               SMALL_PROFILE_IMAGE_HEIGHT_PX + "px");
        
        setStyleName("header-panel");

        // add the logo image
        add(new EditableImage("/images/logo.png"));

        // It only applies to widgets added after this property is set.
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        add(new Label(user.getNickName()));

        add(profileImg);
    }

    @Override
    public final void updateImage(String url)
    {
        imgUrl = url;

        profileImg.updateImage(imgUrl,
                               SMALL_PROFILE_IMAGE_WIDTH_PX + "px",
                               SMALL_PROFILE_IMAGE_HEIGHT_PX + "px");

        boolean isShow = (null != profilePopup) && profilePopup.isShowing();
        
        createProfilePopup();

        if (isShow)
        {
            profilePopup.show();
        }
    }

    @Override
    public void updateImage(String url, String width, String height)
    {
        updateImage(url);
    }

    private void createProfilePopup()
    {
        if (null != profilePopup)
        {
            profilePopup.removeFromParent();
        }
        profilePopup = new PopupPanel(true);
        profilePopup.setWidth(POPUP_WIDTH_PX + "px");

        // It only applies to widgets added after this property is set.
        DockPanel dockpanel = new DockPanel();
        dockpanel.setVerticalAlignment(HasAlignment.ALIGN_TOP);
        dockpanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
        profilePopup.add(dockpanel);

        // South - sign out button
        Button signout = new Button("Sign Out", new OnClickLogout());
        signout.setWidth("100%");
        dockpanel.add(signout, DockPanel.SOUTH);

        // West - image
        EditableImage img = new EditableImage();
        img.setClickHandler(new OnClickImg());
        img.updateImage(imgUrl,
                        BIG_PROFILE_IMAGE_WIDTH_PX + "px",
                        BIG_PROFILE_IMAGE_HEIGHT_PX + "px");
        dockpanel.add(img, DockPanel.WEST);

        // Center - info
        VerticalPanel v = new VerticalPanel();
        dockpanel.add(v, DockPanel.EAST);

        Label nickname = new Label(user.getNickName());
        v.add(nickname);

        if (!user.getNickName().equals(user.getEmail()))
        {
            Label email = new Label(user.getEmail());
            v.add(email);
        }
        v.add(new HTML("<br>"));

        String role = user.isAdmin() ? "Admin" : "User";
        v.add(new Label("Role: " + role));

        int left = profileImg.getAbsoluteLeft() - POPUP_WIDTH_PX;
        int top = profileImg.getAbsoluteTop() + 2 * SMALL_PROFILE_IMAGE_HEIGHT_PX;
        profilePopup.setPopupPosition(left, top);
    }

    private class OnClickProfileImage implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            if (null == profilePopup)
            {
                createProfilePopup();
            }
            profilePopup.show();
        }
    }

    private class OnClickLogout implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            Window.Location.replace(user.getLogoutUrl());
        }
    }

    private class OnClickImg implements ClickHandler
    {

        @Override
        public void onClick(ClickEvent event)
        {
            new FileUploadPanel(HeaderProfilePanel.this, null, null);
        }

    }
}
