package hk.com.lolamove.app.ui.home.favorites.adapter

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hk.com.lolamove.domain.datamodels.Delivery
import java.text.SimpleDateFormat
import java.util.*
import hk.com.lolamove.app.R
import hk.com.lolamove.app.databinding.ItemFavoriteBinding
import timber.log.Timber
import java.text.DecimalFormat
import java.text.ParseException

typealias OnTapItem = ((Delivery) -> Unit)
typealias OnRemoveFromFavorites = ((Delivery) -> Unit)

class ItemFavoritesAdapter(
    private val onTapItem: OnTapItem? = null,
    private val onRemoveFromFavorites: OnRemoveFromFavorites? = null,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Delivery> = listOf()

    override fun getItemCount(): Int = items.size

    /**
     * Each time data is set, we update this variable so that if DiffUtil calculation returns
     * after repetitive updates, we can ignore the old calculation
     */
    private var dataVersion = 0
    @Suppress("DEPRECATION")
    @SuppressLint("StaticFieldLeak")
    @MainThread
    fun replace(update: List<Delivery>) {
        dataVersion++
        if (items.isEmpty()) {
            items = update
            notifyDataSetChanged()
        } else if (update.isEmpty()) {
            val oldSize = items.size
            items = listOf()
            notifyItemRangeRemoved(0, oldSize)
        } else {
            val startVersion = dataVersion
            val oldItems = ArrayList(items)

            object : AsyncTask<Void, Void, DiffUtil.DiffResult>() {
                override fun doInBackground(vararg voids: Void): DiffUtil.DiffResult {
                    return DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                        override fun getOldListSize(): Int = oldItems.size
                        override fun getNewListSize(): Int = update.size

                        override fun areItemsTheSame(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Boolean {
                            val oldItem = oldItems[oldItemPosition]
                            val newItem = update[newItemPosition]
                            return oldItem.id == newItem.id
                        }

                        override fun areContentsTheSame(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Boolean {
                            val oldItem = oldItems[oldItemPosition]
                            val newItem = update[newItemPosition]
                            return oldItem == newItem
                        }
                    })
                }

                override fun onPostExecute(diffResult: DiffUtil.DiffResult) {
                    if (startVersion != dataVersion) {
                        // ignore update
                        return
                    }
                    items = update
                    diffResult.dispatchUpdatesTo(this@ItemFavoritesAdapter)
                }
            }
                .execute()
        }
    }

    override fun getItemViewType(position: Int): Int = VIEW_TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when(viewType) {
            VIEW_TYPE_ITEM -> ItemFavoriteViewHolder(
                ItemFavoriteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> object: RecyclerView.ViewHolder(parent) {}
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ItemFavoriteViewHolder -> {
                val item = items[position]
                holder.bind(item)

                holder.itemView.setOnClickListener { onTapItem?.invoke(item) }

                holder.binding.btnFavorite.setOnClickListener {
                    onRemoveFromFavorites?.invoke(item)
                }
            }
        }
    }

    inner class ItemFavoriteViewHolder(
        val binding: ItemFavoriteBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Delivery) {
            val context = itemView.context

            // Delivery Photo
            item.goodsPicture?.let { url ->
                Glide.with(context)
                    .asDrawable()
                    .load(url)
                    .error(R.drawable.ic_delivery_white)
                    .into(binding.acivDeliveryPhoto)
            } ?: run {
                // Set default
                binding.acivDeliveryPhoto.setImageResource(R.drawable.ic_delivery_white)
            }

            // Favorite Toggle
            when(item.isFavorite) {
                true -> {
                    binding.btnFavorite.setImageResource(R.drawable.ic_favorite)
                    binding.btnFavorite.imageTintList = ContextCompat.getColorStateList(context, R.color.red_a400)
                }
                else -> {
                    binding.btnFavorite.setImageResource(R.drawable.ic_favorite_outline)
                    binding.btnFavorite.imageTintList = ContextCompat.getColorStateList(context, R.color.light_gray)
                }
            }

            // Sender Name
            binding.actvSenderName.text = HtmlCompat.fromHtml(
                context.getString(
                    R.string.deliveries_from_label,
                    item.sender?.name ?: ""
                ),
                HtmlCompat.FROM_HTML_MODE_COMPACT
            )
            // Sender Number
            binding.actvPhoneNumber.text = item.sender?.phone
            // Sender Email
            binding.actvEmail.text = item.sender?.email

            // REMARK(S)
            binding.actvRemarks.text = item.remarks

            // Pickup Time
            binding.actvPickupTime.text = item.pickupTime?.let { pickupTime ->
                try {
                    val dateTime = UTC_DATE_FORMATTER.parse(pickupTime)
                    UI_PICKUP_DATE_FORMAT.format(dateTime)
                } catch (err: ParseException) {
                    err.printStackTrace()
                    null
                }
            }

            // Total Price
            item.totalFee?.let { totalFee ->
                binding.actvTotalPrice.text = "${item.currencySymbol ?: ""}${TOTAL_PRICE_FORMAT.format(totalFee.toDouble())}"
                binding.actvTotalPrice.visibility = View.VISIBLE
            } ?: run {
                binding.actvTotalPrice.visibility = View.GONE
            }

        }
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0x01

        private val TOTAL_PRICE_FORMAT = DecimalFormat("###,###,###,##0.00")

        @SuppressLint("NewApi")
        private val UTC_DATE_FORMATTER: SimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
                .apply {
                    timeZone = TimeZone.getTimeZone("UTC" )
                }
        @SuppressLint("NewApi")
        private val UI_PICKUP_DATE_FORMAT: SimpleDateFormat =
            SimpleDateFormat("@HH:mmaa EEE\nMMM dd, yyyy", Locale.getDefault())
    }

}