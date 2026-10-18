package com.lanrhyme.micyou

import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileNameExtensionFilter

actual fun openPluginFileChooser(onResult: (String?) -> Unit) {
    val filePath = if (!SwingUtilities.isEventDispatchThread()) {
        var result: String? = null
        SwingUtilities.invokeAndWait {
            result = openFileChooserInternal()
        }
        result
    } else {
        openFileChooserInternal()
    }
    onResult(filePath)
}

private fun openFileChooserInternal(): String? {
    val chooser = JFileChooser().apply {
        fileFilter = FileNameExtensionFilter(
            "Plugin Files (*.zip, *.jar)",
            "zip", "jar"
        )
    }
    val chooserResult = chooser.showOpenDialog(null)
    return if (chooserResult == JFileChooser.APPROVE_OPTION) {
        chooser.selectedFile.absolutePath
    } else {
        null
    }
}
