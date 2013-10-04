package foodcenter.client.panels.restaurant.branch.orders;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.WebClientUtils;
import foodcenter.client.panels.common.EditableImage;
import foodcenter.client.service.WebRequestUtils;
import foodcenter.service.enums.TableReservationStatus;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.proxies.TableReservationProxy;
import foodcenter.service.requset.RestaurantChefServiceRequest;

public class PendingReservationsPanel extends FlexTable
{
    private ArrayList<TableReservationProxy> reservations;

    private final static Integer SERVICE_TYPE_IMG_WIDTH_PX = 50;
    private final static Integer SERVICE_TYPE_IMG_HEIGHT_PX = 50;
    
    public PendingReservationsPanel(String branchId)
    {
        super();

        reservations = new ArrayList<TableReservationProxy>();

        this.setWidth("100%");

        RestaurantChefServiceRequest service = WebRequestUtils.getRequestFactory()
            .getRestaurantChefService();

        service.getPendingReservations(branchId)
            .with(OrderProxy.ORDER_WITH)
            .fire(new PendingReservationsReciever());
    }

    public void handleReservationId(String reservationId)
    {
        if (null == reservationId)
        {
            return;
        }
        
        RestaurantChefServiceRequest service = WebRequestUtils.getRequestFactory()
            .getRestaurantChefService();
        service.getReservationById(reservationId).with(TableReservationProxy.TABLE_RESERVATION_WITH).fire(new NewReservationReciever());
    }
    
    public void redraw()
    {
        clear();
        int row = 0;
        // prints the header
        setText(row, 0, "Reservation information");
        setText(row, 1, "Wants to arrive between:");
        
        ++row;
        for (TableReservationProxy o : reservations)
        {
            setWidget(row, 0, new CompanyMembersPanel(o));
            setWidget(row, 1, new PendingAvailableTimesPanel(o));
            setWidget(row, 2, new PendingReservationControlPanel(o));
            
            ++row;
        }
    }

    private class NewReservationReciever extends Receiver<TableReservationProxy>
    {
        @Override
        public void onSuccess(TableReservationProxy response)
        {
            if (null == response)
            {
                return;
            }

            synchronized (reservations)
            {
                
                if (TableReservationStatus.CREATED != response.getStatus())
                {
                    reservations.remove(response);
                    redraw();
                }
                else 
                {
                    reservations.add(response);
                    redraw();
                }
            }
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Window.alert("Can't get order: " + error.getMessage());
        }

    }

    private class PendingReservationsReciever extends Receiver<List<TableReservationProxy>>
    {

        @Override
        public void onSuccess(List<TableReservationProxy> response)
        {
            if (null == response || response.isEmpty())
            {
                return;
            }

            synchronized (reservations)
            {
                for (TableReservationProxy o : response)
                {
                    if (!reservations.contains(o))
                    {
                        reservations.add(o);
                    }
                }
                redraw();
            }
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Window.alert("Can't get orders: " + error.getMessage());
        }

    }

    private class CompanyMembersPanel extends VerticalPanel
    {
        
        private final Label header;
        private final HorizontalPanel body;
        
        
        private final TableReservationProxy reservation;
        
        public CompanyMembersPanel(TableReservationProxy reservation)
        {
            super();
            this.reservation = reservation;
            
            header = new Label();
            add(header);
            
            body = new HorizontalPanel();
            add(body);
            
            VerticalPanel members = getMembersPanel();
            header.setText(reservation.getCompName() + " (" + members.getWidgetCount() + ")");
            
            EditableImage img = new EditableImage(null, null);
            
            String imgUrl = reservation.getImageUrl();
            img.updateImage(imgUrl,
                            SERVICE_TYPE_IMG_WIDTH_PX + "px",
                            SERVICE_TYPE_IMG_HEIGHT_PX + "px");
            body.add(img);
            body.add(members);            
        }
        
        private VerticalPanel getMembersPanel()
        {
            VerticalPanel members = new VerticalPanel();
            members.add(new Label(reservation.getUserEmail()));
            List<String> users = reservation.getUsers(); 
            if (null != users && !users.isEmpty())
            {
                for (String u: users)
                {
                    if (!u.equals(reservation.getUserEmail()))
                    {  
                        members.add(new Label(u));
                    }
                }
            }
            return members;
        }
    }
    
    private class PendingAvailableTimesPanel extends VerticalPanel
    {
        private PendingAvailableTimesPanel(TableReservationProxy reservation)
        {
            super();
            DateTimeFormat formater = WebClientUtils.getDateFormatter();
            Date from = reservation.getFromDate();
            String fromStr = formater.format(from);
            add(new Label(fromStr));
            add(new Label("to"));
            
            Date to = reservation.getToDate();
            String toStr = formater.format(to);
            add(new Label(toStr));
        }
    }

    private class PendingReservationControlPanel extends VerticalPanel
    {            
        private final TableReservationProxy reservation;
        private ListBox hrs = new ListBox();
        private ListBox mins = new ListBox();
        private List<String> minsItems = new LinkedList<String>();
        private List<String> minsItemsFirstHr = new LinkedList<String>();
        private List<String> minsItemsLastHr = new LinkedList<String>();
        
        private PendingReservationControlPanel(TableReservationProxy reservation)
        {
            super();
            this.reservation = reservation;
            
            HorizontalPanel timePanel = new HorizontalPanel();
            add(timePanel);
            timePanel.add(hrs);
            timePanel.add(new Label(":"));
            timePanel.add(mins);
           

            int startMins = reservation.getFromDate().getMinutes();
            int startHrs = reservation.getFromDate().getHours();
            
            int endMins = reservation.getToDate().getMinutes();
            int endHrs = reservation.getToDate().getHours();
            
            for (Integer i = startHrs; i<= endHrs; ++i)
            {
                hrs.addItem(i.toString());
            }
            
            for (Integer i = 0 ; i< 60; i += 10)
            {
                minsItems.add(i.toString());
                if (i <= endMins)
                {
                    minsItemsLastHr.add(i.toString());
                }
                if (i >= startMins)
                {
                    mins.addItem(i.toString());
                    minsItemsFirstHr.add(i.toString());
                }
            }
            
            hrs.addChangeHandler(new ChangeHandler()
            {
                @Override
                public void onChange(ChangeEvent event)
                {
                    int selectedIndex = hrs.getSelectedIndex();
                    List<String> minsToShow = minsItems;
                    if (0 == selectedIndex)
                    {
                        minsToShow = minsItemsFirstHr;
                    }
                    else if ((hrs.getItemCount() - 1) == selectedIndex)
                    {
                        minsToShow = minsItemsLastHr;
                    }
                    
                    mins.clear();
                    for (String m : minsToShow)
                    {
                        mins.addItem(m);
                    }    
                }
            });

            
            
            Button doneButton = new Button("Accept", new OnClickDoneButton());
            doneButton.setWidth("90%");
            add(doneButton);

            Button cancelButton = new Button("Decline", new OnClickCancelButton());
            cancelButton.setWidth("90%");
            add(cancelButton);
        }
        
        private class OnClickDoneButton extends Receiver<TableReservationProxy> implements ClickHandler
        {

            @Override
            public void onClick(ClickEvent event)
            {
                int hour = Integer.parseInt(hrs.getItemText(hrs.getSelectedIndex()));
                int min = Integer.parseInt(mins.getItemText(mins.getSelectedIndex()));
                
                synchronized (reservations)
                {
                    reservations.remove(reservation);
                    redraw();
                }
                RestaurantChefServiceRequest service = WebRequestUtils.getRequestFactory()
                    .getRestaurantChefService();
                service.confirmReservation(reservation.getId(), hour, min).fire(this);

            }

            @Override
            public void onSuccess(TableReservationProxy response)
            {
                // do nothing all is good!
            }

            @Override
            public void onFailure(ServerFailure error)
            {
                Window.alert("Failed to save reservation: " + error.getMessage());
                synchronized (reservations)
                {
                    reservations.add(reservation);
                    redraw();
                }

            }

        }

        private class OnClickCancelButton extends Receiver<TableReservationProxy> implements ClickHandler
        {
            @Override
            public void onClick(ClickEvent event)
            {
                synchronized (reservations)
                {
                    reservations.remove(reservation);
                    redraw();
                }

                RestaurantChefServiceRequest service = WebRequestUtils.getRequestFactory()
                    .getRestaurantChefService();
                service.declineReservation(reservation.getId()).fire(this);
            }

            @Override
            public void onSuccess(TableReservationProxy response)
            {
                // do nothing all is good!
            }

            @Override
            public void onFailure(ServerFailure error)
            {
                Window.alert("Failed to cancel reservation: " + error.getMessage());
                synchronized (reservations)
                {
                    reservations.add(reservation);
                    redraw();
                }
            }
        }
    }

}
