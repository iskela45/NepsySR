package fi.organization.nepsysr.utilities

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment

class PopTimePicker : DialogFragment() {

    private var mListener: OnTimeSetListener? = null
    private var contexti: Context? = null

    // Called when a fragment is first attached to its context.
    override fun onAttach(context: Context) {
        super.onAttach(requireContext())
        this.contexti = context
    }

    fun setListener(mListener: OnTimeSetListener) {
        this.mListener = mListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Retrieves current times from the device
        val c = Calendar.getInstance()
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]

        return TimePickerDialog(
            context,
            mListener,
            hour,
            minute,
            DateFormat.is24HourFormat(context)
        )
    }
}
