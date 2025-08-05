package com.synergos.partner.ui.viewdata

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.synergos.partner.R
import com.synergos.partner.adapter.HealthDataAdapter
import com.synergos.partner.databinding.ActivityViewDataBinding
import com.synergos.partner.model.HealthData
import com.synergos.partner.utils.CommonMethods
import com.synergos.partner.utils.ProgressDialogUtil
import com.synergos.partner.utils.UIState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewData : AppCompatActivity() {

    private lateinit var binding: ActivityViewDataBinding
    private val viewModel: ViewDataViewModel by viewModels()
    private lateinit var adapter: HealthDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            WindowCompat.getInsetsController(window, window.decorView)?.apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }

        setupRecyclerView()
        setupChart()
        observeData()

        binding.btnFilterMorning.setOnClickListener {
            val (start, end) = CommonMethods.getMorningRange()
            viewModel.getByTimeRange(start, end)
        }

        binding.btnFilterAfternoon.setOnClickListener {
            val (start, end) = CommonMethods.getAfternoonRange()
            viewModel.getByTimeRange(start, end)
        }

        binding.btnevening.setOnClickListener {
            val (start, end) = CommonMethods.getEveningRange()
            viewModel.getByTimeRange(start, end)
        }

        binding.btnClearFilter.setOnClickListener {
            viewModel.fetchAllData()
        }

        binding.btnSortAsc.setOnClickListener {
            viewModel.getSortedAsc()
        }

        binding.btnSortDesc.setOnClickListener {
            viewModel.getSortedDesc()
        }
        binding.btnAdd.setOnClickListener {
            showAddBottomSheet()
        }
    }

    private fun setupRecyclerView() {
        adapter = HealthDataAdapter(
            onEdit = { healthData ->
                showEditBottomSheet(healthData)
            },
            onDelete = { healthData ->
                viewModel.delete(healthData)
            }
        )

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupChart() {
        binding.lineChart.description.isEnabled = false
        binding.lineChart.setTouchEnabled(true)
        binding.lineChart.setPinchZoom(true)
        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dataState.collect { state ->
                    when (state) {
                        is UIState.Idle -> Unit

                        is UIState.Loading ->
                        {
                            ProgressDialogUtil.showLoadingProgress(this@ViewData, lifecycleScope)
                        }
                        is UIState.Success -> {

                            ProgressDialogUtil.dismiss()

                            if (state.data.isNullOrEmpty()) {
                                CommonMethods.getToast(this@ViewData, "No data available")
                            } else {
                                updateRecyclerView(state.data)
                                updateChart(state.data)
                            }
                        }
                        is UIState.Error -> {
                            ProgressDialogUtil.dismiss()
                            showError(state.message)
                        }
                    }
                }
            }
        }
    }

    private fun updateRecyclerView(data: List<HealthData>) {
        adapter.submitList(data)
    }

    private fun updateChart(data: List<HealthData>) {
        val entries = data.mapIndexed { index, item ->
            Entry(index.toFloat(), item.value.toFloat())
        }

        val dataSet = LineDataSet(entries, "Metric Values").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            lineWidth = 2f
            setDrawCircles(true)
            setDrawValues(true)
        }

        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData
        binding.lineChart.invalidate()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showEditBottomSheet(data: HealthData) {
        val dialogView = layoutInflater.inflate(R.layout.edit_health_bottom_sheet, null)
        val dialog = BottomSheetDialog(this@ViewData)
        dialog.setContentView(dialogView)

        val etEditType = dialogView.findViewById<AutoCompleteTextView>(R.id.etEditType)
        val etEditValue = dialogView.findViewById<EditText>(R.id.etEditValue)
        val btnUpdate = dialogView.findViewById<Button>(R.id.btnUpdate)

        // Pre-fill values
        etEditType.setText(data.type)
        etEditValue.setText(data.value.toString())

        // AutoComplete setup (optional)
        val metricOptions = arrayOf("Steps", "Calories", "Water", "Sleep")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, metricOptions)
        etEditType.setAdapter(adapter)

        etEditType.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                (view as AutoCompleteTextView).showDropDown()
            }
        }

        etEditType.setOnClickListener {
            etEditType.showDropDown()
        }

        btnUpdate.setOnClickListener {
            val newType = etEditType.text.toString()
            val newValue = etEditValue.text.toString().toDoubleOrNull() ?: return@setOnClickListener

            val updatedData = data.copy(type = newType, value = newValue)
            viewModel.update(updatedData)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showAddBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.edit_health_bottom_sheet, null)
        val dialog = BottomSheetDialog(this@ViewData)
        dialog.setContentView(dialogView)

        val etEditType = dialogView.findViewById<AutoCompleteTextView>(R.id.etEditType)
        val etEditValue = dialogView.findViewById<EditText>(R.id.etEditValue)
        val btnUpdate = dialogView.findViewById<Button>(R.id.btnUpdate)

        btnUpdate.setText("Add")

        val metricOptions = arrayOf("Steps", "Calories", "Water", "Sleep")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, metricOptions)
        etEditType.setAdapter(adapter)

        etEditType.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                (view as AutoCompleteTextView).showDropDown()
            }
        }

        etEditType.setOnClickListener {
            etEditType.showDropDown()
        }

        btnUpdate.setOnClickListener {
            val type = etEditType.text.toString()
            val value = etEditValue.text.toString().toDoubleOrNull() ?: return@setOnClickListener

            val timestamp = System.currentTimeMillis()
            val addData = HealthData(0,type,value,timestamp)
            viewModel.insert(addData)
            dialog.dismiss()
        }

        dialog.show()
    }

}
