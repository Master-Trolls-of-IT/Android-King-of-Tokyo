package com.example.kingoftokyo.ui.win

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.kingoftokyo.R

class WinFragment : Fragment() {
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.win);
        mediaPlayer.isLooping = true;
        mediaPlayer.start();

        var winnerName = arguments?.getString("winner_name")
        val view = inflater.inflate(R.layout.fragment_win, container, false)

        val winTextView = view.findViewById<TextView>(R.id.winTexteView)
        winTextView.text = "Félicitations, vous avez gagné !   " + winnerName

        val winImageView = view.findViewById<ImageView>(R.id.winnerImage)
        winImageView.setImageResource(R.drawable.panda)

        val returnToMenuButton = view.findViewById<Button>(R.id.ReturnToMenuButton)

        returnToMenuButton.setOnClickListener {
            val navController = Navigation.findNavController(requireView())
            navController.navigate(R.id.action_winFragment_to_loginFragment)
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }
}
