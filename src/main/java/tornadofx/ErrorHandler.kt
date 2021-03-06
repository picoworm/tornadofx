package tornadofx

import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox

import java.io.ByteArrayOutputStream
import java.io.PrintWriter

class DefaultErrorHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, error: Throwable) {
        Platform.runLater {
            val cause = Label(if (error.cause != null) error.cause?.message else "").apply {
                style = "-fx-font-weight: bold"
            }

            val textarea = TextArea().apply {
                prefRowCount = 20
                prefColumnCount = 50
                text = stringFromError(error)
            }

            Alert(ERROR).apply {
                title = error.message ?: "An error occured"
                isResizable = true
                headerText = "Error in " + error.stackTrace[0].toString()
                dialogPane.content = VBox(cause, textarea)
                showAndWait()
            }
        }
    }

}

private fun stringFromError(e: Throwable): String {
    val out = ByteArrayOutputStream()
    val writer = PrintWriter(out)
    e.printStackTrace(writer)
    writer.close()
    return out.toString()
}