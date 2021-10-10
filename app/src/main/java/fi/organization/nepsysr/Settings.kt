package fi.organization.nepsysr

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment

class Settings : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    private lateinit var presentAlarmTime : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        this.presentAlarmTime = findViewById(R.id.presentAlarmTime)

        presentAlarmTime.setOnClickListener(View.OnClickListener {
            val timePicker: DialogFragment = PopTimePicker()

            timePicker.show(supportFragmentManager, "time picker")
        })
    }

    // Sets time to the textview when the user has pressed ok on the timepicker
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        presentAlarmTime.text = "$hourOfDay:$minute"
    }
}