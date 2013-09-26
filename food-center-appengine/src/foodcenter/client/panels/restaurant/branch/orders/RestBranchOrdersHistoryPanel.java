package foodcenter.client.panels.restaurant.branch.orders;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.callbacks.RedrawablePanel;
import foodcenter.client.panels.common.LabeledDatePicker;
import foodcenter.client.service.WebRequestUtils;
import foodcenter.service.proxies.CourseOrderProxy;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.requset.RestaurantBranchAdminServiceRequest;

public class RestBranchOrdersHistoryPanel extends VerticalPanel implements RedrawablePanel
{

    private final Logger logger = Logger.getLogger(RestBranchOrdersHistoryPanel.class.toString());

    private final TreeMap<String, CompanyInfoPanel> idComps = new TreeMap<String, CompanyInfoPanel>();

    private final String branchId; // restaurant branch id

    private final FindOrdersPanel findOrdersPanel;

    public RestBranchOrdersHistoryPanel(String branchId)
    {
        super();

        this.branchId = branchId;
        this.findOrdersPanel = new FindOrdersPanel();

        redraw();
    }

    @Override
    public void redraw()
    {
        clear();

        add(findOrdersPanel);

        for (CompanyInfoPanel p : idComps.values())
        {
            add(p);
            p.redrawCompany();
        }
    }

    @Override
    public void close()
    {
        // TODO Auto-generated method stub

    }

    private void addOrder(OrderProxy o)
    {
        CompanyInfoPanel p = idComps.get(o.getCompId());
        if (null == p)
        {
            p = new CompanyInfoPanel(o.getCompName());
            idComps.put(o.getCompId(), p);
        }
        p.addOrderToCompany(o);
    }

    class FindOrdersPanel extends HorizontalPanel
    {
        private FindOrdersPanel()
        {
            super();

            final LabeledDatePicker from = new LabeledDatePicker("From: ");
            add(from);

            final LabeledDatePicker to = new LabeledDatePicker(" To: ");
            add(to);

            Button b = new Button("Search", new ClickHandler()
            {

                @Override
                public void onClick(ClickEvent event)
                {
                    RestaurantBranchAdminServiceRequest service = WebRequestUtils.getRequestFactory()
                        .getRestaurantBranchAdminService();
                    service.getOrders(branchId, from.getDate(), to.getDate())
                        .with(OrderProxy.ORDER_WITH)
                        .fire(new GetOrdersReceiver());
                }
            });

            add(b);
        }

        class GetOrdersReceiver extends Receiver<List<OrderProxy>>
        {
            @Override
            public void onSuccess(List<OrderProxy> response)
            {
                idComps.clear();

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
                redraw();
                
            }

            @Override
            public void onFailure(ServerFailure error)
            {
                Window.alert("Can't get orders: " + error.getMessage());
            }

        }
    }

    class CompanyInfoPanel extends VerticalPanel
    {
        /** branch id -> branchPanel */
        private TreeMap<String, BranchInfoPanel> idBranch = new TreeMap<String, BranchInfoPanel>();
        private final String name;

        public CompanyInfoPanel(String name)
        {
            super();
            setStyleName("orders-selector");
            this.name = name;
        }

        private void redrawCompany()
        {
            clear();
            Label l = new Label(name);
            l.setStyleName("selector-name");
            add(l);

            for (BranchInfoPanel branchInfoPanel : idBranch.values())
            {
                add(branchInfoPanel);
                branchInfoPanel.redrawBranch();
            }
        }

        private void addOrderToCompany(OrderProxy o)
        {
            BranchInfoPanel branchInfoPanel = idBranch.get(o.getCompBranchId());

            if (null == branchInfoPanel)
            {
                branchInfoPanel = new BranchInfoPanel();
                idBranch.put(o.getCompBranchId(), branchInfoPanel);
            }
            branchInfoPanel.addToBranchOrder(o);
        }

        class BranchInfoPanel extends VerticalPanel
        {
            private List<OrderProxy> branchOrders;

            private BranchInfoPanel()
            {
                super();
                setStyleName("branch");
                branchOrders = new ArrayList<OrderProxy>();
            }

            private void redrawBranch()
            {
                clear();

                Label l = new Label(branchOrders.get(0).getCompBranchAddr());
                l.setStyleName("branch-name");
                add(l);

                OrdersInfoPanel ordersInfo = new OrdersInfoPanel();
                add(ordersInfo);
                ordersInfo.redrawOrders();
            }

            private void addToBranchOrder(OrderProxy o)
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

                        setText(row, 0, DateTimeFormat.getFormat("dd.MM.yyyy HH:mm z Z")
                            .format(o.getDate()));
                        setText(row, 1, o.getUserEmail());
                        setText(row, 2, o.getService().getName());
                        setWidget(row, 3, new CoursesInfoPanel(o));
                        setText(row, 4, price.toString());
                        setText(row, 5, o.getStatus().getName());
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
