package zw.co.tayanasoft.views.inflation;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.PermitAll;
import java.time.Duration;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import zw.co.tayanasoft.data.Inflation;
import zw.co.tayanasoft.services.InflationService;
import zw.co.tayanasoft.views.MainLayout;

@PageTitle("Inflation")
@Route(value = "inflation/:inflationID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class InflationView extends Div implements BeforeEnterObserver {

    private final String INFLATION_ID = "inflationID";
    private final String INFLATION_EDIT_ROUTE_TEMPLATE = "inflation/%s/edit";

    private final Grid<Inflation> grid = new Grid<>(Inflation.class, false);

    private DateTimePicker date;
    private TextField type;
    private TextField inflationRate;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Inflation> binder;

    private Inflation inflation;

    private final InflationService inflationService;

    public InflationView(InflationService inflationService) {
        this.inflationService = inflationService;
        addClassNames("inflation-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("date").setAutoWidth(true);
        grid.addColumn("type").setAutoWidth(true);
        grid.addColumn("inflationRate").setAutoWidth(true);
        grid.setItems(query -> inflationService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(INFLATION_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(InflationView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Inflation.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(inflationRate).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("inflationRate");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.inflation == null) {
                    this.inflation = new Inflation();
                }
                binder.writeBean(this.inflation);
                inflationService.update(this.inflation);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(InflationView.class);
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
        Optional<Long> inflationId = event.getRouteParameters().get(INFLATION_ID).map(Long::parseLong);
        if (inflationId.isPresent()) {
            Optional<Inflation> inflationFromBackend = inflationService.get(inflationId.get());
            if (inflationFromBackend.isPresent()) {
                populateForm(inflationFromBackend.get());
            } else {
                Notification.show(String.format("The requested inflation was not found, ID = %s", inflationId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(InflationView.class);
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
        date = new DateTimePicker("Date");
        date.setStep(Duration.ofSeconds(1));
        type = new TextField("Type");
        inflationRate = new TextField("Inflation Rate");
        formLayout.add(date, type, inflationRate);

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

    private void populateForm(Inflation value) {
        this.inflation = value;
        binder.readBean(this.inflation);

    }
}
