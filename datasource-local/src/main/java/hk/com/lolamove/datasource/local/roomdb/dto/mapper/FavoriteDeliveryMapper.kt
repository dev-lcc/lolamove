package hk.com.lolamove.datasource.local.roomdb.dto.mapper

import hk.com.lolamove.datasource.local.roomdb.dto.DTOFavoriteDelivery
import hk.com.lolamove.datasource.local.roomdb.dto.DTORoute
import hk.com.lolamove.datasource.local.roomdb.dto.DTOSender
import hk.com.lolamove.datasource.local.roomdb.dto.Mapper
import hk.com.lolamove.domain.datamodels.Delivery
import hk.com.lolamove.domain.datamodels.Route
import hk.com.lolamove.domain.datamodels.Sender

object FavoriteDeliveryMapper: Mapper<Delivery, DTOFavoriteDelivery> {
    override fun map(input: Delivery): DTOFavoriteDelivery =
        DTOFavoriteDelivery(
            id = input.id!!,
            remarks = input.remarks,
            pickupTime = input.pickupTime,
            goodsPicture = input.goodsPicture,
            deliveryFee = input.deliveryFee,
            surcharge = input.surcharge,
            route = input.route?.let(FavoriteRouteMapper::map),
            sender = input.sender?.let(FavoriteSender::map),
            currencySymbol = input.currencySymbol,
        )
}

object FavoriteRouteMapper: Mapper<Route, DTORoute> {
    override fun map(input: Route): DTORoute =
        DTORoute(
            start = input.start,
            end = input.end,
        )
}

object FavoriteSender: Mapper<Sender, DTOSender> {
    override fun map(input: Sender): DTOSender =
        DTOSender(
            phone = input.phone,
            name = input.name,
            email = input.email,
        )
}