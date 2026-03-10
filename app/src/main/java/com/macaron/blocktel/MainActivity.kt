package com.macaron.blocktel

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.macaron.blocktel.databinding.ActivityMainBinding
import com.macaron.blocktel.ui.PrefixAdapter
import com.macaron.blocktel.ui.RecentCallsAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefixAdapter: PrefixAdapter
    private lateinit var recentAdapter: RecentCallsAdapter
    private lateinit var app: BlockTelApp

    private val screeningRoleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, R.string.screening_granted, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.screening_denied, Toast.LENGTH_LONG).show()
        }
        updateStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        app = application as BlockTelApp

        setupPrefixList()
        setupRecentList()
        setupMasterToggle()
        checkScreeningRole()
        updateStats()
    }

    override fun onResume() {
        super.onResume()
        updateStats()
        updateStatus()
        recentAdapter.update(app.blockedCallLog.getRecentEntries(20))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_prefix -> {
                showAddPrefixDialog()
                true
            }
            R.id.action_select_all -> {
                app.prefixRepository.setAllEnabled(true)
                prefixAdapter.update(app.prefixRepository.getAllRules())
                true
            }
            R.id.action_deselect_all -> {
                app.prefixRepository.setAllEnabled(false)
                prefixAdapter.update(app.prefixRepository.getAllRules())
                true
            }
            R.id.action_clear_log -> {
                app.blockedCallLog.clear()
                recentAdapter.update(emptyList())
                updateStats()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupPrefixList() {
        prefixAdapter = PrefixAdapter(
            rules = app.prefixRepository.getAllRules(),
            onToggle = { prefix, enabled ->
                app.prefixRepository.setEnabled(prefix, enabled)
            },
            onLongClick = { prefix ->
                val rule = app.prefixRepository.getAllRules().find { it.prefix == prefix }
                if (rule?.region == "Personnalise") {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.delete_prefix_title)
                        .setMessage(getString(R.string.delete_prefix_message, prefix))
                        .setPositiveButton(R.string.delete) { _, _ ->
                            app.prefixRepository.removeCustomPrefix(prefix)
                            prefixAdapter.update(app.prefixRepository.getAllRules())
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .show()
                }
            }
        )
        binding.recyclerPrefixes.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = prefixAdapter
        }
    }

    private fun setupRecentList() {
        recentAdapter = RecentCallsAdapter(app.blockedCallLog.getRecentEntries(20))
        binding.recyclerRecent.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = recentAdapter
        }
    }

    private fun setupMasterToggle() {
        binding.switchMaster.isChecked = app.prefixRepository.isBlockingActive()
        binding.switchMaster.setOnCheckedChangeListener { _, isChecked ->
            app.prefixRepository.setBlockingActive(isChecked)
            updateStatus()
        }
    }

    private fun checkScreeningRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                    screeningRoleLauncher.launch(intent)
                }
            }
        }
    }

    private fun updateStats() {
        val total = app.blockedCallLog.getTotalBlocked()
        val today = app.blockedCallLog.getTodayBlocked()
        binding.textStatsTotal.text = getString(R.string.stats_total, total)
        binding.textStatsToday.text = getString(R.string.stats_today, today)
    }

    private fun updateStatus() {
        val active = app.prefixRepository.isBlockingActive()
        val enabledCount = app.prefixRepository.getEnabledPrefixes().size
        if (active && enabledCount > 0) {
            binding.textStatus.text = getString(R.string.status_active, enabledCount)
            binding.textStatus.setTextColor(getColor(R.color.status_active))
            binding.cardStatus.setCardBackgroundColor(getColor(R.color.status_active_bg))
        } else {
            binding.textStatus.text = getString(R.string.status_inactive)
            binding.textStatus.setTextColor(getColor(R.color.status_inactive))
            binding.cardStatus.setCardBackgroundColor(getColor(R.color.status_inactive_bg))
        }

        // Show role warning if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            val hasRole = roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
            binding.textRoleWarning.visibility = if (hasRole) View.GONE else View.VISIBLE
            binding.btnRequestRole.visibility = if (hasRole) View.GONE else View.VISIBLE
            binding.btnRequestRole.setOnClickListener { checkScreeningRole() }
        }
    }

    private fun showAddPrefixDialog() {
        val input = android.widget.EditText(this).apply {
            hint = "Ex: 0173"
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            setPadding(48, 32, 48, 32)
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.add_prefix_title)
            .setView(input)
            .setPositiveButton(R.string.add) { _, _ ->
                val raw = input.text.toString().trim()
                if (raw.length >= 3) {
                    app.prefixRepository.addCustomPrefix(raw, NumberNormalizer.formatForDisplay(raw))
                    prefixAdapter.update(app.prefixRepository.getAllRules())
                } else {
                    Toast.makeText(this, R.string.prefix_too_short, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
