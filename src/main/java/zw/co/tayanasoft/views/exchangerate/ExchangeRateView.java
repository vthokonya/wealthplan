package zw.co.tayanasoft.views.exchangerate;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.PermitAll;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import zw.co.tayanasoft.data.ExchangeRate;
import zw.co.tayanasoft.services.ExchangeRateService;
import zw.co.tayanasoft.views.MainLayout;

@PageTitle("Exchange Rate")
@Route(value = "exchange-rate/:exchangeRateID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class ExchangeRateView extends Div implements BeforeEnterObserver {

    private final String EXCHANGERATE_ID = "exchangeRateID";
    private final String EXCHANGERATE_EDIT_ROUTE_TEMPLATE = "exchange-rate/%s/edit";

    private final Grid<ExchangeRate> grid = new Grid<>(ExchangeRate.class, false);

    private TextField currencyId;
    private TextField primaryCurrencyId;
    private DatePicker startDate;
    private TextField exchangeRate;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<ExchangeRate> binder;

    private ExchangeRate exchangeRate1;

    private final ExchangeRateService exchangeRate1Service;

    public ExchangeRateView(ExchangeRateService exchangeRate1Service) {
        this.exchangeRate1Service = exchangeRate1Service;
        addClassNames("exchange-rate-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("currencyId").setAutoWidth(true);
        grid.addColumn("primaryCurrencyId").setAutoWidth(true);
        grid.addColumn("startDate").setAutoWidth(true);
        grid.addColumn("exchangeRate").setAutoWidth(true);
        grid.setItems(query -> exchangeRate1Service.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(EXCHANGERATE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ExchangeRateView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(ExchangeRate.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(currencyId).withConverter(new StringToUuidConverter("Invalid UUID")).bind("currencyId");
        binder.forField(primaryCurrencyId).withConverter(new StringToUuidConverter("Invalid UUID"))
                .bind("primaryCurrencyId");
        binder.forField(exchangeRate).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("exchangeRate");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.exchangeRate1 == null) {
                    this.exchangeRate1 = new ExchangeRate();
                }
                binder.writeBean(this.exchangeRate1);
                exchangeRate1Service.update(this.exchangeRate1);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(ExchangeRateView.class);
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
        Optional<Long> exchangeRate1Id = event.getRouteParameters().get(EXCHANGERATE_ID).map(Long::parseLong);
        if (exchangeRate1Id.isPresent()) {
            Optional<ExchangeRate> exchangeRate1FromBackend = exchangeRate1Service.get(exchangeRate1Id.get());
            if (exchangeRate1FromBackend.isPresent()) {
                populateForm(exchangeRate1FromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested exchangeRate1 was not found, ID = %s", exchangeRate1Id.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ExchangeRateView.class);
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
        currencyId = new TextField("Currency Id");
        primaryCurrencyId = new TextField("Primary Currency Id");
        startDate = new DatePicker("Start Date");
        exchangeRate = new TextField("Exchange Rate");
        formLayout.add(currencyId, primaryCurrencyId, startDate, exchangeRate);

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

    private void populateForm(ExchangeRate value) {
        this.exchangeRate1 = value;
        binder.readBean(this.exchangeRate1);

    }
}
