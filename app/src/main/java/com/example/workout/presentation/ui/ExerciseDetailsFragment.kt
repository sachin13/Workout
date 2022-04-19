package com.example.workout.presentation.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.workout.R
import com.example.workout.databinding.FragmentExerciseDetailsBinding
import com.example.workout.presentation.viewmodel.ExercisesViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExerciseDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ExerciseDetailsFragment : Fragment() {
    private lateinit var _binding: FragmentExerciseDetailsBinding
    private val binding get() = _binding
    private lateinit var exerciseViewModel: ExercisesViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.let {
            exerciseViewModel = ViewModelProvider(it!!).get(ExercisesViewModel::class.java)
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_exerciseDetailsFragment_to_homeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        exerciseViewModel.selectedExercise.observe(viewLifecycleOwner){
            Log.d("TAG", "onViewCreatedDetail:$it ")
            binding.apply {
                exerciseName.text = it.exerciseName
                difficultyLevelTv.text = it.difficultyLevel
                requiredEquipmentTv.text = it.requiredEquipment
                primaryMusclesTv.text = it.primaryMuscles
                secondaryMusclesTv.text = it.secondaryMuscles
                descriptionTv.text = it.description
                Glide.with(exerciseImage.context)
                    .load(it.secondaryImage)
                    .placeholder(R.drawable.image_three)
                    .fitCenter()
                    .centerCrop()
                    .into(exerciseImage)
            }
        }
    }

}