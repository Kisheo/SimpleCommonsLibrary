package com.dpsoftapps.commons.activities

import android.os.Bundle
import androidx.core.text.HtmlCompat
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import com.dpsoftapps.commons.R
import com.dpsoftapps.commons.databinding.ActivityFaqBinding
import com.dpsoftapps.commons.databinding.ItemFaqBinding
import com.dpsoftapps.commons.extensions.getProperBackgroundColor
import com.dpsoftapps.commons.extensions.getProperPrimaryColor
import com.dpsoftapps.commons.extensions.getProperTextColor
import com.dpsoftapps.commons.extensions.removeUnderlines
import com.dpsoftapps.commons.helpers.APP_FAQ
import com.dpsoftapps.commons.helpers.APP_ICON_IDS
import com.dpsoftapps.commons.helpers.APP_LAUNCHER_NAME
import com.dpsoftapps.commons.helpers.NavigationIcon
import com.dpsoftapps.commons.models.FAQItem

class FAQActivity : BaseSimpleActivity() {
    override fun getAppIconIDs() = intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList()

    override fun getAppLauncherName() = intent.getStringExtra(APP_LAUNCHER_NAME) ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)
        val binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateMaterialActivityViews(binding.faqCoordinator, binding.faqHolder, useTransparentNavigation = true, useTopSearchMenu = false)
        setupMaterialScrollListener(binding.faqNestedScrollview, binding.faqToolbar)

        val textColor = getProperTextColor()
        val backgroundColor = getProperBackgroundColor()
        val primaryColor = getProperPrimaryColor()

        val inflater = LayoutInflater.from(this)
        @Suppress("DEPRECATION")
        val faqItemsAny = intent.getSerializableExtra(APP_FAQ)
        val faqItems = if (faqItemsAny is ArrayList<*>) {
            faqItemsAny.filterIsInstance<FAQItem>() as ArrayList<FAQItem>
        } else {
            ArrayList()
        }

        faqItems.forEach {
            val faqItem = it
            val itemView = inflater.inflate(R.layout.item_faq, binding.faqHolder, false)
            val itemBinding = ItemFaqBinding.bind(itemView)

            itemBinding.faqCard.setCardBackgroundColor(backgroundColor)
            itemBinding.faqTitle.apply {
                text = if (faqItem.title is Int) getString(faqItem.title) else faqItem.title as String
                setTextColor(primaryColor)
            }

            itemBinding.faqText.apply {
                text = if (faqItem.text is Int) HtmlCompat.fromHtml(getString(faqItem.text), HtmlCompat.FROM_HTML_MODE_LEGACY) else faqItem.text as String
                setTextColor(textColor)
                setLinkTextColor(primaryColor)

                movementMethod = LinkMovementMethod.getInstance()
                removeUnderlines()
            }

            binding.faqHolder.addView(itemView)
        }
    }

    override fun onResume() {
        super.onResume()
        setupToolbar(findViewById(R.id.faq_toolbar), NavigationIcon.Arrow)
    }
}
