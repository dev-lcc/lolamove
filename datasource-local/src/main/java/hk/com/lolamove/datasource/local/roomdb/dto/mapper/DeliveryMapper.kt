package hk.com.lolamove.datasource.local.roomdb.dto.mapper

import hk.com.lolamove.datasource.local.roomdb.dto.DTOFavoriteDelivery
import hk.com.lolamove.datasource.local.roomdb.dto.Mapper
import hk.com.lolamove.domain.datamodels.Delivery

object DeliveryMapper: Mapper<DTOFavoriteDelivery, Delivery> {
    override fun map(input: DTOFavoriteDelivery): Delivery =
        Delivery(
            id = input.id,
            remarks = input.remarks,
            pickupTime = input.pickupTime,
            goodsPicture = input.goodsPicture,
            deliveryFee = input.deliveryFee,
            surcharge = input.surcharge,
            route = input.route?.let(RouteMapper::map),
            sender = input.sender?.let(SenderMapper::map),
            currencySymbol = input.currencySymbol,
        )
}