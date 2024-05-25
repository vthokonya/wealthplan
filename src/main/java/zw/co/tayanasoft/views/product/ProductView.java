package zw.co.tayanasoft.views.product;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import zw.co.tayanasoft.data.Product;
import zw.co.tayanasoft.services.ProductService;
import zw.co.tayanasoft.views.MainLayout;

@PageTitle("Product")
@Route(value = "product/:productID?/:action?(edit)", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class ProductView extends Div implements BeforeEnterObserver {

    private final String PRODUCT_ID = "productID";
    private final String PRODUCT_EDIT_ROUTE_TEMPLATE = "product/%s/edit";

    private final Grid<Product> grid = new Grid<>(Product.class, false);

    private TextField ventureId;
    private TextField name;
    private TextField category;
    private Checkbox weight;
    private DatePicker lastCountDate;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Product> binder;

    private Product product;

    private final ProductService productService;

    public ProductView(ProductService productService) {
        this.productService = productService;
        addClassNames("product-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("ventureId").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("category").setAutoWidth(true);
        LitRenderer<Product> weightRenderer = LitRenderer.<Product>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", weight -> weight.isWeight() ? "check" : "minus").withProperty("color",
                        weight -> weight.isWeight()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(weightRenderer).setHeader("Weight").setAutoWidth(true);

        grid.addColumn("lastCountDate").setAutoWidth(true);
        grid.setItems(query -> productService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PRODUCT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ProductView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Product.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(ventureId).withConverter(new StringToUuidConverter("Invalid UUID")).bind("ventureId");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.product == null) {
                    this.product = new Product();
                }
                binder.writeBean(this.product);
                productService.update(this.product);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(ProductView.class);
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
        Optional<Long> productId = event.getRouteParameters().get(PRODUCT_ID).map(Long::parseLong);
        if (productId.isPresent()) {
            Optional<Product> productFromBackend = productService.get(productId.get());
            if (productFromBackend.isPresent()) {
                populateForm(productFromBackend.get());
            } else {
                Notification.show(String.format("The requested product was not found, ID = %s", productId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ProductView.class);
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
        ventureId = new TextField("Venture Id");
        name = new TextField("Name");
        category = new TextField("Category");
        weight = new Checkbox("Weight");
        lastCountDate = new DatePicker("Last Count Date");
        formLayout.add(ventureId, name, category, weight, lastCountDate);

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

    private void populateForm(Product value) {
        this.product = value;
        binder.readBean(this.product);

    }
}
