package hk.com.lolamove.datasource.local.roomdb.dto.mapper

import hk.com.lolamove.datasource.local.roomdb.dto.DTORoute
import hk.com.lolamove.datasource.local.roomdb.dto.Mapper
import hk.com.lolamove.domain.datamodels.Route

object RouteMapper: Mapper<DTORoute, Route> {
    override fun map(input: DTORoute): Route =
        Route(
            start = input.start,
            end = input.end,
        )
}