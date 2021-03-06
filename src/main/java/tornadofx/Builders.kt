package tornadofx

import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.text.Text
import javafx.util.Callback
import javafx.util.StringConverter
import tornadofx.control.DatePickerTableCell
import java.time.LocalDate
import java.util.concurrent.Callable
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

fun Pane.titledpane(title: String, node: Node): TitledPane {
    val pane = TitledPane(title, node)
    return opcr(this, pane)
}

fun Pane.tabpane(op: (TabPane.() -> Unit)? = null) = opcr(this, TabPane(), op)

fun <T : Node> TabPane.tab(text: String, content: T, op: (T.() -> Unit)? = null): Tab {
    val tab = Tab(text, content)
    tabs.add(tab)
    if (op != null) op(content)
    return tab
}

val GridPaneRowIdKey = "TornadoFX.GridPaneRowId";

fun GridPane.row(title: String? = null, op: Pane.() -> Unit) {
    properties[GridPaneRowIdKey] = if (properties.containsKey(GridPaneRowIdKey)) properties[GridPaneRowIdKey] as Int + 1 else 1

    // Allow the caller to add children to a fake pane
    val fake = Pane()
    if (title != null)
        fake.children.add(Label(title))

    op(fake)

    // Create a new row in the GridPane and add the children added to the fake pane
    addRow(properties[GridPaneRowIdKey] as Int, *fake.children.toTypedArray())
}

fun Pane.text(initialValue: String? = null, op: (Text.() -> Unit)? = null) = opcr(this, Text().apply { if (initialValue != null) text = initialValue }, op)
fun Pane.text(property: Property<String>, op: (Text.() -> Unit)? = null) = text(op = op).apply {
    textProperty().bindBidirectional(property)
}

fun <T> Pane.combobox(property: Property<T>? = null, values: ObservableList<T>? = null, op: (ComboBox<T>.() -> Unit)? = null) = opcr(this, ComboBox<T>().apply {
    if (values != null) items = values
    if (property != null) valueProperty().bindBidirectional(property)
}, op)

fun <T> Pane.listview(values: ObservableList<T>? = null, op: (ListView<T>.() -> Unit)? = null) = opcr(this, ListView<T>().apply {
    values?.let { items = it }
}, op)

fun Pane.textfield(value: String? = null, op: (TextField.() -> Unit)? = null) = opcr(this, TextField().apply { if (value != null) text = value }, op)
fun Pane.textfield(property: Property<String>, op: (TextField.() -> Unit)? = null) = textfield(op = op).apply {
    textProperty().bindBidirectional(property)
}

fun <T> Pane.textfield(property: Property<T>, converter: StringConverter<T>, op: (TextField.() -> Unit)? = null) = textfield(op = op).apply {
    textProperty().bindBidirectional(property, converter)
}

fun Pane.datepicker(op: (DatePicker.() -> Unit)? = null) = opcr(this, DatePicker(), op)
fun Pane.datepicker(property: Property<LocalDate>, op: (DatePicker.() -> Unit)? = null) = datepicker(op = op).apply {
    valueProperty().bindBidirectional(property)
}

fun Pane.textarea(value: String? = null, op: (TextArea.() -> Unit)? = null) = opcr(this, TextArea().apply { if (value != null) text = value }, op)
fun Pane.textarea(property: Property<String>, op: (TextArea.() -> Unit)? = null) = textarea(op = op).apply {
    textProperty().bindBidirectional(property)
}

fun <T> Pane.textarea(property: Property<T>, converter: StringConverter<T>, op: (TextArea.() -> Unit)? = null) = textarea(op = op).apply {
    textProperty().bindBidirectional(property, converter)
}

fun Pane.checkbox(text: String? = null, property: Property<Boolean>? = null, op: (CheckBox.() -> Unit)? = null) = opcr(this, CheckBox(text).apply {
    if (property != null) selectedProperty().bindBidirectional(property)
}, op)

fun Pane.progressindicator(op: (ProgressIndicator.() -> Unit)? = null) = opcr(this, ProgressIndicator(), op)

fun Pane.progressbar(initialValue: Double? = null, op: (ProgressBar.() -> Unit)? = null) = opcr(this, ProgressBar().apply { if (initialValue != null) progress = initialValue }, op)
fun Pane.progressbar(property: Property<Double>, op: (ProgressBar.() -> Unit)? = null) = progressbar(op = op).apply {
    progressProperty().bind(property)
}

fun Pane.button(text: String = "", op: (Button.() -> Unit)? = null) = opcr(this, Button(text), op)

fun ToolBar.button(text: String = "", op: (Button.() -> Unit)? = null): Button {
    val button = Button(text)
    items.add(button)
    op?.invoke(button)
    return button
}

fun Pane.label(text: String = "", op: (Label.() -> Unit)? = null) = opcr(this, Label(text), op)
fun Pane.label(property: Property<String>, op: (Label.() -> Unit)? = null) = label(op = op).apply {
    textProperty().bind(property)
}

fun Pane.menubar(op: (MenuBar.() -> Unit)? = null) = opcr(this, MenuBar(), op)

fun Pane.imageview(url: String? = null, op: (ImageView.() -> Unit)? = null) = opcr(this, if (url == null) ImageView() else ImageView(url), op)


fun Pane.scrollpane(op: (Pane.() -> Unit)) {
    val vbox = VBox()
    op(vbox)
    val scrollPane = ScrollPane()
    scrollPane.content = if (vbox.children.size == 1) vbox.children[0] else vbox
    this += scrollPane
}

fun HBox.spacer(prio: Priority = Priority.ALWAYS, op: (Pane.() -> Unit)? = null) = opcr(this, Pane().apply { HBox.setHgrow(this, prio) }, op)
fun VBox.spacer(prio: Priority = Priority.ALWAYS, op: (Pane.() -> Unit)? = null) = opcr(this, Pane().apply { VBox.setVgrow(this, prio) }, op)

fun Pane.toolbar(vararg nodes: Node, op: (ToolBar.() -> Unit)? = null): ToolBar {
    var toolbar = ToolBar()
    if (nodes.isNotEmpty())
        toolbar.items.addAll(nodes)
    opcr(this, toolbar, op)
    return toolbar
}

fun Pane.splitpane(vararg nodes: Node, op: (SplitPane.() -> Unit)? = null): SplitPane {
    var splitpane = SplitPane()
    if (nodes.isNotEmpty())
        splitpane.items.addAll(nodes)
    opcr(this, splitpane, op)
    return splitpane
}

fun SplitPane.items(op: (Pane.() -> Unit)) {
    val fake = Pane()
    op(fake)
    items.addAll(fake.children)
}

fun Pane.accordion(vararg panes: TitledPane, op: (Accordion.() -> Unit)? = null): Accordion {
    var accordion = Accordion()
    if (panes.isNotEmpty())
        accordion.panes.addAll(panes)
    opcr(this, accordion, op)
    return accordion
}

fun Accordion.fold(title: String? = null, op: (Pane.() -> Unit)): TitledPane {
    val vbox = VBox()
    op(vbox)
    val fold = TitledPane(title, if (vbox.children.size == 1) vbox.children[0] else vbox)
    panes += fold
    return fold
}

fun Pane.hbox(spacing: Double? = null, children: Iterable<Node>? = null, op: (HBox.() -> Unit)? = null): HBox {
    val hbox = HBox()
    if (children != null)
        hbox.children.addAll(children)
    if (spacing != null) hbox.spacing = spacing
    return opcr(this, hbox, op)
}

fun Pane.vbox(spacing: Double? = null, children: Iterable<Node>? = null, op: (VBox.() -> Unit)? = null): VBox {
    val vbox = VBox()
    if (children != null)
        vbox.children.addAll(children)
    if (spacing != null) vbox.spacing = spacing
    return opcr(this, vbox, op)
}

fun Pane.stackpane(initialChildren: Iterable<Node>? = null, op: (StackPane.() -> Unit)? = null) = opcr(this, StackPane().apply { if (initialChildren != null) children.addAll(initialChildren) }, op)
fun Pane.gridpane(op: (GridPane.() -> Unit)? = null) = opcr(this, GridPane(), op)
fun Pane.flowpane(op: (FlowPane.() -> Unit)? = null) = opcr(this, FlowPane(), op)
fun Pane.tilepane(op: (TilePane.() -> Unit)? = null) = opcr(this, TilePane(), op)
fun Pane.borderpane(op: (BorderPane.() -> Unit)? = null) = opcr(this, BorderPane(), op)

fun BorderPane.top(op: Pane.() -> Unit) {
    val vbox = VBox()
    op(vbox)
    top = if (vbox.children.size == 1) vbox.children[0] else vbox
}

fun BorderPane.bottom(op: Pane.() -> Unit) {
    val vbox = VBox()
    op(vbox)
    bottom = if (vbox.children.size == 1) vbox.children[0] else vbox
}

fun BorderPane.left(op: Pane.() -> Unit) {
    val vbox = VBox()
    op(vbox)
    left = if (vbox.children.size == 1) vbox.children[0] else vbox
}

fun BorderPane.right(op: Pane.() -> Unit) {
    val vbox = VBox()
    op(vbox)
    right = if (vbox.children.size == 1) vbox.children[0] else vbox
}

fun BorderPane.center(op: Pane.() -> Unit) {
    val vbox = VBox()
    op(vbox)
    center = if (vbox.children.size == 1) vbox.children[0] else vbox
}

/**
 * Add the given node to the pane, invoke the node operation and return the node
 */
internal fun <T : Node> opcr(pane: Pane, node: T, op: (T.() -> Unit)? = null): T {
    pane.children.add(node)
    op?.invoke(node)
    return node
}

inline fun <S> Pane.tableview(items: ObservableList<S>? = null, op: (TableView<S>.() -> Unit)): TableView<S> {
    val tableview = TableView<S>()
    if (items != null) tableview.items = items
    op(tableview)
    children.add(tableview)
    return tableview
}

inline fun <S> Pane.treeview(root: TreeItem<S>? = null, op: (TreeView<S>.() -> Unit)): TreeView<S> {
    val treeview = TreeView<S>()
    if (root != null) treeview.root = root
    op(treeview)
    children.add(treeview)
    return treeview
}

inline fun <S> Pane.treetableview(root: TreeItem<S>? = null, op: (TreeTableView<S>.() -> Unit)): TreeTableView<S> {
    val treetableview = TreeTableView<S>()
    if (root != null) treetableview.root = root
    op(treetableview)
    children.add(treetableview)
    return treetableview
}

fun <S> TableView<S>.makeIndexColumn(name: String = "#", startNumber: Int = 1): TableColumn<S, Number> {
    return TableColumn<S, Number>(name).apply {
        isSortable = false
        prefWidth = width
        this@makeIndexColumn.columns += this
        setCellValueFactory { ReadOnlyObjectWrapper(items.indexOf(it.value) + startNumber) };
    }
}

fun <S, T> TableColumn<S, T>.enableTextWrap(): TableColumn<S, T> {
    setCellFactory {
        TableCell<S, T>().apply {
            val text = Text();
            graphic = text
            prefHeight = Control.USE_COMPUTED_SIZE
            text.wrappingWidthProperty().bind(this@enableTextWrap.widthProperty().subtract(Bindings.multiply(2.0, graphicTextGapProperty())))
            text.textProperty().bind(Bindings.createStringBinding(Callable {
                itemProperty().get()?.toString() ?: ""
            }, itemProperty()))
        }
    }
    return this
}

/**
 * Create a column with a value factory that extracts the value from the given callback.
 */
fun <S, T> TableView<S>.column(title: String, valueProvider: (TableColumn.CellDataFeatures<S, T>) -> ObservableValue<T>): TableColumn<S, T> {
    val column = TableColumn<S, T>(title)
    column.cellValueFactory = Callback { valueProvider(it) }
    columns.add(column)
    return column
}

fun <S, T> TableColumn<S, T?>.makeComboBox(items: ObservableList<T>, afterCommit: ((TableColumn.CellEditEvent<S, T?>) -> Unit)? = null): TableColumn<S, T?> {
    cellFactory = ComboBoxTableCell.forTableColumn(items)
    setOnEditCommit {
        val property = it.tableColumn.getCellObservableValue(it.rowValue) as ObjectProperty<T?>
        property.value = it.newValue
        afterCommit?.invoke(it)
    }
    return this
}

/**
 * Create an editable DatePicker TableCell. This control requires tornadofx-controls on the classpath.
 */
fun <S> TableColumn<S, LocalDate?>.makeDatePicker(converter: StringConverter<LocalDate>? = DatePickerTableCell.DefaultLocalDateConverter(), afterCommit: ((TableColumn.CellEditEvent<S, LocalDate?>) -> Unit)? = null): TableColumn<S, LocalDate?> {
    cellFactory = DatePickerTableCell.forTableColumn(converter)
    setOnEditCommit {
        val property = it.tableColumn.getCellObservableValue(it.rowValue) as ObjectProperty<LocalDate?>
        property.value = it.newValue
        afterCommit?.invoke(it)
    }
    return this
}

inline fun <S, reified T> TableColumn<S, T?>.makeTextField(converter: StringConverter<T>? = null, noinline afterCommit: ((TableColumn.CellEditEvent<S, T?>) -> Unit)? = null): TableColumn<S, T?> {
    when (T::class) {
        String::class -> {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val stringColumn = this as TableColumn<S, String?>
            stringColumn.cellFactory = TextFieldTableCell.forTableColumn()
        }
        else -> {
            if (converter == null)
                throw IllegalArgumentException("You must supply a converter for non String columns")
            cellFactory = TextFieldTableCell.forTableColumn(converter)
        }
    }

    setOnEditCommit {
        val property = it.tableColumn.getCellObservableValue(it.rowValue) as ObjectProperty<T?>
        property.value = it.newValue
        afterCommit?.invoke(it)
    }
    return this
}

fun <S, T> TableColumn<S, T?>.makeChoiceBox(items: ObservableList<T>, afterCommit: ((TableColumn.CellEditEvent<S, T?>) -> Unit)? = null): TableColumn<S, T?> {
    cellFactory = ChoiceBoxTableCell.forTableColumn(items)
    setOnEditCommit {
        val property = it.tableColumn.getCellObservableValue(it.rowValue) as ObjectProperty<T?>
        property.value = it.newValue
        afterCommit?.invoke(it)
    }
    return this
}

fun <S> TableColumn<S, Double?>.makeProgressBar(afterCommit: ((TableColumn.CellEditEvent<S, Double?>) -> Unit)? = null): TableColumn<S, Double?> {
    cellFactory = ProgressBarTableCell.forTableColumn()
    setOnEditCommit {
        val property = it.tableColumn.getCellObservableValue(it.rowValue) as ObjectProperty<Double?>
        property.value = it.newValue
        afterCommit?.invoke(it)
    }
    return this
}

fun <S> TableColumn<S, Boolean?>.makeCheckbox(): TableColumn<S, Boolean?> {
    cellFactory = CheckBoxTableCell.forTableColumn(this)
    return this
}

/**
 * Create a column with a value factory that extracts the value from the given mutable
 * property and converts the property to an observable value.
 */
fun <S, T> TableView<S>.column(title: String, prop: KMutableProperty1<S, T>): TableColumn<S, T> {
    val column = TableColumn<S, T>(title)
    column.cellValueFactory = Callback { observable(it.value, prop) }
    columns.add(column)
    return column
}

fun <S, T> TreeTableView<S>.column(title: String, prop: KMutableProperty1<S, T>): TreeTableColumn<S, T> {
    val column = TreeTableColumn<S, T>(title)
    column.cellValueFactory = Callback { observable(it.value.value, prop) }
    columns.add(column)
    return column
}

/**
 * Create a column with a value factory that extracts the value from the given property and
 * converts the property to an observable value.
 */
fun <S, T> TableView<S>.column(title: String, prop: KProperty1<S, T>): TableColumn<S, T> {
    val column = TableColumn<S, T>(title)
    column.cellValueFactory = Callback { observable(it.value, prop) }
    columns.add(column)
    return column
}

fun <S, T> TreeTableView<S>.column(title: String, prop: KProperty1<S, T>): TreeTableColumn<S, T> {
    val column = TreeTableColumn<S, T>(title)
    column.cellValueFactory = Callback { observable(it.value.value, prop) }
    columns.add(column)
    return column
}

/**
 * Create a column with a value factory that extracts the value from the given ObservableValue property
 */
@JvmName(name = "columnForObservableProperty")
fun <S, T> TableView<S>.column(title: String, prop: KProperty1<S, ObservableValue<T>>): TableColumn<S, T> {
    val column = TableColumn<S, T>(title)
    column.cellValueFactory = Callback { prop.call(it.value) }
    columns.add(column)
    return column
}

@JvmName(name = "columnForObservableProperty")
fun <S, T> TreeTableView<S>.column(title: String, prop: KProperty1<S, ObservableValue<T>>): TreeTableColumn<S, T> {
    val column = TreeTableColumn<S, T>(title)
    column.cellValueFactory = Callback { prop.call(it.value.value) }
    columns.add(column)
    return column
}

/**
 * Create a column with a value factory that extracts the observable value from the given function reference.
 * This method requires that you have kotlin-reflect on your classpath.
 */
inline fun <S, reified T> TableView<S>.column(title: String, observableFn: KFunction<ObservableValue<T>>): TableColumn<S, T> {
    val column = TableColumn<S, T>(title)
    column.cellValueFactory = ReflectionHelper.TableCellValueFunctionRefCallback(observableFn)
    columns.add(column)
    return column
}

inline fun <S, reified T> TreeTableView<S>.column(title: String, observableFn: KFunction<ObservableValue<T>>): TreeTableColumn<S, T> {
    val column = TreeTableColumn<S, T>(title)
    column.cellValueFactory = ReflectionHelper.TreeTableCellValueFunctionRefCallback(observableFn)
    columns.add(column)
    return column
}