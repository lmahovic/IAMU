package hr.algebra.boardgames.dialogs

import android.Manifest.permission.READ_CONTACTS
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import hr.algebra.boardgames.R
import hr.algebra.boardgames.framework.isValidPhoneNumber
import hr.algebra.boardgames.model.Item
import hr.algebra.boardgames.viewmodels.FavoriteItemsViewModel

const val REQUEST_SELECT_CONTACT = 1

class EmailFavoritesDialogFragment : DialogFragment() {

    private lateinit var etPhoneNumber: EditText
    private val viewModel: FavoriteItemsViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.message_favorites_dialog, null)
            etPhoneNumber = view.findViewById(R.id.phone_number)
            val btnPickEmail = view.findViewById<Button>(R.id.btnPickEmail)

            btnPickEmail.setOnClickListener {
                selectContact()
            }

            builder.setView(view)
                .setPositiveButton(R.string.send_message, null)
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    dialog!!.cancel()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (!etPhoneNumber.text.isValidPhoneNumber()) {
                etPhoneNumber.text.clear()
                Toast.makeText(requireContext(), "Invalid phone number!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            sendTextMessage(
                etPhoneNumber.text.toString(),
                viewModel.favoriteItems.value!!.joinToString(", ", transform = Item::name)
            )
            dismiss()
        }
    }

    private fun sendTextMessage(phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            putExtra("sms_body", message)
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No email app found!", Toast.LENGTH_LONG).show()
        }
    }

    private fun selectContact() {
        //        if (intent.resolveActivity(requireContext().packageManager) != null) {
//            resultLauncher.launch(intent)
//        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(READ_CONTACTS)
        } else {
            // Start an activity for the user to pick a email from contacts
            val intent = createSelectContactIntent()
            resultLauncher.launch(intent)
        }
    }

    private fun createSelectContactIntent(): Intent {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        return intent
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Start an activity for the user to pick a email from contacts
            val intent = createSelectContactIntent()
            resultLauncher.launch(intent)
        } else {
            // PERMISSION NOT GRANTED
            Toast.makeText(requireContext(), "Permission not granted!", Toast.LENGTH_LONG).show()

        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val contactUri: Uri = data?.data!!

                val projection: Array<String> =
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                requireContext().contentResolver.query(contactUri, projection, null, null, null)
                    .use { cursor ->
                        if (cursor == null) {
                            return@registerForActivityResult
                        }
                        // If the cursor returned is valid, get the email
                        if (cursor.moveToFirst()) {
                            val emailIndex =
                                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            val email = cursor.getString(emailIndex)
                            // Do something with the email
                            etPhoneNumber.setText(email)
                        }
                    }
            }
        }
}