package zw.co.tayanasoft.views.currency;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import zw.co.tayanasoft.data.Currency;
import zw.co.tayanasoft.services.CurrencyService;
import zw.co.tayanasoft.views.MainLayout;

@PageTitle("Currency")
@Route(value = "currency/:currencyID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class CurrencyView extends Div implements BeforeEnterObserver {

    private final String CURRENCY_ID = "currencyID";
    private final String CURRENCY_EDIT_ROUTE_TEMPLATE = "currency/%s/edit";

    private final Grid<Currency> grid = new Grid<>(Currency.class, false);

    private TextField isoCode;
    private TextField name;
    private TextField countryId;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Currency> binder;

    private Currency currency;

    private final CurrencyService currencyService;

    public CurrencyView(CurrencyService currencyService) {
        this.currencyService = currencyService;
        addClassNames("currency-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("isoCode").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("countryId").setAutoWidth(true);
        grid.setItems(query -> currencyService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(CURRENCY_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(CurrencyView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Currency.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(countryId).withConverter(new StringToUuidConverter("Invalid UUID")).bind("countryId");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.currency == null) {
                    this.currency = new Currency();
                }
                binder.writeBean(this.currency);
                currencyService.update(this.currency);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(CurrencyView.class);
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
        Optional<Long> currencyId = event.getRouteParameters().get(CURRENCY_ID).map(Long::parseLong);
        if (currencyId.isPresent()) {
            Optional<Currency> currencyFromBackend = currencyService.get(currencyId.get());
            if (currencyFromBackend.isPresent()) {
                populateForm(currencyFromBackend.get());
            } else {
                Notification.show(String.format("The requested currency was not found, ID = %s", currencyId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(CurrencyView.class);
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
        countryId = new TextField("Country Id");
        formLayout.add(isoCode, name, countryId);

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

    private void populateForm(Currency value) {
        this.currency = value;
        binder.readBean(this.currency);

    }
}
