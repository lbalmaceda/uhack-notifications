package com.auth0.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.auth0.notifications.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class NotificationsDashboardFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val accessGranted = NotificationListener.isNotificationAccessGranted(requireContext())
        binding.textviewNotificationsAccessStatus.setText(if (accessGranted) R.string.access_granted else R.string.access_not_granted)
        binding.buttonOpenPreferences.apply {
            setOnClickListener {
                NotificationListener.showNotificationsAccessPreference(requireContext())
            }
            isEnabled = !accessGranted
        }

        binding.buttonCreateNotification.setOnClickListener {
            NotificationListener.createSampleNotification(requireContext())
        }
        binding.buttonAcceptNotification.setOnClickListener {
            /*
             * TODO: For debugging purposes. Resolve positively the notification
             *  This could be called from a PendingIntent (i.e.
             */
            Toast.makeText(requireContext(), "Not implemented", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}