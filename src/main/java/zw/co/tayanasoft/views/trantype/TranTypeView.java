package zw.co.tayanasoft.views.trantype;

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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.PermitAll;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import zw.co.tayanasoft.data.TransactionType;
import zw.co.tayanasoft.services.TransactionTypeService;
import zw.co.tayanasoft.views.MainLayout;

@PageTitle("Tran Type")
@Route(value = "tran-type/:transactionTypeID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class TranTypeView extends Div implements BeforeEnterObserver {

    private final String TRANSACTIONTYPE_ID = "transactionTypeID";
    private final String TRANSACTIONTYPE_EDIT_ROUTE_TEMPLATE = "tran-type/%s/edit";

    private final Grid<TransactionType> grid = new Grid<>(TransactionType.class, false);

    private TextField code;
    private TextField name;
    private TextField category;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<TransactionType> binder;

    private TransactionType transactionType;

    private final TransactionTypeService transactionTypeService;

    public TranTypeView(TransactionTypeService transactionTypeService) {
        this.transactionTypeService = transactionTypeService;
        addClassNames("tran-type-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("code").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("category").setAutoWidth(true);
        grid.setItems(query -> transactionTypeService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(TRANSACTIONTYPE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(TranTypeView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(TransactionType.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.transactionType == null) {
                    this.transactionType = new TransactionType();
                }
                binder.writeBean(this.transactionType);
                transactionTypeService.update(this.transactionType);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(TranTypeView.class);
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
        Optional<Long> transactionTypeId = event.getRouteParameters().get(TRANSACTIONTYPE_ID).map(Long::parseLong);
        if (transactionTypeId.isPresent()) {
            Optional<TransactionType> transactionTypeFromBackend = transactionTypeService.get(transactionTypeId.get());
            if (transactionTypeFromBackend.isPresent()) {
                populateForm(transactionTypeFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested transactionType was not found, ID = %s", transactionTypeId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(TranTypeView.class);
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
        code = new TextField("Code");
        name = new TextField("Name");
        category = new TextField("Category");
        formLayout.add(code, name, category);

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

    private void populateForm(TransactionType value) {
        this.transactionType = value;
        binder.readBean(this.transactionType);

    }
}
