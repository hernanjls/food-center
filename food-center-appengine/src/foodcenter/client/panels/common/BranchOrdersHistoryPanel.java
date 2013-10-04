package foodcenter.client.panels.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.WebClientUtils;
import foodcenter.client.service.WebRequestUtils;
import foodcenter.service.enums.OrderStatus;
import foodcenter.service.proxies.CourseOrderProxy;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.requset.CompanyBranchAdminServiceRequest;
import foodcenter.service.requset.RestaurantBranchAdminServiceRequest;

public class BranchOrdersHistoryPanel extends VerticalPanel
{

    private final Logger logger = Logger.getLogger(BranchOrdersHistoryPanel.class.toString());

    private final TreeMap<String, NetInfoPanel> idNet = new TreeMap<String, NetInfoPanel>();

    private final String branchId;

    private final FindOrdersPanel findOrdersPanel;
    
    private final boolean isRestView;
    
    private final Label infoPopupText; // for setting the popup text
    private final PopupPanel infoPopup; // info popup which can be shown whenever needed


    public BranchOrdersHistoryPanel(String branchId, boolean isRestView)
    {
        super();

        this.branchId = branchId;
        this.isRestView = isRestView;

        this.findOrdersPanel = new FindOrdersPanel();
        infoPopup = new PopupPanel(false);
        infoPopupText = new Label();

        redraw();
    }

    private void showPopup(String msg)
    {
        infoPopupText.setText(msg);
        infoPopup.setWidget(infoPopupText);
        infoPopup.center();
        infoPopup.show();
    }

    private void hidePopup()
    {
        infoPopup.clear();
        infoPopup.hide();
    }

    public void redraw()
    {
        clear();

        add(findOrdersPanel);

        for (NetInfoPanel p : idNet.values())
        {
            add(p);
            p.redrawNetwork();
        }
    }

    private void addOrder(OrderProxy o)
    {
        String id = isRestView ? o.getCompId() : o.getRestId();
        NetInfoPanel p = idNet.get(id);
        if (null == p)
        {
            String name = isRestView ? o.getCompName() : o.getRestName();
            p = new NetInfoPanel(name);
            idNet.put(id, p);
        }
        p.addOrderToNetwork(o);
    }

    class FindOrdersPanel extends HorizontalPanel
    {
        private FindOrdersPanel()
        {
            super();
            Date fromDate = new Date();
            CalendarUtil.addMonthsToDate(fromDate, -1);
            final LabeledDatePicker from = new LabeledDatePicker("From: ", fromDate);
            add(from);

            final LabeledDatePicker to = new LabeledDatePicker(" To: ");
            add(to);

            Button b = new Button("Search", new ClickHandler()
            {

                @Override
                public void onClick(ClickEvent event)
                {
                    showPopup("Loading orders...");
                    if (isRestView)
                    {
                        RestaurantBranchAdminServiceRequest service = WebRequestUtils.getRequestFactory()
                            .getRestaurantBranchAdminService();
                        service.getOrders(branchId, from.getDate(), to.getDate())
                            .with(OrderProxy.ORDER_WITH)
                            .fire(new GetOrdersReceiver());
                    }
                    else
                    {
                        CompanyBranchAdminServiceRequest service = WebRequestUtils.getRequestFactory()
                            .getCompanyBranchAdminService();
                        service.getOrders(branchId, from.getDate(), to.getDate())
                            .with(OrderProxy.ORDER_WITH)
                            .fire(new GetOrdersReceiver());
                    }
                }
            });

            add(b);
        }

        class GetOrdersReceiver extends Receiver<List<OrderProxy>>
        {
            @Override
            public void onSuccess(List<OrderProxy> response)
            {
                idNet.clear();

                if (null != response)
                {
                    for (OrderProxy o : response)
                    {
                        addOrder(o);
                    }
                    logger.fine("done, got " + response.size() + " orders");
                }
                else
                {
                    logger.fine("got null response");
                }
                hidePopup();
                redraw();

            }

            @Override
            public void onFailure(ServerFailure error)
            {
                hidePopup();
                Window.alert("Can't get orders: " + error.getMessage());
            }

        }
    }

    class NetInfoPanel extends VerticalPanel
    {
        /** branch id -> branchPanel */
        private TreeMap<String, BranchInfoPanel> idBranch = new TreeMap<String, BranchInfoPanel>();
        private final String name;
        private double total;
        private double totalDelivered;

        public NetInfoPanel(String name)
        {
            super();
            setStyleName("orders-selector");
            this.name = name;
            this.total = 0D;
            this.totalDelivered = 0D;
        }

        private void redrawNetwork()
        {
            clear();
            HorizontalPanel h = new HorizontalPanel();
            add(h);
            h.setStyleName("selector-header");

            Label l = new Label(name);
            h.add(l);
            l.setStylePrimaryName("selector-header-text");
            h.setCellWidth(l, "60%");

            for (BranchInfoPanel branchInfoPanel : idBranch.values())
            {
                add(branchInfoPanel);
                branchInfoPanel.redrawBranch();

                total += branchInfoPanel.total;
                totalDelivered += branchInfoPanel.totalDelivered;
            }

            Label priceLable = new Label("Delivered / Total price: " + totalDelivered
                                         + " / "
                                         + total);
            h.add(priceLable);
            priceLable.setStylePrimaryName("selector-header-text");
            h.setCellWidth(priceLable, "40%");
        }

        private void addOrderToNetwork(OrderProxy o)
        {
            String id = isRestView ? o.getCompBranchId() : o.getRestBranchId();
            BranchInfoPanel branchInfoPanel = idBranch.get(id);

            if (null == branchInfoPanel)
            {
                branchInfoPanel = new BranchInfoPanel();

                idBranch.put(id, branchInfoPanel);
            }
            branchInfoPanel.addOrderToBranch(o);

        }

        class BranchInfoPanel extends VerticalPanel
        {
            private List<OrderProxy> branchOrders;
            private double total;
            private double totalDelivered;

            private BranchInfoPanel()
            {
                super();
                setStyleName("branch");
                branchOrders = new ArrayList<OrderProxy>();
                this.total = 0D;
                this.totalDelivered = 0D;
            }

            private void redrawBranch()
            {
                clear();

                HorizontalPanel h = new HorizontalPanel();
                add(h);
                h.setStyleName("branch-header");

                String addr = isRestView ? branchOrders.get(0).getCompBranchAddr()
                                        : branchOrders.get(0).getRestBranchAddr();
                Label l = new Label(addr);
                h.add(l);
                l.setStyleName("branch-header-text");
                h.setCellWidth(l, "73%");

                OrdersInfoPanel ordersInfo = new OrdersInfoPanel();
                add(ordersInfo);
                ordersInfo.redrawOrders();

                Label priceLabel = new Label("Delivered / Total price: " + totalDelivered
                                             + " / "
                                             + total);
                h.add(priceLabel);
                priceLabel.setStyleName("branch-header-text");
                h.setCellWidth(priceLabel, "26%");
            }

            private void addOrderToBranch(OrderProxy o)
            {
                branchOrders.add(o);
            }

            class OrdersInfoPanel extends FlexTable
            {
                public OrdersInfoPanel()
                {
                    super();
                    setStyleName("one-column-emphasis");
                }

                private void redrawOrders()
                {
                    clear();
                    setText(0, 0, "Date");
                    setText(0, 1, "User");
                    setText(0, 2, "Type");
                    setText(0, 3, "Courses");
                    setText(0, 4, "Price");
                    setText(0, 5, "Status");

                    getRowFormatter().setStyleName(0, "th");

                    int row = 1;
                    for (OrderProxy o : branchOrders)
                    {
                        if (null == o.getCourses())
                        {
                            continue;
                        }

                        Double price = 0D;
                        for (CourseOrderProxy c : o.getCourses())
                        {
                            price += c.getPrice();
                        }
                        total += price;

                        if (OrderStatus.DELIVERED == o.getStatus())
                        {
                            totalDelivered += price;
                        }

                        setText(row, 0, WebClientUtils.getDateFormatter().format(o.getDate()));
                        setText(row, 1, o.getUserEmail());
                        setText(row, 2, o.getService().getName());
                        setWidget(row, 3, new CoursesInfoPanel(o));
                        setText(row, 4, price.toString());
                        setText(row, 5, o.getStatus().getName());
                        getRowFormatter().setStyleName(row, "td");
                        ++row;
                    }
                }

                private class CoursesInfoPanel extends VerticalPanel
                {
                    private CoursesInfoPanel(OrderProxy order)
                    {
                        if (null == order.getCourses())
                        {
                            return;
                        }
                        for (CourseOrderProxy c : order.getCourses())
                        {
                            add(new Label(c.getName()));
                        }

                    }
                }
            }
        }
    }
}
