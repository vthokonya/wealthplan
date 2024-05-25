package zw.co.tayanasoft.views.shares;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.converter.StringToUuidConverter;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import zw.co.tayanasoft.data.Shares;
import zw.co.tayanasoft.services.SharesService;
import zw.co.tayanasoft.views.MainLayout;

@PageTitle("Shares")
@Route(value = "shares/:sharesID?/:action?(edit)", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
@Uses(Icon.class)
public class SharesView extends Div implements BeforeEnterObserver {

    private final String SHARES_ID = "sharesID";
    private final String SHARES_EDIT_ROUTE_TEMPLATE = "shares/%s/edit";

    private final Grid<Shares> grid = new Grid<>(Shares.class, false);

    private TextField stockExchangeId;
    private TextField ticker;
    private TextField name;
    private TextField sharesInIssue;
    private TextField industry;
    private TextField type;
    private TextField price;
    private Checkbox active;
    private Checkbox owned;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Shares> binder;

    private Shares shares;

    private final SharesService sharesService;

    public SharesView(SharesService sharesService) {
        this.sharesService = sharesService;
        addClassNames("shares-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("stockExchangeId").setAutoWidth(true);
        grid.addColumn("ticker").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("sharesInIssue").setAutoWidth(true);
        grid.addColumn("industry").setAutoWidth(true);
        grid.addColumn("type").setAutoWidth(true);
        grid.addColumn("price").setAutoWidth(true);
        LitRenderer<Shares> activeRenderer = LitRenderer.<Shares>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", active -> active.isActive() ? "check" : "minus").withProperty("color",
                        active -> active.isActive()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(activeRenderer).setHeader("Active").setAutoWidth(true);

        LitRenderer<Shares> ownedRenderer = LitRenderer.<Shares>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", owned -> owned.isOwned() ? "check" : "minus").withProperty("color",
                        owned -> owned.isOwned()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(ownedRenderer).setHeader("Owned").setAutoWidth(true);

        grid.setItems(query -> sharesService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SHARES_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(SharesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Shares.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(stockExchangeId).withConverter(new StringToUuidConverter("Invalid UUID"))
                .bind("stockExchangeId");
        binder.forField(sharesInIssue).withConverter(new StringToUuidConverter("Invalid UUID")).bind("sharesInIssue");
        binder.forField(price).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("price");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.shares == null) {
                    this.shares = new Shares();
                }
                binder.writeBean(this.shares);
                sharesService.update(this.shares);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(SharesView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> sharesId = event.getRouteParameters().get(SHARES_ID).map(Long::parseLong);
        if (sharesId.isPresent()) {
            Optional<Shares> sharesFromBackend = sharesService.get(sharesId.get());
            if (sharesFromBackend.isPresent()) {
                populateForm(sharesFromBackend.get());
            } else {
                Notification.show(String.format("The requested shares was not found, ID = %s", sharesId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(SharesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        stockExchangeId = new TextField("Stock Exchange Id");
        ticker = new TextField("Ticker");
        name = new TextField("Name");
        sharesInIssue = new TextField("Shares In Issue");
        industry = new TextField("Industry");
        type = new TextField("Type");
        price = new TextField("Price");
        active = new Checkbox("Active");
        owned = new Checkbox("Owned");
        formLayout.add(stockExchangeId, ticker, name, sharesInIssue, industry, type, price, active, owned);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Shares value) {
        this.shares = value;
        binder.readBean(this.shares);

    }
}
