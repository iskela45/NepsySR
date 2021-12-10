import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import fi.organization.nepsysr.R
import android.graphics.drawable.ColorDrawable
import fi.organization.nepsysr.utilities.ProfileInterface
import java.lang.String


class ColorPickerFragment : DialogFragment() {

    private lateinit var communicator: ProfileInterface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_color_picker, container, false)

        var submitButton = rootView.findViewById<Button>(R.id.btSubmit)
        var colorRadioGroup = rootView.findViewById<RadioGroup>(R.id.rgColors)
        submitButton.setOnClickListener {
            val selectedID = colorRadioGroup.checkedRadioButtonId
            val selectedButton = rootView.findViewById<RadioButton>(selectedID)
            val viewColor = selectedButton.background as ColorDrawable
            val color = viewColor.color
            val hexColor = "#" + Integer.toHexString(color).substring(2)

            Toast.makeText(context, hexColor, Toast.LENGTH_LONG).show()

            communicator = activity as ProfileInterface
            communicator.passData(hexColor)

            dismiss()
        }

        return rootView
    }
}