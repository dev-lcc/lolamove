package hk.com.lolamove.datasource.remote.dto.mapper

import hk.com.lolamove.datasource.remote.dto.DTODelivery
import hk.com.lolamove.datasource.remote.dto.Mapper
import hk.com.lolamove.domain.datamodels.Delivery

object DeliveryMapper : Mapper<DTODelivery, Delivery> {
    override fun map(input: DTODelivery): Delivery {
        val deliveryFee = input.deliveryFee?.let { deliveryFee ->
            REGEX_CURRENCY_VALUE.find(deliveryFee)?.value?.toFloat()
        }
        val surcharge = input.surcharge?.let { surcharge ->
            REGEX_CURRENCY_VALUE.find(surcharge)?.value?.toFloat()
        }
        val totalFee = (deliveryFee ?: 0f) + (surcharge ?: 0f)
        val currencySymbol = run {
            val symbol1 = input.deliveryFee?.let { deliveryFee ->
                REGEX_CURRENCY_SYMBOL.find(deliveryFee)?.value
            }
            val symbol2 = input.surcharge?.let { surcharge ->
                REGEX_CURRENCY_SYMBOL.find(surcharge)?.value
            }

            return@run symbol1 ?: symbol2
        }

        return Delivery(
            id = input.id,
            remarks = input.remarks,
            pickupTime = input.pickupTime,
            goodsPicture = input.goodsPicture,
            deliveryFee = deliveryFee,
            surcharge = surcharge,
            route = input.route?.let(RouteMapper::map),
            sender = input.sender?.let(SenderMapper::map),
            totalFee = totalFee,
            currencySymbol = currencySymbol,
        )
    }

    private val REGEX_CURRENCY_SYMBOL = Regex("^([\\p{Sc}]|([A-Za-z]{1,3}))")
    private val REGEX_CURRENCY_VALUE = Regex("([0-9]+\\.[0-9]{2})\$")
}