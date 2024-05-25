package zw.co.tayanasoft.views.venture;

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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import zw.co.tayanasoft.data.Venture;
import zw.co.tayanasoft.services.VentureService;
import zw.co.tayanasoft.views.MainLayout;

@PageTitle("Venture")
@Route(value = "venture/:ventureID?/:action?(edit)", layout = MainLayout.class)
@AnonymousAllowed
public class VentureView extends Div implements BeforeEnterObserver {

    private final String VENTURE_ID = "ventureID";
    private final String VENTURE_EDIT_ROUTE_TEMPLATE = "venture/%s/edit";

    private final Grid<Venture> grid = new Grid<>(Venture.class, false);

    private TextField ownerId;
    private TextField ownerName;
    private TextField type;
    private TextField currencyId;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Venture> binder;

    private Venture venture;

    private final VentureService ventureService;

    public VentureView(VentureService ventureService) {
        this.ventureService = ventureService;
        addClassNames("venture-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("ownerId").setAutoWidth(true);
        grid.addColumn("ownerName").setAutoWidth(true);
        grid.addColumn("type").setAutoWidth(true);
        grid.addColumn("currencyId").setAutoWidth(true);
        grid.setItems(query -> ventureService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(VENTURE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(VentureView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Venture.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(ownerId).withConverter(new StringToUuidConverter("Invalid UUID")).bind("ownerId");
        binder.forField(currencyId).withConverter(new StringToUuidConverter("Invalid UUID")).bind("currencyId");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.venture == null) {
                    this.venture = new Venture();
                }
                binder.writeBean(this.venture);
                ventureService.update(this.venture);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(VentureView.class);
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
        Optional<Long> ventureId = event.getRouteParameters().get(VENTURE_ID).map(Long::parseLong);
        if (ventureId.isPresent()) {
            Optional<Venture> ventureFromBackend = ventureService.get(ventureId.get());
            if (ventureFromBackend.isPresent()) {
                populateForm(ventureFromBackend.get());
            } else {
                Notification.show(String.format("The requested venture was not found, ID = %s", ventureId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(VentureView.class);
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
        ownerId = new TextField("Owner Id");
        ownerName = new TextField("Owner Name");
        type = new TextField("Type");
        currencyId = new TextField("Currency Id");
        formLayout.add(ownerId, ownerName, type, currencyId);

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

    private void populateForm(Venture value) {
        this.venture = value;
        binder.readBean(this.venture);

    }
}
