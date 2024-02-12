package com.example.android_take_home_exercise_shanglin_yang.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_take_home_exercise_shanglin_yang.R
import com.example.android_take_home_exercise_shanglin_yang.data.Repository
import com.example.android_take_home_exercise_shanglin_yang.data.SecondViewModel
import com.example.android_take_home_exercise_shanglin_yang.data.SecondViewModelFactory
import com.example.android_take_home_exercise_shanglin_yang.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {
    private var _binding: FragmentSecondBinding? = null
    private val adapter = RecyclerViewAdapter()
    private lateinit var viewModel: SecondViewModel
    private var pageNumberTextView: TextView? = null
    private var prevPageButton: ImageView? = null
    private var nextPageButton: ImageView? = null
    private var firstPageButton: ImageView? = null
    private var lastPageButton: ImageView? = null
    private var isToastShowing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        _binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        _binding?.recyclerView?.adapter = adapter
        return _binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = SecondViewModelFactory(Repository(requireContext()))
        viewModel = ViewModelProvider(this, viewModelFactory)[SecondViewModel::class.java]

        pageNumberTextView = view.findViewById(R.id.pageNumberTextView)
        prevPageButton = view.findViewById(R.id.prevPageButton)
        nextPageButton = view.findViewById(R.id.nextPageButton)
        firstPageButton = view.findViewById(R.id.firstPageButton)
        lastPageButton = view.findViewById(R.id.lastPageButton)

        viewModel.data.observe(viewLifecycleOwner) { newData ->
            // Update the UI with the new data
            adapter.setItems(newData)

            // Set the page number after the adapter has been updated
            pageNumberTextView?.text = getString(R.string.page_number_text, viewModel.getCurrentPage() + 1)
        }

        prevPageButton?.setOnClickListener {
            // debug: print page
            Log.d("debug_print", "Adapter Current Page: ${viewModel.getCurrentPage()}")

            if (viewModel.getCurrentPage() > 0) {
                viewModel.setCurrentPage(viewModel.getCurrentPage() - 1)
                viewModel.fetchData()
            } else {
                // Handle the case when already on the first page
                if (!isToastShowing) {
                    Toast.makeText(context, getString(R.string.already_on_first_page), Toast.LENGTH_SHORT).show()

                    isToastShowing = true

                    // Allow showing Toast again after a delay (e.g., 2 seconds)
                    Handler(Looper.myLooper()!!).postDelayed({
                        isToastShowing = false
                    }, 2000)
                }
            }
        }

        nextPageButton?.setOnClickListener {
            // debug: print page
            Log.d("debug_print", "Adapter Current Page: ${viewModel.getCurrentPage()}")

            if (viewModel.getCurrentPage() < viewModel.getTotalPages() - 1) {
                viewModel.setCurrentPage(viewModel.getCurrentPage() + 1)
                viewModel.fetchData()
            } else {
                // Handle the case when already on the last page
                if (!isToastShowing) {
                    Toast.makeText(context, getString(R.string.already_on_last_page), Toast.LENGTH_SHORT).show()

                    isToastShowing = true

                    // Allow showing Toast again after a delay (e.g., 2 seconds)
                    Handler(Looper.myLooper()!!).postDelayed({
                        isToastShowing = false
                    }, 2000)
                }
            }
        }

        firstPageButton?.setOnClickListener {
            // debug: print page
            Log.d("debug_print", "Adapter Current Page: ${viewModel.getCurrentPage()}")

            if (viewModel.getCurrentPage() > 0) {
                viewModel.setCurrentPage(0)
                viewModel.fetchData()
            } else {
                // Handle the case when already on the first page
                if (!isToastShowing) {
                    Toast.makeText(context, getString(R.string.already_on_first_page), Toast.LENGTH_SHORT).show()

                    isToastShowing = true

                    // Allow showing Toast again after a delay (e.g., 2 seconds)
                    Handler(Looper.myLooper()!!).postDelayed({
                        isToastShowing = false
                    }, 2000)
                }
            }
        }

        lastPageButton?.setOnClickListener {
            // debug: print page
            Log.d("debug_print", "Adapter Current Page: ${viewModel.getCurrentPage()}")

            if (viewModel.getCurrentPage() < viewModel.getTotalPages() - 1) {
                viewModel.setCurrentPage(viewModel.getTotalPages() - 1)
                viewModel.fetchData()
            } else {
                // Handle the case when already on the last page
                if (!isToastShowing) {
                    Toast.makeText(context, getString(R.string.already_on_last_page), Toast.LENGTH_SHORT).show()

                    isToastShowing = true

                    // Allow showing Toast again after a delay (e.g., 2 seconds)
                    Handler(Looper.myLooper()!!).postDelayed({
                        isToastShowing = false
                    }, 2000)
                }
            }
        }

        fun showGoToPageDialog(context: Context, totalPages: Int, onGoToPage: (Int) -> Unit) {
            val editText = EditText(context)
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL
            layout.addView(editText)

            AlertDialog.Builder(context)
                .setTitle(getString(R.string.go_to_page))
                .setMessage(R.string.enter_page_number)
                .setView(layout)
                .setPositiveButton(R.string.jump) { _, _ ->
                    val pageNumberStr = editText.text.toString()
                    val pageNumber = pageNumberStr.toIntOrNull()
                    if (pageNumber != null && pageNumber >= 1 && pageNumber <= totalPages) {
                        onGoToPage(pageNumber - 1)
                    } else {
                        Toast.makeText(context,
                            getString(R.string.invalid_page_number), Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        pageNumberTextView?.setOnClickListener {
            context?.let { it1 ->
                showGoToPageDialog(it1, viewModel.getTotalPages()) { selectedPage ->
                    viewModel.setCurrentPage(selectedPage)
                    viewModel.fetchData()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
