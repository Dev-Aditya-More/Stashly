package nodomain.aditya1875more.stashly.ui.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import nodomain.aditya1875more.stashly.ui.viewmodels.getFileName

@Composable
fun ReplaceFileButton(
    modifier: Modifier = Modifier,
    onFileReplaced: (Uri, String) -> Unit
) {
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        // OpenDocument (not GetContent) so the permission can be persisted like a fresh upload;
        // otherwise the replaced file stops opening after a restart
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { pickedUri ->
            try {
                context.contentResolver.takePersistableUriPermission(
                    pickedUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                Log.e("ReplaceFileButton", "Failed to take persistable permission", e)
            }
            val fileName = getFileName(context, pickedUri)
            onFileReplaced(pickedUri, fileName)
        }
    }

    Button(
        onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Replace File"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Replace File")
    }
}

