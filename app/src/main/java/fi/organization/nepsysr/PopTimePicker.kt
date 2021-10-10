package fi.organization.nepsysr

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment

class PopTimePicker : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Retrieves current times from the device
        val c = Calendar.getInstance()
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]

        return TimePickerDialog(
            activity, activity as OnTimeSetListener?, hour, minute, DateFormat.is24HourFormat(
                activity
            )
        )
    }
}
