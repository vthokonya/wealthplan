package zw.co.tayanasoft.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import org.vaadin.lineawesome.LineAwesomeIcon;
import zw.co.tayanasoft.data.User;
import zw.co.tayanasoft.security.AuthenticatedUser;
import zw.co.tayanasoft.views.bank.BankView;
import zw.co.tayanasoft.views.country.CountryView;
import zw.co.tayanasoft.views.currency.CurrencyView;
import zw.co.tayanasoft.views.exchangerate.ExchangeRateView;
import zw.co.tayanasoft.views.inflation.InflationView;
import zw.co.tayanasoft.views.ledger.LedgerView;
import zw.co.tayanasoft.views.ownerform.OwnerFormView;
import zw.co.tayanasoft.views.product.ProductView;
import zw.co.tayanasoft.views.propertyform.PropertyFormView;
import zw.co.tayanasoft.views.propertytransactionform.PropertyTransactionFormView;
import zw.co.tayanasoft.views.shares.SharesView;
import zw.co.tayanasoft.views.stockexchange.StockExchangeView;
import zw.co.tayanasoft.views.tradingplatform.TradingPlatformView;
import zw.co.tayanasoft.views.tranchannel.TranChannelView;
import zw.co.tayanasoft.views.trantype.TranTypeView;
import zw.co.tayanasoft.views.venture.VentureView;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("WealthPlan");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (accessChecker.hasAccess(OwnerFormView.class)) {
            nav.addItem(new SideNavItem("Owner Form", OwnerFormView.class, LineAwesomeIcon.USER.create()));

        }
        if (accessChecker.hasAccess(CountryView.class)) {
            nav.addItem(new SideNavItem("Country", CountryView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(CurrencyView.class)) {
            nav.addItem(new SideNavItem("Currency", CurrencyView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(TranTypeView.class)) {
            nav.addItem(new SideNavItem("Tran Type", TranTypeView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(ExchangeRateView.class)) {
            nav.addItem(
                    new SideNavItem("Exchange Rate", ExchangeRateView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(InflationView.class)) {
            nav.addItem(new SideNavItem("Inflation", InflationView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(BankView.class)) {
            nav.addItem(new SideNavItem("Bank", BankView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(StockExchangeView.class)) {
            nav.addItem(
                    new SideNavItem("Stock Exchange", StockExchangeView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(TradingPlatformView.class)) {
            nav.addItem(new SideNavItem("Trading Platform", TradingPlatformView.class,
                    LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(TranChannelView.class)) {
            nav.addItem(new SideNavItem("Tran Channel", TranChannelView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(LedgerView.class)) {
            nav.addItem(new SideNavItem("Ledger", LedgerView.class, LineAwesomeIcon.FILTER_SOLID.create()));

        }
        if (accessChecker.hasAccess(PropertyFormView.class)) {
            nav.addItem(new SideNavItem("Property Form", PropertyFormView.class, LineAwesomeIcon.USER.create()));

        }
        if (accessChecker.hasAccess(PropertyTransactionFormView.class)) {
            nav.addItem(new SideNavItem("Property Transaction Form", PropertyTransactionFormView.class,
                    LineAwesomeIcon.USER.create()));

        }
        if (accessChecker.hasAccess(VentureView.class)) {
            nav.addItem(new SideNavItem("Venture", VentureView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(ProductView.class)) {
            nav.addItem(new SideNavItem("Product", ProductView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(SharesView.class)) {
            nav.addItem(new SideNavItem("Shares", SharesView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
