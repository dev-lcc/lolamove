package hk.com.lolamove.datasource.remote.dto.mapper

import hk.com.lolamove.domain.datamodels.Route
import hk.com.lolamove.datasource.remote.dto.DTORoute
import hk.com.lolamove.datasource.remote.dto.Mapper

object RouteMapper: Mapper<DTORoute, Route> {
    override fun map(input: DTORoute): Route =
        Route(
            start = input.start,
            end = input.start
        )
}