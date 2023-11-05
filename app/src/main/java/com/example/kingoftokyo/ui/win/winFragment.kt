package com.example.kingoftokyo.ui.win

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.kingoftokyo.R

class WinFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_win, container, false)



        val winTextView = view.findViewById<TextView>(R.id.winTexteView)
        winTextView.text = "Félicitations, vous avez gagné !" // Personnalisez votre texte ici

        val returnToMenuButton = view.findViewById<Button>(R.id.ReturnToMenuButton)

        returnToMenuButton.setOnClickListener {
            val navController = Navigation.findNavController(requireView())
            navController.navigate(R.id.action_winFragment_to_loginFragment)
        }

        return view
    }
}
