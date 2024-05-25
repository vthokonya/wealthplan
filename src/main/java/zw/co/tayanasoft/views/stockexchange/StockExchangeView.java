package zw.co.tayanasoft.views.stockexchange;

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
import com.vaadin.flow.data.converter.StringToUuidConverter;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.PermitAll;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import zw.co.tayanasoft.data.StockExchange;
import zw.co.tayanasoft.services.StockExchangeService;
import zw.co.tayanasoft.views.MainLayout;

@PageTitle("Stock Exchange")
@Route(value = "stock-exchange/:stockExchangeID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class StockExchangeView extends Div implements BeforeEnterObserver {

    private final String STOCKEXCHANGE_ID = "stockExchangeID";
    private final String STOCKEXCHANGE_EDIT_ROUTE_TEMPLATE = "stock-exchange/%s/edit";

    private final Grid<StockExchange> grid = new Grid<>(StockExchange.class, false);

    private TextField isoCode;
    private TextField name;
    private TextField currencyId;
    private Checkbox active;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<StockExchange> binder;

    private StockExchange stockExchange;

    private final StockExchangeService stockExchangeService;

    public StockExchangeView(StockExchangeService stockExchangeService) {
        this.stockExchangeService = stockExchangeService;
        addClassNames("stock-exchange-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("isoCode").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("currencyId").setAutoWidth(true);
        LitRenderer<StockExchange> activeRenderer = LitRenderer.<StockExchange>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", active -> active.isActive() ? "check" : "minus").withProperty("color",
                        active -> active.isActive()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(activeRenderer).setHeader("Active").setAutoWidth(true);

        grid.setItems(query -> stockExchangeService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(STOCKEXCHANGE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(StockExchangeView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(StockExchange.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(currencyId).withConverter(new StringToUuidConverter("Invalid UUID")).bind("currencyId");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.stockExchange == null) {
                    this.stockExchange = new StockExchange();
                }
                binder.writeBean(this.stockExchange);
                stockExchangeService.update(this.stockExchange);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(StockExchangeView.class);
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
        Optional<Long> stockExchangeId = event.getRouteParameters().get(STOCKEXCHANGE_ID).map(Long::parseLong);
        if (stockExchangeId.isPresent()) {
            Optional<StockExchange> stockExchangeFromBackend = stockExchangeService.get(stockExchangeId.get());
            if (stockExchangeFromBackend.isPresent()) {
                populateForm(stockExchangeFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested stockExchange was not found, ID = %s", stockExchangeId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(StockExchangeView.class);
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
        isoCode = new TextField("Iso Code");
        name = new TextField("Name");
        currencyId = new TextField("Currency Id");
        active = new Checkbox("Active");
        formLayout.add(isoCode, name, currencyId, active);

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

    private void populateForm(StockExchange value) {
        this.stockExchange = value;
        binder.readBean(this.stockExchange);

    }
}
