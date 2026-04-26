@file:Suppress("unused", "UNUSED_PARAMETER", "DEPRECATION", "RedundantQualifierName")

package com.dpsoftapps.commons.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isEmpty
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.ActivityAboutBinding
import com.dpsoftapps.commons.databinding.ItemAboutBinding
import com.dpsoftapps.commons.dialogs.ConfirmationAdvancedDialog
import com.dpsoftapps.commons.dialogs.RateStarsDialog
import com.dpsoftapps.commons.extensions.*
import com.dpsoftapps.commons.helpers.*
import com.dpsoftapps.commons.models.FAQItem

@Suppress("unused")
class AboutActivity : BaseSimpleActivity() {
    private var appName = ""
    private var primaryColor = 0
    private var textColor = 0
    private var backgroundColor = 0
    private lateinit var inflater: LayoutInflater
    private lateinit var binding: ActivityAboutBinding

    private var firstVersionClickTS = 0L
    private var clicksSinceFirstClick = 0
    private val EASTER_EGG_TIME_LIMIT = 3000L
    private val EASTER_EGG_REQUIRED_CLICKS = 7

    override fun getAppIconIDs() = intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList()

    override fun getAppLauncherName() = intent.getStringExtra(APP_LAUNCHER_NAME) ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        primaryColor = getProperPrimaryColor()
        textColor = getProperTextColor()
        backgroundColor = getProperBackgroundColor()
        inflater = LayoutInflater.from(this)

        updateMaterialActivityViews(binding.aboutCoordinator, binding.aboutHolder, useTransparentNavigation = true, useTopSearchMenu = false)
        setupMaterialScrollListener(binding.aboutNestedScrollview, binding.aboutToolbar)

        appName = intent.getStringExtra(APP_NAME) ?: ""

        listOf(binding.aboutSupport, binding.aboutHelpUs, binding.aboutSocial, binding.aboutOther).forEach { tv ->
            tv.setTextColor(primaryColor)
        }
    }

    override fun onResume() {
        super.onResume()
        updateTextColors(binding.aboutNestedScrollview)
        setupToolbar(binding.aboutToolbar, NavigationIcon.Arrow)

        binding.aboutSupportLayout.removeAllViews()
        binding.aboutHelpUsLayout.removeAllViews()
        binding.aboutSocialLayout.removeAllViews()
        binding.aboutOtherLayout.removeAllViews()

        setupFAQ()
      //  setupEmail()
      //  setupRateUs()
      //  setupInvite()
        setupContributors()
       // setupDonate()
      //  setupFacebook()
      //  setupGitHub()
       // setupReddit()
       // setupTelegram()
      //  setupMoreApps()
      //  setupWebsite()
      //  setupPrivacyPolicy()
      //  setupLicense()
      //  setupVersion()
    }

    private fun setupFAQ() {
        @Suppress("DEPRECATION")
        val faqItemsAny = intent.getSerializableExtra(APP_FAQ)
        val faqItems = if (faqItemsAny is ArrayList<*>) faqItemsAny.filterIsInstance<FAQItem>() as ArrayList<FAQItem> else ArrayList()
        if (faqItems.isNotEmpty()) {
            val itemView = inflater.inflate(R.layout.item_about, binding.aboutSupportLayout, false)
            itemView.let {
                setupAboutItem(it, R.drawable.ic_question_mark_vector, R.string.frequently_asked_questions)
                binding.aboutSupportLayout.addView(it)

                it.setOnClickListener {
                    launchFAQActivity()
                }
            }
        }
    }

    private fun launchFAQActivity() {
        @Suppress("DEPRECATION")
        val faqItemsAny = intent.getSerializableExtra(APP_FAQ)
        val faqItems = if (faqItemsAny is ArrayList<*>) faqItemsAny.filterIsInstance<FAQItem>() as ArrayList<FAQItem> else ArrayList()
        Intent(applicationContext, FAQActivity::class.java).apply {
            putExtra(APP_ICON_IDS, getAppIconIDs())
            putExtra(APP_LAUNCHER_NAME, getAppLauncherName())
            putExtra(APP_FAQ, faqItems)
            startActivity(this)
        }
    }

    private fun setupEmail() {
        if (resources.getBoolean(R.bool.hide_all_external_links)) {
            if (binding.aboutSupportLayout.isEmpty()) {
                binding.aboutSupport.beGone()
                binding.aboutSupportDivider.beGone()
            }

            return
        }

        val itemView = inflater.inflate(R.layout.item_about, binding.aboutSupportLayout, false)
        itemView.let {
            setupAboutItem(it, R.drawable.ic_mail_vector, R.string.my_email)
            binding.aboutSupportLayout.addView(it)

            it.setOnClickListener {
                val msg = "${getString(R.string.before_asking_question_read_faq)}\n\n${getString(R.string.make_sure_latest)}"
                if (intent.getBooleanExtra(SHOW_FAQ_BEFORE_MAIL, false) && !baseConfig.wasBeforeAskingShown) {
                    baseConfig.wasBeforeAskingShown = true
                    ConfirmationAdvancedDialog(this@AboutActivity, msg, 0, R.string.read_faq, R.string.skip) { success ->
                        if (success) {
                            launchFAQActivity()
                        } else {
                            launchEmailIntent()
                        }
                    }
                } else {
                    launchEmailIntent()
                }
            }
        }
    }

    private fun launchEmailIntent() {
        val appVersion = getString(R.string.app_version, intent.getStringExtra(APP_VERSION_NAME))
        val deviceOS = getString(R.string.device_os, Build.VERSION.RELEASE)
        val newline = "\n"
        val separator = "------------------------------"
        val body = "$appVersion$newline$deviceOS$newline$separator$newline$newline"

        val address = if (packageName.startsWith("com.dpsoft")) {
            "dompango@outlook.com"
        } else {
            "dompango@outlook.com"
        }

        val selectorIntent = Intent(ACTION_SENDTO)
            .setData("mailto:$address".toUri())
        val emailIntent = Intent(ACTION_SEND).apply {
            putExtra(EXTRA_EMAIL, arrayOf(address))
            putExtra(EXTRA_SUBJECT, appName)
            putExtra(EXTRA_TEXT, body)
            selector = selectorIntent
        }

        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            val chooser = createChooser(emailIntent, getString(R.string.send_email))
            try {
                startActivity(chooser)
            } catch (e: Exception) {
                toast(R.string.no_email_client_found)
            }
        } catch (e: Exception) {
            showErrorToast(e)
        }
    }

    private fun setupRateUs() {
        if (resources.getBoolean(R.bool.hide_google_relations) || resources.getBoolean(R.bool.hide_all_external_links)) {
            return
        }

        val itemView = inflater.inflate(R.layout.item_about, binding.aboutHelpUsLayout, false)
        itemView.let {
            setupAboutItem(it, R.drawable.ic_star_vector, R.string.rate_us)
            binding.aboutHelpUsLayout.addView(it)

            it.setOnClickListener {
                if (baseConfig.wasBeforeRateShown) {
                    launchRateUsPrompt()
                } else {
                    baseConfig.wasBeforeRateShown = true
                    val msg = "${getString(R.string.before_rate_read_faq)}\n\n${getString(R.string.make_sure_latest)}"
                    ConfirmationAdvancedDialog(this@AboutActivity, msg, 0, R.string.read_faq, R.string.skip) { success ->
                        if (success) {
                            launchFAQActivity()
                        } else {
                            launchRateUsPrompt()
                        }
                    }
                }
            }
        }
    }

    private fun launchRateUsPrompt() {
        if (baseConfig.wasAppRated) {
            redirectToRateUs()
        } else {
            RateStarsDialog(this@AboutActivity)
        }
    }

    private fun setupInvite() {
        if (resources.getBoolean(R.bool.hide_google_relations) || resources.getBoolean(R.bool.hide_all_external_links)) {
            return
        }

        val itemView = inflater.inflate(R.layout.item_about, binding.aboutHelpUsLayout, false)
        itemView.let {
            setupAboutItem(it, R.drawable.ic_add_person_vector, R.string.invite_friends)
            binding.aboutHelpUsLayout.addView(it)

            it.setOnClickListener {
                val text = String.format(getString(R.string.share_text), appName, getStoreUrl())
                Intent().apply {
                    action = ACTION_SEND
                    putExtra(EXTRA_SUBJECT, appName)
                    putExtra(EXTRA_TEXT, text)
                    type = "text/plain"
                    startActivity(createChooser(this, getString(R.string.invite_via)))
                }
            }
        }
    }

    private fun setupContributors() {
        val itemView = inflater.inflate(R.layout.item_about, binding.aboutHelpUsLayout, false)
        itemView.let {
            setupAboutItem(it, R.drawable.ic_face_vector, R.string.contributors)
            binding.aboutHelpUsLayout.addView(it)

            it.setOnClickListener {
                val intent = Intent(applicationContext, ContributorsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupDonate() {
        if (resources.getBoolean(R.bool.show_donate_in_about) && !resources.getBoolean(R.bool.hide_all_external_links)) {
            val itemView = inflater.inflate(R.layout.item_about, binding.aboutHelpUsLayout, false)
            itemView.let {
                setupAboutItem(it, R.drawable.ic_dollar_vector, R.string.donate)
                binding.aboutHelpUsLayout.addView(it)

                it.setOnClickListener {
                    launchViewIntent(getString(R.string.donate_url))
                }
            }
        }
    }

    private fun setupFacebook() {
        if (resources.getBoolean(R.bool.hide_all_external_links)) {
            return
        }

        val itemView = inflater.inflate(R.layout.item_about, binding.aboutSocialLayout, false)
        itemView.let {
            val itemBinding = ItemAboutBinding.bind(it)
            itemBinding.aboutItemIcon.setImageResource(R.drawable.ic_facebook_vector)
            itemBinding.aboutItemLabel.setText(R.string.facebook)
            itemBinding.aboutItemLabel.setTextColor(textColor)
            binding.aboutSocialLayout.addView(it)

            it.setOnClickListener {
                var link = "https://www.facebook.com/simplemobiletools"
                try {
                    packageManager.getPackageInfo("com.facebook.katana", 0)
                    link = "fb://page/150270895341774"
                } catch (ignored: Exception) {
                }

                launchViewIntent(link)
            }
        }
    }

    private fun setupGitHub() {
        if (resources.getBoolean(R.bool.hide_all_external_links)) {
            return
        }

        val itemView = inflater.inflate(R.layout.item_about, binding.aboutSocialLayout, false)
        itemView.let {
            val itemBinding = ItemAboutBinding.bind(it)
            itemBinding.aboutItemIcon.setImageDrawable(resources.getColoredDrawableWithColor(R.drawable.ic_github_vector, backgroundColor.getContrastColor()))
            itemBinding.aboutItemLabel.setText(R.string.github)
            itemBinding.aboutItemLabel.setTextColor(textColor)
            binding.aboutSocialLayout.addView(it)

            it.setOnClickListener {
                launchViewIntent("https://github.com/Kisheo")
            }
        }
    }

    private fun setupReddit() {
        if (resources.getBoolean(R.bool.hide_all_external_links)) {
            return
        }

        val itemView = inflater.inflate(R.layout.item_about, binding.aboutSocialLayout, false)
        itemView.let {
            val itemBinding = ItemAboutBinding.bind(it)
            itemBinding.aboutItemIcon.setImageResource(R.drawable.ic_reddit_vector)
            itemBinding.aboutItemLabel.setText(R.string.reddit)
            itemBinding.aboutItemLabel.setTextColor(textColor)
            binding.aboutSocialLayout.addView(it)

            it.setOnClickListener {
                launchViewIntent("https://www.reddit.com/r/dpsoftApps")
            }
        }
    }

    private fun setupTelegram() {
        if (resources.getBoolean(R.bool.hide_all_external_links)) {
            if (binding.aboutSocialLayout.isEmpty()) {
                binding.aboutSocial.beGone()
                binding.aboutSocialDivider.beGone()
            }

            return
        }

        val itemView = inflater.inflate(R.layout.item_about, binding.aboutSocialLayout, false)
        itemView.let {
            val itemBinding = ItemAboutBinding.bind(it)
            itemBinding.aboutItemIcon.setImageResource(R.drawable.ic_telegram_vector)
            itemBinding.aboutItemLabel.setText(R.string.telegram)
            itemBinding.aboutItemLabel.setTextColor(textColor)
            binding.aboutSocialLayout.addView(it)

            it.setOnClickListener {
                launchViewIntent("https://t.me/Dpsxxxxxxx")
            }
        }
    }

    private fun setupMoreApps() {
        if (resources.getBoolean(R.bool.hide_google_relations)) {
            return
        }

        val itemView = inflater.inflate(R.layout.item_about, binding.aboutOtherLayout, false)
        itemView.let {
            setupAboutItem(it, R.drawable.ic_heart_vector, R.string.more_apps_from_us)
            binding.aboutOtherLayout.addView(it)

            it.setOnClickListener {
                launchMoreAppsFromUsIntent()
            }
        }
    }

    private fun setupWebsite() {
        if (!resources.getBoolean(R.bool.show_donate_in_about) || resources.getBoolean(R.bool.hide_all_external_links)) {
            return
        }

        val itemView = inflater.inflate(R.layout.item_about, binding.aboutOtherLayout, false)
        itemView.let {
            setupAboutItem(it, R.drawable.ic_link_vector, R.string.website)
            binding.aboutOtherLayout.addView(it)

            it.setOnClickListener {
                launchViewIntent("https://website.com/")
            }
        }
    }

    private fun setupPrivacyPolicy() {
        if (resources.getBoolean(R.bool.hide_all_external_links)) {
            return
        }

        val itemView = inflater.inflate(R.layout.item_about, binding.aboutOtherLayout, false)
        itemView.let {
            setupAboutItem(it, R.drawable.ic_unhide_vector, R.string.privacy_policy)
            binding.aboutOtherLayout.addView(it)

            it.setOnClickListener {
                val appId = baseConfig.appId.removeSuffix(".debug").removeSuffix(".pro").removePrefix("com.dpsoftapps.")
                val url = "https://Kisheo.com/privacy/$appId.txt"
                launchViewIntent(url)
            }
        }
    }

    private fun setupLicense() {
        val itemView = inflater.inflate(R.layout.item_about, binding.aboutOtherLayout, false)
        itemView.let {
            setupAboutItem(it, R.drawable.ic_article_vector, R.string.third_party_licences)
            binding.aboutOtherLayout.addView(it)

            it.setOnClickListener {
                Intent(applicationContext, LicenseActivity::class.java).apply {
                    putExtra(APP_ICON_IDS, getAppIconIDs())
                    putExtra(APP_LAUNCHER_NAME, getAppLauncherName())
                    putExtra(APP_LICENSES, intent.getLongExtra(APP_LICENSES, 0))
                    startActivity(this)
                }
            }
        }
    }

    private fun setupVersion() {
        var version = intent.getStringExtra(APP_VERSION_NAME) ?: ""
        if (baseConfig.appId.removeSuffix(".debug").endsWith(".pro")) {
            version += " ${getString(R.string.pro)}"
        }

        val itemView = inflater.inflate(R.layout.item_about, binding.aboutOtherLayout, false)
        itemView.let {
            val itemBinding = ItemAboutBinding.bind(it)
            itemBinding.aboutItemIcon.setImageDrawable(resources.getColoredDrawableWithColor(R.drawable.ic_info_vector, textColor))
            val fullVersion = getString(R.string.version_placeholder, version)
            itemBinding.aboutItemLabel.text = fullVersion
            itemBinding.aboutItemLabel.setTextColor(textColor)
            binding.aboutOtherLayout.addView(it)

            it.setOnClickListener {
                if (firstVersionClickTS == 0L) {
                    firstVersionClickTS = System.currentTimeMillis()
                    Handler(Looper.getMainLooper()).postDelayed({
                        firstVersionClickTS = 0L
                        clicksSinceFirstClick = 0
                    }, EASTER_EGG_TIME_LIMIT)
                }

                clicksSinceFirstClick++
                if (clicksSinceFirstClick >= EASTER_EGG_REQUIRED_CLICKS) {
                    toast(R.string.hello)
                    firstVersionClickTS = 0L
                    clicksSinceFirstClick = 0
                }
            }
        }
    }

    private fun setupAboutItem(view: View, drawableId: Int, textId: Int) {
        val itemBinding = ItemAboutBinding.bind(view)
        itemBinding.aboutItemIcon.setImageDrawable(resources.getColoredDrawableWithColor(drawableId, textColor))
        itemBinding.aboutItemLabel.setText(textId)
        itemBinding.aboutItemLabel.setTextColor(textColor)
    }
}
