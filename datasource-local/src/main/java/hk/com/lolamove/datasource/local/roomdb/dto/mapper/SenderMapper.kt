package hk.com.lolamove.datasource.local.roomdb.dto.mapper

import hk.com.lolamove.datasource.local.roomdb.dto.DTOSender
import hk.com.lolamove.datasource.local.roomdb.dto.Mapper
import hk.com.lolamove.domain.datamodels.Sender

object SenderMapper: Mapper<DTOSender, Sender> {
    override fun map(input: DTOSender): Sender =
        Sender(
            phone = input.phone,
            name = input.name,
            email = input.email,
        )
}